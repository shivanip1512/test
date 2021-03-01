package com.cannontech.web.tools.points.model;

import java.util.Set;

import com.cannontech.common.device.model.DevicePointDetail;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PointInfoModel {

    private int pointId;
    private String name;
    private PointIdentifier pointIdentifier;
    private int stateGroupId;
    private Set<BuiltInAttribute> attributes;

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PointIdentifier getPointIdentifier() {
        return pointIdentifier;
    }

    public void setPointIdentifier(PointIdentifier pointIdentifier) {
        this.pointIdentifier = pointIdentifier;
    }

    public int getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(int stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public Set<BuiltInAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<BuiltInAttribute> attributes) {
        this.attributes = attributes;
    }
    
    public static PointInfoModel of(DevicePointDetail devicePointDetail, Set<BuiltInAttribute> attributes) {
        PointInfoModel pointInfoModel = new PointInfoModel();
        pointInfoModel.setPointId(devicePointDetail.getPointId());
        pointInfoModel.setName(devicePointDetail.getPointName());
        pointInfoModel.setPointIdentifier(devicePointDetail.getPaoPointIdentifier().getPointIdentifier());
        pointInfoModel.setStateGroupId(devicePointDetail.getStateGroupId());
        pointInfoModel.setAttributes(attributes);
        return pointInfoModel;
    }
}
