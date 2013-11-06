package com.huddle.handle.client.model;

import java.io.IOException;

import org.json.JSONObject;

public class TaskUser extends ModelObject {

    private String id;
    private String name;
    private String team;
    private String teamId;

    public TaskUser(Object json) throws IOException {
        super(json);
    }

    public void parse(Object json) {
        if (json == null) return;
        JSONObject jsonObject = (JSONObject)json;
        id = jsonObject.optString("Id");
        name = jsonObject.optString("Name");
        team = jsonObject.optString("Team");
        teamId = jsonObject.optString("TeamId");
    }

    public void refresh() {
        throw new RuntimeException("A tasks user cannot be refreshed individually.");
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeam() {
        return team;
    }

    public String getTeamId() {
        return teamId;
    }
}
