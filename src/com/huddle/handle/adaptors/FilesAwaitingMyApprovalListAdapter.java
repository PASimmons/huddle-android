package com.huddle.handle.adaptors;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.FileApproval;

public class FilesAwaitingMyApprovalListAdapter extends ArrayAdapter<FileApproval> {

	int resource;
	LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
	
	public FilesAwaitingMyApprovalListAdapter (Context _context, int _resource,
			List<FileApproval> _items) {
		super(_context, _resource, _items);
		resource = _resource;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		FileApproval wkspc = getItem(position);
		
		LinearLayout headerView;
		
		if (convertView == null) {
			headerView = new LinearLayout(getContext());
			
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (LinearLayout)convertView;
		}

		TextView entry = (TextView)headerView.findViewById(R.id.approval_item);
		if (entry != null) {
			
			entry.setText(wkspc.getFileName());
			
		}

		return headerView;
	}
	
}
