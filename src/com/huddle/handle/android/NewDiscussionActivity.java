package com.huddle.handle.android;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Discussions;

public class NewDiscussionActivity extends Activity implements Runnable {
	
	public static final String PARENT_WORKSPACE_ID = "parentWorkspaceId";
	public static final String PARENT_WORKSPACE_NAME = "parentWorkspaceName";
	public static final String PARENT_WORKSPACE = "parentWorkspace";
	
	private Discussions ds;
//	private TextView header;
	private EditText title;
	private EditText post;
	private Button submit;
	Future<?> f;
	Runnable r;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.new_discussion);
		ds = DiscussionsActivity.discussions;
		r = this;

//		header = (TextView)findViewById(R.id.discussion_header);
		title = (EditText)findViewById(R.id.discussion_title);
		post = (EditText)findViewById(R.id.discussion_post);
		submit = (Button)findViewById(R.id.discussion_submit);
		submit.setOnClickListener(submitListener);
	}    

	View.OnClickListener submitListener = new View.OnClickListener()
	{
		public void onClick(View view) {
			if (f == null) {
				f = Executors.newSingleThreadExecutor().submit(r);
				userNotify("New Discussion Submitted");
				finish();
			}
		}
	};
	
	public void userNotify(String text) {
	    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	public void run() {
		new Thread() {

			@Override
			public synchronized void start() {
				super.start();
				try {
					ds.createDiscussion(title.getText().toString(), post.getText().toString());
					DiscussionsActivity.posted = true;
				} catch (IOException e) {
					e.printStackTrace();
					userNotify("Discussion Not Submitted");
				}
			}
			
		}.start();

	}
}

