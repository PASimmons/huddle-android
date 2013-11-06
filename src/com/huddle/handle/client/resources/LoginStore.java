package com.huddle.handle.client.resources;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.huddle.handle.android.HuddleApplication;

public class LoginStore {

	public static final String LOGIN_DETAILS = "LOGIN_DETAILS";
	public static final String LOGGED_IN = "logged_in";
	protected static final String REMEMBER_LOGIN = "remember_login";
	protected static final String USER_NAME = "username";
	protected static final String PASSWORD = "password";
	public static final String AUTHENTICATED = "authenticated";
	
    public static void setCredentials(String login, String password, boolean remember) {
		SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
		Editor e = prefs.edit();
        e.putString(USER_NAME, login);
        e.putString(PASSWORD, password);
        e.putBoolean(LOGGED_IN, true);
        e.putBoolean(REMEMBER_LOGIN, remember);
        e.commit();
    }

    public static String getLogin() {
		SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
		return prefs.getString(USER_NAME, null);
    }

    public static String getPassword() {
		SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
		return prefs.getString(PASSWORD, null);
    }

    public static boolean remember() {
		SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
		return prefs.getBoolean(REMEMBER_LOGIN, false);
    }
    
    public static boolean loggedIn() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	return prefs.getBoolean(LOGGED_IN, false);
    }
    
    public static void logOut() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean(LOGGED_IN, false);
    	editor.commit();
    }
    
    public static void deAuthenticate() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean(AUTHENTICATED, false);
    	editor.commit();
    }
    
    public static void clear() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.clear();
    	editor.commit();
    }
    
    public static void authenticate() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	SharedPreferences.Editor editor = prefs.edit();
    	editor.putBoolean(AUTHENTICATED, true);
    	editor.commit();
    }
    
    public static boolean authenticated() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	return prefs.getBoolean(AUTHENTICATED, false);
    }
    
    public static void finishSession() {
    	SharedPreferences prefs = HuddleApplication.getInstance().getSharedPreferences(LOGIN_DETAILS, 0);
    	if (!remember()) {
    		prefs.edit().clear().commit();
    	}
    }
}
