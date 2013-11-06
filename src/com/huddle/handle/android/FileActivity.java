package com.huddle.handle.android;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.File;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.model.Workspace;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.DownloadTask;
import com.huddle.handle.tasks.ShareTask;

public class FileActivity extends AbstractHuddleActivity {
	

	File f;
	FileSystemObject fso;
	TextView header;
	String workspaceTitle;
	java.io.File downloadFile;
	Activity ctx;
	ImageButton viewFile;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		ctx = this;
		title = getAndSetViewAndHeader(this, R.layout.file, getString(R.string.file));
	}    

	@Override
	void clear() { }

	@Override
	void updateGui() {
		if (f!= null) {
			TextView view = (TextView)findViewById(R.id.file_title2);
			header = (TextView)findViewById(R.id.file_title);
			header.setText(f.getTitle());
			view.setText(f.getTitle());
			view = (TextView)findViewById(R.id.file_description);
			view.setText(f.getDescription());
			
			if (workspaceTitle != null) {
				view = (TextView)findViewById(R.id.file_workspace);
				view.setText("Workspace: " + workspaceTitle);
			}

			if (f.getApprovalDueDate().getDay() != null) {
				view = (TextView)findViewById(R.id.file_approval_due);
				view.setText("Approval Due: " + f.getApprovalDueDate().getFormattedDate());
			}

			view = (TextView)findViewById(R.id.file_size);
			view.setText("Size: " + f.getSizeInK());
			view = (TextView)findViewById(R.id.file_type);
			view.setText("Type: " + f.getMimeType());
			
			view = (TextView)findViewById(R.id.file_added_by);
			StringBuilder sb = new StringBuilder();
			sb.append("Created by ").append(f.getCreatedByUserDisplayName());
			sb.append("<br/>on ").append(f.getCreated().getFormattedDate());
			view.setText(Html.fromHtml(sb.toString()));
			
			view = (TextView)findViewById(R.id.file_updated);
			if (f.getLastEditedByUserId() != null) {
				sb = new StringBuilder();
				sb.append("Updated by ").append(f.getLastEditedByUserDisplayName());
				sb.append("<br/>on ").append(f.getLastUpdated().getFormattedDate());
				view.setText(Html.fromHtml(sb.toString()));
			}
			
			viewFile = (ImageButton)findViewById(R.id.view_file);
			viewFile.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					viewFile();
				}
				
			});
		}
	}

	public void run() {
		try {
			if (getIntent().getExtras() != null && getIntent().getExtras().getString("targetId") != null) {
				String targetId = getIntent().getExtras().getString("targetId");
				workspaceTitle = getIntent().getExtras().getString("workspaceTitle");
				String targetText = getIntent().getExtras().getString("targetText");
				fso = new FileSystemObject(targetId, targetText);
				f = fso.getFileDetails(true);
			} else {
				f= FilesActivity.clickedFile.getFileDetails(true);
				Workspace w = FilesActivity.clickedFile.getParentWorkspace();
				if (w != null) {
					workspaceTitle = w.getTitle();
				}
			}
			postToGui();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.file_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share:
			try {
				BackgroundTask task = new ShareTask(this, f);
				task.execute(this);
			} catch (Exception e) {
				e.printStackTrace();
				userNotify("Unable to share document");
			}
			break;
		case R.id.view:
			viewFile();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void viewFile() {
		try {
			BackgroundTask task = new DownloadTask(this, f);
			task.execute(this);
		} catch (Exception e) {
			e.printStackTrace();
			userNotify("Unable to open document");
		}
	}
	

}

