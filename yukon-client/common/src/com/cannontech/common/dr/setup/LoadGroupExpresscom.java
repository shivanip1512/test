package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;

public class LoadGroupExpresscom extends LoadGroupBase {
    private Integer routeID;
    private String serialNumber;
    private Integer serviceProvider;
    private Integer geo;
    private Integer substation;
    private String feeder;
    private Integer zip;
    private Integer user;
    private Integer program;
    private Integer splinter;
    private List<AddressUsage> addressUsage;
    private List<Loads> relayUsage;
    private ControlPriority protocolPriority;

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

    public String getFeeder() {
        return feeder;
    }

    public void setFeeder(String feeder) {
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


    public List<AddressUsage> getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(List<AddressUsage> addressUsage) {
        this.addressUsage = addressUsage;
    }


    public List<Loads> getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(List<Loads> relayUsage) {
        this.relayUsage = relayUsage;
    }


    public ControlPriority getProtocolPriority() {
        return protocolPriority;
    }

    public void setProtocolPriority(ControlPriority protocolPriority) {
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
        Integer feederAddress = ((LMGroupExpressCom) loadGroup).getFeederAddress().getAddress();
        setFeeder(getFeederBinary(feederAddress));
        setProgram(((LMGroupExpressCom) loadGroup).getProgramAddress().getAddress());
        setZip(((LMGroupExpressCom) loadGroup).getZipCodeAddress().getAddress());
        setUser(((LMGroupExpressCom) loadGroup).getUserAddress().getAddress());
        setSplinter(((LMGroupExpressCom) loadGroup).getSplinterAddress().getAddress());

        // Set expresscom fields
        setRouteID(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getRouteID());
        setSerialNumber(((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getSerialNumber());
        String addressUsageString = ((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getAddressUsage();
        List<AddressUsage> addressUsage = new ArrayList<>();
        for (int i = 0; i < addressUsageString.length(); i++) {
            addressUsage.add(AddressUsage.getDisplayValue(addressUsageString.charAt(i)));
        }
        setAddressUsage(addressUsage);
        String loadsString = ((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getRelayUsage();
        if(loadsString!= null) {
            loadsString = loadsString.trim();
        }
        List<Loads> loads = new ArrayList<>();
        for (int i = 0; i < loadsString.length(); i++) {
            loads.add(Loads.getDisplayValue(Character.getNumericValue(loadsString.charAt(i))));
        }
        setRelayUsage(loads);
        Integer controlPriorityValue = ((LMGroupExpressCom) loadGroup).getLMGroupExpressComm().getProtocolPriority();
        setProtocolPriority(ControlPriority.getDisplayValue(controlPriorityValue));
    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set Addressing
        ((LMGroupExpressCom) group).setServiceProviderAddress(
            createAddress(getServiceProvider(), AddressUsage.SERVICE));
        ((LMGroupExpressCom) group).setGeoAddress(createAddress(getGeo(), AddressUsage.GEO));
        ((LMGroupExpressCom) group).setSubstationAddress(createAddress(getSubstation(), AddressUsage.SUBSTATION));
        Integer feeder = getFeederInteger(getFeeder()); 
        ((LMGroupExpressCom) group).setFeederAddress(createAddress(feeder, AddressUsage.FEEDER));
        ((LMGroupExpressCom) group).setProgramAddress(createAddress(getProgram(), AddressUsage.PROGRAM));
        ((LMGroupExpressCom) group).setZipCodeAddress(createAddress(getZip(), AddressUsage.ZIP));
        ((LMGroupExpressCom) group).setUserAddress(createAddress(getUser(), AddressUsage.USER));
        ((LMGroupExpressCom) group).setSplinterAddress(createAddress(getSplinter(), AddressUsage.SPLINTER));


        // Set Expresscom
        com.cannontech.database.db.device.lm.LMGroupExpressCom expresscom =
            ((LMGroupExpressCom) group).getLMGroupExpressComm();
        if (group.getPaoType() == PaoType.LM_GROUP_RFN_EXPRESSCOMM) {
            expresscom.setRouteID(0);
        } else {
            expresscom.setRouteID(getRouteID());
        }
        expresscom.setSerialNumber(getSerialNumber());
        
        String addressUsageAbbreviation = getAddressUsage().stream().map(e -> e.getAbbreviation()).
                map(String::valueOf).collect(Collectors.joining());
        expresscom.setAddressUsage(addressUsageAbbreviation);

        String loads = getRelayUsage().stream().map(e -> e.getLoadNumber()).map(String::valueOf).collect(Collectors.joining());
        expresscom.setRelayUsage(loads);
        expresscom.setProtocolPriority(getProtocolPriority().getControlPriorityValue());
        ((LMGroupExpressCom) group).setLMGroupExpressComm(expresscom);
    }
    
    /**
     * Converts binary value to integer for feeder
     */
    private Integer getFeederInteger(String feeder) {
        int val = 0;
        for (int i = feeder.length() - 1; i >= 0; i--) {
            val <<= 1;
            if (feeder.charAt(i) == '1') {
                val += 1;
            }
        }
        return val;
    }
    
    /**
     * Converts integer value to binary value for feeder
     */
    private String getFeederBinary(Integer feeder) {
        StringBuffer feederBinary = new StringBuffer(); 
        for (int i = 0; i < 16; i++) {
            if ((feeder & 1) == 0) {
                feederBinary.append(0);
            } else {
                feederBinary.append(1);
            }
            feeder >>= 1;
        }
        return feederBinary.toString();
    }

    /**
     * Create addressing for passed addressing type
     */
    private LMGroupExpressComAddress createAddress(Integer address, AddressUsage type) {
        if (address == null) {
            return com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS;
        }
        LMGroupExpressComAddress expresscomAddress = new LMGroupExpressComAddress(type.toString());
        expresscomAddress.setAddress(address);
        return expresscomAddress;
    }
}