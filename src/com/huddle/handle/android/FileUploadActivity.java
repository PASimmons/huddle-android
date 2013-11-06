package com.huddle.handle.android;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.FileListAdapter;
import com.huddle.handle.client.model.File;
import com.huddle.handle.client.resources.IconStore;
import com.huddle.handle.tasks.BackgroundTask;

public class FileUploadActivity extends AbstractHuddleActivity implements AdapterView.OnItemClickListener {

	static final String FILE_TO_UPLOAD = "file_to_upload";
	private static final String OPEN = "Open";
	private static final String UPLOAD = "Upload";
	private ListView listView1;
	private FileListAdapter allAdaptor;
	private Activity ctx;
	private String mime;
	IconStore icons;
	Runnable r;
	java.io.File[] children;
	
	public static java.io.File clickedFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        icons = new IconStore(this);
        ctx = this;
        r = this;
        title = getAndSetViewAndHeader(this, R.layout.file_upload, getString(R.string.file_upload));
		
		TextView header = (TextView) findViewById(R.id.upload_header);
		StringBuffer sb = new StringBuffer();
		if (WorkspacesActivity.clickedParent != null) {
			sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		}
		sb.append("Select file for upload");
		header.setText(Html.fromHtml(sb.toString()));

		listView1 = (ListView) findViewById(R.id.upload_files);
		allAdaptor = new FileListAdapter(this, R.layout.file_list_item, new ArrayList<java.io.File>());
		listView1.setAdapter(allAdaptor);
		listView1.setOnItemClickListener(this);
		listView1.setOnCreateContextMenuListener(this);
	}
    
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK ) {
			if (clickedFile != null) {
				if( clickedFile.getParentFile() != null &&
					clickedFile.getParent().contains("sdcard")	) {
					clickedFile = clickedFile.getParentFile();
					if (clickedFile != null) {
						guiThread.post(this);
						return true;
					} else {
						finish();
						return super.onKeyUp(keyCode, event);
					}
				} else {
					clickedFile = null;
					finish();
					return super.onKeyUp(keyCode, event);
				}
			} else {
				finish();
				return super.onKeyUp(keyCode, event);
			}
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}
	
    public void run() {

    	if (clickedFile != null && clickedFile.isDirectory()) {
    		allAdaptor = new FileListAdapter(ctx, R.layout.file_list_item, new ArrayList<java.io.File>());

    		if (listView1 == null) {
    			listView1 = (ListView) findViewById(R.id.upload_files);

    			if (listView1 == null) {
    				setContentView(R.layout.file_upload);	
    				listView1 = (ListView) findViewById(R.id.upload_files);
    			}
    			listView1.setOnItemClickListener(this);
    		}

    	} else {
    		clickedFile = new java.io.File("/sdcard");
    	}

    	children = clickedFile.listFiles();
    	postToGui();
    }
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		clickedFile = (java.io.File) listView1.getAdapter().getItem(info.position);
		
		menu.setHeaderTitle("File Action");
		
		menu.add(UPLOAD);
		menu.add(OPEN);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		clickedFile = (java.io.File) listView1.getAdapter().getItem(info.position);
		String action = (String) item.getTitle();
		if (action.equals(OPEN)) {
			openFile();
		} else {
			selectFile();
		}

		return true;
	}

	private void openFile() {
		try {
			Intent intent = new Intent(); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.setAction(android.content.Intent.ACTION_VIEW); 
			mime = File.getUploadMimeType(clickedFile.getAbsolutePath());
			intent.setDataAndType(Uri.fromFile(clickedFile), mime);
			ctx.startActivity(intent);
		} catch (ActivityNotFoundException ex) {
			noViewerDialog();
		}
	}
	
	
	protected void noViewerDialog() {
		BackgroundTask.taskOKDialog(ctx, "Document View Failed",
				"Unable to view " + title + 
					" .There is no document view installed for document type " + mime,
				null);
	}

	private void selectFile() {
		Intent data = getIntent();
		data.putExtra(FILE_TO_UPLOAD, clickedFile);
		setResult(RESULT_OK, data);
		finish();
	}

	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		clickedFile = (java.io.File) listView1.getAdapter().getItem(position);
		if (clickedFile.isDirectory()) {
			allAdaptor = new FileListAdapter(ctx, R.layout.file_list_item, new ArrayList<java.io.File>());
			guiThread.post(this);
		} else {
			selectFile();
		}
	}

	@Override
	void updateGui() {
		
		listView1.setAdapter(allAdaptor);
        
		if (children != null) {
	        Arrays.sort(children, new AlphaComparator());
			for (java.io.File tsk:children) {
				allAdaptor.add(tsk);
				allAdaptor.notifyDataSetChanged();
			}
		}
	}

	@Override
	void clear() {
		allAdaptor.clear();
	}

    private static class AlphaComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            java.io.File ws1 = (java.io.File)o1;
            java.io.File ws2 = (java.io.File)o2;
            return ws1.getName().compareTo(ws2.getName());
        }
    }
}