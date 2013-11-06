package com.huddle.handle.adaptors;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.FileApproval;

public class FileApprovalListAdapter extends ArrayAdapter<FileApproval> {

	int resource;
	
	public FileApprovalListAdapter (Context _context, int _resource,
			List<FileApproval> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final FileApproval fso = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			
			headerView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
			
		} else {
			
			headerView = (LinearLayout)convertView;
		}
		
		TextView header = (TextView)headerView.findViewById(R.id.file_item);
		header.setText(fso.getTitle());
		Drawable img = getContext().getResources().getDrawable(R.drawable.approval);
		header.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		
		return headerView;
	}
	
}
