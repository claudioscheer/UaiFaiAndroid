package model;

import java.util.Date;

public class UserModel {

    private String _id;
    private Date createdAt;
    private String googleUserName;
    private String googleUserEmail;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getGoogleUserName() {
        return googleUserName;
    }

    public void setGoogleUserName(String googleUserName) {
        this.googleUserName = googleUserName;
    }

    public String getGoogleUserEmail() {
        return googleUserEmail;
    }

    public void setGoogleUserEmail(String googleUserEmail) {
        this.googleUserEmail = googleUserEmail;
    }
}
