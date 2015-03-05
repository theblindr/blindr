package com.lesgens.blindr.models;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lesgens.blindr.R;

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
	    public ImageView avatar;
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
			rowView = getInflater().inflate(R.layout.match_item, parent, false);
			
			ViewHolder holder = new ViewHolder();
			holder.name = (TextView) rowView.findViewById(R.id.name);
			holder.mutual = (TextView) rowView.findViewById(R.id.mutual);
			holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
			rowView.setTag(holder);
		} else{
			rowView = convertView;
		}
		
		ViewHolder holder = (ViewHolder) rowView.getTag();
		final Match match = matches.get(position);
		
		if(match.isMutual()){
			holder.name.setText(match.getRealName());
		} else{
			holder.name.setText(match.getFakeName());
		}
		holder.avatar.setImageBitmap(match.getMatchedUser().getAvatar());
		
		if(match.isMutual()){
			holder.mutual.setText("Click here to chat");
		} else{
			holder.mutual.setText("Pending request");
		}
		

		return rowView;
	}
}
