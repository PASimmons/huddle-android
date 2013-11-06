package com.huddle.handle.tasks;

import java.io.IOException;

import android.app.Activity;
import android.util.Log;

import com.huddle.handle.android.TaskActivity;
import com.huddle.handle.android.TaskTabsActivity;

public class UpdateTaskTask extends BackgroundTask {

	public UpdateTaskTask(Activity ctx) {
		this.ctx = ctx;
	}
	
	@Override
	protected void onPreExecute() {
    	message("Updating Task");
	}

    @Override
	protected void onPostExecute(Void result) {
		finishLoading();
	}

	@Override
	protected Void doInBackground(Activity... params) {
		
		try {
			if (TaskActivity.t.markComplete()) {
				TaskTabsActivity.removed = TaskActivity.t;
				userNotify("Task Updated Successfully");
			} else {
				userNotify("Task Not Updated");
			}
		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception updating task " + e.getMessage());
			userNotify("Task Update Failed");
		}

		ctx.finish();
        return null;
    }
}