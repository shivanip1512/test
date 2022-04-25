package com.cannontech.common.device.terminal.model;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.device.port.DBPersistentConverter;
import com.cannontech.common.dr.setup.LMDto;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.IEDBase;
import com.cannontech.database.db.device.DeviceIED;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXISTING_PROPERTY, visible = true)
@JsonSubTypes({
        @Type(value = PagingTapTerminal.class, name = "TAPTERMINAL"),
        @Type(value = SNPPTerminal.class, name = "SNPP_TERMINAL"),
        @Type(value = TNPPTerminal.class, name = "TNPP_TERMINAL"),
        @Type(value = WCTPTerminal.class, name = "WCTP_TERMINAL"),
})
public class TerminalBase<T extends IEDBase> implements DBPersistentConverter<T> {
    private Integer id;
    private String name;
    private PaoType type;
    private boolean enabled;
    private String password = StringUtils.EMPTY;
    private LMDto commChannel;

    public TerminalBase() {
        super();
    }

    public TerminalBase(Integer id, String name, PaoType type, boolean enabled, String password, LMDto commChannel) {
        super();
        this.id = id;
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.password = password;
        this.commChannel = commChannel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LMDto getCommChannel() {
        return commChannel;
    }

    public void setCommChannel(LMDto commChannel) {
        this.commChannel = commChannel;
    }

    @Override
    public void buildModel(T iedBase) {
        setId(iedBase.getPAObjectID());
        setName(iedBase.getPAOName());
        setType(iedBase.getPaoType());
        setEnabled(!iedBase.isDisabled());
        if (getType() != PaoType.SNPP_TERMINAL && getType() != PaoType.WCTP_TERMINAL) {
            setPassword(iedBase.getDeviceIED().getPassword());
        }
        LMDto commChannel = new LMDto();
        commChannel.setId(iedBase.getDeviceDirectCommSettings().getPortID());
        setCommChannel(commChannel);
    }

    @Override
    public void buildDBPersistent(T iedBase) {
        DeviceIED deviceIED = iedBase.getDeviceIED();
        deviceIED.setDeviceID(getId());
        if (getType() != PaoType.SNPP_TERMINAL && getType() != PaoType.WCTP_TERMINAL) {
            deviceIED.setPassword(getPassword());
        } else {
            deviceIED.setPassword("(none)");
        }
        // Tap Terminals cannot be slaves like some IED meters
        deviceIED.setSlaveAddress("Master");
        iedBase.setDeviceIED(deviceIED);
        iedBase.setDisabled(!isEnabled());
        iedBase.setPAOName(getName());
        iedBase.getDeviceDirectCommSettings().setPortID(getCommChannel().getId());
        iedBase.getDeviceDirectCommSettings().setDeviceID(getId());
    }
}