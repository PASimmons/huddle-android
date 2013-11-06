package com.huddle.handle.tasks;

import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.huddle.handle.client.model.File;
import com.huddle.handle.client.model.FileApproval;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.model.ThatsTooBigException;

public class DownloadTask extends BackgroundTask {
	
	File f;
	FileSystemObject fso;
	FileApproval fa;
	java.io.File downloadFile;
	String type;
	String title;
	public DownloadTask(Activity activity, File file) {
		ctx = activity;
		f = file;
	}
	
	public DownloadTask(Activity activity, FileSystemObject fso) {
		ctx = activity;
		this.fso = fso;
	}
	
	public DownloadTask(Activity activity, FileApproval fa) {
		ctx = activity;
		this.fa = fa;
	}
	
	@Override
	protected void onPreExecute() {
    	loading();
	}

	@Override
	protected void onPostExecute(Void result) {
		finishLoading();

		if (downloadFile != null) {

			try {
				Intent intent = new Intent(); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
				intent.setAction(android.content.Intent.ACTION_VIEW); 
				intent.setDataAndType(Uri.fromFile(downloadFile), type);
				ctx.startActivity(Intent.createChooser(intent, "Open File.."));	
			} catch (ActivityNotFoundException ex) {
				noViewerDialog();
			}
		} else {
			viewFileError();
		}
	}

	
	@SuppressWarnings("unused")
	private boolean canDisplayFile(String mime) {
	    PackageManager packageManager = ctx.getPackageManager();
	    Intent testIntent = new Intent(Intent.ACTION_VIEW);
	    testIntent.setType(mime);
	    if (packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0) {
	        return true;
	    } else {
	        return false;
	    }
	}
	
	@Override
	protected Void doInBackground(Activity... params) {
		try {

			if (fso != null) {
				f = fso.getFileDetails(true);
				downloadFile = saveFile(f.getFileName(), fso.getFileContent());
				type = f.getMimeType();
				title = fso.getFileName();
			} else if (fa != null) {
				downloadFile = saveFile(fa.getFileName(), fa.getFileContent());
				type = fa.getMimeType();
				title = fa.getFileName();
			} else {
				downloadFile = saveFile(f.getFileName(), f.getParentFSO().getFileContent());
				type = f.getMimeType();
				title = f.getFileName();
			}

		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception downloading file " + e.getMessage());
			e.printStackTrace();
			userNotify("File download failed");
		} catch (ThatsTooBigException ex) {
			tooBigError();
		}
		
		return null;
	}
	
	protected void noViewerDialog() {
		taskOKDialog(ctx, "Document View Failed",
				"Unable to view " + title + 
					" .There is no document view installed for document type " + type,
				null);
	}

	private void tooBigError() {
		taskOKDialog(ctx, "File Too Big",
			"Sorry, viewing file " + title + " has failed.  The file is too big to download",
			null);
	}
	

	private void viewFileError() {
		taskOKDialog(ctx, "Can't view file",
			"Sorry, file viewing has failed.  The file has not been found",
			null);
	}
}