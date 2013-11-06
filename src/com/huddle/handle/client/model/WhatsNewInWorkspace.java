package com.huddle.handle.client.model;

import java.io.IOException;

import com.huddle.handle.client.Model;

public class WhatsNewInWorkspace extends WhatsNew {

    public WhatsNewInWorkspace(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public void refresh() throws IOException {
        parse(Model.getJSON("GET", "https://api.huddle.net/v1/json/workspaces/" + getParentWorkspace().getId() + "/whats-new"));
        setLoaded();
    }

}
