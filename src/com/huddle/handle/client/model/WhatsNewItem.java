package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

/**
 *
 * @author grahamc
 */
public class WhatsNewItem extends ModelObject {

    public static final String TARGET_TYPE_DISCUSSION_POST = "DiscussionPost";
    public static final String TARGET_TYPE_DISCUSSION = "Discussion";
    public static final String TARGET_TYPE_DOCUMENT = "Document";
    public static final String TARGET_TYPE_USER = "User";
    public static final String TARGET_TYPE_WHITEBOARD = "Whiteboard";
    public static final String TARGET_TYPE_TASK = "Task";
    public static final String TARGET_TYPE_MEETING = "Meeting";
    public static final String TARGET_TYPE_COMMENT = "Comment";

    private String description;
    private String targetText;
    private String workspaceTitle;
    private String displayName;
    private String uri;

	private String formattedDate;
    private String workspaceId;
    private String versionNumber;
    private String targetId;
    private String userId;
    private String targetType;
    private boolean insertDateHeader;

    public WhatsNewItem(Workspace parentWorkspace, Object json) throws IOException {
        super(parentWorkspace, json);
    }
    
    public WhatsNewItem(Workspace parentWorkspace) {
    	super(parentWorkspace);
    }

    public void parse(Object json) {
    	if (json == null)
    		return;
        JSONObject jsonObject = (JSONObject)json;
        description = jsonObject.optString("Description");
        targetText = jsonObject.optString("TargetText");
        workspaceTitle = jsonObject.optString("WorkspaceTitle");
        displayName = jsonObject.optString("DisplayName");
        uri = jsonObject.optString("Uri");
        formattedDate = jsonObject.optJSONObject("DateAdded").optString("FormattedDate");
        workspaceId = jsonObject.optString("WorkspaceId");
        versionNumber = jsonObject.optString("VersionNumber");
        targetId = jsonObject.optString("TargetId");
        userId = jsonObject.optString("UserId");
        targetType = jsonObject.optString("TargetType");
    }

    public void refresh() {
        throw new RuntimeException("Refresh called at wrong level of model.");
    }

    public String getDescription() {
        return description;
    }

    public String getTargetText() {
        return targetText;
    }

    public String getWorkspaceTitle() {
        return workspaceTitle;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUri() {
        return uri;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getTargetId() {
        return targetId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTargetType() {
        return targetType;
    }
    
    public boolean isInsertDateHeader() {
		return insertDateHeader;
	}

	public void setInsertDateHeader(boolean insertDateHeader) {
		this.insertDateHeader = insertDateHeader;
	}

	public void setFormattedDate(String formattedDate) {
		this.formattedDate = formattedDate;
	}

}
