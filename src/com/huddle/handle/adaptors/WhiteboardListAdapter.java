package com.huddle.handle.adaptors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Whiteboard;



public class WhiteboardListAdapter extends ArrayAdapter<Whiteboard> {

	int resource;
	
	public WhiteboardListAdapter (Context _context, int _resource,
			List<Whiteboard> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Whiteboard wkspc = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			
			headerView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
			
		} else {

			headerView = (LinearLayout)convertView;
		}
		
		TextView header = (TextView)headerView.findViewById(R.id.whiteboard_item);
		
		if (header != null) {
			header.setText(wkspc.getTitle());
		}
		
		return headerView;
	}
}
