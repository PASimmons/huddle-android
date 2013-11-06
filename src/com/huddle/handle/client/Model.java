package com.huddle.handle.client;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.huddle.handle.client.comms.JSONHTTPClient;
import com.huddle.handle.client.resources.Base64;
import com.huddle.handle.client.resources.LoginStore;

/**
 * Content and types are just placeholders until the objects are finalised.
 * @author grahamc
 */
public class Model {


    public static String lastCommsErrors = "";

    public static String getEncodedAuthentication() throws IOException {
        String credentials = LoginStore.getLogin() + ":" + LoginStore.getPassword();
        byte[] bytes = credentials.getBytes();
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }


    public static byte[] getImage(String url) throws  IOException {
        JSONHTTPClient c = new JSONHTTPClient();
        try {
            c.setCredentials(Model.getEncodedAuthentication());
            return c.getImage(url, "GET");
        } finally {
            lastCommsErrors = c.getCommsErrors();
        }
    }

    public static JSONObject uploadFile(String url, String method, String requestContents, String[][] parameters, byte[] file) throws  IOException {
        JSONHTTPClient c = new JSONHTTPClient();
        try {
            c.setCredentials(Model.getEncodedAuthentication());
            return new JSONObject(new JSONTokener(c.uploadFile(url, method, requestContents, parameters, file)));
        } catch (JSONException e) {e.printStackTrace();
            lastCommsErrors += "J,";
            throw new IOException("J,");
        } finally {
            lastCommsErrors = c.getCommsErrors();
        }
    }

    public static JSONObject getJSONObject(String method, String url)throws  IOException {

        return getJSONObject(method, url, null);
    }

    public static JSONObject getJSONObject(String method, String url, String requestContents) throws  IOException {
        JSONHTTPClient c = new JSONHTTPClient();
         try {
             c.setCredentials(Model.getEncodedAuthentication());
             String json = c.getJson(url, method, requestContents);
             return new JSONObject(new JSONTokener(json));
         } catch (JSONException e) {e.printStackTrace();
             lastCommsErrors += "J,";
             throw new IOException("J,");
         } finally {
             lastCommsErrors = c.getCommsErrors();
         }
    }

    public static JSONArray getJSON(String method, String url) throws IOException {
        JSONHTTPClient c = new JSONHTTPClient();
         c.setCredentials(Model.getEncodedAuthentication());
         try {
             String json = c.getJson(url, method, null);
             if (json.startsWith("[")) {
                 json = "{ Data : " + json + "}";
             }
             return new JSONObject(new JSONTokener(json)).optJSONArray("Data");
         } catch (JSONException e) {e.printStackTrace();
             lastCommsErrors += "J,";
             throw new IOException("J,");
         } finally {
             lastCommsErrors = c.getCommsErrors();
         }
    }

//    public static void handleForbiddden() {
//        UiApplication.getUiApplication().invokeLater(new Thread() {
//            public void run() {
//                Dialog.alert("You do not have permission to make that request.");
//            }
//        });
//    }
//
//    public static void commsError(PaintableScreen screen) {
//        AnimatedLoadingScreen.deallocate();
//        AlertScreen.showScreen(screen, "Communication Error", "There was a problem connecting to the Huddle servers, if this presists please contact support. (" + lastCommsErrors + ")");
//    }
//
//    public static void fileSystemError(PaintableScreen screen) {
//        AnimatedLoadingScreen.deallocate();
//        AlertScreen.showScreen(screen, "File Access Error", "There was a problem accessing the BlackBerry file system, if this presists please contact support.");
//    }
}
