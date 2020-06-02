package com.cannontech.web.tools.points.model;

import org.apache.commons.lang3.BooleanUtils;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.db.point.Point;
import com.cannontech.web.editor.point.StaleData;
import com.cannontech.web.tools.points.service.impl.JsonDeserializePointTypeLookup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(value = { "pointId" }, allowGetters = true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(using = JsonDeserializePointTypeLookup.class)
public class PointBaseModel<T extends PointBase> extends LitePointModel implements DBPersistentConverter<T> {

    private Integer stateGroupId;
    private Boolean enable;
    private PointArchiveType archiveType;
    private Integer archiveInterval;
    private PointLogicalGroups timingGroup;
    private Boolean alarmsDisabled;

    private StaleData staleData;
    private PointAlarming alarming;

    public PointLogicalGroups getTimingGroup() {
        return timingGroup;
    }

    public void setTimingGroup(PointLogicalGroups timingGroup) {
        this.timingGroup = timingGroup;
    }

    public Integer getStateGroupId() {
        return stateGroupId;
    }

    public void setStateGroupId(Integer stateGroupId) {
        this.stateGroupId = stateGroupId;
    }

    public PointArchiveType getArchiveType() {
        return archiveType;
    }

    public void setArchiveType(PointArchiveType archiveType) {
        this.archiveType = archiveType;
    }

    public Integer getArchiveInterval() {
        return archiveInterval;
    }

    public void setArchiveInterval(Integer archiveInterval) {
        this.archiveInterval = archiveInterval;
    }

    public StaleData getStaleData() {
        return staleData;
    }

    public void setStaleData(StaleData staleData) {
        this.staleData = staleData;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Boolean getAlarmsDisabled() {
        return alarmsDisabled;
    }

    public void setAlarmsDisabled(Boolean alarmsDisabled) {
        this.alarmsDisabled = alarmsDisabled;
    }

    public PointAlarming getAlarming() {
        if (alarming == null) {
            alarming = new PointAlarming();
        }
        return alarming;
    }

    public void setAlarming(PointAlarming alarming) {
        this.alarming = alarming;
    }

    @Override
    public void buildModel(T point) {
        Point pt = point.getPoint();
        setPointId(pt.getPointID());
        setPaoId(pt.getPaoID());
        setPointType(pt.getPointTypeEnum());
        setPointName(pt.getPointName());
        setPointOffset(pt.getPointOffset());
        if (pt.getPointOffset() > 0) {
            setPhysicalOffset(true);
        }
        setStateGroupId(pt.getStateGroupID());
        setEnable(pt.getServiceFlag() == 'N' ? true : false );
        setArchiveType(pt.getArchiveType());
        setArchiveInterval(pt.getArchiveInterval());
        setTimingGroup(PointLogicalGroups.getLogicalGroupValue(pt.getLogicalGroup()));
        setAlarmsDisabled(pt.isAlarmsDisabled());
    }

    @Override
    public void buildDBPersistent(T point) {
        Point pt = point.getPoint();
        if (getPointId() != null) {
            pt.setPointID(getPointId());
        }
        if (getPointName() != null) {
            pt.setPointName(getPointName());
        }
        if (getPaoId() != null) {
            pt.setPaoID(getPaoId());
        }
        if (getPointOffset() != null) {
            pt.setPointOffset(getPointOffset());
        }
        if (getPointType() != null) {
            pt.setPointType(getPointType().getPointTypeString());
        }
        if (getStateGroupId() != null) {
            pt.setStateGroupID(getStateGroupId());
        }

        if (getEnable() != null) {
            pt.setServiceFlag(BooleanUtils.isFalse(getEnable()) ? 'Y' : 'N');
        }

        if (getArchiveType() != null) {
            pt.setArchiveType(getArchiveType());
        }

        if (getArchiveInterval() != null) {
            pt.setArchiveInterval(getArchiveInterval());
        }

        if (getTimingGroup() != null) {
            pt.setLogicalGroup(getTimingGroup().getDbValue());
        }
        if (getAlarmsDisabled() != null) {
            pt.setAlarmsDisabled(getAlarmsDisabled());
        }

    }

}
