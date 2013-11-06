package com.huddle.handle.adaptors;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Discussion;
import com.huddle.handle.client.resources.NumberDrawable;

public class DiscussionsListAdapter extends ArrayAdapter<Discussion> {

	int resource;
	Activity ctx;
	
	public DiscussionsListAdapter (Activity _context, int _resource, List<Discussion> _items) {
		super(_context, _resource, _items);
		ctx = _context;
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Discussion m = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			headerView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (LinearLayout)convertView;
		}
		
		TextView header = (TextView)headerView.findViewById(R.id.discussion_item);
		header.setText(m.getTitle());
		header.setCompoundDrawablesWithIntrinsicBounds(ctx.getResources().getDrawable(R.drawable.discussion), null, 
				NumberDrawable.createNumber(ctx, m.getPostCount()), null);
		
		return headerView;
	}
}
