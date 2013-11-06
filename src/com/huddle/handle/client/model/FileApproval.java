package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class FileApproval extends Document {

    private String applicationURL;
    private String description;
    private String fileName;
    private String id;
    private String mimeType;
    private String mimeTypeClass;
    private String title;
    private String url;
    private String workspaceId;
    private String workspaceTitle;
    private String workspacePath;
    private String approvalURL;
    private Vector<Approval> approvals;

    public FileApproval(JSONObject json) throws IOException {
        super(json);
    }

    public void parse(Object json) {
        JSONObject data = (JSONObject)json;
        applicationURL = data.optString("ApplicationUrl");
        description = data.optString("Description");
        fileName = data.optString("FileName");
        id = data.optString("Id");
        mimeType = data.optString("MimeType");
        mimeTypeClass = data.optString("MimeTypeClass");
        title = data.optString("Title");
        url = data.optString("Url");
        workspaceId = data.optString("WorkspaceId");
        workspaceTitle = data.optString("WorkspaceTitle");
        workspacePath = data.optString("WorkspacePath");
        approvalURL = data.optString("ApprovalUrl");

        JSONArray approvalsArray = data.optJSONArray("Approvals");
        if (approvalsArray != null) {
            approvals = new Vector<Approval>(approvalsArray.length());

            for (int i=0;i<approvalsArray.length();i++) {
                approvals.addElement(new Approval(approvalsArray.optJSONObject(i)));
            }
        }
    }

    public void refresh() {
        throw new RuntimeException("Cannot be independently loaded");
    }

    public byte[] getFileContent() throws IOException {
        return Model.getImage("https://my.huddle.net/" + getUrl() + "/download");
    }

    public String getApplicationURL() {
        return applicationURL;
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

    public String getMimeType() {
        return mimeType;
    }

    public String getMimeTypeClass() {
        return mimeTypeClass;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getWorkspaceTitle() {
        return workspaceTitle;
    }

    public Vector<Approval> getApprovals() {
        return approvals;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public String getApprovalURL() {
        return approvalURL;
    }

    public boolean isViewableCheckRequiresLoading() {
        return hasBeenLoaded();
    }
}
