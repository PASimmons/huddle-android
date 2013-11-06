package com.huddle.handle.android;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Discussion;

public class NewPostActivity extends Activity implements Runnable {
	
	public static final String PARENT_WORKSPACE_ID = "parentWorkspaceId";
	public static final String PARENT_WORKSPACE_NAME = "parentWorkspaceName";
	public static final String PARENT_WORKSPACE = "parentWorkspace";
	
	private Discussion ds;
	private TextView header;
	private EditText post;
	private Button submit;
	Future<?> f;
	Runnable r;
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.new_post);
		ds = DiscussionActivity.discussion;
		r = this;

		header = (TextView)findViewById(R.id.post_header);
		StringBuffer sb = new StringBuffer();
		sb.append("<small>").append(ds.getTitle()).append("</small><br/>");
		sb.append("New Post");
		header.setText(Html.fromHtml(sb.toString()));
		post = (EditText)findViewById(R.id.post_content);
		submit = (Button)findViewById(R.id.post_submit);
		submit.setOnClickListener(submitListener);
	}    

	View.OnClickListener submitListener = new View.OnClickListener()
	{
		public void onClick(View view) {
			if (f == null) {
				f = Executors.newSingleThreadExecutor().submit(r);
				userNotify("New Post Submitted");
				finish();
			}
		}
	};
	
	public void userNotify(String text) {
	    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	public void run() {
		try {
			ds.createPost(post.getText().toString());
		} catch (IOException e) {
			e.printStackTrace();
			userNotify("Post Not Submitted");
		}
	}
}

