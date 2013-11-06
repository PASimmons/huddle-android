package com.huddle.handle.client.comms;

import java.io.IOException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.huddle.handle.android.HuddleApplication;


public class JSONHTTPClient extends HttpsClient {

    private static final byte JSON_REQUEST = 0;
    private static final byte IMAGE_REQUEST = 1;
    private static final byte UPLOAD_REQUEST = 2;

    String errorList = "";
    public String getJson(String url, String method, String requestContents) throws IOException {
        return (String)execute(url, method, requestContents, JSON_REQUEST, null, null);
    }

    public byte[] getImage(String url, String method) throws IOException {
        return (byte[])execute(url, method, null, IMAGE_REQUEST, null, null);
    }

    public String uploadFile(String url, String method, String requestContents, String[][] parameters, byte[] file) throws IOException {
        return (String)execute(url, method, requestContents, UPLOAD_REQUEST, parameters, file);
    }

    public String getCommsErrors() {
        return errorList;
    }
    
    public boolean isNetworkAvailable() {  
    	Context context = HuddleApplication.getInstance().getApplicationContext();  
    	ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);  
    	if (connectivity == null) {  
    		Log.w(this.getClass().getName(), "Connectivity manager unavailable");
    		return false;
    	} else {  
    		NetworkInfo[] info = connectivity.getAllNetworkInfo();  
    		if (info != null) {  
    			for (int i = 0; i < info.length; i++) {  
    				if (info[i].getState() == NetworkInfo.State.CONNECTED) {  
    					return true;  
    				}  
    			}  
    		}  
    	}  
    	return false;  
    }  
    
    private Object execute(String url, String method, String requestContents, byte requestType, String[][] parameters, byte[] file) throws IOException {

        if (!isNetworkAvailable()) throw new IOException("Connection unavailable.");

        try { // If WLAN is active, try wlan
            return executeRequest(url, method, requestContents, requestType, parameters, file);
        } catch (IOException ex) {ex.printStackTrace();
            errorList += "1,";
            if (ex.getMessage().equals("Received authentication challenge is null")) // brutal hack
                throw new IOException("Authentication failed.");
            throw ex;
        } catch (Exception ex) {ex.printStackTrace();
            errorList += "2,";
	        throw new IOException("No connections available.");
        } catch (Throwable ex) {ex.printStackTrace();
            errorList += "3,";
	        throw new IOException("No connections available.");
        }

    }

    private Object executeRequest(String decoratedURL, String method, String requestContents,  byte requestType, String[][] parameters, byte[] file) throws IOException {

        if (requestType == JSON_REQUEST) {
            return super.executeHTTPS(method, decoratedURL, requestContents);
        } else if (requestType == UPLOAD_REQUEST) {
            return super.executeHTTPS(method, decoratedURL, requestContents, parameters, file);
        } else {
            return super.getImage(decoratedURL);
        }

    }

    public void setCredentials(String encodedCredentials) {
        this.encodedCredentials = encodedCredentials;
    }

}
