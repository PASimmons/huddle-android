package com.huddle.handle.tasks;

import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.huddle.handle.client.model.File;
import com.huddle.handle.client.model.FileSystemObject;
import com.huddle.handle.client.model.ThatsTooBigException;

public class ShareTask extends BackgroundTask {
	
	File f;
	FileSystemObject fso;
	java.io.File downloadFile;
	String shareText;
	String shareTextType;
	String type;
	String title;

	public ShareTask(Activity activity, File file) {
		ctx = activity;
		f = file;
	}
	
	public ShareTask(Activity activity, FileSystemObject fso) {
		ctx = activity;
		this.fso = fso;
	}
	
	public ShareTask(Activity activity, String title, String text, String type) {
		ctx = activity;
		shareText = text;
		shareTextType = type;
		this.title = title;
	}
	
	@Override
	protected void onPreExecute() {
		if (f != null || fso != null) {
	    	loading();
		}
	}

	@Override
	protected void onPostExecute(Void result) {
		if (f != null || fso != null) {
			finishLoading();
		}

		try {
			Intent share = new Intent(Intent.ACTION_SEND);
			if (f != null) {
				share.setType(f.getMimeType());
				share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(downloadFile));
				ctx.startActivity(Intent.createChooser(share, "Share File"));
			} else if (shareText != null) {
				share.setType(shareTextType);
				share.putExtra(Intent.EXTRA_TEXT, shareText);
				ctx.startActivity(Intent.createChooser(share, "Share"));
			}
			
		} catch (ActivityNotFoundException ex) {
			noViewerDialog();
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
			} else if (f != null) {
				downloadFile = saveFile(f.getFileName(), f.getParentFSO().getFileContent());
				type = f.getMimeType();
				title = f.getFileName();
			}

		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception downloading file " + e.getMessage());
			e.printStackTrace();
			userNotify("File download for sharing failed");
		} catch (ThatsTooBigException ex) {
			tooBigError();
		}
		return null;
	}
	
	protected void premiumVersion() {
		taskOKDialog(ctx, "Upgrade", 
			"Sorry, file sharing is unavailable in the lite version of Huddle for Android", 
			null);
	}
	
	protected void noViewerDialog() {
		taskOKDialog(ctx, "Document Share Failed",
				"Unable to share " + title + 
					" .There is no application installed that can share documents of type " + type,
				null);
	}

	private void tooBigError() {
		taskOKDialog(ctx, "File Too Big",
			"Sorry, sharing file " + title + " has failed.  The file is too big to download for sharing",
			null);
	}
}