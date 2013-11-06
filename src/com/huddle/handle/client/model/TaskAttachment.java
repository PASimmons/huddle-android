package com.huddle.handle.client.model;

import org.json.JSONObject;

public class TaskAttachment {

    private String applicationUrl;
    private String approvalUrl;
    private String description;
    private String fileName;
    private String id;
    private String title;
    private String url;

    public TaskAttachment(JSONObject json) {
        if (json == null) return;
        applicationUrl = json.optString("ApplicationUrl");
        approvalUrl = json.optString("ApprovalUrl");
        description = json.optString("Description");
        fileName = json.optString("FileName");
        id = json.optString("Id");
        title = json.optString("Title");
        url = json.optString("Url");
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public String getApprovalUrl() {
        return approvalUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

}
