package com.huddle.handle.client.model;

import java.io.IOException;

import android.content.Context;

public abstract class Document extends ModelObject {

    public Document(boolean load, Context context) throws IOException {
        super(load);
    }

    public Document(Object json) throws IOException {
        super(json);
    }

    public Document(Workspace parentWorkspace, boolean load) throws IOException {
        super(parentWorkspace, load);
    }

    public Document(Workspace parentWorkspace, Object json) throws IOException {
        super(parentWorkspace, json);
    }

    public abstract byte[] getFileContent() throws IOException;

    public abstract String getFileName() throws IOException;

    public abstract boolean isViewableCheckRequiresLoading() throws IOException;

}
