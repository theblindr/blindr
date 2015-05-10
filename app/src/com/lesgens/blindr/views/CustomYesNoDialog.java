package com.lesgens.blindr.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lesgens.blindr.R;

public class CustomYesNoDialog extends Dialog implements
android.view.View.OnClickListener {

	public Button yes, no;

	public CustomYesNoDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.dialog_yes_no);
		
		setCancelable(false);
		setCanceledOnTouchOutside(false);
		
		yes = (Button) findViewById(R.id.btn_yes);
		no = (Button) findViewById(R.id.btn_no);
		
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
	}

	public void setDialogText(String text){
		if(findViewById(R.id.txt_dia) != null){
			((TextView) findViewById(R.id.txt_dia)).setText(text);
		}
	}

	public void setDialogText(int resourceId){
		if(findViewById(R.id.txt_dia) != null){
			((TextView) findViewById(R.id.txt_dia)).setText(resourceId);
		}
	}
	
	public void setYesText(int resourceId){
		if(findViewById(R.id.btn_yes) != null){
			((TextView) findViewById(R.id.btn_yes)).setText(resourceId);
		}
	}
	
	public void setYesText(String text){
		if(findViewById(R.id.btn_yes) != null){
			((TextView) findViewById(R.id.btn_yes)).setText(text);
		}
	}
	
	public void setNoText(int resourceId){
		if(findViewById(R.id.btn_no) != null){
			((TextView) findViewById(R.id.btn_no)).setText(resourceId);
		}
	}
	
	public void setNoText(String text){
		if(findViewById(R.id.btn_no) != null){
			((TextView) findViewById(R.id.btn_no)).setText(text);
		}
	}
	
	public void transformAsOkDialog(){
		((TextView) findViewById(R.id.btn_yes)).setText(getContext().getString(R.string.ok));
		findViewById(R.id.btn_no).setVisibility(View.GONE);
	}

	public void onPositiveClick(){}
	public void onNegativeClick(){}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_yes:
			onPositiveClick();
			dismiss();
			break;
		case R.id.btn_no:
			onNegativeClick();
			dismiss();
			break;
		default:
			break;
		}
		dismiss();
	}
}