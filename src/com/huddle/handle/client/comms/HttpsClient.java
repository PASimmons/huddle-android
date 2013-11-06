package com.huddle.handle.client.comms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.util.Log;

import com.huddle.handle.client.model.ThatsTooBigException;

public class HttpsClient  {

    protected String errorList = "";

    protected String encodedCredentials;

    public void setCredentials(String encodedCredentials) {
        this.encodedCredentials = encodedCredentials;
    }

    protected byte[] getImage(String url) throws IOException {

        HttpURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        int responseCode;

        try {
            conn = getHttpURLConnection(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);

            responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
            	
	            Log.w(this.getClass().getName(), "Request Failure:" + responseCode);
                if (responseCode == HttpURLConnection.HTTP_ENTITY_TOO_LARGE) {
                    errorList += "F" + responseCode + ",";
                    throw new ThatsTooBigException();
                }
                errorList += "BR" + responseCode + ",";
                throw new IOException("HTTP response code: " + responseCode);
            }
            
            is = conn.getInputStream();
            long length = conn.getContentLength();
            byte[] result;
            if (length < 0) {
                result = readContentOfUnknownLength(is);
            } else {
                result = readContentOfLength(is, (int) length);
            }
            return result;
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
            if (conn != null) conn.disconnect();
        }
    }

    protected Object executeHTTPS(String method, String url, String requestContents, String [][]parameters, byte[] file) throws IOException {
        HttpsURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        int responseCode;

        try {
            conn = getHttpsConnection(url);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");

            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    conn.setRequestProperty(parameters[i][0], parameters[i][1]);
                }
            }

            if (encodedCredentials != null) {
                conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
            }

            if (requestContents != null) {
                byte[] terminator = "\r\n--633524921018662935--".getBytes("UTF-8");
                byte[] bytes = requestContents.getBytes("UTF-8");
                conn.setRequestProperty("Content-Length", String.valueOf(bytes.length + file.length + terminator.length));
                conn.setDoOutput(true);
                conn.setDoInput(true);
                os = conn.getOutputStream();
                os.write(bytes);
                os.write(file);
                os.write(terminator);
                os.flush();
                os.close();
            }

            responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK &&
                    responseCode != HttpURLConnection.HTTP_CREATED) {

                if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    errorList += "F" + responseCode + ",";
                    throw new RuntimeException();
                }
                errorList += "BR" + responseCode + ",";
                throw new IOException("HTTP response code: " + responseCode);
            }
            is = conn.getInputStream();
            long length = conn.getContentLength();
            byte[] result;
            if (length < 0) {
                result = readContentOfUnknownLength(is);
            } else {
                result = readContentOfLength(is, (int) length);
            }
            return convertResponse(result);
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
            if (conn != null) conn.disconnect();
        }
    }

    protected Object executeHTTPS(String method, String url, String requestContents) throws IOException {
        HttpsURLConnection conn = null;
        InputStream is = null;
        OutputStream os = null;
        int responseCode;

        try {
            conn = getHttpsConnection(url);
            conn.setRequestMethod(method);
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Content-Type", "application/json");
            if (encodedCredentials != null) {
                conn.setRequestProperty("Authorization", "Basic " + encodedCredentials);
                if (requestContents != null) {
                	conn.setDoOutput(true);
                	conn.setDoInput(true);
                    os = conn.getOutputStream();
                    os.write(requestContents.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                }
            }

            responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK &&
                    responseCode != HttpURLConnection.HTTP_CREATED) {

                if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    errorList += "F" + responseCode + ",";
                    throw new RuntimeException();
                }
                errorList += "BR" + responseCode + ",";
                throw new IOException("HTTP response code: " + responseCode);
            }
            is = conn.getInputStream();
            long length = conn.getContentLength();
            byte[] result;
            if (length < 0) {
                result = readContentOfUnknownLength(is);
            } else {
                result = readContentOfLength(is, (int) length);
            }
            return convertResponse(result);
        } finally {
            if (is != null) is.close();
            if (os != null) os.close();
            if (conn != null) conn.disconnect();
        }
    }

    public static String convertResponse(byte[] bytes) {
        return new String(bytes,0,bytes.length);
    }

    public byte[] readContentOfLength(InputStream is, int length)
            throws IOException {
        int lastRead = 0;
        int totalRead = 0;
        byte[] data = new byte[length];

        while ((totalRead != length) && (lastRead != -1)) {
            lastRead = is.read(data, totalRead, length - totalRead);
            totalRead += lastRead;
        }
        return data;
    }

    public static byte[] readContentOfUnknownLength(InputStream is)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int ch;
        while ((ch = is.read()) != -1) {
            baos.write(ch);
        }
        return baos.toByteArray();
    }

    private HttpsURLConnection getHttpsConnection(String url) throws IOException {
        try {
        	URL u = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection)u.openConnection();
            connection.setRequestProperty("Connection", "close");
            return connection;
        } catch (ClassCastException ex) {
            throw new IOException("Not an HTTPS url: " + url);
        }
    }

    private HttpURLConnection getHttpURLConnection(String url) throws IOException {
        try {
        	URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestProperty("Connection", "close");
            return connection;
        } catch (ClassCastException ex) {
            throw new IOException("Not an HTTP url: " + url);
        }
    }

}
