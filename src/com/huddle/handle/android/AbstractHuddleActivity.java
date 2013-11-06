package com.huddle.handle.android;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils.TruncateAt;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.WhatsNewItem;
import com.huddle.handle.client.model.Workspaces;
import com.huddle.handle.client.resources.LoginStore;
import com.huddle.handle.tasks.AuthenticateTask;
import com.huddle.handle.tasks.BackgroundTask;
import com.huddle.handle.tasks.BackgroundTaskActionHandler;
import com.huddle.handle.tasks.DialogClickActionHandler;

public abstract class AbstractHuddleActivity extends Activity implements Runnable, BackgroundTaskActionHandler {

	protected static final int LOGIN_ACTIVITY = 898;
	protected static final String HEADER = "header";
	protected static final String BREADCRUMB_SEPARATOR = " > ";

	public Handler guiThread;
	protected ExecutorService backgroundThread;
	protected String title;
	
	public static WhatsNewItem clickedItem;
	public static Workspaces workspaces;
	Bundle savedInstanceState;
	protected boolean contentViewSet;
	protected static boolean refreshing;
	Activity aty;
	
	abstract void updateGui();
	abstract void clear();

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.savedInstanceState = savedInstanceState;
		aty = this;
		refreshing = false;

		if (LoginStore.loggedIn()) {
			if (!LoginStore.authenticated() || workspaces == null) {
				authenticate();
			} else {
				initThreading();
			}
		} else {
			doLogin();
		}
	}

    protected void postToGui() {
    	
    	guiThread.post(new Runnable() {

			public void run() {
				updateGui();
			}
    	});
    }
    
	private void initThreading() {
		guiThread = new Handler();
		backgroundThread = Executors.newCachedThreadPool();
		backgroundThread.execute(this);
	}
	
	public void userNotify(String text) {
	    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	protected void networkError(final IOException e) {

		BackgroundTask.taskOKDialog(this, "Network Error",
				"An error occurred whilst connecting to the network:\n " + e.getMessage() + "\nPlease try again later.", 
				new DialogClickActionHandler() {

			public void dialogClickAction() {
				aty.finish();
			}

		});
	}

	protected void premiumVersion(String function) {

		BackgroundTask.taskOKDialog(this, "Upgrade",
				"Sorry, " + function + " is unavailable in the lite version of Huddle for Android", 
				new DialogClickActionHandler() {

			public void dialogClickAction() {
			}
		});
	}
	
	protected ProgressDialog mProgress = null;
	protected boolean mLoading = false;
	
	protected void loading(){
    	if(!mLoading){
			mProgress = ProgressDialog.show(this,"","Loading. Please wait...",true);
			mProgress.setCancelable(false);
    		mLoading = true;
    		setProgressBarIndeterminateVisibility(true);
    	}
    }
	    
    protected void finishLoading(){
		setProgressBarIndeterminateVisibility(false);
		mProgress.dismiss();
		mLoading = false;
	}
    
    protected String getAndSetViewAndHeader(Activity activity, int resource, String activityName) {

    	if (! contentViewSet) {
    		activity.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
    		activity.setContentView(resource);
    		activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

    		String header;
    		Intent intent = activity.getIntent();
    		if (intent != null && intent.getExtras() != null) {
    			header = intent.getExtras().getString(HEADER);

    			if (header != null) {
    				header += BREADCRUMB_SEPARATOR + activityName;
    			}
    		} else {
    			header = activityName;
    		}

    		TextView view = (TextView)activity.getWindow().findViewById(R.id.caption_text);
    		view.setText(header);
    		view.setEllipsize(TruncateAt.MIDDLE);
    		return header;
    	}
    	return "";
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
	
	public void refresh() {
		refreshing = true;
		clear();
		if (backgroundThread != null) {
			backgroundThread.execute(this);
		} else {
			initThreading();
		}
	}

	public void doLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivityForResult(intent, LOGIN_ACTIVITY);
	}
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == LOGIN_ACTIVITY) {
			if (resultCode == Activity.RESULT_OK) {
				contentViewSet = true;
				refresh();
			}	
			else if (resultCode == 999) {
				finish();
			}
		}
	}

	protected void authenticate() {
		AuthenticateTask task = new AuthenticateTask(this, this);
    	task.execute(this);
	}
	
	public void onPostExecute() {
		initThreading();
	}

	public void onException() {
		doLogin();
	}
	
	public void onBackground() { }
	
	public void onPreExecute() { }
}
