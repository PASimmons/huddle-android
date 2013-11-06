package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class Whiteboards extends ModelObject {

    private Vector<Whiteboard> items;
    private Whiteboard currentWhiteboard;

    public Whiteboards(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public Whiteboards(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v2/workspaces/" + getParentWorkspace().getId() + "/whiteboards"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        items = new Vector<Whiteboard>(data.length());
        for (int i=0;i<data.length();i++) {
            items.addElement(new Whiteboard(getParentWorkspace(), data.optJSONObject(i)));
        }
    }

    public Whiteboard findWhiteboard(String id) {
        for (int i=0;i<items.size();i++) {
            Whiteboard wb = (Whiteboard)items.elementAt(i);
            if (wb.getId().equals(id)) return wb;
        }
        return null;
    }

    public Vector<Whiteboard> getWhiteboards() {
        return items;
    }

    public void selectWhiteboard(Whiteboard whiteboard) {
        this.currentWhiteboard = whiteboard;
    }

    public Whiteboard getCurrentWhiteboard() {
        return currentWhiteboard;
    }
}
