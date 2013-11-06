package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class Workspaces extends ModelObject {

    private Vector<Workspace> items;

    public Workspaces(boolean load) throws IOException {
        super(load);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/workspaces"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray data = (JSONArray)json;
        items = new Vector<Workspace>(data.length());
        for (int i=0;i<data.length();i++) {
            items.addElement(new Workspace(data.optJSONObject(i)));
        }
    }

    public Vector<Workspace> getWorkspaceItems() {
        return items;
    }

    public Workspace findWorkspace(String id) {
        for (int i=0;i<items.size();i++) {
            Workspace ws = (Workspace)items.elementAt(i);
            if (ws.getId().equals(id)) return ws;
        }
        return null;
    }

    public int count() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    public Workspace[] getRecentlyModifiedWorkspaces() {
        Workspace[] a = new Workspace[items.size()];
        for (int i=0;i<items.size();i++) {
            a[i] = (Workspace)items.elementAt(i);
        }
        Arrays.sort(a, new RecentlyModifiedComparator());
        return a;
    }

    public Workspace[] getOrderedWorkspaceList() {
        Workspace[] a = new Workspace[items.size()];
        for (int i=0;i<items.size();i++) {
            a[i] = (Workspace)items.elementAt(i);
        }
        Arrays.sort(a, new AlphaComparator());
        return a;
    }

    private static class RecentlyModifiedComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            Workspace ws1 = (Workspace)o1;
            Workspace ws2 = (Workspace)o2;

            /* Current date format is:
             *
             *     Date(1274201110000+0100)
             *
             *  which is the time in milliseconds + offest embedded in a text wrapper.
             */

            if(ws1.getLastActionTime() > ws2.getLastActionTime()) {
                return -1;
            } else if (ws1.getLastActionTime() == ws2.getLastActionTime()) {
                return 0;
            }
            return 1;
        }
    }

    private static class AlphaComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            Workspace ws1 = (Workspace)o1;
            Workspace ws2 = (Workspace)o2;
            return ws1.getTitle().compareTo(ws2.getTitle());
        }
    }
}
