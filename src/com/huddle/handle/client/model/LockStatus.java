package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

public class LockStatus extends ModelObject {

    private String lockHolder;
    private String lockHolderId;
    private Date date;

    public LockStatus(Object json) throws IOException {
        super(json);
    }

    public void refresh() {
        throw new RuntimeException("Refresh may not be called on this object.");
    }

    public void parse(Object json) {
        if (json == null) return;
        JSONObject jsonObject = (JSONObject)json;
        lockHolder = jsonObject.optString("LockHolder");
        lockHolderId = jsonObject.optString("LockHolderId");
        date = new Date(jsonObject.optJSONObject("Date"));
    }

    public String getLockHolder() {
        return lockHolder;
    }

    public String getLockHolderId() {
        return lockHolderId;
    }

    public Date getDate() {
        return date;
    }
}
