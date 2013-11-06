package com.huddle.handle.tasks;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;

import com.huddle.handle.android.AbstractHuddleActivity;
import com.huddle.handle.client.model.Workspaces;
import com.huddle.handle.client.resources.LoginStore;

public class AuthenticateTask extends BackgroundTask {

	BackgroundTaskActionHandler handler;
	
	public AuthenticateTask(Activity ctx, BackgroundTaskActionHandler handler) {
		this.handler = handler;
		this.ctx = ctx;
	}
	
	@Override
	protected void onPreExecute() {
    	loading();
	}

    @Override
	protected void onPostExecute(Void result) {
		finishLoading();
		if (handler != null) {
			handler.onPostExecute();
		}
	}

	@Override
	protected Void doInBackground(Activity... params) {
		
		try {
			// Try requesting workspaces to make connection with the credentials
			AbstractHuddleActivity.workspaces = new Workspaces(true);
			LoginStore.authenticate();
			
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception getting workspaces " + e.getMessage());
			
			if (handler != null) {
				handler.onException();
			}
		}
        return null;
    }
}