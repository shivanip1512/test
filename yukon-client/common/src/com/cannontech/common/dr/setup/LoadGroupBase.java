package com.cannontech.common.dr.setup;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.db.device.Device;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({ 
    @JsonSubTypes.Type(value = LoadGroupExpresscom.class, name = "LM_GROUP_EXPRESSCOMM"),
    @JsonSubTypes.Type(value = LoadGroupItron.class, name = "LM_GROUP_ITRON"),
    @JsonSubTypes.Type(value = LoadGroupDigiSep.class, name = "LM_GROUP_DIGI_SEP"),
    @JsonSubTypes.Type(value = LoadGroupEmetcon.class, name = "LM_GROUP_EMETCON"),
    @JsonSubTypes.Type(value = LoadGroupVersacom.class, name = "LM_GROUP_VERSACOM"),
    @JsonSubTypes.Type(value = LoadGroupEcobee.class, name = "LM_GROUP_ECOBEE"),
    @JsonSubTypes.Type(value = LoadGroupHoneywell.class, name = "LM_GROUP_HONEYWELL"),
    @JsonSubTypes.Type(value = LoadGroupNest.class, name = "LM_GROUP_NEST"),
    @JsonSubTypes.Type(value = LoadGroupDisconnect.class, name = "LM_GROUP_METER_DISCONNECT"),
    @JsonSubTypes.Type(value = LoadGroupMCT.class, name = "LM_GROUP_MCT"),
    @JsonSubTypes.Type(value = LoadGroupPoint.class, name = "LM_GROUP_POINT")
    })
@JsonIgnoreProperties(value={"id"}, allowGetters= true, ignoreUnknown = true)
public class LoadGroupBase<T extends LMGroup> implements LoadGroupSetupBase<T> {
    private Integer id;
    private String name;
    private PaoType type;
    private Double kWCapacity;
    private boolean disableGroup;
    private boolean disableControl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PaoType getType() {
        return type;
    }

    public void setType(PaoType type) {
        this.type = type;
    }

    public Double getkWCapacity() {
        return kWCapacity;
    }

    public void setkWCapacity(Double kWCapacity) {
        if (kWCapacity != null) {
            this.kWCapacity = new BigDecimal(kWCapacity).setScale(3, RoundingMode.HALF_DOWN).doubleValue();
        } else {
            this.kWCapacity = kWCapacity;
        }
    }

    public boolean isDisableGroup() {
        return disableGroup;
    }

    public void setDisableGroup(boolean disableGroup) {
        this.disableGroup = disableGroup;
    }

    public boolean isDisableControl() {
        return disableControl;
    }

    public void setDisableControl(boolean disableControl) {
        this.disableControl = disableControl;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public void buildModel(T loadGroup) {
        setName(loadGroup.getPAOName());
        setId(loadGroup.getPAObjectID());
        setType(loadGroup.getPaoType());
        setkWCapacity(loadGroup.getLmGroup().getKwCapacity());
        boolean disableControl = loadGroup.getDevice().getControlInhibit() == 'N' ? false : true;
        setDisableControl(disableControl);
        setDisableGroup(loadGroup.isDisabled());
    }

    @Override
    public void buildDBPersistent(T group) {
        // PAO settings
        group.setPAOName(getName());
        group.setDisabled(isDisableGroup());

        // Device settings
        Device lmDevice = DeviceFactory.createDevice(getType()).getDevice();
        char disableControl = isDisableControl() ? 'Y' : 'N';
        lmDevice.setControlInhibit(disableControl);
        lmDevice.setDeviceID(getId());
        group.setDevice(lmDevice);

        // Load Group settings
        com.cannontech.database.db.device.lm.LMGroup lmGroup = group.getLmGroup();
        lmGroup.setKwCapacity(getkWCapacity());
        group.setLmGroup(lmGroup);
    }
}
