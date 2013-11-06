package com.huddle.handle.adaptors;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.huddle.handle.android.R;
import com.huddle.handle.android.WorkspacesActivity;
import com.huddle.handle.client.model.Member;
import com.huddle.handle.client.model.Members;
import com.huddle.handle.client.model.Post;

public class DiscussionListAdapter extends ArrayAdapter<Post> {

	int resource;
	Handler handler;
	Members mbs;
	
	public DiscussionListAdapter (Context _context, int _resource, List<Post> _items,
			Handler guiThread) {
		super(_context, _resource, _items);
		resource = _resource;
		handler = guiThread;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final Post p = getItem(position);
		final RelativeLayout headerView;
		
		if (convertView == null) {
			headerView = new RelativeLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
			vi.inflate(resource, headerView, true);
		} else {
			headerView = (RelativeLayout)convertView;
		}
		
		final TextView header = (TextView)headerView.findViewById(R.id.post_author);
		final TextView content = (TextView)headerView.findViewById(R.id.post_detail);
		final ImageView picture = (ImageView)headerView.findViewById(R.id.post_picture);
		header.setText(p.getAuthorName());
		content.setText(Html.fromHtml(p.getContent()));
		
		Thread pictureThread = new Thread() {
			public void run() {
				
				if (mbs == null) {
					getWorkspaceMembers();
				}
				if (mbs != null) {
					Member m = mbs.findMember(p.getAuthorId());
					if (m != null) {
						picture.setImageBitmap(m.getBitmap());
						picture.setMaxWidth(100);
						picture.setScaleType(ScaleType.CENTER_CROP);
						content.invalidate();
						headerView.requestLayout();
					}
				}
			}
		};
		handler.post(pictureThread);
		
		return headerView;
	}

	private synchronized void getWorkspaceMembers() {
		try {
			mbs = WorkspacesActivity.clickedParent.getMembers(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
