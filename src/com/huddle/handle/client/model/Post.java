package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

public class Post extends ModelObject {

    private String authorId;
    private String authorName;
    private String content;
    private Date created;
    private String id;

    public Post(Object json) throws IOException {
        super(json);
    }

    public void refresh() {
        throw new RuntimeException("A post cannot be refreshed individually.");
    }

    public void parse(Object json) {
        JSONObject jsonObject = (JSONObject)json;
        authorId = jsonObject.optString("AuthorId");
        authorName = jsonObject.optString("AuthorName");
        content = jsonObject.optString("Content");
        created = new Date(jsonObject.optJSONObject("Created"));
        id = jsonObject.optString("Id");
    }

    public String getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getContent() {
        return content;
    }

    public Date getCreated() {
        return created;
    }

    public String getId() {
        return id;
    }
}
