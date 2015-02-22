package com.mchacks.blindr.models;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

import com.mchacks.blindr.R;
import com.mchacks.blindr.controllers.Controller;
import com.mchacks.blindr.views.CustomYesNoDialog;

public class ClickListenerSwipe implements OnClickListener {
	private Message message;
	private Activity activity;
	
	public ClickListenerSwipe(Activity context, Message message){
		this.activity = context;
		this.message = message;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.delete_user){
			CustomYesNoDialog dialog = new CustomYesNoDialog(activity){

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
					Controller.getInstance().addBlockPerson(activity, message.getUser().getId());
				}

			};

			dialog.show();
			dialog.setDialogText(activity.getString(R.string.want_to_delete, message.getFakeName()));
			//Delete user from my database
		} else if(v.getId() == R.id.like_user){
			//Like user to database
			CustomYesNoDialog dialog = new CustomYesNoDialog(activity){

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
					Server.like(message.getUser(), message.getFakeName());
				}

			};

			dialog.show();
			dialog.setDialogText(activity.getString(R.string.want_to_like, message.getFakeName()));
		}
	}

}
