package com.lesgens.blindr.adapters;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lesgens.blindr.R;
import com.lesgens.blindr.models.Message;

public class PrivateChatAdapter extends ArrayAdapter<Message> implements StickyListHeadersAdapter {
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Message> messages;
	private SimpleDateFormat sdfMessage = new SimpleDateFormat("HH:mm");
	private SimpleDateFormat sdfDaySameWeek = new SimpleDateFormat("EEEE");
	private SimpleDateFormat sdfDaySameYear = new SimpleDateFormat("dd MMM");
	private SimpleDateFormat sdfDayAnotherYear = new SimpleDateFormat("dd MMM yyyy");
	private static SimpleDateFormat sdfDateForDays = new SimpleDateFormat("dd.MM.yyyy");
	private Date sameWeek;
	private Calendar sameYear;
	private Typeface tf;


	public PrivateChatAdapter(Context context, ArrayList<Message> chatValue) {  
		super(context,-1, chatValue);
		mContext = context;
		messages = chatValue;
		sameYear = Calendar.getInstance();
		sameYear.add(Calendar.DAY_OF_MONTH, -7);
		sameWeek = sameYear.getTime();
		sameYear.add(Calendar.DAY_OF_MONTH, +7);
		sameYear.set(Calendar.DAY_OF_YEAR, 0);
		tf = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway_Thin.otf");
	}

	static class ViewHolder {
		public TextView message;
		public TextView time;
	}

	static class HeaderViewHolder {
		public TextView day;
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
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);
			rowView.setTag(viewHolder);
		} else{
			rowView = getInflater().inflate(R.layout.chat_even_private, parent, false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.message = (TextView) rowView.findViewById(R.id.message);
			viewHolder.time = (TextView) rowView.findViewById(R.id.time);

			rowView.setTag(viewHolder);

		}

		// fill data
		ViewHolder holder = (ViewHolder) rowView.getTag();

		holder.message.setText(message.getMessage());
		holder.time.setText(sdfMessage.format(message.getTimestamp()));

		return rowView;
	}

	public void addMessage(Message message){
		if(!messages.isEmpty()){
			if(!messages.contains(message)){
				/*final Message lastMessage = messages.get(messages.size()-1);
				if(lastMessage.getUser().getId().equals(message.getUser().getId())){
					lastMessage.addMessage(message.getMessage(), message.getId());
				} else{*/
				super.add(message);
				//}
			}
		} else{
			super.add(message);
		}
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


}
