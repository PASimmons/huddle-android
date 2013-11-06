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

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.WhiteboardListAdapter;
import com.huddle.handle.client.model.Whiteboard;
import com.huddle.handle.client.model.Whiteboards;

public class WhiteboardsActivity extends AbstractHuddleActivity implements
			AdapterView.OnItemClickListener{
	
	private Whiteboards whiteboards;
	private WhiteboardListAdapter adaptor;
	private List<Whiteboard> whtbds;
	public static Whiteboard clickedParent;
	private Vector<Whiteboard> spcs;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getAndSetViewAndHeader(this, R.layout.whiteboards, 
				WorkspacesActivity.clickedParent.getTitle());
		
		ListView WhiteboardList = (ListView) findViewById(R.id.whiteboard_list);
		TextView header = (TextView) findViewById(R.id.wb_header);
		StringBuffer sb = new StringBuffer();
		sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		sb.append(getResources().getString(R.string.whiteboards));
		header.setText(Html.fromHtml(sb.toString()));
		
		WhiteboardList.setOnItemClickListener(this);
		
		whtbds = new ArrayList<Whiteboard>();
		adaptor = new WhiteboardListAdapter(this, R.layout.whiteboard_list_item, whtbds);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		WhiteboardList.setAdapter(adaptor);
		
    }

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		
		Intent intent = new Intent(this, WhiteboardActivity.class);
		intent.putExtra(HEADER, title);
		
		clickedParent = adaptor.getItem(position);
		startActivity(intent);
	}

	public void run() {

		try {
			whiteboards = new Whiteboards(WorkspacesActivity.clickedParent, true);
			postToGui();
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting Whiteboards " + e.getMessage());

			networkError(e);
		}
	}

	@Override
	void updateGui() {
		if (whiteboards != null) {
			spcs =  whiteboards.getWhiteboards();
			for (Whiteboard spc:spcs) {
				adaptor.add(spc);
			}

			adaptor.notifyDataSetChanged();
		}
	}

	@Override
	void clear() {
		
		adaptor.clear();
	}
}

