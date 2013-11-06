package com.huddle.handle.client.model;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import com.huddle.handle.client.Model;

public class File {

    private Date approvalDueDate;
    private Vector<Approval> approvals;
    private Date created;
    private String createdByUserDisplayName;
    private String createdByUserId;
    private String description;
    private String fileName;
    private String id;
    private String lastEditedByUserDisplayName;
    private String lastEditedByUserId;
    private Date lastUpdated;
    private String mimeType;
    private String size;
    private String title;
    private String url;
    private String version;
    private LockStatus lockStatus;

    private FileSystemObject parentFSO;

    public File(FileSystemObject parentFSO, boolean load) throws IOException {
        this.parentFSO = parentFSO;
        if (load) {
            refresh();
        }
    }

    public File(JSONObject data) throws IOException {
        parse(data);
    }

    public void refresh() throws IOException {
        parse(Model.getJSONObject("GET", "https://api.huddle.net/v1/json/files/" + parentFSO.getId()));
    }

    public void parse(JSONObject json) throws IOException {

        JSONObject data = json.optJSONObject("Data");

        approvalDueDate = new Date(data.optJSONObject("ApprovalDueDate"));

        JSONArray approvalsArray = data.optJSONArray("Approvals");
        if (approvalsArray != null) {
            approvals = new Vector<Approval>(approvalsArray.length());

            for (int i=0;i<approvalsArray.length();i++) {
                approvals.addElement(new Approval(approvalsArray.optJSONObject(i)));
            }
        }

        created = new Date(data.optJSONObject("Created"));
        createdByUserDisplayName = data.optString("CreatedByUserDisplayName");
        createdByUserId = data.optString("CreatedByUserId");
        description = data.optString("Description");
        fileName = data.optString("FileName");
        id = data.optString("Id");
        lastEditedByUserDisplayName = data.optString("LastEditedByUserDisplayName");
        lastEditedByUserId = data.optString("LastEditedByUserId");
        lastUpdated = new Date(data.optJSONObject("LastUpdated"));
        mimeType = data.optString("MimeType");
        size = data.optString("Size");
        title = data.optString("Title");
        url = data.optString("Url");
        version = data.optString("Version");

        lockStatus = new LockStatus(json.optJSONObject("LockStatus"));
    }

    public Date getApprovalDueDate() {
        return approvalDueDate;
    }

    public Vector<Approval> getApprovals() {
        return approvals;
    }

    public Date getCreated() {
        return created;
    }

    public String getCreatedByUserDisplayName() {
        return createdByUserDisplayName;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public String getDescription() {
        return description;
    }

    public String getFileName() {
        return fileName;
    }

    public String getId() {
        return id;
    }

    public String getLastEditedByUserDisplayName() {
        return lastEditedByUserDisplayName;
    }

    public String getLastEditedByUserId() {
        return lastEditedByUserId;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getSize() {
        return size;
    }

    public String getSizeInK() {
        return (Long.parseLong(size) / 1024) + "k";
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public LockStatus getLockStatus() {
        return lockStatus;
    }

    public FileSystemObject getParentFSO() {
        return parentFSO;
    }


    public static boolean uploadFile(FileInputStream is, String title, String description,
            String name, String folderId, long length) throws IOException {

        byte[] file = null;
        try {
        	ByteArrayOutputStream bos = new ByteArrayOutputStream();
            file = new byte[(int)length];
            bos.write(is.read(file));
            
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
        	is.close();
        }

        String boundary = "--633524921018662935";
        String newLine = "\r\n";
        StringBuffer sb = new StringBuffer();
        sb.append(boundary).append(newLine).append("Content-Disposition: form-data; name=\"folderid\"").append(newLine).append(newLine).append(folderId).append(newLine)
        .append(boundary).append(newLine).append("Content-Disposition: form-data; name=\"title\"").append(newLine).append(newLine).append(title).append(newLine)
        .append(boundary).append(newLine).append("Content-Disposition: form-data; name=\"description\"").append(newLine).append(newLine).append(description).append(newLine)
        .append(boundary).append(newLine).append("Content-Disposition: form-data; name=\"my file\"; filename=\"").append(name).append("\" Content-Type: ").append(getUploadMimeType(name)).append(";").append(newLine).append(newLine);

        String[][] params = new String[][]{
                { "Content-Type",  "multipart/form-data;boundary=633524921018662935"}
        };
        JSONObject response = Model.uploadFile("https://api.huddle.net/v1/json/files/upload", "POST", sb.toString(), params, file);
        return "true".equals(response.optString("Success"));
    }

    public static String getUploadMimeType(String fullPath) {
        return MimeTypeExtensionMappings.getMimeTypeForExtension(fullPath.substring(fullPath.lastIndexOf('.') + 1));

    }

}
