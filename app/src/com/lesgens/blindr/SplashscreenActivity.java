package com.lesgens.blindr;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.models.City;
import com.lesgens.blindr.models.Server;
import com.lesgens.blindr.models.UserAuthenticatedListener;
import com.lesgens.blindr.views.CustomYesNoDialog;
import com.todddavies.components.progressbar.ProgressWheel;

public class SplashscreenActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener, UserAuthenticatedListener {
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private static final String[] PERMISSIONS = {"public_profile", "user_photos"};
	private boolean connected = false;
	private boolean authenticated = false;
	private boolean geolocated = false;

	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	}; 

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.i("SplashscreenActivity", "onSessionStateChange");
		Controller.getInstance().setSession(session);
		if(state.isOpened() && !connected){
			connected = true;
			session.refreshPermissions();
			List<String> permissions = session.getPermissions();
			Log.i("FACEBOOK_CONNECTION", "Logged in..." + permissions.toString());
			findViewById(R.id.authButton).setVisibility(View.GONE);
			findViewById(R.id.splash_text).setVisibility(View.GONE);
			findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
			((ProgressWheel) findViewById(R.id.progressBar)).spin();
			Server.connect(session.getAccessToken());
		} else if(state.isClosed()) {
			connected = false;
			Log.i("FACEBOOK_CONNECTION", "Logged out...");
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Controller.getInstance().setDimensionAvatar(this);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splashscreen);

		TextView tv = (TextView) findViewById(R.id.splash_text);

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		tv.setTypeface(tf);

		if(!isNetworkAvailable()){
			CustomYesNoDialog dialog = new CustomYesNoDialog(this){

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
					finish();
				}

			};

			dialog.show();
			dialog.transformAsOkDialog();
			dialog.setDialogText(getString(R.string.no_network));
		} else{

			new Handler(getMainLooper()).postDelayed(new Runnable(){

				@Override
				public void run() {
					buildGoogleApiClient();

					mGoogleApiClient.connect();
				}

			}, 300);
		}
		
		Server.addUserAuthenticatedListener(this);
		LoginButton authButton = (LoginButton)findViewById(R.id.authButton);
		authButton.setReadPermissions(PERMISSIONS);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
	}

	public void onDestroy(){
		super.onDestroy();
		uiHelper.onDestroy();
		
		if(mGoogleApiClient != null){
			if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
				mGoogleApiClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("SplashscreenActivity", "onActivityResult");
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i("SplashscreenActivity", "onPause");
		uiHelper.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i("SplashscreenActivity", "onResume");
		Session session = Session.getActiveSession();
		if (session != null &&
				(session.isOpened() || session.isClosed()) ) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.i("SplashscreenActivity", "onSaveInstanceState");
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onUserAuthenticated() {
		Log.i("SplashscreenActivity", "onUserAuthenticated");
		authenticated = true;
		if(geolocated){
			goToPublicChat();
		}
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		if (mLastLocation != null) {
			Geocoder geoCoder = new Geocoder(this, Locale.CANADA);
			try {
				List<Address> address = geoCoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);
				final String city = address.get(0).getLocality();
				android.util.Log.i("Blindr", "City name=" + city);
				Controller.getInstance().setCity(new City(city));
				geolocated = true;
				if(authenticated){
					goToPublicChat();
				}
			} catch (IOException e) {
				e.printStackTrace();
				CustomYesNoDialog dialog = new CustomYesNoDialog(this){

					@Override
					public void onPositiveClick() {
						super.onPositiveClick();
						finish();
					}

				};

				dialog.show();
				dialog.transformAsOkDialog();
				dialog.setDialogText(getString(R.string.location_not_found));
			}
			catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void goToPublicChat(){
		Intent i = new Intent(SplashscreenActivity.this, PublicChatActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}
}
