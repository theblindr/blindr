package com.mchacks.blindr;

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
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.models.City;

public class SplashscreenActivity extends Activity implements
ConnectionCallbacks, OnConnectionFailedListener {
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;

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
			finish();
		}

		new Handler(getMainLooper()).postDelayed(new Runnable(){

			@Override
			public void run() {
				buildGoogleApiClient();

				mGoogleApiClient.connect();
			}

		}, 300);
	}

	public void onDestroy(){
		super.onDestroy();

		if(mGoogleApiClient != null){
			if(mGoogleApiClient.isConnected() || mGoogleApiClient.isConnecting()){
				mGoogleApiClient.disconnect();
			}
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
				Intent i = new Intent(SplashscreenActivity.this, ConnectFacebookActivity.class);
				startActivity(i);
				finish();
			} catch (IOException e) {
				e.printStackTrace();
			}
			catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
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
