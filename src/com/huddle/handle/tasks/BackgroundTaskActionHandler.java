package com.huddle.handle.tasks;

public interface BackgroundTaskActionHandler {
	
	public void onPostExecute();
	public void onPreExecute();
	public void onException();
	public void onBackground();
}
