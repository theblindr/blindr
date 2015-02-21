package com.mchacks.blindr.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mchacks.blindr.R;
import com.mchacks.blindr.controllers.Controller;

public class ChatAdapter extends ArrayAdapter<Message>{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;

	public ChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,R.layout.chat_even, chatValue);
		mContext = context;     
		messages = chatValue;     
	}

	private LayoutInflater getInflater(){
		if(mInflater == null)
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return mInflater;       
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;
		Message message = messages.get(position);

		if(convertView == null){ // Only inflating if necessary is great for performance
			if(message.getUser().getTokenId().equals(Controller.getInstance().getMyself().getTokenId())){
				rowView = getInflater().inflate(R.layout.chat_odd, parent, false);
			} else{
				rowView = getInflater().inflate(R.layout.chat_even, parent, false);

				TextView name = (TextView) rowView.findViewById(R.id.name);
				name.setText(message.getUser().getName());

				ImageView avatar = (ImageView) rowView.findViewById(R.id.avatar);
				avatar.setImageBitmap(message.getUser().getAvatar());
			}
		} else{
			rowView = convertView;
		}



		TextView mess = (TextView) rowView.findViewById(R.id.message);
		mess.setText(message.getMessage());

		return rowView;
	}

	public void addMessage(Message message){
		if(!messages.isEmpty()){
			final Message lastMessage = messages.get(messages.size()-1);
			if(lastMessage.getUser().getTokenId().equals(message.getUser().getTokenId())){
				lastMessage.addMessage(message.getMessage());
			} else{
				super.add(message);
			}
		} else{
			super.add(message);
		}

	}
}
