package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.DiscussionListAdapter;
import com.huddle.handle.client.model.Discussion;
import com.huddle.handle.client.model.Discussions;
import com.huddle.handle.client.model.Post;
import com.huddle.handle.client.model.Posts;
import com.huddle.handle.client.model.Workspace;
import com.huddle.handle.client.model.Workspaces;

public class DiscussionActivity extends AbstractHuddleActivity  {
	
	static Discussion discussion;
	private Posts posts;
	private DiscussionListAdapter adaptor;
	private List<Post> postList;
	public static Post clickedParent;
	private ImageButton newPost;
	private TextView header;
	Context ctx;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx = this;
		title = getAndSetViewAndHeader(this, R.layout.posts, getString(R.string.discussion));
		
		ListView workspaceList = (ListView) findViewById(R.id.post_list);
		header = (TextView) findViewById(R.id.posts_header);
		
		postList = new ArrayList<Post>();
		adaptor = new DiscussionListAdapter(this, R.layout.posts_list_item, postList, guiThread);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		workspaceList.setAdapter(adaptor);
		
		newPost = (ImageButton)findViewById(R.id.new_post);
		newPost.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(ctx, NewPostActivity.class);
				intent.putExtra(HEADER, title);
				startActivityForResult(intent, 0);
			}
			
		});
    }

	public void run() {

		try {
			if (getIntent().getExtras() != null && getIntent().getExtras().getString("targetId") != null) {
				String targetId = getIntent().getExtras().getString("targetId");
				String workspaceId = getIntent().getExtras().getString("workspaceId");
				Workspace w = new Workspaces(true).findWorkspace(workspaceId);
				WorkspacesActivity.clickedParent = w;
				Discussions discussions = w.getDiscussions(true);
				discussion = discussions.findDiscussion(targetId);
				if (discussion == null) {
					discussions.refresh();
					discussion = discussions.findDiscussion(targetId);
				}
			} else {
				discussion = DiscussionsActivity.clickedParent;
			}
			
			posts = discussion.getPosts(true);
			postToGui();

		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting discussions " + e.getMessage());

			networkError(e);
		}
	}

	@Override
	void updateGui() {
		if (discussion != null) {
			
			header.setText(discussion.getTitle());
			
			Vector<Post> spcs =  posts.getPostList();
			for (Post spc:spcs) {
				adaptor.add(spc);
				adaptor.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public void onActivityResult(int reqCode, int resultCode, Intent data) {
		super.onActivityResult(reqCode, resultCode, data);

		new Thread() {

			@Override
			public synchronized void start() {
				super.start();
				try {
					// Wait for the post to become available
					Thread.sleep(2000);
					refresh();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	@Override
	void clear() {
		adaptor.clear();
	}
}
