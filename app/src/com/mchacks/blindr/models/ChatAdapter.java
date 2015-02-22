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
import com.mchacks.blindr.views.CustomYesNoDialog;

public class ChatAdapter extends ArraySwipeAdapter<Message> implements OnClickListener, SwipeLayout.SwipeListener{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;
	private ArrayList<SwipeLayout> swipeLayouts;

	public ChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,-1, chatValue);
		mContext = context;     
		messages = chatValue;     
		swipeLayouts = new ArrayList<SwipeLayout>();
	}

	static class ViewHolder {
		public TextView name;
		public ImageView avatar;
		public TextView message;
		public SwipeLayout swipeLayout;
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

		android.util.Log.i("Blindr", "isIncoming="+message.isIncoming() + " message=" + message.getMessage());
		if(!message.isIncoming()){
			rowView = getInflater().inflate(R.layout.chat_odd, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = null;
			viewHolder.avatar = null;
			viewHolder.swipeLayout = null;
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			rowView.setTag(viewHolder);
		} else{
			rowView = getInflater().inflate(R.layout.chat_even, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			viewHolder.swipeLayout =  (SwipeLayout) rowView.findViewById(R.id.swipe_layout);

			rowView.setTag(viewHolder);

		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.message.setText(message.getMessage());

		if(holder.name != null){
			holder.name.setText(message.getUser().getName());
		}

		if(holder.avatar != null){
			holder.avatar.setImageBitmap(message.getUser().getAvatar());
		}

		if(holder.swipeLayout != null){
			swipeLayouts.add(holder.swipeLayout);
			//set show mode.
			holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

			//set drag edge.
			holder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

			holder.swipeLayout.addSwipeListener(this);

			rowView.findViewById(R.id.delete_user).setOnClickListener(this);
			rowView.findViewById(R.id.like_user).setOnClickListener(this);
		}

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
			CustomYesNoDialog dialog = new CustomYesNoDialog(getContext()){

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
				}

			};

			dialog.show();
			//Delete user from my database
		} else if(v.getId() == R.id.like_user){
			//Like user to database
			CustomYesNoDialog dialog = new CustomYesNoDialog(getContext()){

				@Override
				public void onPositiveClick() {
					super.onPositiveClick();
				}

			};

			dialog.show();
			dialog.setDialogText(R.string.want_to_like);
		}
	}
}
