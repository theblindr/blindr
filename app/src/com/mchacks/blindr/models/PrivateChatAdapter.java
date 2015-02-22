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
import com.mchacks.blindr.models.Message.Gender;

public class PrivateChatAdapter extends ArrayAdapter<Message> {
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;

	public PrivateChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,-1, chatValue);
		mContext = context;     
		messages = chatValue;
	}

	static class ViewHolder {
		public TextView name;
		public ImageView avatar;
		public TextView message;
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
		if(!message.isIncoming()){
			rowView = getInflater().inflate(R.layout.chat_odd, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = null;
			viewHolder.avatar = null;
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			rowView.setTag(viewHolder);
		} else{
			rowView = getInflater().inflate(R.layout.chat_even_private, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);

			rowView.setTag(viewHolder);

		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.message.setText(message.getMessage());

		if(holder.name != null){
			holder.name.setText(message.getRealName());
			if(message.getGender() == Gender.Female){
				holder.name.setTextColor(mContext.getResources().getColor(R.color.pink));
			} else if(message.getGender() == Gender.Male){
				holder.name.setTextColor(mContext.getResources().getColor(R.color.main_color));
			} else{
				holder.name.setTextColor(mContext.getResources().getColor(R.color.grey));
			}
		}

		if(holder.avatar != null){
			holder.avatar.setImageBitmap(message.getUser().getAvatar());
		}

		return rowView;
	}

	public void addMessage(Message message){
		if(!messages.isEmpty()){
			if(!messages.contains(message)){
				final Message lastMessage = messages.get(messages.size()-1);
				if(lastMessage.getUser().getId().equals(message.getUser().getId())){
					lastMessage.addMessage(message.getMessage(), message.getId());
				} else{
					super.add(message);
				}
			}
		} else{
			super.add(message);
		}
	}


}
