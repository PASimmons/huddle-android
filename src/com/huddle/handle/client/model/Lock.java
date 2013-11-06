package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

public class Lock extends ModelObject {

    private Author lockHolder;
    private String lockDate;

    public Lock(Object json) throws IOException {
        super(json);
    }

    public void parse(Object json) {
        JSONObject jsonObject = (JSONObject)json;
        lockHolder = new Author(jsonObject.optJSONObject("lockHolder"));
        lockDate = jsonObject.optString("lockDate");
    }

    public void refresh() {
        throw new RuntimeException("Refresh may not be called on this object");
    }

    public Author getLockHolder() {
        return lockHolder;
    }

    public String getLockDate() {
        return lockDate;
    }
}
