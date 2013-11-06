package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

public class Whiteboard extends ModelObject {

    private Author author;
    private String title;
    private String description;
    private String createdDate;
    private String id;
    private String uri;
    private Lock lock;
    private WhiteboardContent content;

    public Whiteboard(Workspace parentWorkspace, JSONObject json) throws IOException {
        super(parentWorkspace, json);
    }

    public void parse(Object json) throws IOException {
        JSONObject jsonObject = (JSONObject)json;
        if (json == null) return;
        author = new Author(jsonObject.optJSONObject("author"));
        title = jsonObject.optString("title");
        description = jsonObject.optString("description");
        createdDate = jsonObject.optString("createdDate");
        id = jsonObject.optString("id");
        uri = jsonObject.optString("uri");
        JSONObject o = jsonObject.optJSONObject("lock");
        if (o.length() > 0) {
            lock = new Lock(o);
        }
    }

    public void refresh() throws IOException {
       getParentWorkspace().getWhiteboards(true);
       setLoaded();
    }

    public WhiteboardContent getContent(boolean load) throws IOException {
        if (content == null) {
            content = new WhiteboardContent(this, getParentWorkspace(), load);
        }
        return content;
    }

    public boolean hasContent() {
        return content != null;
    }

    public Author getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public Lock getLock() {
        return lock;
    }
}
