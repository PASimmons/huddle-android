package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.WhatsNewListAdapter;
import com.huddle.handle.client.model.AllTasks;
import com.huddle.handle.client.model.FilesAwaitingMyApproval;
import com.huddle.handle.client.model.WhatsNew;
import com.huddle.handle.client.model.WhatsNewItem;
import com.huddle.handle.client.model.Workspaces;
import com.huddle.handle.client.resources.LoginStore;
import com.huddle.handle.client.resources.NumberDrawable;

public class Dashboard extends AbstractHuddleActivity implements OnClickListener {

	private Button btnWorkspaces;
	private Button btnOverdueTasks;
	private Button btnFilesAwaitingApproval;
	private Activity activity;
	private ListView list;
	WhatsNewListAdapter adapter;
	
	protected static FilesAwaitingMyApproval myApprovals;
    protected static WhatsNew whatsNew;
    protected static AllTasks overdueTasks;
	private List<WhatsNewItem> items;
    
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		
		setupDashboard();
    }

	private void setupDashboard() {
		title = getAndSetViewAndHeader(this, R.layout.dashboard, getResources().getString(R.string.app_name));
		LinearLayout headerView;
		
		headerView = new LinearLayout(this);
		LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		vi.inflate(R.layout.dashboard_buttons, headerView, true);
			
		list = (ListView)findViewById(R.id.dashboard_list);
		list.addHeaderView(headerView);
		list.setItemsCanFocus(true);
		list.requestDisallowInterceptTouchEvent(false);
		
		items = new ArrayList<WhatsNewItem>();
		adapter = new WhatsNewListAdapter(this, R.layout.whatsnew_list_item, items, 
				this, getResources().getString(R.string.my_dashboard));
		
		// set the adaptor to only notify of data changes when we tell it to
		adapter.setNotifyOnChange(false);
		list.setAdapter(adapter);
		list.setOnItemClickListener(adapter);
		list.setOnCreateContextMenuListener(adapter);
		list.setAdapter(adapter);
		
		btnWorkspaces = (Button)headerView.findViewById(R.id.workspaces_button);
		btnWorkspaces.setFocusable(true);
		btnOverdueTasks = (Button)headerView.findViewById(R.id.overdue_tasks_button);
		btnOverdueTasks.setFocusable(true);
		btnFilesAwaitingApproval = (Button)headerView.findViewById(R.id.files_awaiting_approval_button);
		btnFilesAwaitingApproval.setFocusable(true);

		btnWorkspaces.setOnClickListener(this);
		btnOverdueTasks.setOnClickListener(this);
		btnFilesAwaitingApproval.setOnClickListener(this);
	}
	
	@Override
    public void onDestroy() {
    	super.onDestroy();

    	LoginStore.deAuthenticate();
    	if (isFinishing()) {
    		logout();
    	}
    }
	
    public static void logout() {
    	if (!LoginStore.remember()) {
	    	LoginStore.logOut();
    	}
    	
		LoginStore.finishSession();
		
        whatsNew = null;
        workspaces = null;
    }

	public void onClick(View v) {
		
		if (!getCurrentFocus().equals(v)) {
			getCurrentFocus().clearFocus();
		}
		
		Intent intent = null;
		switch (v.getId()) {
		case R.id.workspaces_button:
			intent = new Intent(activity, WorkspacesActivity.class);
			break;
		case R.id.files_awaiting_approval_button:
			intent = new Intent(activity, FileApprovalsTabsActivity.class);
			break;
		case R.id.overdue_tasks_button:
			WorkspacesActivity.clickedParent = null;
			intent = new Intent(activity, TaskTabsActivity.class);
			break;
		default:
			intent = getIntent();
		}
		
		intent.putExtra(HEADER, getResources().getString(R.string.my_dashboard));
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		activity.startActivity(intent);
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.logout:
	    	LoginStore.clear();
	    	doLogin();
	        return true;
	    case R.id.about:
	        startActivity(new Intent(this, AboutActivity.class));
	        return true;
	    case R.id.refresh:
	        refresh();
	        return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		WhatsNewListAdapter.clickedItem = items.get(info.position - 1);
		adapter.startChildActivity((String) item.getTitle());
		
		return true;
	}

	public void run() {

		try {
			whatsNew = new WhatsNew(true);
			postToGui();
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting whats new " + e.getMessage());
			e.printStackTrace();

			networkError(e);
		}

		Runnable ma = new Runnable() {
			public void run() {
				try {
					myApprovals = new FilesAwaitingMyApproval(true);

					guiThread.post(new Runnable() {
						public void run() {
							btnFilesAwaitingApproval.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.approval), null, 
									NumberDrawable.createNumber(activity, myApprovals.getApprovals().size()), null);

						}
					});

				} catch (IOException e) {
					Log.e(this.getClass().getName(), "Exception getting approvals " + e.getMessage());
					e.printStackTrace();
					networkError(e);
				}
			}
		};

		Runnable ot = new Runnable() {
			public void run() {
				try {
					overdueTasks = new AllTasks(true);

					guiThread.post(new Runnable() {
						public void run() {
							btnOverdueTasks.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.task), null, 
									NumberDrawable.createNumber(activity, overdueTasks.countOverdue()), null);
						}
					});
				} catch (IOException e) {
					Log.e(this.getClass().getName(), "Exception getting overdue tasks " + e.getMessage());
					e.printStackTrace();
					networkError(e);
				}
			}
		};

		Runnable w = new Runnable() {
			public void run() {
				try {
					if (workspaces == null || refreshing) { 
						workspaces = new Workspaces(true);
						refreshing = false;
					}

					guiThread.post(new Runnable() {
						public void run() {
							if (workspaces != null && workspaces.getRecentlyModifiedWorkspaces() != null)
								btnWorkspaces.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.workspace), null, 
										NumberDrawable.createNumber(activity, workspaces.getRecentlyModifiedWorkspaces().length), null);
						}
					});
				} catch (IOException e) {
					Log.e(this.getClass().getName(), "Exception getting workspaces" + e.getMessage());
					e.printStackTrace();
					networkError(e);
				}
			}
		};
		
		backgroundThread.execute(ma);
		backgroundThread.execute(ot);
		backgroundThread.execute(w);
	}

	@Override
	void updateGui() {
		if (whatsNew != null) {
			Vector<WhatsNewItem> items =  whatsNew.getWhatsNewItems();
			
			for (WhatsNewItem item:items) {
				adapter.add(item);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	void clear() {
		
		adapter.clear();
	}
}
