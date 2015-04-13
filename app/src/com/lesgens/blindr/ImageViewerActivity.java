package com.lesgens.blindr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class ImageViewerActivity extends Activity implements OnClickListener{

	public static void show(Context context, Bitmap bitmap) {
		Intent i = new Intent(context, ImageViewerActivity.class);
		FileOutputStream out = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory(),  "Pic.jpg");
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			i.putExtra("photoUri", Uri.fromFile(file));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		context.startActivity(i);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.image_viewer);

		Uri photoUri = getIntent().getParcelableExtra("photoUri");


		if(photoUri == null){
			finish();
		}

		
		try{
			Bitmap bitmap = android.provider.MediaStore.Images.Media
					.getBitmap(getContentResolver(), photoUri);
			ImageView imageView = (ImageView) findViewById(R.id.image);
			imageView.setImageBitmap(bitmap);
		} catch(Exception e){
			e.printStackTrace();
			finish();
		}

		findViewById(R.id.container).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.container){
			finish();
		}
	}


}
