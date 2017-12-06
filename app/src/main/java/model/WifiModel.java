package model;

import java.util.Date;

public class WifiModel {

    private String _id;
    private Date createdAt;
    private Date updatedAt;
    private GeoJSONModel location;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public GeoJSONModel getLocation() {
        return location;
    }

    public void setLocation(GeoJSONModel location) {
        this.location = location;
    }
}
