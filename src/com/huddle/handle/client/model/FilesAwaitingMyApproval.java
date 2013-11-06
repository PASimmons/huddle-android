package com.huddle.handle.client.model;

import java.io.IOException;

import com.huddle.handle.client.Model;

public class FilesAwaitingMyApproval extends MyFilesAwaitingApproval {

    public FilesAwaitingMyApproval(boolean load) throws IOException {
        super(load);
    }

    public FilesAwaitingMyApproval(Object json) throws IOException {
        super(json);
    }

    public void refresh() throws IOException {
        parse(Model.getJSONObject("GET", "https://api.huddle.net/v1/json/files/query?view=files-awaiting-my-approval"));
        setLoaded();
    }

}
