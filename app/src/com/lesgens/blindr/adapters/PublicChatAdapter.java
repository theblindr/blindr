package com.lesgens.blindr.adapters;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.SwipeLayout.Status;
import com.lesgens.blindr.ImageViewerActivity;
import com.lesgens.blindr.R;
import com.lesgens.blindr.adapters.PrivateChatAdapter.HeaderViewHolder;
import com.lesgens.blindr.controllers.Controller;
import com.lesgens.blindr.listeners.ClickSwipeListener;
import com.lesgens.blindr.models.Message;
import com.lesgens.blindr.models.Message.Gender;
import com.lesgens.blindr.utils.Utils;

public class PublicChatAdapter extends ArraySwipeAdapter<Message> implements SwipeLayout.SwipeListener, StickyListHeadersAdapter, OnClickListener{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;
	private ArrayList<SwipeLayout> swipeLayouts;
	private SimpleDateFormat sdfMessage = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdfDaySameWeek = new SimpleDateFormat("EEEE");
	private SimpleDateFormat sdfDaySameYear = new SimpleDateFormat("dd MMM");
	private SimpleDateFormat sdfDayAnotherYear = new SimpleDateFormat("dd MMM yyyy");
	private static SimpleDateFormat sdfDateForDays = new SimpleDateFormat("dd.MM.yyyy");
	private Date sameWeek;
	private Calendar sameYear;
	private Typeface tf;

	public PublicChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,-1, chatValue);
		mContext = context;     
		messages = chatValue;     
		swipeLayouts = new ArrayList<SwipeLayout>();
		sameYear = Calendar.getInstance();
		sameYear.add(Calendar.DAY_OF_MONTH, -7);
		sameWeek = sameYear.getTime();
		sameYear.add(Calendar.DAY_OF_MONTH, +7);
		sameYear.set(Calendar.DAY_OF_YEAR, 0);
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Thin.otf");
	}

	static class ViewHolder {
		public TextView name;
		public ImageView avatar;
		public TextView message;
		public TextView time;
		public TextView timePicture;
		public SwipeLayout swipeLayout;
		public ImageView picture;
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
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);
			viewHolder.timePicture = (TextView) rowView.findViewById(R.id.time_picture);
			viewHolder.picture = (ImageView) rowView.findViewById(R.id.picture);
			rowView.setTag(viewHolder);
		} else{
			rowView = getInflater().inflate(R.layout.chat_even, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) rowView.findViewById(R.id.name);
			viewHolder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);
			viewHolder.timePicture = (TextView) rowView.findViewById(R.id.time_picture);
			viewHolder.swipeLayout =  (SwipeLayout) rowView.findViewById(R.id.swipe_layout);
			viewHolder.picture = (ImageView) rowView.findViewById(R.id.picture);

			rowView.setTag(viewHolder);

		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		if(message.getMessage().startsWith(Utils.BLINDR_IMAGE_BASE)){
			holder.message.setVisibility(View.GONE);
			holder.time.setVisibility(View.GONE);
			holder.timePicture.setVisibility(View.VISIBLE);
			holder.timePicture.setText(sdfMessage.format(message.getTimestamp()));
			holder.picture.setVisibility(View.VISIBLE);
			String encoded = message.getMessage().substring(Utils.BLINDR_IMAGE_BASE.length());
			byte[] bytes = Base64.decode(encoded, Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
			holder.picture.setImageBitmap(bitmap);
			holder.picture.setOnClickListener(this);
		} else{
			holder.message.setVisibility(View.VISIBLE);
			holder.time.setVisibility(View.VISIBLE);
			holder.timePicture.setVisibility(View.GONE);
			holder.message.setText(message.getMessage());
			holder.time.setText(sdfMessage.format(message.getTimestamp()));
		}

		if(holder.name != null){
			if(Controller.getInstance().checkIfMutualWith(message.getFakeName())){
				holder.name.setText(message.getRealName());	
			} else{
				holder.name.setText(message.getFakeName());
			}
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

		if(holder.swipeLayout != null){
			swipeLayouts.add(holder.swipeLayout);
			//set show mode.
			holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

			//set drag edge.
			holder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);

			holder.swipeLayout.addSwipeListener(this);

			rowView.findViewById(R.id.delete_user).setOnClickListener(new ClickSwipeListener((Activity) getContext(), message));
			rowView.findViewById(R.id.like_user).setOnClickListener(new ClickSwipeListener((Activity) mContext, message));
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
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		HeaderViewHolder holder;
		if (convertView == null) {
			holder = new HeaderViewHolder();
			convertView = getInflater().inflate(R.layout.header, parent, false);
			holder.day = (TextView) convertView.findViewById(R.id.day);
			convertView.setTag(holder);
		} else {
			holder = (HeaderViewHolder) convertView.getTag();
		}
		//set header text as first char in name
		Timestamp time = messages.get(position).getTimestamp();
		String headerText = getHeaderText(time);

		holder.day.setTypeface(tf);
		holder.day.setText(headerText);

		return convertView;
	}

	public String getHeaderText(Timestamp time){
		if(sameWeek.before(time)){
			return sdfDaySameWeek.format(time);
		} else if(sameYear.before(time)){
			return sdfDaySameYear.format(time);
		} else{
			return sdfDayAnotherYear.format(time);
		}
	}

	@Override
	public long getHeaderId(int position) {
		return getDayCount(sdfDateForDays.format(messages.get(position).getTimestamp().getTime()), sdfDateForDays.format(sameYear.getTime()));
	}

	private long getDayCount(String start, String end) {
		long diff = -1;
		try {
			Date dateStart = sdfDateForDays.parse(start);
			Date dateEnd = sdfDateForDays.parse(end);

			//time is always 00:00:00 so rounding should help to ignore the missing hour when going from winter to summer time as well as the extra hour in the other direction
			diff = Math.round((dateEnd.getTime() - dateStart.getTime()) / (double) 86400000);
		} catch (Exception e) {
			//handle the exception according to your own situation
		}
		return diff;
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ImageView){
			ImageView image = (ImageView) v;
			Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
			ImageViewerActivity.show(getContext(), bitmap);
		}
	}
}
