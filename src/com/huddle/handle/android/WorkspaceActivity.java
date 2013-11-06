package com.huddle.handle.android;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Tasks;
import com.huddle.handle.client.model.WhatsNewInWorkspace;
import com.huddle.handle.client.model.Workspace;
import com.huddle.handle.client.resources.NumberDrawable;

public class WorkspaceActivity extends AbstractHuddleActivity implements OnClickListener{
	
	private TextView txtWhatsNew;
	private TextView txtWhiteboards;
	private TextView txtTasks;
	private TextView txtDiscussions;
	private TextView txtFiles;
	private TextView txtPeople;
	public static Workspace parentId;
	private String title;
	private Activity activity;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		
		if (WorkspacesActivity.clickedParent != null) {
			parentId = WorkspacesActivity.clickedParent;
			title = getAndSetViewAndHeader(this, R.layout.workspace, parentId.getTitle());
		}

		if (parentId != null) {
			TextView header = (TextView)findViewById(R.id.header);
			header.setText(parentId.getTitle());
			title = parentId.getTitle();
		}
		
		txtWhatsNew = (TextView)findViewById(R.id.whats_new);
		txtWhiteboards = (TextView)findViewById(R.id.whiteboards);
		txtTasks = (TextView)findViewById(R.id.tasks);
		txtDiscussions = (TextView)findViewById(R.id.discussions);
		txtFiles = (TextView)findViewById(R.id.files);
		txtPeople = (TextView)findViewById(R.id.people);
		
		if (txtWhatsNew == null) {
			finish();
			return;
		}
		
		txtWhatsNew.setOnClickListener(this);
		txtWhatsNew.setFocusable(true);
		txtWhiteboards.setOnClickListener(this);
		txtWhiteboards.setFocusable(true);
		txtTasks.setOnClickListener(this);
		txtTasks.setFocusable(true);
		txtDiscussions.setOnClickListener(this);
		txtDiscussions.setFocusable(true);
		txtFiles.setOnClickListener(this);
		txtFiles.setFocusable(true);
		txtPeople.setOnClickListener(this);
		txtPeople.setFocusable(true);
	}

	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.whats_new:
			intent = new Intent(this, WhatsNewActivity.class);
			break;
		case R.id.whiteboards:
			intent = new Intent(this, WhiteboardsActivity.class);
			break;
		case R.id.tasks:
			intent = new Intent(this, TaskTabsActivity.class);
			break;
		case R.id.discussions:
			intent = new Intent(this, DiscussionsActivity.class);
			break;
		case R.id.files:
			intent = new Intent(this, FilesActivity.class);
			break;
		case R.id.people:
			intent = new Intent(this, PeopleActivity.class);
			break;
		default:
			intent = getIntent();
		}
		
		intent.putExtra(AbstractHuddleActivity.HEADER, title);
		
		startActivity(intent);
	}

	public void run() {

		Runnable ot = new Runnable() {
			public void run() {
				try {
					final Tasks overdueTasks = new Tasks(WorkspacesActivity.clickedParent, true);

					guiThread.post(new Runnable() {
						public void run() {
							txtTasks.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.task), null, 
									NumberDrawable.createNumber(activity, overdueTasks.countOverdue()), null);
						}
					});

				} catch (IOException e) {
					e.printStackTrace();
					networkError(e);
				}
			}
		};

		Runnable w = new Runnable() {
			public void run() {
				try {
					final WhatsNewInWorkspace wniw = new WhatsNewInWorkspace(WorkspacesActivity.clickedParent, true);

					guiThread.post(new Runnable() {
						public void run() {
							if (workspaces != null && workspaces.getRecentlyModifiedWorkspaces() != null)
								txtWhatsNew.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.whatsnew), null, 
										NumberDrawable.createNumber(activity, wniw.count()), null);
						}
					});

				} catch (IOException e) {
					e.printStackTrace();
					networkError(e);
				}
			}
		};

		backgroundThread.submit(ot);
		backgroundThread.submit(w);
		
	}

	@Override
	void updateGui() {
		
	}

	@Override
	void clear() {
		
	}
}
