package com.huddle.handle.android;

import android.app.Application;
import android.content.ContextWrapper;

public class HuddleApplication extends Application {
	
	private static ContextWrapper ctx;

	@Override
	public void onCreate() {
		super.onCreate();
		ctx = this;
	}

	public static ContextWrapper getInstance() {
		return ctx;
	}
}
