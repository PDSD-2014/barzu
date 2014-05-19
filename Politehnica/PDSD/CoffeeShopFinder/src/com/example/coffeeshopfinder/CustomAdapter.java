package com.example.coffeeshopfinder;


import java.util.ArrayList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter {

	private ArrayList<CoffeeShop> content;
	private Activity context;
	
	public CustomAdapter(Activity context, ArrayList<CoffeeShop> content) {
		
		this.content = content;
		this.context = context;
	}

	@Override
	public int getCount() {
		
		return content.size();
	}

	@Override
	public Object getItem(int position) {
		
		return content.get(position);
	}

	@Override
	public long getItemId(int position) {
		
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		LayoutInflater layoutInflater = context.getLayoutInflater();
		CoffeeShop cartoonContent = (CoffeeShop)getItem(position);
		
		if (position % 4 == 0)
			convertView = layoutInflater.inflate(R.layout.list_layout1, parent, false);
		else if(position % 4 == 1)
			convertView = layoutInflater.inflate(R.layout.list_layout2, parent, false);
		else if(position % 4 == 2)
			convertView = layoutInflater.inflate(R.layout.list_layout3, parent, false);
		else if(position % 4 == 3)
			convertView = layoutInflater.inflate(R.layout.list_layout4, parent, false);
		
		TextView name = (TextView)convertView.findViewById(R.id.name);
		name.setText(cartoonContent.GetName());
		TextView address = (TextView)convertView.findViewById(R.id.address);
		address.setText(cartoonContent.GetAddress());
		TextView distance = (TextView)convertView.findViewById(R.id.distance);
		distance.setText(cartoonContent.GetDistance());		
		
		return convertView;
	}

}
