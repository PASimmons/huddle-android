package com.huddle.handle.android;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

import com.huddle.handle.android.R;
import com.huddle.handle.adaptors.TaskListAdapter;
import com.huddle.handle.client.model.AllTasks;
import com.huddle.handle.client.model.Task;
import com.huddle.handle.client.model.Tasks;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class TaskTabsActivity extends TabActivity implements Runnable, OnTabChangeListener {

	private static final String LATE = "Overdue";
	private static final String UPCOMING = "Upcoming";
	private static final String COMPLETED = "Completed";

	// The two views in our tabbed example
	private ListView listView1;
	private ListView listView2;
	private ListView listView3;
	
	protected Handler guiThread;
	protected ExecutorService updateThread;
	protected Runnable updateTask;
	private TabHost tabHost;
	private Tasks tasks;
	private static TaskListAdapter lateAdaptor;
	private static TaskListAdapter upcomingAdaptor;
	private static TaskListAdapter completeAdaptor;
	public static Task clickedParent;
	Context ctx;
	public static Task removed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.tasks);
		TextView header = (TextView) findViewById(R.id.tasks_header);
		StringBuffer sb = new StringBuffer();
		if (WorkspacesActivity.clickedParent != null) {
			sb.append("<small>").append(WorkspacesActivity.clickedParent.getTitle()).append("</small><br/>");
		}
		sb.append("Tasks");
		header.setText(Html.fromHtml(sb.toString()));

		tabHost = getTabHost();
		tabHost.setOnTabChangedListener(this);

		listView1 = (ListView) findViewById(R.id.late);
		listView2 = (ListView) findViewById(R.id.upcoming);
		listView3 = (ListView) findViewById(R.id.completed);

		lateAdaptor = new TaskListAdapter(this, R.layout.task_list_item, new ArrayList<Task>());
		upcomingAdaptor = new TaskListAdapter(this, R.layout.task_list_item, new ArrayList<Task>());
		completeAdaptor = new TaskListAdapter(this, R.layout.task_list_item, new ArrayList<Task>());
		
		listView1.setAdapter(lateAdaptor);
		listView2.setAdapter(upcomingAdaptor);
		listView3.setAdapter(completeAdaptor);

		listView1.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clickedParent = (Task) listView1.getAdapter().getItem(position);
				startTaskActivity();
			}
		});
		listView2.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clickedParent = (Task) listView2.getAdapter().getItem(position);
				startTaskActivity();
			}
		});
		listView3.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				clickedParent = (Task) listView3.getAdapter().getItem(position);
				startTaskActivity();
			}
		});
		
		// add views to tab host
		tabHost.addTab(tabHost.newTabSpec(LATE).setIndicator(buildIndicator(LATE)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView1;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(UPCOMING).setIndicator(buildIndicator(UPCOMING)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView2;
			}
		}));
		tabHost.addTab(tabHost.newTabSpec(COMPLETED).setIndicator(buildIndicator(COMPLETED)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return listView3;
			}
		}));
		
		initThreading();
		
		// Workaroudn bug that shows content through tabs
		tabHost.setCurrentTab(2);
		tabHost.setCurrentTab(1);
		tabHost.setCurrentTab(0);
	}
    
    private View buildIndicator(String textRes) {
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
                getTabWidget(), false);
        indicator.setText(textRes);
        return indicator;
    }
    
	private void initThreading() {
		guiThread = new Handler();
		guiThread.post(this);
	}

	/**
	 * Implement logic here when a tab is selected
	 */
	public void onTabChanged(String tabName) {
		if(tabName.equals(UPCOMING)) {
			upcomingAdaptor.notifyDataSetChanged();
		}
		else if(tabName.equals(COMPLETED)) {
			completeAdaptor.notifyDataSetChanged();
		}
		else if(tabName.equals(LATE)) {
			lateAdaptor.notifyDataSetChanged();
		}
	}
	
	public void run() {
		
		try {
			if (WorkspacesActivity.clickedParent == null) {
				tasks = new AllTasks(true);
			} else {
				tasks = new Tasks(WorkspacesActivity.clickedParent, true);
			}
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting tasks " + e.getMessage());

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

		if (tasks != null) {
			Vector<Task> tsks =  tasks.getTasks();
			for (Task tsk:tsks) {
				Log.d(this.getClass().getName(), tsk.getTitle() + " : " + tsk.getStatus());
				if (tsk.getStatus().equals(LATE)) {
					lateAdaptor.add(tsk);
				} else if (tsk.getStatus().equals(UPCOMING)) {
					upcomingAdaptor.add(tsk);
				} else if (tsk.getStatus().equals(COMPLETED)) {
					completeAdaptor.add(tsk);
				}
			}
		}
	}

	private void startTaskActivity() {
		Intent intent = new Intent(this, TaskActivity.class);
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
	    	refresh();
	        return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	protected void refresh() {
		clear();
		initThreading();
	}

	private void clear() {
		lateAdaptor.clear();
		upcomingAdaptor.clear();
		completeAdaptor.clear();
	}
	
	private void removeTask(Task t) {
		lateAdaptor.remove(t);
		lateAdaptor.notifyDataSetChanged();
		upcomingAdaptor.remove(t);
		upcomingAdaptor.notifyDataSetChanged();
		if (!completeAdaptor.items.contains(t)) {
			completeAdaptor.add(t);
			completeAdaptor.notifyDataSetChanged();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if (removed != null) {
			removeTask(removed);
			removed = null;
		}
	}
}