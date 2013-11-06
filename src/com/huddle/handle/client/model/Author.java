package com.huddle.handle.client.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class Author {

    private String webApplicationUrl;
    private String primaryAccountId;
    private String[] managedAccountIds;
    private String useHuddlePhoneConferencing;
    private String username;
    private String logoPath;
    private String email;
    private String id;
    private String displayName;


    public Author(JSONObject json) {
        if (json == null) return;
        webApplicationUrl = json.optString("webApplicationUrl");
        primaryAccountId = json.optString("primaryAccountId");

        JSONArray array = json.optJSONArray("managedAccountIds");
        managedAccountIds = new String[array.length()];
        for (int i=0;i<array.length();i++) {
            managedAccountIds[i] = array.optString(i);
        }

        useHuddlePhoneConferencing = json.optString("useHuddlePhoneConferencing");
        username = json.optString("username");
        logoPath = json.optString("logoPath");
        email = json.optString("email");
        id = json.optString("id");
        displayName = json.optString("displayName");
    }



    public String getWebApplicationUrl() {
        return webApplicationUrl;
    }

    public String getPrimaryAccountId() {
        return primaryAccountId;
    }

    public String[] getManagedAccountIds() {
        return managedAccountIds;
    }

    public String getUseHuddlePhoneConferencing() {
        return useHuddlePhoneConferencing;
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
