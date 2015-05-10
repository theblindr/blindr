package com.lesgens.blindr;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class FirstTimeExperienceActivity extends FragmentActivity {
	private ArrayList<Fragment> fragments;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    public Typeface typeFace;
    private WelcomeToBlindrPageFragment welcomeFragment;
    private PickFacebookPhotosFragment pickPhotoFragment;
    private PickInterestedInPageFragment pickInterestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_time_pager);
        
        typeFace = Typeface.createFromAsset(getAssets(), "fonts/Raleway_Thin.otf");

        fragments = new ArrayList<Fragment>();
        
        welcomeFragment = new WelcomeToBlindrPageFragment();
        pickPhotoFragment = new PickFacebookPhotosFragment();
        pickInterestFragment = new PickInterestedInPageFragment();
        
        fragments.add(welcomeFragment);
        fragments.add(pickPhotoFragment);
        fragments.add(pickInterestFragment);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new FirstTimeExperiencePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }
    
    public void goBack(){
    	if(mPager.getCurrentItem() == 0){
    		return;
    	}
    	
    	mPager.setCurrentItem(mPager.getCurrentItem() - 1);
    }
    
    public void goNext(){
    	if(mPager.getCurrentItem() + 1 == fragments.size()){
    		return;
    	}
    	
    	mPager.setCurrentItem(mPager.getCurrentItem() + 1);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class FirstTimeExperiencePagerAdapter extends FragmentStatePagerAdapter {
        public FirstTimeExperiencePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

	public String getFacebookUrls() {
		return pickPhotoFragment.getFacebookUrls();
	}
	
	public void goToChooseRoom(){
		Intent i = new Intent(this, ChooseRoomActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(i);
		finish();
	}
}