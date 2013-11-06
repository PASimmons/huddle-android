package com.huddle.handle.adaptors;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Workspace;

public class WorkspaceListAdapter extends ArrayAdapter<Workspace> {

	int resource;
	LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	
	public WorkspaceListAdapter (Context _context, int _resource,
			List<Workspace> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Workspace wkspc = getItem(position);
		
		LinearLayout headerView;
		
		if (convertView == null) {
			headerView = new LinearLayout(getContext());
			
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (LinearLayout)convertView;
		}

		TextView entry = (TextView)headerView.findViewById(R.id.workspace_title);
		if (entry != null) {
			
			if (wkspc.isAllHeader() || wkspc.isRecentlyModifiedHeader()) {
				entry.setText(wkspc.isAllHeader() ? "All" : "Recently Modified");
				entry.setPadding(5, 0, 5, 0);
				entry.setTextSize(10);
				entry.setTextColor(Color.BLACK);
				entry.setBackgroundColor(Color.LTGRAY);
				entry.setClickable(false);
				entry.setFocusable(false);
				entry.setCompoundDrawables(null, null, null, null);
			} else {
				entry.setText(wkspc.getTitle());
				entry.setPadding(5, 5, 5, 5);
				entry.setTextSize(16);
				entry.setTextColor(Color.BLACK);
				entry.setBackgroundColor(Color.TRANSPARENT);
				entry.setCompoundDrawablesWithIntrinsicBounds(getContext().getResources().getDrawable(R.drawable.workspace), null, null, null);
			}
		}

		return headerView;
	}
	
}
