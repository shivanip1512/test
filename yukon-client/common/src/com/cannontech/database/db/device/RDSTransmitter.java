package com.cannontech.database.db.device;

import java.sql.SQLException;

import com.cannontech.database.db.DBPersistent;

public class RDSTransmitter extends DBPersistent {
    private final String RDS_TRANSMITTER_TABLE_NAME = "RDSTransmitter";
    private final String PAOBJECTID = "PAObjectId";
    private final String SITE_ADDRESS = "SiteAddress";
    private final String ENCODER_ADDRESS = "EncoderAddress";
    private final String TRANSMIT_SPEED = "TransmitSpeed";
    private final String GROUP_TYPE = "GroupType";
    
    private Integer paobjectId = null;
    private Integer siteAddress = null;
    private Integer encoderAddress = null;
    private Double transmitSpeed = null;
    private String groupType = null;
    
    public RDSTransmitter() {
        super();
    }
    
    public RDSTransmitter(Integer paobjectId){
        super();
        this.paobjectId = paobjectId;
    }
    
    @Override
    public void add() throws SQLException {
        Object[] valuesToAdd = {getPaobjectId(), getSiteAddress(), getEncoderAddress(), getTransmitSpeed(), getGroupType()};
        add(RDS_TRANSMITTER_TABLE_NAME, valuesToAdd);
    }

    @Override
    public void delete() throws SQLException {
        delete(RDS_TRANSMITTER_TABLE_NAME, PAOBJECTID, paobjectId);
    }

    @Override
    public void retrieve() throws SQLException {
        String[] selectColumns = {SITE_ADDRESS, ENCODER_ADDRESS, TRANSMIT_SPEED, GROUP_TYPE};
        String[] constraintColumns = {PAOBJECTID};
        Object[] constraintValues = {getPaobjectId()};
        
        Object[] results = retrieve(selectColumns, RDS_TRANSMITTER_TABLE_NAME, constraintColumns, constraintValues);
         
        if(results.length == selectColumns.length){
            setSiteAddress((Integer)results[0]);
            setEncoderAddress((Integer)results[1]);
            setTransmitSpeed((Double)results[2]);
            setGroupType((String)results[3]);
        }
    }

    @Override
    public void update() throws SQLException {
        String[] updateColumns = {SITE_ADDRESS, ENCODER_ADDRESS, TRANSMIT_SPEED, GROUP_TYPE};
        Object[] updateValues = {siteAddress,  encoderAddress,  transmitSpeed, groupType};
        String[] constraintColumns = {PAOBJECTID};
        Object[] constraintValues = {getPaobjectId()};
        
        update(RDS_TRANSMITTER_TABLE_NAME, updateColumns, updateValues, constraintColumns, constraintValues);
    }
    
    public Integer getPaobjectId() {
        return paobjectId;
    }

    public void setPaobjectId(Integer paobjectId) {
        this.paobjectId = paobjectId;
    }
    
    
    public Integer getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(Integer siteAddress) {
        this.siteAddress = siteAddress;
    }

    public Integer getEncoderAddress() {
        return encoderAddress;
    }

    public void setEncoderAddress(Integer encoderAddress) {
        this.encoderAddress = encoderAddress;
    }

    public Double getTransmitSpeed() {
        return transmitSpeed;
    }

    public void setTransmitSpeed(Double transmitSpeed) {
        this.transmitSpeed = transmitSpeed;
    }

    public String getGroupType() {
        return groupType;
    }
    
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}
