package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class Discussion extends ModelObject {

    private String id;
    private Date created;
    private String createdByUserDisplayName;
    private String createdByUserId;

    private String lastPostAuthorDisplayName;
    private String lastPostAuthorId;
    private String lastPostId;
    private String lastUpdatedByUserDisplayName;
    private String lastUpdatedByUserId;
    private Date updated;

    private String postCount;
    private String title;

    private Posts posts;

    public Discussion (Workspace parentWorkspace, Object json) throws IOException {
        super(parentWorkspace, json);
    }

    public void parse(Object json) {
        if (json == null) return;
        JSONObject jsonObject = (JSONObject)json;
        created = new Date(jsonObject.optJSONObject("Created"));
        createdByUserDisplayName = jsonObject.optString("CreatedByUserDisplayName");
        createdByUserId = jsonObject.optString("CreatedByUserId");
        id = jsonObject.optString("Id");

        JSONObject lastPost = jsonObject.optJSONObject("LastPost");
        lastPostAuthorDisplayName = lastPost.optString("AuthorDisplayName");
        lastPostAuthorId = lastPost.optString("AuthorId");
        lastPostId = lastPost.optString("Id");
        lastUpdatedByUserDisplayName = lastPost.optString("LastUpdatedByUserDisplayName");
        lastUpdatedByUserId = lastPost.optString("LastUpdatedByUserId");
        updated = new Date(lastPost.optJSONObject("Updated"));

        postCount = jsonObject.optString("PostCount");
        title = jsonObject.optString("Title");
    }

    public Posts getPosts(boolean load) throws IOException {
        if (posts == null) {
            posts = new Posts(this, load);
        }
        return posts;
    }

    public void refresh() {
        throw new RuntimeException("You cannot refresh this object.");
    }

    public boolean createPost(String content) throws IOException {
        String request = "{\"Data\":{" +
                            "\"Text\":\"" + escape(content) + "\"}" +
                         "}";

        JSONObject response = Model.getJSONObject("POST",
                                  "https://api.huddle.net/v1/json/discussions/" + getId() + "/create",
                                  request
                                  );

        if ("true".equals(response.optString("Success"))) {
            posts.refresh();
            return true;
        }
        return false;
    }

    public boolean hasPosts() {
        return posts != null;
    }

    public String getId() {
        return id;
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedByUserDisplayName() {
        return createdByUserDisplayName;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public String getLastPostAuthorDisplayName() {
        return lastPostAuthorDisplayName;
    }

    public String getLastPostAuthorId() {
        return lastPostAuthorId;
    }

    public String getLastPostId() {
        return lastPostId;
    }

    public String getLastUpdatedByUserDisplayName() {
        return lastUpdatedByUserDisplayName;
    }

    public String getLastUpdatedByUserId() {
        return lastUpdatedByUserId;
    }

    public Date getUpdated() {
        return updated;
    }

    public int getPostCount() {
        return Integer.parseInt(postCount);
    }

    public String getTitle() {
        return title;
    }
}
