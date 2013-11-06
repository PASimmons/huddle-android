package com.huddle.handle.tasks;

import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public abstract class BackgroundTask extends AsyncTask<Activity, Void, Void>{

	protected static final String HUDDLE_PATH = "/Huddle";
	protected Activity ctx;
	
	protected String loadingMessage = "Loading. Please wait...";
	
	public void userNotify(final String text) {
		Handler mHandler = new Handler(Looper.getMainLooper());		
		mHandler.post(new Runnable() {

			public void run() {
			    Toast.makeText(ctx.getApplicationContext(), text, Toast.LENGTH_LONG).show();
			}
		});
	}

	protected ProgressDialog mProgress = null;
	protected boolean mLoading = false;
	
	protected void loading() {
		message(loadingMessage);
	}
		
	protected void message(final String message) {
		Handler mHandler = new Handler(Looper.getMainLooper());		
		mHandler.post(new Runnable() {
			public void run() {
				if(!mLoading){
					mProgress = ProgressDialog.show(ctx,"", message, true);
					mProgress.setCancelable(false);
					mLoading = true;
					ctx.setProgressBarIndeterminateVisibility(true);
				}
			}
		});

	}

	protected void finishLoading() {
		Handler mHandler = new Handler(Looper.getMainLooper());		
		mHandler.post(new Runnable() {
			public void run() {
				try {
					ctx.setProgressBarIndeterminateVisibility(false);
					mProgress.dismiss();
					mLoading = false;
				} catch (Exception e) {};
			}
		});
	}


	public static void taskOKDialog(final Activity context, final String title, final String message, final DialogClickActionHandler click) {
		Handler mHandler = new Handler(Looper.getMainLooper());		
		mHandler.post(new Runnable() {
			public void run() {
				AlertDialog.Builder alertbox = new AlertDialog.Builder(context);
				alertbox.setTitle(title);
				alertbox.setMessage(message);
				alertbox.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface arg0, int arg1) {
				    	if (click != null) {
				    		click.dialogClickAction();
				    	}
				    }
				});
				alertbox.show();
			}
		});
	}

	protected java.io.File saveFile(String fileName, byte[] bytes) throws IOException {
		
		java.io.File huddleDir = new java.io.File(Environment.getExternalStorageDirectory() + HUDDLE_PATH);
		if (!huddleDir.exists()) {
			huddleDir.mkdir();
		}
		
		String downloaded = Environment.getExternalStorageDirectory() + HUDDLE_PATH + "/" + fileName;
		java.io.File file = new java.io.File(downloaded);
		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();
		
		return file;
	}

	protected void premiumVersion() {
		taskOKDialog(ctx, "Upgrade", 
			"Sorry, file viewing is unavailable in the lite version of Huddle for Android", 
			null);
	}
}
