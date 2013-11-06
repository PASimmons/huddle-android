package com.huddle.handle.client.model;

import java.io.IOException;
import java.util.Calendar;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class Task extends ModelObject {

    public static final String[] statuses = new String[]{"Overdue","Upcoming","Complete"};

    private Vector<TaskUser> assignedTo;
    private TaskAttachment attachment;
    private Vector<TaskUser> completedBy;
    private Date completedDate;
    private Date createdDate;
    private String description;
    private Date dueDate;
    private String id;
    private String isApproval;
    private String status;
    private String title;
    private String uri;
    private String workspaceId;
    private String workspaceName;

    public Task(Workspace parentWorkspace, Object json) throws IOException {
        super(parentWorkspace, json);
    }

    public void refresh() {
        throw new RuntimeException("A task cannot be refreshed individually.");
    }

    public void parse(Object json) throws IOException {
        JSONObject jsonObject = (JSONObject) json;
        JSONObject assignedToObject = jsonObject.optJSONObject("AssignedTo");
        if (assignedToObject != null) {
            JSONArray users = assignedToObject.optJSONArray("Users");
            if (users != null) {
                assignedTo = new Vector<TaskUser>(users.length());
                for (int i=0;i<users.length();i++) {
                    assignedTo.addElement(new TaskUser(users.optJSONObject(i)));
                }
            }
        }
        attachment = new TaskAttachment(jsonObject.optJSONObject("Attachment"));

        JSONArray completeByArray = jsonObject.optJSONArray("CompletedBy");
        if (completeByArray != null) {
            completedBy = new Vector<TaskUser>(completeByArray.length());
            for (int i=0;i<completeByArray.length();i++) {
                completedBy.addElement(new TaskUser(completeByArray.optJSONObject(i)));
            }
        }
        completedDate = new Date(jsonObject.optJSONObject("CompletedDate"));
        createdDate = new Date(jsonObject.optJSONObject("CreatedDate"));
        description = jsonObject.optString("Description");
        dueDate = new Date(jsonObject.optJSONObject("DueDate"));
        id = jsonObject.optString("Id");
        isApproval = jsonObject.optString("IsApproval");
        status = jsonObject.optString("Status");
        title = jsonObject.optString("Title");
        uri = jsonObject.optString("Uri");
        workspaceId = jsonObject.optString("WorkspaceId");
        workspaceName = jsonObject.optString("WorkspaceName");

    }

    public boolean markComplete() throws IOException {
        JSONObject response = Model.getJSONObject("POST", "https://api.huddle.net/v1/json/tasks/" + id, "{\"Complete\":true}");
        if ("true".equals(response.optString("Success"))) {
            status = statuses[2];
            return true;
        }
        return false;
    }

    public boolean markIncomplete() throws IOException {
        JSONObject response = Model.getJSONObject("POST", "https://api.huddle.net/v1/json/tasks/" + id, "{\"Complete\":false}");
        if ("true".equals(response.optString("Success"))) {

            Calendar now = Calendar.getInstance();
            now.setTime(new java.util.Date());
            Calendar taskDueDate = Calendar.getInstance();
            taskDueDate.set(Calendar.YEAR, Integer.parseInt(dueDate.getYear()));
            taskDueDate.set(Calendar.MONTH, Integer.parseInt(dueDate.getMonth()) - 1);
            taskDueDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dueDate.getDay()));
            taskDueDate.set(Calendar.HOUR_OF_DAY, Integer.parseInt(dueDate.getHour()));
            taskDueDate.set(Calendar.MINUTE, Integer.parseInt(dueDate.getMinute()));

            if (now.before(taskDueDate)) {
                status = statuses[1];
            } else {
                status = statuses[0];
            }
            return true;
        }
        return false;
    }

    /**
     * @return list of TaskUsers
     */
    public Vector<TaskUser> getAssignedTo() {
        return assignedTo;
    }

    public TaskAttachment getAttachment() {
        return attachment;
    }

    /**
     * @return list of TaskUsers
     */
    public Vector<TaskUser> getCompletedBy() {
        return completedBy;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public String getId() {
        return id;
    }

    public String getIsApproval() {
        return isApproval;
    }

    public String getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getUri() {
        return uri;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

	public void setCompletedDate(Date completedDate) {
		this.completedDate = completedDate;
	}
}
