package com.huddle.handle.client.model;

import org.json.JSONObject;

public class Approval {

    private String approved;
    private String userDisplayName;
    private String userId;
    private String userProfilePath;
    private String username;
    private String status;

    public Approval(JSONObject json) {
        approved = json.optString("Approved");
        userDisplayName = json.optString("UserDisplayName");
        userId = json.optString("UserId");
        userProfilePath = json.optString("UserProfilePath");
        username = json.optString("Username");
        status = json.optString("Status");
    }

    public String getApproved() {
        return approved;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserProfilePath() {
        return userProfilePath;
    }

    public String getUsername() {
        return username;
    }

    public String getStatus() {
        return status;
    }

}
