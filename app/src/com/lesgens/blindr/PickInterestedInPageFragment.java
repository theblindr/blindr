package com.lesgens.blindr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.controllers.PreferencesController;
import com.lesgens.blindr.network.Server;
import com.lesgens.blindr.views.CustomYesNoDialog;

public class PickInterestedInPageFragment extends Fragment implements OnClickListener{
	private CheckBox checkMen;
	private CheckBox checkWomen;
	private CheckBox checkOther;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(
				R.layout.pick_interested_in, container, false);

		((TextView) v.findViewById(R.id.splash_text)).setTypeface(((FirstTimeExperienceActivity) getActivity()).typeFace);

		((ImageView) v.findViewById(R.id.avatar)).setImageBitmap(Controller.getInstance().getMyself().getAvatar());

		checkMen = (CheckBox) v.findViewById(R.id.checkBox_men);
		checkWomen = (CheckBox) v.findViewById(R.id.checkBox_women);
		checkOther = (CheckBox) v.findViewById(R.id.checkBox_other);

		v.findViewById(R.id.back).setOnClickListener(this);
		v.findViewById(R.id.next).setOnClickListener(this);

		return v;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.next){
			String facebookUrls = ((FirstTimeExperienceActivity) getActivity()).getFacebookUrls();
			if(facebookUrls.split(",").length >= 2){
				Server.setFacebookUrls(facebookUrls);
				Server.setInterestedIn(getInterests());
				PreferencesController.setPreference(getActivity(), PreferencesController.FIRST_TIME_USE, false);
				((FirstTimeExperienceActivity) getActivity()).goToChooseRoom();
			} else{
				showNotEnoughPictures();
			}
		} else if(v.getId() == R.id.back){
			((FirstTimeExperienceActivity) getActivity()).goBack();
		}
	}
	
	private void showNotEnoughPictures(){
		CustomYesNoDialog dialog = new CustomYesNoDialog(getActivity()){

			@Override
			public void onPositiveClick() {
				super.onPositiveClick();
				dismiss();
			}

		};

		dialog.show();
		dialog.transformAsOkDialog();
		dialog.setDialogText(R.string.not_enough_pictures);
	}

	private String getInterests() {
		String interests = "";

		if(checkMen.isChecked()){
			interests += "m";
		}

		if(checkWomen.isChecked()){
			if(!interests.isEmpty()){
				interests += ",";
			}
			interests += "f";
		}

		if(checkOther.isChecked()){
			if(!interests.isEmpty()){
				interests += ",";
			}
			interests += "o";
		}

		return interests;
	}
}