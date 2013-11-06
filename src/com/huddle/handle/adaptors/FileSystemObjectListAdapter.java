package com.huddle.handle.adaptors;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.resources.IconStore;

public class FileSystemObjectListAdapter extends ArrayAdapter<FileSystemObject>{

	int resource;
	IconStore icons;
	Activity activity;
   private ExecutorService transThread;
	
	public FileSystemObjectListAdapter (Activity _context, int _resource, List<FileSystemObject> _items) {
		super(_context, _resource, _items);
		resource = _resource;
		activity = _context;
		icons = new IconStore(_context);
        transThread = Executors.newFixedThreadPool(3);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final FileSystemObject fso = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			headerView = (LinearLayout)vi.inflate(resource, parent, false);
			
		} else {
			
			headerView = (LinearLayout)convertView;
		}
		
		final TextView header = (TextView)headerView.findViewById(R.id.file_item);
		
		header.setText(fso.getTitle());
		if (fso.isFolder()) {
			Drawable img = getContext().getResources().getDrawable(R.drawable.folder);
			header.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
		} else {
			
			Thread detailsThread = new Thread() {
				public void run() {
					try {
						File file = fso.getFileDetails(true);
						updateGui(file, header);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			transThread.submit(detailsThread);
		}
		
		return headerView;
	}

	public void updateGui(final Object object, final View view) {
		activity.runOnUiThread(new Runnable() {

			public void run() {
				File file = (File)object;
				TextView header = (TextView)view;
				Drawable img = icons.getIconFromMimeType(file.getMimeType());
				header.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
				StringBuilder sb = new StringBuilder();
				sb.append(file.getFileName()).append("<br/>").append("<font color=\"#C0C0C0\"><small>");
				sb.append(file.getSizeInK()).append("</small></font>");
				header.setText(Html.fromHtml(sb.toString()));
				
			}
		});
	}

}
