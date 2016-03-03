package org.registrator.community.dto.JSON;

import java.util.List;

/**
 * Created by Oleksiy on 02.01.2016.
 */
public class PolygonJSON {
    private String DT_RowId;
    private String resourceDescription;
    private String identifier;
    private String resourceType;
    private String date;
    private List<PointJSON> points;

    public String getResourceDescription() {
        return resourceDescription;
    }

    public void setResourceDescription(String resourceDescription) {
        this.resourceDescription = resourceDescription;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDT_RowId() {
        return DT_RowId;
    }

    public void setDT_RowId(String DT_RowId) {
        this.DT_RowId = DT_RowId;
    }

    public List<PointJSON> getPoints() {
        return points;
    }

    public void setPoints(List<PointJSON> points) {
        this.points = points;
    }
}
