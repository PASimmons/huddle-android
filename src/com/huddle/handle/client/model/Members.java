package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;

import com.huddle.handle.client.Model;

public class Members extends ModelObject {

    private Vector<Member> items;
    private Member currentMember;

    public Members(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public Members(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v2/workspaces/" + getParentWorkspace().getId() + "/members"));
        setLoaded();
    }

    public void parse(Object json) throws IOException {
        JSONArray jsonArray = (JSONArray)json;
        items = new Vector<Member>(jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            items.addElement(new Member(getParentWorkspace(), jsonArray.optJSONObject(i)));
        }
    }

    public Vector<Member> getMembers() {
        return items;
    }

    public void selectMember(Member member) {
        this.currentMember = member;
    }

    public Member getCurrentMember() {
        return currentMember;
    }

    public Member findMember(String id) {
        for (int i = 0; items != null && i < items.size(); i++) {
            Member m = (Member)items.elementAt(i);
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }
}
