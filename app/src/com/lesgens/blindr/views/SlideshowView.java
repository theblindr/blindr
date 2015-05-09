package com.lesgens.blindr.views;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lesgens.blindr.ImageViewerActivity;
import com.lesgens.blindr.R;

public class SlideshowView extends RelativeLayout implements OnClickListener {
	private int mSelectedColor;
	private int mCurrentColor;
	private int mUnselectedColor;
	private int mPicturesWidth;
	private int mPicturesHeight;
	private int mSelectedItem;
	private int mDotsSize;
	private LinearLayout mPicturesContainer;
	private LinearLayout mDotsContainer;
	private ArrayList<Bitmap> mImages;

	public SlideshowView(Context context) {
		super(context);
		initViews();
	}

	public SlideshowView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(attrs);
		initViews();
	}

	public SlideshowView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(attrs);
		initViews();
	}

	public void addPicture(Bitmap bitmap){
		mPicturesContainer.addView(createImageViewFromBitmap(bitmap));
		mDotsContainer.addView(createDot());
		mImages.add(bitmap);
		invalidate();
		requestLayout();
	}
	
	public void recycleBitmaps(){
		for(Bitmap b : mImages){
			b.recycle();
		}
	}

	public void addPicture(int resourceId){
		Bitmap bm = BitmapFactory.decodeResource(getResources(), resourceId);
		addPicture(bm);
	}

	private ImageView createImageViewFromBitmap(Bitmap bitmap){
		ImageView iv = new ImageView(getContext());
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mPicturesWidth, mPicturesHeight);
		iv.setLayoutParams(params);
		iv.setScaleType(ImageView.ScaleType.CENTER);
		iv.setTag(mImages.size());
		iv.setImageBitmap(bitmap);
		iv.setOnClickListener(this);

		return iv;
	}

	private TextView createDot(){
		TextView tv = new TextView(getContext());
		tv.setText(".");
		tv.setTextSize(mDotsSize);
		tv.setTextColor(mUnselectedColor);

		return tv;
	}

	public int getSelectedItem(){
		return mSelectedItem;
	}

	public Bitmap getSelectedImage(){
		return mImages.get(mSelectedItem);
	}

	public void select(int item){
		if(item < mImages.size()){
			for(int i = 0 ; i < mPicturesContainer.getChildCount() ; i++){
				mPicturesContainer.getChildAt(i).setBackgroundColor(0);
			}
			for(int i = 0 ; i < mDotsContainer.getChildCount() ; i++){
				((TextView) mDotsContainer.getChildAt(i)).setTextColor(mUnselectedColor);
			}
			mPicturesContainer.getChildAt(item).setBackgroundColor(mSelectedColor);
			((TextView) mDotsContainer.getChildAt(item)).setTextColor(mSelectedColor);
			mSelectedItem = item;
		}
	}

	private void initViews() {
		View view = inflate(getContext(), R.layout.slideshow, this);

		mPicturesContainer = (LinearLayout) view.findViewById(R.id.pictures_container);
		mDotsContainer = (LinearLayout) view.findViewById(R.id.dots_container);
		mDotsContainer.setPadding(0, -mDotsSize*2, 0, 0);

		mImages = new ArrayList<Bitmap>();
		mSelectedItem = -1;
	}

	private void initAttrs(AttributeSet attrs){
		TypedArray a = getContext().getTheme().obtainStyledAttributes(
				attrs,
				R.styleable.SlideshowView,
				0, 0);

		try {
			mSelectedColor = a.getColor(R.styleable.SlideshowView_selectedColor, Color.BLACK);
			mUnselectedColor = a.getColor(R.styleable.SlideshowView_unselectedColor, Color.WHITE);
			mCurrentColor = a.getColor(R.styleable.SlideshowView_currentColor, Color.GRAY);
			mPicturesWidth = a.getDimensionPixelSize(R.styleable.SlideshowView_picturesWidth, LayoutParams.WRAP_CONTENT);
			mPicturesHeight= a.getDimensionPixelSize(R.styleable.SlideshowView_picturesHeight, LayoutParams.WRAP_CONTENT);
			mDotsSize = a.getDimensionPixelSize(R.styleable.SlideshowView_dotsSize, 72);
		} finally {
			a.recycle();
		}
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ImageView){
			select((Integer) v.getTag());
			ImageViewerActivity.show(getContext(), getSelectedImage());
		}
	}

}
