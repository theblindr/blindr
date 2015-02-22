package com.mchacks.blindr.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mchacks.blindr.R;

public class MatchAdapter extends ArrayAdapter<Match>{
	private Context mContext;
	private LayoutInflater mInflater = null;

	private ArrayList<Match> matches;

	public MatchAdapter(Context context, ArrayList<Match> chatValue) {  
		super(context,R.layout.chat_even, chatValue);
		mContext = context;     
		matches = chatValue;     
	}
	
	static class ViewHolder {
	    public TextView name;
	    public TextView mutual;
	  }

	private LayoutInflater getInflater(){
		if(mInflater == null)
			mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		return mInflater;       
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView;

		if(convertView == null){ // Only inflating if necessary is great for performance
			rowView = getInflater().inflate(R.layout.private_item, parent, false);
			
			ViewHolder holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.name);
			holder.mutual = (TextView) rowView.findViewById(R.id.mutual);
			rowView.setTag(holder);
		} else{
			rowView = convertView;
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		final Match match = matches.get(position);
		
		String name = match.getMatchedUser().getName();
		holder.name.setText(name);
		
		if(match.isMutual()){
			holder.mutual.setText("Click here to chat");
		} else{
			holder.mutual.setText("Pending request");
		}
		

		return rowView;
	}
}
