package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.huddle.handle.client.Model;

public class Member extends ModelObject {

    private String lastActivity;
    private String isManager;
    private String teamId;
    private String teamDisplayName;
    private String timezoneOffset;
    private String timeZoneId;
    private String username;
    private String logoPath;
    private String email;
    private String id;
    private String displayName;

    private Bitmap img;

    public Member(Workspace parentWorkpspace, Object json) throws IOException {
        super(parentWorkpspace, json);
    }

    public void refresh() {
        throw new RuntimeException("Refresh cannot be called on an individual member.");
    }


    public void parse(Object json) {
        JSONObject jsonObject = (JSONObject)json;
        lastActivity = jsonObject.optString("lastActivity");
        isManager = jsonObject.optString("isManager");

        JSONObject team = jsonObject.optJSONObject("team");
        if (team != null) {
            teamId = team.optString("id");
            teamDisplayName = team.optString("displayName");
        }

        JSONObject timeZoneInfo = jsonObject.optJSONObject("timeZoneInfo");
        if (timeZoneInfo != null) {
            timezoneOffset = timeZoneInfo.optString("offset");
            timeZoneId = timeZoneInfo.optString("tzId");
        }

        username = jsonObject.optString("username");
        logoPath = jsonObject.optString("logoPath");
        email = jsonObject.optString("email");
        id = jsonObject.optString("id");
        displayName = jsonObject.optString("displayName");
    }


    public Bitmap getBitmap() {
        if (img == null) {
            try {
                byte[] imageBytes =  Model.getImage(logoPath);
                img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                //actual width of the image (img is a Bitmap object)
//                int width = img.getWidth();
//                int height = img.getHeight();
//
//                //new width / heigth
//                int newWidth = 100;
//                int newHeight = 60;
//
//                // calculate the scale
//                float scaleWidth = (float) newWidth / width;
//                float scaleHeight = (float) newHeight / height;
//
//                // create a matrix for the manipulation
//                Matrix matrix = new Matrix();
//
//                // resize the bit map
//                matrix.postScale(scaleWidth, scaleHeight);
//
//                // recreate the new Bitmap and set it back
//                Bitmap resizedBitmap = Bitmap.createBitmap(img, 0, 0,width, height, matrix, true);
//                img = resizedBitmap;
            } catch (IOException e) {
            	// ignore
            }
        }
        return img;
    }

    public boolean hasImage() {
        return img != null;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public String getIsManager() {
        return isManager;
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamDisplayName() {
        return teamDisplayName;
    }

    public String getTimezoneOffset() {
        return timezoneOffset;
    }

    public String getTimeZoneId() {
        return timeZoneId;
    }

    public String getUsername() {
        return username;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public String getEmail() {
        return email;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }




}
