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
import com.lesgens.blindr.network.Server;

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
			Server.setFacebookUrls(((FirstTimeExperienceActivity) getActivity()).getFacebookUrls());
			Server.setInterestedIn(getInterests());
			((FirstTimeExperienceActivity) getActivity()).goToChooseRoom();
		} else if(v.getId() == R.id.back){
			((FirstTimeExperienceActivity) getActivity()).goBack();
		}
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