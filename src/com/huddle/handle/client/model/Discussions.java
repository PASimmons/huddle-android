package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class Discussions extends ModelObject {

    private Vector<Discussion> items;
    private Discussion currentDiscussion;

    public Discussions(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public Discussions(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/workspaces/" + getParentWorkspace().getId() + "/discussions"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        items = new Vector<Discussion>(data.length());
        for (int i=0;i<data.length();i++) {
            items.addElement(new Discussion(getParentWorkspace(), data.optJSONObject(i)));
        }
    }

    public Discussion findDiscussion(String id) {
        for (int i=0;i<items.size();i++) {
            Discussion d = (Discussion)items.elementAt(i);
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    /**
     * Posts the discussion and then refreshes entire discussion list.
     * @param title
     * @param content
     * @return
     */
    public boolean createDiscussion(String title, String content) throws IOException {

        String request = "{\"Data\":{" +
                             "\"Title\":\"" + escape(title) + "\"," +
                             "\"Text\":\"" + escape(content) + "\"," +
                             "\"DontNotifyUsers\":true}" +
                          "}";

        JSONObject response = Model.getJSONObject("POST",
                                               "https://api.huddle.net/v1/json/workspaces/" + getParentWorkspace().getId() + "/discussions/create",
                                               request
                                               );

        if ("true".equals(response.optString("Success"))) {
            refresh();
            return true;
        }
        return false;
    }

    public Vector<Discussion> getDiscussions() {
        return items;
    }

    public void selectDiscussion(Discussion discussion) {
        this.currentDiscussion = discussion;
    }

    public Discussion getCurrentDiscussion() {
        return currentDiscussion;
    }

}
