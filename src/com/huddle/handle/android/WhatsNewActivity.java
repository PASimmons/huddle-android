package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.WhatsNewListAdapter;
import com.huddle.handle.client.model.WhatsNew;
import com.huddle.handle.client.model.WhatsNewInWorkspace;
import com.huddle.handle.client.model.WhatsNewItem;

public class WhatsNewActivity extends AbstractHuddleActivity {
	

	private WhatsNew whatsNew;
	private WhatsNewListAdapter adaptor;
	private List<WhatsNewItem> items;

	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		title = getAndSetViewAndHeader(this, R.layout.whatsnew_list, getString(R.string.whats_new));
		
		ListView whatsNewList = (ListView) findViewById(R.id.whatsnew_list);
		
		
		items = new ArrayList<WhatsNewItem>();
		adaptor = new WhatsNewListAdapter(this, R.layout.whatsnew_list_item, items, this, title);
		
		// set the adaptor to only notify of data changes when we tell it to
		this.adaptor.setNotifyOnChange(false);
		whatsNewList.setAdapter(adaptor);
		whatsNewList.setOnItemClickListener(adaptor);
		whatsNewList.setOnCreateContextMenuListener(adaptor);

    }
    
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		WhatsNewListAdapter.clickedItem = items.get(info.position);
		adaptor.startChildActivity((String) item.getTitle());
		
		return true;
	}

	public void run() {
		
		try {
			whatsNew = new WhatsNewInWorkspace(WorkspacesActivity.clickedParent, true);
			postToGui();
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting workspaces " + e.getMessage());

			networkError(e);
		}
	}

	@Override
	void updateGui() {
		if (whatsNew != null) {
			items =  whatsNew.getWhatsNewItems();
			
			for (WhatsNewItem item:items) {
				adaptor.add(item);
				adaptor.notifyDataSetChanged();
			}
		}
	}

	@Override
	void clear() {
		
		adaptor.clear();
	}
}
