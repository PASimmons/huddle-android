package com.huddle.handle.android;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Date;
import com.huddle.handle.client.model.Task;
import com.huddle.handle.client.model.TaskUser;
import com.huddle.handle.client.model.Tasks;
import com.huddle.handle.client.model.Workspaces;
import com.huddle.handle.tasks.UpdateTaskTask;

public class TaskActivity extends AbstractHuddleActivity {
	
	public static Task t;
	Tasks ts;
	TextView header;
	ImageButton button;
	static final int UPDATE = 100;
	Activity ctx;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		ctx = this;
		setContentView(R.layout.task);
		
		header = (TextView)findViewById(R.id.title);
		header.setOnClickListener(updateTaskListener);
		button = (ImageButton)findViewById(R.id.update_task);
		button.setOnClickListener(updateTaskListener);
	}    

	View.OnClickListener updateTaskListener = new View.OnClickListener()
	{
		public void onClick(View view) {
			UpdateTaskTask task = new UpdateTaskTask(ctx);
	    	task.execute(ctx);
		}
	};

	@Override
	void clear() { }

	@Override
	void updateGui() {
		if (t!= null) {
			TextView view = (TextView)findViewById(R.id.title2);
			header.setText(t.getTitle());
			view.setText(t.getTitle());
			view = (TextView)findViewById(R.id.description);
			view.setText(t.getDescription());
			view = (TextView)findViewById(R.id.workspace);
			view.setText("Workspace: " + t.getWorkspaceName());
			view = (TextView)findViewById(R.id.due_date);
			view.setText("Due: " + t.getDueDate().getFormattedDate());
			view = (TextView)findViewById(R.id.assigned);

			StringBuilder sb = new StringBuilder();
			sb.append("Created by ");
			for (TaskUser u:t.getAssignedTo()) {
				sb.append(u.getName()).append("  ");
			}
			sb.append("\non ").append(t.getCreatedDate().getFormattedDate());
			view.setText(sb.toString());

			if (t.getCompletedDate().getDay() != null) {
				button.setVisibility(View.GONE);
				header.setPadding(10, 10, 10, 10);
				t.setCompletedDate(new Date(new java.util.Date()));
			}
		}
	}

	public void run() {
		if (getIntent().getExtras() != null && getIntent().getExtras().getString("targetId") != null) {
			try {
				String targetId = getIntent().getExtras().getString("targetId");
				String workspaceId = getIntent().getExtras().getString("workspaceId");
				ts = new Workspaces(true).findWorkspace(workspaceId).getTasks(true);
				t = ts.findTask(targetId);
				if (t == null) {
					ts.refresh();
					t = ts.findTask(targetId);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			t= TaskTabsActivity.clickedParent;
		}
		postToGui();
	}
}

