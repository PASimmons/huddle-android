package com.huddle.handle.android;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huddle.handle.android.R;
import com.huddle.handle.client.model.Workspaces;
import com.huddle.handle.client.resources.LoginStore;



public class LoginActivity extends Activity implements OnClickListener {
	private static final int ERROR_DIALOG = 0;
	private static final int ERROR_LOGIN_DIALOG = ERROR_DIALOG + 1;
	private static final int ERROR_BLANK_DIALOG = ERROR_DIALOG + 2;
	private static final String SIGNUP_URL = "http://www.huddle.com/plans-pricing/";
	private EditText etUsername;
	private EditText etPassword;
	private Button btnLogin;
	private CheckBox chkRemember;
	private TextView signUp;
	private ImageView logo;
	private ImageView powered;
	private String ex;
	
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.login);
		
		// Get the EditText and Button References
		etUsername = (EditText)findViewById(R.id.username);
		logo = (ImageView)findViewById(R.id.logo);
		etPassword = (EditText)findViewById(R.id.password);
		btnLogin = (Button)findViewById(R.id.login_button);
		chkRemember = (CheckBox)findViewById(R.id.remember);
		signUp = (TextView)findViewById(R.id.signup_url);
		powered = (ImageView)findViewById(R.id.powered);
		
		logo.setImageDrawable(getResources().getDrawable(R.drawable.huddle_logo));
		powered.setImageDrawable(getResources().getDrawable(R.drawable.powered));
		powered.bringToFront();
		
		StringBuilder sb = new StringBuilder();
		sb.append("<u><a href=\"" + SIGNUP_URL + "\">Create your account</a></u>");
		signUp.setText(Html.fromHtml(sb.toString()));
		signUp.setMovementMethod(LinkMovementMethod.getInstance());
		
		btnLogin.setOnClickListener(this);
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK ) {
    		return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent data = getIntent();
			setResult(999, data);
			finish();
    		return true;
    	}
    	return super.onKeyUp(keyCode, event);
    }
    
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_button:
			String username = etUsername.getText().toString();
	        String password = etPassword.getText().toString();
	        
	        if (username == null || username.length() == 0 || 
	        	password == null || password.length() == 0) {
				showDialog(ERROR_BLANK_DIALOG);	
	        } else {
		        boolean remember = chkRemember.isChecked();
		        LoginStore.setCredentials(username, password, remember);
		        
		        loading();
	        	this.execute(new Handler());
	        }
			break;
		}
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
    
    @Override
    public void onConfigurationChanged(Configuration c){
    	if( mProgress != null ) {//In case has never been created
	    	if( mProgress.isShowing() ){
	    		mProgress.dismiss();
	        	mProgress = ProgressDialog.show(this,"","Loading. Please wait...",true);
	    		mProgress.setCancelable(false);
	    	}
    	}	
    	super.onConfigurationChanged(c);
    }
	
	public void execute(Handler handler){
		handler.post(new Runnable() {
			public void run() {
				SharedPreferences prefs = getSharedPreferences(LoginStore.LOGIN_DETAILS, 0);
				SharedPreferences.Editor editor = prefs.edit();
				try {
					// Try requesting workspaces to make connection with the credentials
					AbstractHuddleActivity.workspaces =  new Workspaces(true);
					finishLoading();
					editor.putBoolean(LoginStore.LOGGED_IN, true);
					editor.putBoolean(LoginStore.AUTHENTICATED, true);
					editor.commit();
					Intent data = getIntent();
					setResult(RESULT_OK, data);
					finish();
				} catch (IOException e) {
					finishLoading();
					Log.e(this.getClass().getName(), "Exception getting workspaces " + e.getMessage());
					ex = e.getMessage();
					editor.putBoolean(LoginStore.LOGGED_IN, false);
					editor.putBoolean(LoginStore.AUTHENTICATED, false);
					editor.commit();
					showDialog(ERROR_LOGIN_DIALOG);	
				}
			}
		});
	}
	
	String errorMsg = null;
	@Override
	protected Dialog onCreateDialog(int id) {
		String title = "Login Error";
		switch (id) {
			case ERROR_DIALOG:
				errorMsg = "General Error";
				break;
			case ERROR_LOGIN_DIALOG:
				errorMsg = "Login Failed:\n" + ex;
				break;
			case ERROR_BLANK_DIALOG:
				errorMsg = "All fields required";
				break;
			}
		
		AlertDialog.Builder alertbox = new AlertDialog.Builder(this).setTitle(title).setMessage(errorMsg);
		alertbox. setNeutralButton("OK", new DialogInterface.OnClickListener() {
	
		    public void onClick(DialogInterface arg0, int arg1) {
		    }
		});
		return alertbox.create();
	}
}

