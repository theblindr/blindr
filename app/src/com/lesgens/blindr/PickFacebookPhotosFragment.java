package com.lesgens.blindr;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grio.fbphotopicker.FBPhotoPickerActivity;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.utils.Utils;
import com.lesgens.blindr.views.SlideshowView;

public class PickFacebookPhotosFragment extends Fragment implements OnClickListener {
	private final static int PICKED_PHOTO = 9000;
	private SlideshowView slideshow;
	private ArrayList<String> urls;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(
				R.layout.pick_your_photos, container, false);

		slideshow = (SlideshowView) v.findViewById(R.id.slideshow);

		urls = new ArrayList<String>();

		((TextView) v.findViewById(R.id.splash_text)).setTypeface(((FirstTimeExperienceActivity) getActivity()).typeFace);

		((ImageView) v.findViewById(R.id.avatar)).setImageBitmap(Controller.getInstance().getMyself().getAvatar());

		v.findViewById(R.id.btn_add).setOnClickListener(this);

		v.findViewById(R.id.back).setOnClickListener(this);
		v.findViewById(R.id.next).setOnClickListener(this);

		return v;
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == PICKED_PHOTO){
			if(resultCode == Activity.RESULT_OK){
				String photoUrl = data.getStringExtra(FBPhotoPickerActivity.PHOTO_URL);
				new ImageDownloader().execute(photoUrl);
				urls.add(photoUrl);
				((TextView) getView().findViewById(R.id.empty_text)).setText("");
			}
		}
	}

	@Override
	public void onDestroy(){
		slideshow.recycleBitmaps();
		super.onDestroy();
	}

	public void showErrorTooMuchImages(){

	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_add){
			if(urls.size() < 5){
				Intent i = new Intent(getActivity(), FBPhotoPickerActivity.class);
				startActivityForResult(i, PICKED_PHOTO);
			} else{
				showErrorTooMuchImages();
			}
		} else if(v.getId() == R.id.next){
			((FirstTimeExperienceActivity) getActivity()).goNext();
		} else if(v.getId() == R.id.back){
			((FirstTimeExperienceActivity) getActivity()).goBack();
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
			slideshow.addPicture(result);
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
						final Bitmap bitmapScaled = Utils.scaleDown(bitmap, Utils.dpInPixels(getActivity(), 300), true);
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

	public String getFacebookUrls() {
		String facebookUrls = "";
		for(int i = 0; i < urls.size(); i++){
			if(i > 0){
				facebookUrls += ",";
			}
			
			facebookUrls += urls.get(i);
		}
		
		return facebookUrls;
	}
}
