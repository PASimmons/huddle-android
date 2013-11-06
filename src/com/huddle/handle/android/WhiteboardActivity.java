package com.huddle.handle.android;

import java.io.IOException;

import android.app.TabActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Whiteboard;
import com.huddle.handle.client.model.WhiteboardContent;
import com.huddle.handle.client.model.Whiteboards;
import com.huddle.handle.client.model.Workspaces;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class WhiteboardActivity extends TabActivity implements Runnable {

	private static final String CONTENT = "Content";
	private static final String DETAILS = "Details";
	
	protected Handler guiThread;
	private TabHost mtabHost;
	String content;
	WebView webView;
	LinearLayout detailView;
	WhiteboardContent wbc;
	Whiteboard w;
	boolean contentLoaded, detailsLoaded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.whiteboard);

		mtabHost = getTabHost();
		mtabHost.setHorizontalFadingEdgeEnabled(false);

		detailView = (LinearLayout) findViewById(R.id.wb_detail);
		webView = (WebView) findViewById(R.id.wb_content);
		webView.getSettings().setJavaScriptEnabled(false);
		
		// add views to tab host
		mtabHost.addTab(mtabHost.newTabSpec(CONTENT).setIndicator(buildIndicator(CONTENT)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return webView;
			}
		}));
		mtabHost.addTab(mtabHost.newTabSpec(DETAILS).setIndicator(buildIndicator(DETAILS)).setContent(new TabContentFactory() {
			public View createTabContent(String arg0) {
				return detailView;
			}
		}));
		
		// Workaround bug that shows content through tabs
		mtabHost.setCurrentTab(1);
		mtabHost.setCurrentTab(0);
		
		TextView header = (TextView)findViewById(R.id.wb_title);
		
		if (WhiteboardsActivity.clickedParent != null) {
			header.setText(WhiteboardsActivity.clickedParent.getTitle());
		}
		
		guiThread = new Handler();
		guiThread.post(this);
	}
    
    private View buildIndicator(String textRes) {
        final TextView indicator = (TextView) getLayoutInflater().inflate(R.layout.tab_indicator,
                getTabWidget(), false);
        indicator.setText(textRes);
        return indicator;
    }

    public void run() {

    	try {
    		if (getIntent().getExtras() != null && getIntent().getExtras().getString("targetId") != null) {
    			String targetId = getIntent().getExtras().getString("targetId");
    			String workspaceId = getIntent().getExtras().getString("workspaceId");
    			Whiteboards wbs = new Workspaces(true).findWorkspace(workspaceId).getWhiteboards(true);
    			w = wbs.findWhiteboard(targetId);
    			if (w == null) {
    				wbs.refresh();
    				w = wbs.findWhiteboard(targetId);
    			}
    		} else {
    			w = WhiteboardsActivity.clickedParent;
    		}
    		
    		wbc = w.getContent(true);
    		content = wbc.getContents();	
    		webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
    		TextView header = (TextView)findViewById(R.id.wb_title1);
    		header.setText(w.getTitle());
    		header = (TextView)findViewById(R.id.wb_title);
    		header.setText(w.getTitle());
    		header = (TextView)findViewById(R.id.wb_description);
    		header.setText(w.getDescription());
    		header = (TextView)findViewById(R.id.wb_workspace);
    		header.setText("Workspace: " + w.getParentWorkspace().getTitle());
    		header = (TextView)findViewById(R.id.wb_created);
    		header.setText(Html.fromHtml("Created by " + w.getAuthor().getDisplayName() + "<br/>on " + w.getCreatedDate()));
    		
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
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
			guiThread.post(this);
	        return true;
	    default:
	    	return super.onOptionsItemSelected(item);
	    }
	}
}