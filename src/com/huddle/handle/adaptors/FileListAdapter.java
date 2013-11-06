package com.huddle.handle.adaptors;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.File;
import com.huddle.handle.client.resources.IconStore;

public class FileListAdapter extends ArrayAdapter<java.io.File>{

	int resource;
	IconStore icons;
	Activity activity;
	
	public FileListAdapter (Activity _context, int _resource, List<java.io.File> _items) {
		super(_context, _resource, _items);
		resource = _resource;
		activity = _context;
		icons = new IconStore(_context);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final java.io.File fso = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			headerView = (LinearLayout)vi.inflate(resource, parent, false);
			
		} else {
			
			headerView = (LinearLayout)convertView;
		}
		
		final TextView header = (TextView)headerView.findViewById(R.id.file_item);
		
		header.setText(fso.getName());
		if (fso.isDirectory()) {
			Drawable img = getContext().getResources().getDrawable(R.drawable.folder);
			header.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		} else {
			String mime = File.getUploadMimeType(fso.getAbsolutePath());
			Drawable img = icons.getIconFromMimeType(mime);
			header.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
			StringBuilder sb = new StringBuilder();
			sb.append(fso.getName()).append("<br/>").append("<font color=\"#C0C0C0\"><small>");
			sb.append((fso.length() / 1024) + "k").append("</small></font>");
			header.setText(Html.fromHtml(sb.toString()));
		}

		return headerView;
	}
}
