package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class MyFilesAwaitingApproval extends ModelObject {

    protected Vector<FileApproval> items;

    public MyFilesAwaitingApproval(boolean load) throws IOException {
        super(load);
    }

    public MyFilesAwaitingApproval(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSONObject("GET", "https://api.huddle.net/v1/json/files/query?view=my-files-awaiting-approval"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONObject jsonObject = (JSONObject)json;
        items = new Vector<FileApproval>();
        JSONArray data = jsonObject.optJSONArray("Data");
        for (int i = 0; i < data.length(); i++) {
            items.addElement(new FileApproval(data.optJSONObject(i)));
        }
    }

    public boolean hasApprovals() {
        return items != null || items.size() == 0;
    }

    public Vector<FileApproval> getApprovals() {
        return items;
    }

    public int count() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public Vector<FileApproval> forWorkspace(String workspaceId) {
        Vector<FileApproval> aps = new Vector<FileApproval>();

        for (int i = 0; i < items.size(); i++) {
            FileApproval fa = (FileApproval)items.elementAt(i);
            if (fa.getWorkspaceId().equals(workspaceId)) {
                aps.addElement(fa);
            }
        }
        return aps;
    }
}
