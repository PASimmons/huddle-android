package com.huddle.handle.client.model;

import java.io.IOException;
import java.io.Serializable;

import org.json.JSONObject;

public class Workspace extends ModelObject implements Serializable {

	private static final long serialVersionUID = -510311797646536730L;
	private String id;
    private String title;
//    private String statusId;
//    private String statusValue;
//    private String statusDisplay;
//    private String userId;
//    private String username;
//    private String displayName;
    private String lastAction;
    private long lastActionTime;

    private Whiteboards whiteboards;
    private Tasks tasks;
    private Discussions discussions;
//    private FileSystemObject filesAndFolders;
    private Members members;
    private WhatsNewInWorkspace whatsNew;
    private boolean isAllHeader;
    private boolean isRecentlyModifiedHeader;

    public Workspace(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        setLoaded();
    }
    
    public Workspace() throws IOException {
    	super(false);
    }

    public void parse(Object json) {
        JSONObject jsonObject = (JSONObject)json;
        id = jsonObject.optString("Id");
        title = jsonObject.optString("Title");
//        statusId = jsonObject.optString("StatusId");
//        statusValue = jsonObject.optString("StatusValue");
//        statusDisplay = jsonObject.optString("StatusDisplay");
//        userId = jsonObject.optString("UserId");
//        username = jsonObject.optString("Username");
//        displayName = jsonObject.optString("DisplayName");
        lastAction = jsonObject.optString("LastAction");
        lastActionTime = Long.parseLong(lastAction.substring(lastAction.indexOf('(') + 1,lastAction.indexOf('+')));
    }

    public Whiteboards getWhiteboards(boolean load) throws IOException {
        if (whiteboards == null) {
            whiteboards = new Whiteboards(this, load);
        }
        return whiteboards;
    }

    public boolean hasWhiteboards() {
        return whiteboards != null;
    }

    public Tasks getTasks(boolean load) throws IOException {
        if (tasks == null) {
            tasks = new Tasks(this, load);
        }
        return tasks;
    }

    public boolean hasTasks() {
        return tasks != null;
    }

    public Discussions getDiscussions(boolean load) throws IOException {
        if (discussions == null) {
            discussions = new Discussions(this, load);
        }
        return discussions;
    }

    public boolean hasDiscussions() {
        return discussions != null;
    }

    public Members getMembers(boolean load) throws IOException {
        if (members == null) {
            members = new Members(this, load);
        }
        return members;
    }

    public boolean hasMembers() {
        return members != null;
    }

    public WhatsNewInWorkspace getWhatsNewInWorkspace(boolean load) throws IOException {
        if (whatsNew == null) {
            whatsNew = new WhatsNewInWorkspace(this, load);
        }
        return whatsNew;
    }

    public boolean hasWhatsNew() {
        return whatsNew != null;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public long getLastActionTime() {
        return lastActionTime;
    }

	public boolean isAllHeader() {
		return isAllHeader;
	}

	public void setAllHeader(boolean isAllHeader) {
		this.isAllHeader = isAllHeader;
	}

	public boolean isRecentlyModifiedHeader() {
		return isRecentlyModifiedHeader;
	}

	public void setRecentlyModifiedHeader(boolean isRecentlyModifiedHeader) {
		this.isRecentlyModifiedHeader = isRecentlyModifiedHeader;
	}
}
