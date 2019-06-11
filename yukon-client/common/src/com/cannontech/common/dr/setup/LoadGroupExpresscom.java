package com.cannontech.common.dr.setup;

import org.apache.commons.lang.StringUtils;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;

public class LoadGroupExpresscom extends LoadGroupBase {
    private Integer routeID = null;
    private String serialNumber = IlmDefines.NONE_ADDRESS_ID.toString();
    private Integer serviceProvider;
    private Integer geo;
    private Integer substation;
    private Integer feeder;
    private Integer zip;
    private Integer user;
    private Integer program;
    private Integer splinter;
    private String addressUsage = StringUtils.EMPTY;
    private String relayUsage = StringUtils.EMPTY;
    private Integer protocolPriority = 3;

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Integer getServiceProvider() {
        return serviceProvider;
    }

    public void setServiceProvider(Integer serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Integer getGeo() {
        return geo;
    }

    public void setGeo(Integer geo) {
        this.geo = geo;
    }

    public Integer getSubstation() {
        return substation;
    }

    public void setSubstation(Integer substation) {
        this.substation = substation;
    }

    public Integer getFeeder() {
        return feeder;
    }

    public void setFeeder(Integer feeder) {
        this.feeder = feeder;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getProgram() {
        return program;
    }

    public void setProgram(Integer program) {
        this.program = program;
    }

    public Integer getSplinter() {
        return splinter;
    }

    public void setSplinter(Integer splinter) {
        this.splinter = splinter;
    }

    public String getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(String addressUsage) {
        this.addressUsage = addressUsage;
    }

    public String getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(String relayUsage) {
        this.relayUsage = relayUsage;
    }

    public Integer getProtocolPriority() {
        return protocolPriority;
    }

    public void setProtocolPriority(Integer protocolPriority) {
        this.protocolPriority = protocolPriority;
    }

    @Override
    public void buildModel(LMGroup loadGroup) {
        // Set parent fields
        super.buildModel(loadGroup);

        // Set addressing fields
        setServiceProvider(((LMGroupExpressCom) loadGroup).getServiceProviderAddress().getAddress());
        setGeo(((LMGroupExpressCom) loadGroup).getGeoAddress().getAddress());
        setSubstation(((LMGroupExpressCom) loadGroup).getSubstationAddress().getAddress());
        setFeeder(((LMGroupExpressCom) loadGroup).getFeederAddress().getAddress());
        setProgram(((LMGroupExpressCom) loadGroup).getProgramAddress().getAddress());
        setZip(((LMGroupExpressCom) loadGroup).getZipCodeAddress().getAddress());
        setUser(((LMGroupExpressCom) loadGroup).getUserAddress().getAddress());
        setSplinter(((LMGroupExpressCom) loadGroup).getSplinterAddress().getAddress());

        // Set expresscom fields
        setRouteID(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getRouteID());
        setSerialNumber(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getSerialNumber());
        setAddressUsage(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getAddressUsage());
        setProtocolPriority(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getProtocolPriority());

    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set Addressing
        ((LMGroupExpressCom) group).setServiceProviderAddress(
            createAddress(getServiceProvider(), IlmDefines.TYPE_SERVICE));
        ((LMGroupExpressCom) group).setGeoAddress(createAddress(getGeo(), IlmDefines.TYPE_GEO));
        ((LMGroupExpressCom) group).setSubstationAddress(createAddress(getSubstation(), IlmDefines.TYPE_SUBSTATION));
        ((LMGroupExpressCom) group).setFeederAddress(createAddress(getFeeder(), IlmDefines.TYPE_FEEDER));
        ((LMGroupExpressCom) group).setProgramAddress(createAddress(getProgram(), IlmDefines.TYPE_PROGRAM));
        ((LMGroupExpressCom) group).setZipCodeAddress(createAddress(getZip(), IlmDefines.TYPE_ZIP));
        ((LMGroupExpressCom) group).setUserAddress(createAddress(getUser(), IlmDefines.TYPE_USER));
        ((LMGroupExpressCom) group).setSplinterAddress(createAddress(getSplinter(), IlmDefines.TYPE_SPLINTER));


        // Set Expresscom
        com.cannontech.database.db.device.lm.LMGroupExpressCom expresscom =
            ((LMGroupExpressCom) group).getLMGroupExpressComm();
        expresscom.setRouteID(getRouteID());
        expresscom.setSerialNumber(getSerialNumber());
        expresscom.setAddressUsage(getAddressUsage());
        expresscom.setRelayUsage(getRelayUsage());
        expresscom.setProtocolPriority(getProtocolPriority());
        ((LMGroupExpressCom) group).setLMGroupExpressComm(expresscom);
    }

    /**
     * Create addressing for passed addressing type
     */
    private LMGroupExpressComAddress createAddress(Integer address, String type) {
        if (address == null) {
            return com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS;
        }
        LMGroupExpressComAddress expresscomAddress = new LMGroupExpressComAddress(address, type);
        expresscomAddress.setAddress(address);
        return expresscomAddress;
    }
}