package com.lesgens.blindr;

import com.lesgens.blindr.controllers.Controller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WelcomeToBlindrPageFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup v = (ViewGroup) inflater.inflate(
				R.layout.welcome_to_blindr, container, false);

		((TextView) v.findViewById(R.id.splash_text)).setTypeface(((FirstTimeExperienceActivity) getActivity()).typeFace);
		
		((ImageView) v.findViewById(R.id.avatar)).setImageBitmap(Controller.getInstance().getMyself().getAvatar());


		v.findViewById(R.id.next).setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.next){
			((FirstTimeExperienceActivity) getActivity()).goNext();
		}
	}
}
