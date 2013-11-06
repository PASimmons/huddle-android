package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;


public class FileSystemObject extends Document {

    public static boolean halt;

    private String id;
    private String title;
    private String type;
    private String mimeType;
    private File fileDetails;

    private Vector<FileSystemObject> children;
    private FileSystemObject parentFolder;

    /**
     * Default constructor is called for workspace file/folder list.
     */
    public FileSystemObject(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public FileSystemObject(String id, String title) throws IOException {
        super(null, false);
        this.id = id;
        this.title = title;
        type = "File";
    }

    public FileSystemObject(Workspace parentWorkspace, Object json, FileSystemObject parentFolder) throws IOException {
        super(parentWorkspace, json);
        this.parentFolder = parentFolder;
    }

    public void refresh() throws IOException {
        if (isWorkspaceFolder()) { // workspace level
            parseChildren(Model.getJSON("GET", "https://api.huddle.net/v1/json/workspaces/" + getParentWorkspace().getId() + "/items"));
        } else if (isFolder()) { // folder
            parseChildren(Model.getJSON("GET", "https://api.huddle.net/v1/json/folders/" + id + "/items"));
        } else { // file
            parentFolder.refresh();
        }
        setLoaded();
    }

    public void parse(Object json) {
        JSONObject jsonObject = (JSONObject)json;
        if (json == null) return;
        id = jsonObject.optString("Id");
        title = jsonObject.optString("Title");
        if (title != null) title = title.trim();
        type = jsonObject.optString("Type");
        mimeType = jsonObject.optString("MimeType");
    }

    public Vector<FileSystemObject> getChildren(boolean load) throws IOException {
        if (!isFolder()) throw new RuntimeException("You can't request children for a file.");
        if (load && !hasChildren()) {
            refresh();
        }
        return children;
    }

    public boolean hasChildren() {
        return children != null;
    }

    protected void parseChildren(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        children = new Vector<FileSystemObject>(data.length());
        for (int i=0;i<data.length();i++) {
            children.addElement(new FileSystemObject(getParentWorkspace(), data.optJSONObject(i), this));
        }
    }

    public boolean isFolder() {
        return type == null || "Folder".equals(type);
    }

    public boolean isWorkspaceFolder() {
        return id == null;
    }

    public File getFileDetails(boolean load) throws IOException {
        if (isFolder()) throw new RuntimeException("You can't file details for a folder.");
        if (fileDetails == null) {
            fileDetails = new File(this, load);
        }
        return fileDetails;
    }

    public boolean hasFileDetails() {
        return fileDetails != null && fileDetails.getId() != null;
    }

    public FileSystemObject getParentFolder() {
        return parentFolder;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }
    
    public String getMimeType() {
    	return mimeType;
    }

    public String getFileName() throws IOException {
        if (fileDetails == null) {
            fileDetails = new File(this, true);
        } else if (fileDetails.getId() == null) {
            fileDetails.refresh();
        }
        return fileDetails.getFileName();
    }

    public boolean isViewableCheckRequiresLoading() throws IOException {
        return getFileDetails(false).getId() == null;
    }

    public byte[] getFileContent() throws IOException {
        if (Long.parseLong(fileDetails.getSize()) > (500 * 1024)) {
            throw new ThatsTooBigException();
        }
        return Model.getImage("https://my.huddle.net/" + fileDetails.getUrl());
    }

}
