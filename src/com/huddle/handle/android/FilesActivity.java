package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.FileSystemObjectListAdapter;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.resources.IconStore;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.DownloadTask;
import com.huddle.handle.tasks.ShareTask;
import com.huddle.handle.tasks.UploadTask;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class FilesActivity extends AbstractHuddleActivity implements AdapterView.OnItemClickListener {

	private static final String OPEN = "Open";
	private static final String DETAILS = "Details";
	private static final String SHARE = "Share";
	private ListView listView1;
	private ImageButton upload;
	private TextView header;
	private FileSystemObjectListAdapter allAdaptor;
	private Activity ctx;
	IconStore icons;
	Runnable r;
	Vector<FileSystemObject> children;
	
	public static FileSystemObject clickedFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		icons = new IconStore(this);
        ctx = this;
        r = this;
		title = getAndSetViewAndHeader(this, R.layout.files, getString(R.string.files));
		
		header = (TextView) findViewById(R.id.files_header);
		StringBuffer sb = new StringBuffer();
		if (WorkspacesActivity.clickedParent != null) {
			sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		}
		sb.append("Files");
		header.setText(Html.fromHtml(sb.toString()));

		listView1 = (ListView) findViewById(R.id.all);
		allAdaptor = new FileSystemObjectListAdapter(this, R.layout.file_list_item, new ArrayList<FileSystemObject>());
		listView1.setAdapter(allAdaptor);
		listView1.setOnItemClickListener(this);
		listView1.setOnCreateContextMenuListener(this);
		
		upload = (ImageButton)findViewById(R.id.upload);
		upload.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ctx, FileUploadActivity.class);
				intent.putExtra(HEADER, title);
				startActivityForResult(intent, 1000);
			}
		});
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		String folderId = null;
		String folderName = null;
		
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case 1000:
				if (clickedFile == null) {
					try {
						clickedFile = new FileSystemObject(WorkspacesActivity.clickedParent, true);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (clickedFile.getParentFolder() == null) {
					for(FileSystemObject o:children) {
						// There will always be a Documents folder in the top level
						if (o.getTitle().equals("Documents")) {
							folderId = o.getId();
							folderName = o.getTitle();
							break;
						}
					}
					
				} else {
					folderId = clickedFile.getId();
					folderName = clickedFile.getTitle();
				}
				java.io.File file = (java.io.File)data.getExtras().get(FileUploadActivity.FILE_TO_UPLOAD);
				
				BackgroundTask upload = new UploadTask(this, clickedFile, file, folderId, folderName);
				upload.execute(this);
			}	
		}
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
				if( clickedFile.getParentFolder() != null) {
					clickedFile = clickedFile.getParentFolder();
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

		try {
			if (WorkspacesActivity.clickedParent != null) {
				if (clickedFile == null) {
					clickedFile = new FileSystemObject(WorkspacesActivity.clickedParent, true);
				} else {
					allAdaptor = new FileSystemObjectListAdapter(ctx, R.layout.file_list_item, new ArrayList<FileSystemObject>());
					
					if (listView1 == null) {
						listView1 = (ListView) findViewById(R.id.all);
						
						if (listView1 == null) {
							setContentView(R.layout.files);	
							listView1 = (ListView) findViewById(R.id.all);
						}
						listView1.setOnItemClickListener(this);
					}
					
				}

				children = clickedFile.getChildren(true);
				postToGui();
			}

		} catch (IOException e) {
			networkError(e);
		}
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		clickedFile = (FileSystemObject) listView1.getAdapter().getItem(info.position);
		
		menu.setHeaderTitle("File Action");
		
		menu.add(DETAILS);
		menu.add(SHARE);
		menu.add(OPEN);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		clickedFile = (FileSystemObject) listView1.getAdapter().getItem(info.position);
		String action = (String) item.getTitle();
		if (action.equals(OPEN)) {
			Handler mHandler = new Handler(Looper.getMainLooper());
			mHandler.post(new Runnable() {
				public void run() {
					BackgroundTask task = new DownloadTask(ctx, clickedFile);
					task.execute(ctx);	
				}
			});
		} else if (action.equals(SHARE)) {
			Handler mHandler = new Handler(Looper.getMainLooper());
			mHandler.post(new Runnable() {
				public void run() {
					BackgroundTask task = new ShareTask(ctx, clickedFile);
					task.execute(ctx);	
				}
			});
		} else {
			showFileDetails();
		}

		return true;
	}

	private void showFileDetails() {
		Intent intent = new Intent(this, FileActivity.class);
		intent.putExtra(getString(R.string.whats_new), title);
		startActivity(intent);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		clickedFile = (FileSystemObject) listView1.getAdapter().getItem(position);
		if (clickedFile.isFolder()) {
			allAdaptor = new FileSystemObjectListAdapter(ctx, R.layout.file_list_item, new ArrayList<FileSystemObject>());
			guiThread.post(this);
		} else {
			showFileDetails();
		}
	}

	@Override
	void updateGui() {
		
		listView1.setAdapter(allAdaptor);
		if (children != null) {
			for (FileSystemObject tsk:children) {
				allAdaptor.add(tsk);
				allAdaptor.notifyDataSetChanged();
			}
		}
	}

	@Override
	public
	void clear() {
		allAdaptor.clear();
		
	}
}