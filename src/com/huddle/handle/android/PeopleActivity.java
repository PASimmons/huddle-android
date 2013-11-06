package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.PeopleListAdapter;
import com.huddle.handle.client.model.Member;
import com.huddle.handle.client.model.Members;

public class PeopleActivity extends AbstractHuddleActivity implements OnItemClickListener {
	
	private Members members;
	private PeopleListAdapter adaptor;
	private List<Member> mbrs;
	public static Member clickedParent;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getAndSetViewAndHeader(this, R.layout.people, getString(R.string.people));
		
		ListView workspaceList = (ListView) findViewById(R.id.user_list);
		TextView header = (TextView) findViewById(R.id.user_header);
		StringBuffer sb = new StringBuffer();
		sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		sb.append("People");
		header.setText(Html.fromHtml(sb.toString()));
		
		workspaceList.setOnItemClickListener(this);
		
		mbrs = new ArrayList<Member>();
		adaptor = new PeopleListAdapter(this, R.layout.people_list_item, mbrs);
		workspaceList.setOnScrollListener(adaptor);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		workspaceList.setAdapter(adaptor);
		
    }

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(this, UserActivity.class);
		intent.putExtra(HEADER, title);
		clickedParent = adaptor.getItem(position);
		startActivity(intent);
	}

	public void run() {

		try {
			members = new Members(WorkspacesActivity.clickedParent, true);
			postToGui();
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting workspaces " + e.getMessage());

			networkError(e);
		}

	}

	@Override
	void updateGui() {

		if (members != null) {
			Vector<Member> spcs =  members.getMembers();
			for (Member spc:spcs) {
				adaptor.add(spc);
				adaptor.notifyDataSetChanged();
			}
		}
	}

	@Override
	void clear() {
		
		adaptor.clear();
	}
}
