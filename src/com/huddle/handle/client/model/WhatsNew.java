package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class WhatsNew extends ModelObject {

    private Vector<WhatsNewItem> items;
    private WhatsNewItem whatsNewItem;

    public WhatsNew(boolean load) throws IOException {
        super(load);
    }

    public WhatsNew(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/whats-new?count=20"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        items = new Vector<WhatsNewItem>(data.length());
        for (int i=0;i<data.length();i++) {
            items.addElement(new WhatsNewItem(getParentWorkspace(), data.optJSONObject(i)));
        }
    }

    public Vector<WhatsNewItem> getWhatsNewItems() {
        return items;
    }

    public void selectWhatsNewItem(WhatsNewItem whatsNewItem) {
        this.whatsNewItem = whatsNewItem;
    }

    public WhatsNewItem getCurrentWhatsNewItem() {
        return whatsNewItem;
    }

    public int count() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }
}
