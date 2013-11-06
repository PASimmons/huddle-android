package com.huddle.handle.tasks;

import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.EditText;

import com.huddle.handle.client.model.File;
import com.huddle.handle.client.model.FileApproval;
import com.huddle.handle.client.model.FileSystemObject;

public class UploadTask extends BackgroundTask {
	
	java.io.File f;
	FileSystemObject fso;
	FileApproval fa;
	java.io.File downloadFile;
	String id;
	String title;
	volatile String description = null;
	
	public UploadTask(Activity activity, FileSystemObject fso, java.io.File file, String folderId, String folderName) {
		ctx = activity;
		this.fso = fso;
		f = file;
		id = folderId;
		title = folderName;
		loadingMessage = "Uploading.  Please wait...";
	}
	
	@Override
	protected void onPreExecute() {
			promptForDescription();
	}

	@Override
	protected void onPostExecute(Void result) {
		
	}

	@Override
	protected Void doInBackground(Activity... params) {

		try {

			if (fso != null) {

				while (description == null) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) { }
				}

				userNotify("File uploading...");

				FileInputStream is = new FileInputStream(f);
				File.uploadFile(is, f.getName(), description, f.getName(), id, f.length());

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) { }

				userNotify("File uploaded to " + title + " folder");
			}

		} catch (IOException e) {
			Log.e(this.getClass().getName(), "Exception uploading file " + e.getMessage());
			e.printStackTrace();
			userNotify("File upload failed");
		}
		return null;
	}

	private void promptForDescription() {
		Handler mHandler = new Handler(Looper.getMainLooper());		
		mHandler.post(new Runnable() {
			public void run() {
				AlertDialog.Builder alert = new AlertDialog.Builder(ctx);  
				alert.setTitle("File Upload");  
				alert.setMessage("Provide a description for the file");  
				final EditText input = new EditText(ctx);  
				alert.setView(input);  

				alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {  
						description = input.getText().toString();  
					}  
				});  

				alert.show();
			}});
	}
}