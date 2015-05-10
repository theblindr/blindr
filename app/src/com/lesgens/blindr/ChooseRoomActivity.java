package com.lesgens.blindr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.grio.fbphotopicker.FBPhotoPickerActivity;
import com.lesgens.blindr.adapters.TrendingAdapter;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.controllers.PreferencesController;
import com.lesgens.blindr.models.City;
import com.lesgens.blindr.models.Trend;

public class ChooseRoomActivity extends FragmentActivity implements OnClickListener, OnMapLongClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnItemClickListener {
	private GoogleMap map;
	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;
	private LocationRequest mLocationRequest;
	private Handler handler;
	private ListView trendingList;
	private TrendingAdapter trendingAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.choose_map);
		
		handler = new Handler(getMainLooper());

		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		((TextView) findViewById(R.id.textViewTop)).setTypeface(tf);
		findViewById(R.id.lastUsedAddress).setOnClickListener(this);
		
		trendingList = (ListView) findViewById(R.id.trending_list);
		trendingList.setOnItemClickListener(this);

		((ImageView) findViewById(R.id.avatar)).setImageBitmap(Controller.getInstance().getMyself().getAvatar());
		((TextView) findViewById(R.id.fake_name)).setText("Angry Plot");

		mLocationRequest = new LocationRequest();
		mLocationRequest.setInterval(2000);
		mLocationRequest.setNumUpdates(1);
		mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		new Handler(getMainLooper()).postDelayed(new Runnable(){

			@Override
			public void run() {
				buildGoogleApiClient();

				mGoogleApiClient.connect();
			}

		}, 300);
		// Get a handle to the Map Fragment
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		map.setMyLocationEnabled(true);
		map.setOnMapLongClickListener(this);
	}

	@Override
	public void onResume(){
		super.onResume();
		String lastConnection = PreferencesController.getLastConnection(this);
		if(!lastConnection.isEmpty()){
			findViewById(R.id.lastUsedAddress).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.lastUsedAddress)).setText(getString(R.string.reconnect_to_last, lastConnection));
		} else{
			findViewById(R.id.lastUsedAddress).setVisibility(View.GONE);
		}
		
		refreshList();
	}
	
	public void refreshList(){
		//Fetch from server
		ArrayList<Trend> trends = new ArrayList<Trend>();
		trends.add(new Trend("Montreal, Quebec", 65));
		trends.add(new Trend("Rue de la Visitation, Montreal", 34));
		trends.add(new Trend("Quebec, Canada", 842));
		trends.add(new Trend("United States", 43223));
		trendingAdapter = new TrendingAdapter(trends);
		trendingList.setAdapter(trendingAdapter);
	}

	@Override
	public void onMapLongClick(final LatLng point) {
		handler.post(new Runnable(){

			@Override
			public void run() {
				Geocoder geoCoder = new Geocoder(ChooseRoomActivity.this);

				List<Address> matches;
				try {
					matches = geoCoder.getFromLocation(point.latitude, point.longitude, 10);
					Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
					if(bestMatch != null) {
						ArrayList<String> items = new ArrayList<String>();

						if(bestMatch.getFeatureName() != null && bestMatch.getFeatureName().equals(bestMatch.getThoroughfare())){
							if(bestMatch.getFeatureName() != null){
								items.add(bestMatch.getFeatureName());
							}
						}
						else{
							if(bestMatch.getThoroughfare() != null){
								items.add(bestMatch.getThoroughfare());
							}
							if(bestMatch.getLocality() != null){
								items.add(bestMatch.getLocality());
							}
							if(bestMatch.getAdminArea() != null){
								items.add(bestMatch.getAdminArea());
							}
							if(bestMatch.getCountryName() != null){
								items.add(bestMatch.getCountryName());
							}
						}

						if(items.size() > 0){
							String[] itemsS = new String[items.size()];
							itemsS= items.toArray(itemsS);
							onCreateDialog(itemsS).show();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}});
	}

	public Dialog onCreateDialog(final String[] items) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.pick_a_room)
		.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String city = items[which];
				if(which != items.length - 1){
					city += ", " + items[which + 1];
				}
				goToPublicChat(city);
			}
		});
		return builder.create();
	}

	public void goToPublicChat(String city){
		Controller.getInstance().setCity(new City(city));
		PreferencesController.setPreference(this, PreferencesController.LAST_CONNECTION, city);
		Intent i = new Intent(ChooseRoomActivity.this, PublicChatActivity.class);
		startActivity(i);
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
				mGoogleApiClient);
		CameraPosition cameraPosition = new CameraPosition.Builder()
		.target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))      // Sets the center of the map to Mountain View
		.zoom(15)                   // Sets the zoom
		.build();                   // Creates a CameraPosition from the builder
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(LocationServices.API)
		.build();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.lastUsedAddress){
			String lastCity = PreferencesController.getLastConnection(this);
			goToPublicChat(lastCity);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Trend trend = trendingAdapter.getItem(position);
		goToPublicChat(trend.getCity());
	}
}