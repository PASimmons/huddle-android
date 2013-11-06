package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.DiscussionsListAdapter;
import com.huddle.handle.client.model.Discussion;
import com.huddle.handle.client.model.Discussions;

public class DiscussionsActivity extends AbstractHuddleActivity implements OnItemClickListener {
	
	static Discussions discussions;
	private DiscussionsListAdapter adaptor;
	private List<Discussion> discussionList;
	public static Discussion clickedParent;
	private ImageButton newDiscussion;
	public static Thread updateThread;
	public static boolean posted;
	Context ctx;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		title = getAndSetViewAndHeader(this, R.layout.discussions, getString(R.string.discussions));
		
		ListView workspaceList = (ListView) findViewById(R.id.discussion_list);
		TextView header = (TextView) findViewById(R.id.discussions_header);
		StringBuffer sb = new StringBuffer();
		sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		sb.append("Discussions");
		header.setText(Html.fromHtml(sb.toString()));
		
		workspaceList.setOnItemClickListener(this);
		
		discussionList = new ArrayList<Discussion>();
		adaptor = new DiscussionsListAdapter(this, R.layout.discussions_list_item, discussionList);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		workspaceList.setAdapter(adaptor);
		
		newDiscussion = (ImageButton)findViewById(R.id.new_discussion);
		newDiscussion.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
 				posted = false;
				Intent intent = new Intent(ctx, NewDiscussionActivity.class);
				intent.putExtra(HEADER, title);
				startActivityForResult(intent, 1000);
			}
			
		});


    }

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(this, DiscussionActivity.class);
		intent.putExtra(HEADER, title);
		clickedParent = adaptor.getItem(position);
		startActivity(intent);
	}

	public void run() {

		try {
			discussions = new Discussions(WorkspacesActivity.clickedParent, true);
			postToGui();
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting discussions " + e.getMessage());

			networkError(e);
		}
		
	}

	@Override
	void updateGui() {
		if (discussions != null) {
			Vector<Discussion> spcs =  discussions.getDiscussions();
			for (Discussion spc:spcs) {
				adaptor.add(spc);
				adaptor.notifyDataSetChanged();
			}
		}
	}

	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		new Thread() {

			@Override
			public synchronized void start() {
				super.start();
				// Wait for the discussion to become available
				try {
					Thread.sleep(2000);
					refresh();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				posted = false;
			}
		}.start();
	}

	@Override
	void clear() {
		adaptor.clear();
	}
	
}
