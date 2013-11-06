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
import com.huddle.handle.client.model.Task;

public class TaskListAdapter extends ArrayAdapter<Task> {

	int resource;
	public List<Task> items;
	
	public TaskListAdapter (Context _context, int _resource,
			List<Task> _items) {
		super(_context, _resource, _items);
		resource = _resource;
		items = _items;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Task tsk = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			
			headerView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
			
		} else {
			
			headerView = (LinearLayout)convertView;
		}
		
		TextView header = (TextView)headerView.findViewById(R.id.task_item);
		TextView date = (TextView)headerView.findViewById(R.id.task_date);
		header.setText(tsk.getTitle());
		date.setText(tsk.getDueDate().getShortTimeOrDate());
		
		return headerView;
	}
	
}
