package com.cannontech.common.dr.program.setup.model;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(value={ "groupName"}, allowGetters= true, ignoreUnknown = true)
public class ProgramGroup implements DBPersistentConverter<LMProgramDirectGroup> {

    private Integer groupId;
    private String groupName;
    private Integer groupOrder;
    private PaoType type;

    public Integer getGroupId() {
        return groupId;
    }
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }
    public Integer getGroupOrder() {
        return groupOrder;
    }
    public void setGroupOrder(Integer groupOrder) {
        this.groupOrder = groupOrder;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    public PaoType getType() {
        return type;
    }
    public void setType(PaoType type) {
        this.type = type;
    }

    @Override
    public void buildModel(LMProgramDirectGroup directGroup) {
        setGroupId(directGroup.getLmGroupDeviceID());
        setGroupOrder(directGroup.getGroupOrder());
    }

    @Override
    public void buildDBPersistent(LMProgramDirectGroup group) {
        group.setLmGroupDeviceID(getGroupId());
    }

}
