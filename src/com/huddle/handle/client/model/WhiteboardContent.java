package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class WhiteboardContent extends ModelObject {

    private Whiteboard whiteboard;

    private Vector<?> comments;
    private String contents;
    private Author lastModifiedBy;
    private String lastUpdated;

    public WhiteboardContent(Whiteboard whiteboard, Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace,false);
        this.whiteboard = whiteboard;
        if (load) {
            refresh();
            setLoaded();
        }
    }

    public WhiteboardContent(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSONObject("GET", whiteboard.getUri()));
        setLoaded();
    }

    public void parse(Object json) {
        if (json == null) return;
        JSONObject jsonObject = (JSONObject)json;
        contents = jsonObject.optString("contents");
        lastModifiedBy = new Author(jsonObject.optJSONObject("lastModifiedBy"));
        lastUpdated = jsonObject.optString("lastUpdated");
    }

    public Whiteboard getWhiteboard() {
        return whiteboard;
    }

    public Vector<?> getComments() {
        return comments;
    }

    public String getContents() {
        return contents;
    }

    public Author getLastModifiedBy() {
        return lastModifiedBy;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

}
