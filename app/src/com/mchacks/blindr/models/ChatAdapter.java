package com.mchacks.blindr.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.SwipeLayout.Status;
import com.mchacks.blindr.R;
import com.mchacks.blindr.controllers.Controller;

public class ChatAdapter extends ArraySwipeAdapter<Message> implements OnClickListener, SwipeLayout.SwipeListener{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;
	private ArrayList<SwipeLayout> swipeLayouts;

	public ChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,R.layout.chat_even, chatValue);
		mContext = context;     
		messages = chatValue;     
		swipeLayouts = new ArrayList<SwipeLayout>();
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
				
				rowView.findViewById(R.id.delete_user).setOnClickListener(this);
				rowView.findViewById(R.id.like_user).setOnClickListener(this);

				SwipeLayout swipeLayout =  (SwipeLayout) rowView.findViewById(R.id.swipe_layout);
				swipeLayouts.add(swipeLayout);
				//set show mode.
				swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

				//set drag edge.
				swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

				swipeLayout.addSwipeListener(this);

			}
		} else{
			rowView = convertView;
		}



		TextView mess = (TextView) rowView.findViewById(R.id.message);
		mess.setText(message.getMessage());

		return rowView;
	}

	public void closeAllSwipeLayout(){
		for(SwipeLayout swipeLayout : swipeLayouts){
			swipeLayout.close(true);
		}
	}

	public void closeAllSwipeLayoutExcept(SwipeLayout layout){
		for(SwipeLayout swipeLayout : swipeLayouts){
			if(!swipeLayout.equals(layout)){
				swipeLayout.close(true);
			}
		}
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

	public ArrayList<SwipeLayout> getSwipeLayoutsOpened(){
		return swipeLayouts;
	}

	public boolean isOneOpened(){
		for(SwipeLayout layout : swipeLayouts){
			if(layout.getOpenStatus() == Status.Open){
				return true;
			}
		}
		return false;
	}

	@Override
	public int getSwipeLayoutResourceId(int position) {
		return R.id.swipe_layout;
	}

	@Override
	public void onClose(SwipeLayout arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHandRelease(SwipeLayout arg0, float arg1, float arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onOpen(SwipeLayout layout) {
		android.util.Log.i("Blindr", "Swipe layout opened");
		closeAllSwipeLayoutExcept(layout);
	}

	@Override
	public void onStartClose(SwipeLayout arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartOpen(SwipeLayout arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpdate(SwipeLayout arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.delete_user){
			//Delete user from my database
		} else if(v.getId() == R.id.like_user){
			//Like user to database
		}
	}
}
