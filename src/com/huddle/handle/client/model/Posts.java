package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class Posts extends ModelObject {

    private Vector<Post> items;
    private Discussion discussion;

    public Posts(Discussion discussion, boolean load) throws IOException {
        super(false);
        this.discussion = discussion;
        if (load) refresh();
    }

    public Posts(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET","https://api.huddle.net/v1/json/discussions/" + discussion.getId()));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        if (data == null) return;
        items = new Vector<Post>(data.length());
        for (int i=0;i<data.length();i++) {
            items.addElement(new Post(data.optJSONObject(i)));
        }
    }

    public Discussion getParentDiscussion() {
        return discussion;
    }

    public Vector<Post> getPostList() {
        return items;
    }
}
