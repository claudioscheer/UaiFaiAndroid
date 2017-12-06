package model;

import java.util.Date;

public class WifiSimpleDataSQLiteModel {

    private Integer id;
    private Date createdAt;
    private String key;
    private Integer power;
    private Double distanceToAccessPoint;
    private GeoJSONModel location;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Double getDistanceToAccessPoint() {
        return distanceToAccessPoint;
    }

    public void setDistanceToAccessPoint(Double distanceToAccessPoint) {
        this.distanceToAccessPoint = distanceToAccessPoint;
    }

    public GeoJSONModel getLocation() {
        return location;
    }

    public void setLocation(GeoJSONModel location) {
        this.location = location;
    }
}
