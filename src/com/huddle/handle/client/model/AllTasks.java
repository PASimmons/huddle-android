package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class AllTasks extends Tasks {

    public AllTasks(boolean load) throws IOException {
        super((Workspace)null, load);
    }

    public AllTasks(JSONArray data) throws IOException {
        super(data);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/tasks/?status=all"));
        setLoaded();
    }


}
