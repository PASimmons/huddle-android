package com.huddle.handle.adaptors;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView.ScaleType;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Member;
import com.huddle.handle.client.resources.PictureView;

public class PeopleListAdapter extends ArrayAdapter<Member> implements OnScrollListener {

	int resource;
	Activity ctx;
	private ExecutorService downloadThread;
	private LinkedList<PictureView> queue;
	private Integer mScrollState = SCROLL_STATE_IDLE;

	public PeopleListAdapter (Activity _context, int _resource, List<Member> _items) {
		super(_context, _resource, _items);
		ctx = _context;
		resource = _resource;
		downloadThread = Executors.newFixedThreadPool(3);
		queue = new LinkedList<PictureView>();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Member m = getItem(position);
		LinearLayout headerView;
		
		if (convertView == null) {
			headerView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (LinearLayout)convertView;
		}
		
		final TextView header = (TextView)headerView.findViewById(R.id.user);
		final ImageView picture = (ImageView)headerView.findViewById(R.id.people_list_picture);
	
		if (header != null) {
			
			header.setText(m.getDisplayName());
			picture.setImageBitmap(null);
			picture.setMaxWidth(100);
			picture.setScaleType(ScaleType.CENTER_CROP);	
			
			// Load the picture asynchronously
			Runnable pictureThread = new Runnable() {
				public void run() {
					if (m.hasImage()) {
						updateGui(m.getBitmap(), picture);
						return;
					}
					
					// load the image
					Bitmap bm = m.getBitmap();
					
					synchronized(mScrollState) {
						if (mScrollState ==SCROLL_STATE_IDLE) {
							updateGui(bm, picture);
							return;
						}
					}

					synchronized(queue) {
						PictureView pv = new PictureView();
						pv.setImage(bm);
						pv.setPicture(picture);
						queue.addLast(pv);
					}
				}
			};
			downloadThread.submit(pictureThread);	
		}
		
		return headerView;
	}
	
	public void updateGui(final Object object, final View view) {
		ctx.runOnUiThread(new Runnable() {
			public void run() {
				Bitmap b = (Bitmap)object;
				ImageView picture = (ImageView)view;
				picture.setImageBitmap(b);
			}
		});
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == SCROLL_STATE_IDLE) {
			synchronized (queue) {
				while(!queue.isEmpty()) {
					PictureView pv = queue.getFirst();
					queue.removeFirst();
					updateGui(pv.getImage(), pv.getPicture());
				}
			}
		}
		
		synchronized(mScrollState) {
			mScrollState = scrollState;
		}
	}
	
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
}
