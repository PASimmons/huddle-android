package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class Tasks extends ModelObject {

    private Vector<Task> items;
    private int overdueCount;

    public Tasks(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public Tasks(Object json) throws IOException {
        super(json);
    }

    public Tasks(Workspace parentWorkspace, Object json) throws IOException {
        super(parentWorkspace, json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/tasks/?workspaceId=" + getParentWorkspace().getId()));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        items = new Vector<Task>(data.length());
        for (int i=0;i<data.length();i++) {
            Task t = new Task(getParentWorkspace(), data.optJSONObject(i));
            if (Task.statuses[0].equals(t.getStatus())){
                overdueCount++;
            }
            items.addElement(t);
        }
    }

    public Task findTask(String id) {
        for (int i=0;i<items.size();i++) {
            Task d = (Task)items.elementAt(i);
            if (d.getId().equals(id)) return d;
        }
        return null;
    }

    public int countOverdue() {
        return overdueCount;
    }

    public int count() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public Vector<Task> getTasks() {
        return items;
    }

}
