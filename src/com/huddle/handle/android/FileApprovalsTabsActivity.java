package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.FileApprovalListAdapter;
import com.huddle.handle.client.model.FileApproval;
import com.huddle.handle.client.model.FilesAwaitingMyApproval;
import com.huddle.handle.client.model.MyFilesAwaitingApproval;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.DownloadTask;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class FileApprovalsTabsActivity extends TabActivity implements OnTabChangeListener, OnItemClickListener {

	private static final String MY_APPROVALS = "My Approvals";
	private static final String FOR_APPROVAL = "For Approval";

	// The two views in our tabbed example
	private ListView listView2;
	private ListView listView3;
	
	protected Handler guiThread;
	private TabHost tabHost;
	private FilesAwaitingMyApproval filesAwaitingMyApproval;
	private MyFilesAwaitingApproval myFilesAwaitingApproval;
	private FileApprovalListAdapter myApprovalsAdaptor;
	private FileApprovalListAdapter forApprovalAdaptor;
	private boolean myApprovalsLoaded, forApprovalLoaded;
	
	public static FileApproval clickedApproval;
	Activity ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.approvals);
        ctx = this;
		TextView header = (TextView) findViewById(R.id.approvals_header);
		StringBuffer sb = new StringBuffer();
		if (WorkspacesActivity.clickedParent != null) {
			sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		}
		sb.append("Approvals");
		header.setText(Html.fromHtml(sb.toString()));

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);
		TabWidget tw = getTabWidget();

		for (int i = 0; i < tw.getChildCount(); i++) {
			View v = tw.getChildAt(i);
			v.setBackgroundColor(R.color.background);
		}
		
		initThreading();

		listView2 = (ListView) findViewById(R.id.approvals);
		listView3 = (ListView) findViewById(R.id.for_approval);

		myApprovalsAdaptor = new FileApprovalListAdapter(this, R.layout.file_list_item, new ArrayList<FileApproval>());
		forApprovalAdaptor = new FileApprovalListAdapter(this, R.layout.file_list_item, new ArrayList<FileApproval>());
		
		listView2.setAdapter(myApprovalsAdaptor);
		listView3.setAdapter(forApprovalAdaptor);

		listView2.setOnItemClickListener(this);
		listView3.setOnItemClickListener(this);
		
		listView2.setOnCreateContextMenuListener(this);
		listView3.setOnCreateContextMenuListener(this);
		
		tabHost.addTab(tabHost.newTabSpec(FOR_APPROVAL).setIndicator(buildIndicator(FOR_APPROVAL)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView3;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(MY_APPROVALS).setIndicator(buildIndicator(MY_APPROVALS)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView2;
			}
		}));
	}
    
    private View buildIndicator(String textRes) {
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
                getTabWidget(), false);
        indicator.setText(textRes);
        return indicator;
    }
    

	private void initThreading() {
		guiThread = new Handler();
	}

	/**
	 * Implement logic here when a tab is selected
	 */
	public void onTabChanged(String tabName) {
		if (guiThread == null) {
			return;
		}
		
		if(tabName.equals(MY_APPROVALS)) {
			if (!myApprovalsLoaded) {
				guiThread.post(myApprovalThread);
				myApprovalsAdaptor.notifyDataSetChanged();
				myApprovalsLoaded = true;
			}
		}
		else if(tabName.equals(FOR_APPROVAL)) {
			if (!forApprovalLoaded) {
				guiThread.post(forApprovalThread);
				forApprovalAdaptor.notifyDataSetChanged();
				forApprovalLoaded = true;
			}
		}
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle("File Action");
		menu.add("Details");
		menu.add("Open");
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		if ( tabHost.getCurrentTab() == 1) {
			clickedApproval = (FileApproval) listView2.getAdapter().getItem(info.position);
		} else {
			clickedApproval = (FileApproval) listView3.getAdapter().getItem(info.position);
		}
		String action = (String) item.getTitle();
		if (action.equals("Open")) {
			Handler mHandler = new Handler(Looper.getMainLooper());
			mHandler.post(new Runnable() {
				public void run() {
					BackgroundTask task = new DownloadTask(ctx, clickedApproval);
					task.execute(ctx);	
				}
			});

		} else {
			Intent intent = new Intent(this, ApprovalActivity.class);
			intent.putExtra(getString(R.string.approvals), R.string.my_dashboard);
			intent.putExtra("workspaceId", clickedApproval.getWorkspaceId());
			intent.putExtra("workspaceTitle", clickedApproval.getWorkspaceTitle());
			startActivity(intent);
		}
		
		return true;
	}
	
	private void networkError(IOException e) {
		Log.e(this.getClass().getName(), "Exception caught " + e.getMessage());

		AlertDialog.Builder alertbox = new AlertDialog.Builder(ctx);
		alertbox.setTitle("Network Error");
		alertbox.setMessage("An error occurred whilst connecting to the network:\n " + e.getMessage() + "\nPlease try again later.");
		alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {

		    // click listener on the alert box
		    public void onClick(DialogInterface arg0, int arg1) {
		        finish();
		    }
		});

		alertbox.show();
	}

	Runnable myApprovalThread = new Runnable() {
		public void run() {
			
				try {
					Vector<FileApproval> children;
					myFilesAwaitingApproval = new MyFilesAwaitingApproval(true);
					children = myFilesAwaitingApproval.getApprovals();
					
					if (children != null) {
						for (FileApproval tsk:children) {
							myApprovalsAdaptor.add(tsk);
						}
						myApprovalsAdaptor.notifyDataSetChanged();
					}
				} catch (IOException e) {
					networkError(e);
				}
		}
	};
	
	Runnable forApprovalThread = new Runnable() {
		public void run() {

			try {
				Vector<FileApproval> children;
				filesAwaitingMyApproval = new FilesAwaitingMyApproval(true);
				children = filesAwaitingMyApproval.getApprovals();

				if (children != null) {
					for (FileApproval tsk:children) {
						forApprovalAdaptor.add(tsk);
					}
					forApprovalAdaptor.notifyDataSetChanged();
				}
			} catch (IOException e) {
				networkError(e);
			}
		}
	};

	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if ( tabHost.getCurrentTab() == 1) {
			clickedApproval = (FileApproval) listView2.getAdapter().getItem(position);
		} else {
			clickedApproval = (FileApproval) listView3.getAdapter().getItem(position);
		}
		Intent intent = new Intent(this, ApprovalActivity.class);
		intent.putExtra(getString(R.string.approvals), R.string.my_dashboard);
		intent.putExtra("workspaceId", clickedApproval.getWorkspaceId());
		intent.putExtra("workspaceTitle", clickedApproval.getWorkspaceTitle());
		startActivity(intent);
	}	
	
    
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.refresh_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.refresh:
			guiThread.post(forApprovalThread);
			guiThread.post(myApprovalThread);
			myApprovalsAdaptor.notifyDataSetChanged();
			forApprovalAdaptor.notifyDataSetChanged();
	        return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}

}