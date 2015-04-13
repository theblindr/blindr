package com.lesgens.blindr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lesgens.blindr.adapters.PrivateChatAdapter;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.listeners.EventsListener;
import com.lesgens.blindr.listeners.FacebookProfileListener;
import com.lesgens.blindr.models.Event;
import com.lesgens.blindr.models.Match;
import com.lesgens.blindr.models.Message;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.network.Server;
import com.lesgens.blindr.receivers.NetworkStateReceiver;
import com.lesgens.blindr.receivers.NetworkStateReceiver.NetworkStateReceiverListener;
import com.lesgens.blindr.utils.DropDownAnim;
import com.lesgens.blindr.utils.Utils;
import com.lesgens.blindr.views.SlideshowView;

public class PrivateChatActivity extends Activity implements OnClickListener, EventsListener, NetworkStateReceiverListener, FacebookProfileListener {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Typeface tf;
	private ImageView sendBt;
	private PrivateChatAdapter chatAdapter;
	private StickyListHeadersListView listMessages;
	private EditText editText;
	private ScheduledExecutorService scheduler;
	private Future<?> future;
	private User remoteUser;
	private TextView tvConnectionProblem;
	private NetworkStateReceiver networkStateReceiver;
	private SlideshowView slideshow;
	private View slideshowSep;
	private Uri imageUri;
	private boolean isComingBackFromTakingPhoto;

	public static void show(Context context, String tokenId, String realName){
		Intent i = new Intent(context, PrivateChatActivity.class);
		i.putExtra("tokenId", tokenId);
		i.putExtra("realName", realName);
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.private_chat);
		
		isComingBackFromTakingPhoto = false;

		remoteUser = Controller.getInstance().getUser(getIntent().getStringExtra("tokenId"));
		TextView fbName = (TextView) findViewById(R.id.fbName);

		tf = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");
		fbName.setTypeface(tf);
		fbName.setText(getIntent().getStringExtra("realName"));

		((ImageView) findViewById(R.id.avatar)).setImageBitmap(remoteUser.getAvatar());
		findViewById(R.id.back).setOnClickListener(this);
		
		findViewById(R.id.photos).setOnClickListener(this);
		findViewById(R.id.photos).setEnabled(false);
		
