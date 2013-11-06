package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.WorkspaceListAdapter;
import com.huddle.handle.client.model.Workspace;
import com.huddle.handle.client.model.Workspaces;

public class WorkspacesActivity extends AbstractHuddleActivity implements AdapterView.OnItemClickListener {
	
	public static final String PARENT_WORKSPACE_ID = "parentWorkspaceId";
	public static final String PARENT_WORKSPACE_NAME = "parentWorkspaceName";
	public static final String PARENT_WORKSPACE = "parentWorkspace";
	private Workspaces workspaces;
	private WorkspaceListAdapter adaptor;
	private List<Workspace> wkspcs;
	public static Workspace clickedParent;
	Workspace[] spcs;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		title = getAndSetViewAndHeader(this, R.layout.workspaces, getResources().getString(R.string.workspaces));
		
		ListView workspaceList = (ListView) findViewById(R.id.workspace_list);
		workspaceList.setOnItemClickListener(this);
		
		wkspcs = new ArrayList<Workspace>();
		adaptor = new WorkspaceListAdapter(this, R.layout.workspace_list_item, wkspcs);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		workspaceList.setAdapter(adaptor);
    }

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(this, WorkspaceActivity.class);
		intent.putExtra(HEADER, title);
		
		clickedParent = adaptor.getItem(position);
		if (clickedParent.isAllHeader() || clickedParent.isRecentlyModifiedHeader()) {
			return;
		}
		
		startActivity(intent);
	}

	public void run() {

		try {
			workspaces = new Workspaces(true);
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting workspaces " + e.getMessage());

			networkError(e);
		}

		if (workspaces != null) {
			spcs =  workspaces.getRecentlyModifiedWorkspaces();

		}
		
		postToGui();
	}

	@Override
	void updateGui() {

		int i = 0;
		if (spcs != null) {
			for (Workspace spc:spcs) {
				try {
					if (i == 0) {
						Workspace s = new Workspace();
						s.setRecentlyModifiedHeader(true);
						adaptor.add(s);
					} else if (i == 4) {
						Workspace s = new Workspace();
						s.setAllHeader(true);
						adaptor.add(s);
					}
					i++;
				} catch (IOException e) { }
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

