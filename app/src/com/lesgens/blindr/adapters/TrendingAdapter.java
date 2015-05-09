package com.lesgens.blindr.adapters;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lesgens.blindr.R;
import com.lesgens.blindr.models.Trend;

public class TrendingAdapter extends BaseAdapter {
	private final ArrayList<Trend> mData;

	public TrendingAdapter(ArrayList<Trend> map) {
		mData = new ArrayList<Trend>();
		mData.addAll(map);
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Trend getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO implement you own logic with ID
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final View result;

		if (convertView == null) {
			result = LayoutInflater.from(parent.getContext()).inflate(R.layout.trending_listitem, parent, false);
		} else {
			result = convertView;
		}

		Trend item = getItem(position);

		// TODO replace findViewById by ViewHolder
		((TextView) result.findViewById(android.R.id.text1)).setText(item.getCity() + " (" + item.getNumberOfParticipants() + ")");

		return result;
	}
}