		if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) == false){
			findViewById(R.id.send_picture).setVisibility(View.GONE);
		} else{
			findViewById(R.id.send_picture).setOnClickListener(this);
		}

		editText = (EditText) findViewById(R.id.editText);
		editText.clearFocus();

		sendBt = (ImageView) findViewById(R.id.send);
		sendBt.setOnClickListener(this);
		
		tvConnectionProblem = (TextView) findViewById(R.id.connection_problem);
		
		slideshow = (SlideshowView) findViewById(R.id.slideshow);
		slideshowSep = findViewById(R.id.slideshow_sep);

		chatAdapter = new PrivateChatAdapter(this, new ArrayList<Message>());
		listMessages = (StickyListHeadersListView) findViewById(R.id.list);
		listMessages.setAdapter(chatAdapter);
		listMessages.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);

		Server.addEventsListener(this);
		Server.addProfileListener(this);
		Server.getUserFacebookInfos(remoteUser);
		scheduler = Executors.newSingleThreadScheduledExecutor();
		
		networkStateReceiver = new NetworkStateReceiver(this);

	}

	@Override
	public void onResume(){
		super.onResume();
		if(!isComingBackFromTakingPhoto){
			Server.getUserEvents(remoteUser);
		}
		if(scheduler != null){
			future = scheduler.scheduleAtFixedRate
					(new Runnable() {
						public void run() {
							Server.getEvents();
						}
					}, 0, 5, TimeUnit.SECONDS);
		}
		
		networkStateReceiver.addListener(this);
	    this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
	}

	@Override
	public void onPause(){
		super.onPause();
		if(future != null){
			future.cancel(true);
		}
		
		networkStateReceiver.removeListener(this);
	    this.unregisterReceiver(networkStateReceiver);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(scheduler != null){
			scheduler.shutdownNow();
		}
		Server.removeProfileListener(this);
		Server.removeEventsListener(this);
		slideshow.recycleBitmaps();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == Activity.RESULT_OK) {
				getContentResolver().notifyChange(imageUri, null);
				
				new Handler(getMainLooper()).post(new Runnable(){

					@Override
					public void run() {
						try {
							Bitmap bitmap = android.provider.MediaStore.Images.Media
									.getBitmap(getContentResolver(), imageUri);
							ByteArrayOutputStream stream = new ByteArrayOutputStream();

							final Bitmap bitmapScaled = Utils.scaleDown(bitmap, Utils.dpInPixels(PrivateChatActivity.this, 400), true);
							bitmap.recycle();
							bitmapScaled.compress(Bitmap.CompressFormat.PNG, 100, stream);
							byte[] byteArray = stream.toByteArray();

							String encoded = Utils.BLINDR_IMAGE_BASE + Base64.encodeToString(byteArray, Base64.DEFAULT);
							Message message = new Message(Controller.getInstance().getMyself(), encoded, false);
							chatAdapter.addMessage(message);
							chatAdapter.notifyDataSetChanged();
							Server.sendPrivateMessage(remoteUser, message.getMessage());
							editText.setText("");
							scrollMyListViewToTheBottomNowWeHere();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}});
				



			}
		}   
	}
	
	public void takePhoto() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        isComingBackFromTakingPhoto = true;
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send){
			final String text = editText.getText().toString();
			if(!text.isEmpty()){
				Message message = new Message(Controller.getInstance().getMyself(), text, false);
				chatAdapter.addMessage(message);
				chatAdapter.notifyDataSetChanged();
				Server.sendPrivateMessage(remoteUser, message.getMessage());
				editText.setText("");
				scrollMyListViewToTheBottomNowWeHere();
			}
		} else if(v.getId() == R.id.back){
			onBackPressed();
		} else if(v.getId() == R.id.photos){
			onPhotoPressed();
		} else if(v.getId() == R.id.send_picture){
			takePhoto();
		}
	}

	private void onPhotoPressed() {
		if(slideshow.getVisibility() == View.GONE){
			Animation dropDown = new DropDownAnim(slideshow, Utils.dpInPixels(this, 200), true);
			dropDown.setDuration(200);
			dropDown.setAnimationListener(new AnimationListener(){

				@Override
				public void onAnimationStart(Animation animation) {
					slideshow.setVisibility(View.VISIBLE);
					slideshowSep.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationEnd(Animation animation) {}

				@Override
				public void onAnimationRepeat(Animation animation) {}});
			slideshow.startAnimation(dropDown);
		} else{
			Animation dropDown = new DropDownAnim(slideshow, Utils.dpInPixels(this, 200), false);
			dropDown.setDuration(200);
			dropDown.setAnimationListener(new AnimationListener(){

				@Override
				public void onAnimationStart(Animation animation) {}

				@Override
				public void onAnimationEnd(Animation animation) {
					slideshow.setVisibility(View.GONE);
					slideshowSep.setVisibility(View.GONE);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {}});
			slideshow.startAnimation(dropDown);
		}
	}

	private void scrollMyListViewToTheBottomNowWeHere() {
		listMessages.post(new Runnable() {
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				listMessages.setSelection(chatAdapter.getCount() - 1);
			}
		});
	}

	@Override
	public void onEventsReceived(List<Event> events) {
		for(Event e : events){
			android.util.Log.i("Blindr", "New event=" + e);
			if(e instanceof Message && e.getDestination() instanceof User){
				if(((User) e.getDestination()).getId().equals(Controller.getInstance().getMyId())){
					chatAdapter.addMessage((Message) e);
					chatAdapter.notifyDataSetChanged();
					scrollMyListViewToTheBottomNowWeHere();
				}
			}
		}

	}

	@Override
	public void onOldMatchesReceives(List<Match> matches) {
		Controller.getInstance().setMatches(matches);
	}

	@Override
	public void onUserHistoryReceived(List<Event> events) {
		chatAdapter.clear();
		Collections.sort(events, dateComparator);
		for(Event e : events){
			if(e instanceof Message && e.getDestination() instanceof User){
				if((e.getUser()).getId().equals(Controller.getInstance().getMyId())){
					((Message) e).setIsIncoming(false);
				}
				chatAdapter.addMessage((Message) e);
				chatAdapter.notifyDataSetChanged();
				scrollMyListViewToTheBottomNowWeHere();
			}
		}
	}

	@Override
	public void onUserLiked(User user, String userFakeName) {
		// TODO Auto-generated method stub
		
	}
	
	Comparator<Event> dateComparator = new Comparator<Event>()
			{
			    @Override
			    public int compare(Event lhs, Event rhs)
			    {
			        try
			        {
			            return lhs.getTimestamp().compareTo(rhs.getTimestamp());
			        }
			        catch (Exception e)
			        {
			            return 0;
			        }
			    }
			};

	@Override
	public void onNetworkAvailable() {
		tvConnectionProblem.setVisibility(View.GONE);
		sendBt.setEnabled(true);
	}

	@Override
	public void onNetworkUnavailable() {
		tvConnectionProblem.setVisibility(View.VISIBLE);
		sendBt.setEnabled(false);
	}
	
	@Override
	public void onProfilePicturesReceived(List<String> pictures) {
		for(String pic : pictures){
			new ImageDownloader().execute(pic);
		}
		Animation anim = AnimationUtils.loadAnimation(this, R.anim.blink);
		findViewById(R.id.photos).startAnimation(anim);
	}
	
	private class ImageDownloader extends AsyncTask<String,Void,Bitmap> {

		@Override
		protected Bitmap doInBackground(String... param) {
			// TODO Auto-generated method stub
			return downloadBitmap(param[0]);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			Log.i("Async-Example", "onPostExecute Called");
			slideshow.addPicture(result);
			findViewById(R.id.photos).setEnabled(true);
		}

		private Bitmap downloadBitmap(String url) {
			// initilize the default HTTP client object
			final DefaultHttpClient client = new DefaultHttpClient();

			//forming a HttoGet request 
			final HttpGet getRequest = new HttpGet(url);
			try {

				HttpResponse response = client.execute(getRequest);

				//check 200 OK for success
				final int statusCode = response.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					Log.w("ImageDownloader", "Error " + statusCode + 
							" while retrieving bitmap from " + url);
					return null;

				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						// getting contents from the stream 
						inputStream = entity.getContent();

						// decoding stream data back into image Bitmap that android understands
						final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
						final Bitmap bitmapScaled = Utils.scaleDown(bitmap, Utils.dpInPixels(PrivateChatActivity.this, 300), true);
						bitmap.recycle();
						return bitmapScaled;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				// You Could provide a more explicit error message for IOException
				getRequest.abort();
				Log.e("ImageDownloader", "Something went wrong while" +
						" retrieving bitmap from " + url + e.toString());
			} 

			return null;
		}
	}
}
