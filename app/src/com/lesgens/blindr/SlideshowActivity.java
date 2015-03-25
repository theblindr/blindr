package com.lesgens.blindr;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.listeners.FacebookProfileListener;
import com.lesgens.blindr.models.User;
import com.lesgens.blindr.network.Server;
import com.todddavies.components.progressbar.ProgressWheel;

public class SlideshowActivity extends Activity implements FacebookProfileListener{

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private ViewFlipper mViewFlipper;	
	private Context mContext;
	private User remoteUser;
	private ArrayList<Bitmap> bitmaps;
	private List<String> pictures;

	private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

	public static void show(Context context, String tokenId){
		Intent i = new Intent(context, SlideshowActivity.class);
		i.putExtra("tokenId", tokenId);
		context.startActivity(i);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.slideshow);
		mContext = this;
		remoteUser = Controller.getInstance().getUser(getIntent().getStringExtra("tokenId"));
		mViewFlipper = (ViewFlipper) this.findViewById(R.id.view_flipper);
		mViewFlipper.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(final View view, final MotionEvent event) {
				detector.onTouchEvent(event);
				return true;
			}
		});
		
		((ProgressWheel) findViewById(R.id.progressBar)).spin();

		bitmaps = new ArrayList<Bitmap>();
		Server.addProfileListener(this);
		Server.getUserFacebookInfos(remoteUser);
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		finish();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		for(Bitmap iv : bitmaps){
			Log.i("SlideshowActivity", "Recycling bitmap");
			iv.recycle();
		}
	}

	class SwipeGestureDetector extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				// right to left swipe
				if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));					
					mViewFlipper.showNext();
					return true;
				} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
					mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_out));
					mViewFlipper.showPrevious();
					return true;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return false;
		}
	}

	@Override
	public void onProfilePicturesReceived(List<String> pictures) {
		this.pictures = pictures;
		for(String pic : pictures){
			new ImageDownloader().execute(pic);
		}
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
			bitmaps.add(result);
			ImageView imageView = new ImageView(SlideshowActivity.this);
			imageView.setImageBitmap(result);
			mViewFlipper.addView(imageView);
			if(bitmaps.size() == 2){
				findViewById(R.id.swipe_left).setVisibility(View.VISIBLE);
				findViewById(R.id.swipe_right).setVisibility(View.VISIBLE);
			} else if(bitmaps.size() == pictures.size()){
				findViewById(R.id.progressBar).setVisibility(View.GONE);
			}
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

						return bitmap;
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
