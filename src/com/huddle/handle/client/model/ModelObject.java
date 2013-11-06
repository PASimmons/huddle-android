package com.huddle.handle.client.model;

import java.io.IOException;

public abstract class ModelObject {

    private boolean loaded;
    protected Workspace parentWorkspace;

    public ModelObject(boolean load) throws IOException {
        if (load) {
            refresh();
            setLoaded();
        }
    }

    public ModelObject(Workspace parentWorkspace, boolean load) throws IOException {
        this.parentWorkspace = parentWorkspace;
        if (load) {
            refresh();
            setLoaded();
        }
    }
    
    public ModelObject(Workspace parentWorkspace) {
    	this.parentWorkspace = parentWorkspace;
    }

    public ModelObject(Object json) throws IOException {
        parse(json);
        setLoaded();
    }

    public ModelObject(Workspace parentWorkspace, Object json) throws IOException {
        this.parentWorkspace = parentWorkspace;
        parse(json);
        setLoaded();
    }

    public abstract void parse(Object json) throws IOException;

    public abstract void refresh() throws IOException;

    public void setLoaded() {
        loaded = true;
    }

    public boolean hasBeenLoaded(){
        return loaded;
    }

    public Workspace getParentWorkspace() {
        return parentWorkspace;
    }

    public static String escape(String s) {
        String temp = s.replaceAll("\"", "\\\"");
        temp = s.replaceAll("'", "\'");
        temp = s.replaceAll("\n", "\\n");
        return temp;
    }

}
