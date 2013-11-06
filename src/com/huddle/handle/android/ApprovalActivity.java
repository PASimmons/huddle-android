package com.huddle.handle.android;

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
import com.huddle.handle.client.model.Approval;
import com.huddle.handle.client.model.FileApproval;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.DownloadTask;

public class ApprovalActivity extends AbstractHuddleActivity {
	FileApproval f;
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
		getAndSetViewAndHeader(this, R.layout.approval, "File Approval");
	}    

	@Override
	void clear() { }

	@Override
	void updateGui() {
		f= FileApprovalsTabsActivity.clickedApproval;
		workspaceTitle = getIntent().getExtras().getString("workspaceTitle");
		if (f!= null) {
			TextView view = (TextView)findViewById(R.id.approval_title2);
			header = (TextView)findViewById(R.id.approval_title);
			header.setText(f.getTitle());
			view.setText(f.getTitle());
			view = (TextView)findViewById(R.id.approval_description);
			view.setText(f.getDescription());
			view = (TextView)findViewById(R.id.approval_workspace);
			view.setText("Workspace: " + workspaceTitle);
			
			if (f.getApprovals().size() > 0) {
				
				StringBuilder sb = new StringBuilder();
				sb.append("Approvals:<br/>").append("<small>");
				
				for (Approval approval:f.getApprovals()) {
					sb.append("<br/>");
					sb.append(approval.getUserDisplayName()).append("<br/>");
					if (approval.getApproved().equals("true")) {
						sb.append("Approved");
					} else {
						sb.append("Awaiting approval");
					}
				}
				sb.append("<small>");

				view = (TextView)findViewById(R.id.approvals);
				view.setText(Html.fromHtml(sb.toString()));
				
				viewFile = (ImageButton)findViewById(R.id.view_file);
				viewFile.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						viewFile();
					}
					
				});
			}
		}
	}

	public void run() {
		postToGui();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.file_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.view:
			try {
				
				if (f != null) {
					BackgroundTask task = new DownloadTask(ctx, f);
					task.execute(ctx);
				}

			} catch (Exception e) {
				e.printStackTrace();
				userNotify("Unable to open document");
			}
		default:
			return super.onOptionsItemSelected(item);
		}
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

