package com.mchacks.blindr.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mchacks.blindr.R;

public class PrivateChatAdapter extends ArrayAdapter<String>{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<String> privates;

	public PrivateChatAdapter(Context context, ArrayList<String> chatValue) {  
		super(context,R.layout.chat_even, chatValue);
		mContext = context;     
		privates = chatValue;     
	}

	private LayoutInflater getInflater(){
		if(mInflater == null)
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return mInflater;       
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		String message = privates.get(position);

		if(convertView == null){ // Only inflating if necessary is great for performance
			rowView = getInflater().inflate(R.layout.private_item, parent, false);
		} else{
			rowView = convertView;
		}



		TextView mess = (TextView) rowView.findViewById(R.id.name);
		mess.setText(message);

		return rowView;
	}
}
