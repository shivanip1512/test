package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.db.device.lm.LMGroupExpressComAddress;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(value={ "routeName"}, allowGetters= true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LoadGroupExpresscom extends LoadGroupBase<LMGroupExpressCom> implements LoadGroupRoute {
    private Integer routeId;
    private String routeName;
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


    @Override
    public Integer getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteName() {
        return routeName;
    }

    @Override
    public void setRouteName(String routeName) {
        this.routeName = routeName;
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
    public void buildModel(LMGroupExpressCom lmGroupExpresscom) {
        // Set parent fields
        super.buildModel(lmGroupExpresscom);
        // Set addressing fields
        setServiceProvider(lmGroupExpresscom.getServiceProviderAddress().getAddress());
        setGeo(lmGroupExpresscom.getGeoAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getGeoAddress().getAddress());
        setSubstation(lmGroupExpresscom.getSubstationAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getSubstationAddress().getAddress());
        Integer feederAddress = lmGroupExpresscom.getFeederAddress().getAddress();
        setFeeder(feederAddress == 0 ? null : StringUtils.convertIntegerToBinary(feederAddress));
        setProgram(lmGroupExpresscom.getProgramAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getProgramAddress().getAddress());
        setZip(lmGroupExpresscom.getZipCodeAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getZipCodeAddress().getAddress());
        setUser(lmGroupExpresscom.getUserAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getUserAddress().getAddress());
        setSplinter(lmGroupExpresscom.getSplinterAddress().getAddress() == 0 ? null
            : lmGroupExpresscom.getSplinterAddress().getAddress());

        // Set expresscom fields
        setRouteId(lmGroupExpresscom.getLMGroupExpressComm().getRouteID() == 0 ? null
            : lmGroupExpresscom.getLMGroupExpressComm().getRouteID());
        setSerialNumber(lmGroupExpresscom.getLMGroupExpressComm().getSerialNumber().equals("0") ? null
            : lmGroupExpresscom.getLMGroupExpressComm().getSerialNumber());

        // Address usage can not be blank, no need to handle default
        String addressUsageString = lmGroupExpresscom.getLMGroupExpressComm().getAddressUsage().trim();
        List<AddressUsage> addressUsage = new ArrayList<>();
        for (int i = 0; i < addressUsageString.length(); i++) {
            addressUsage.add(AddressUsage.getForAbbreviation(addressUsageString.charAt(i)));
        }
        // SPID is not send in address usage but is stored, so removing.
        addressUsage.remove(AddressUsage.SERVICE);
        // If serial has value then set it in address usage.
        if (!lmGroupExpresscom.getLMGroupExpressComm().getSerialNumber().equals("0")) {
            addressUsage.add(AddressUsage.SERIAL);
        }
        setAddressUsage(addressUsage);
        String loadsString = lmGroupExpresscom.getLMGroupExpressComm().getRelayUsage();
        if (loadsString != null) {
            loadsString = loadsString.trim();
            List<Loads> loads = new ArrayList<>();
            for (int i = 0; i < loadsString.length(); i++) {
                loads.add(Loads.getForLoads(Character.getNumericValue(loadsString.charAt(i))));
            }
            setRelayUsage(loads);
        }

        Integer controlPriorityValue = lmGroupExpresscom.getLMGroupExpressComm().getProtocolPriority();
        setProtocolPriority(ControlPriority.getForPriority(controlPriorityValue));
    }

    @Override
    public void buildDBPersistent(LMGroupExpressCom lmGroupExpressCom) {
        // Set parent fields
        super.buildDBPersistent(lmGroupExpressCom);

        // Set Addressing
        lmGroupExpressCom.setServiceProviderAddress(createAddress(getServiceProvider(), AddressUsage.SERVICE));
        lmGroupExpressCom.setGeoAddress(createAddress(getGeo(), AddressUsage.GEO));
        lmGroupExpressCom.setSubstationAddress(createAddress(getSubstation(), AddressUsage.SUBSTATION));
        if (getFeeder() != null) {
            Integer feeder = StringUtils.convertBinaryToInteger(getFeeder());
            lmGroupExpressCom.setFeederAddress(createAddress(feeder, AddressUsage.FEEDER));
        }

        lmGroupExpressCom.setProgramAddress(createAddress(getProgram(), AddressUsage.PROGRAM));
        lmGroupExpressCom.setZipCodeAddress(createAddress(getZip(), AddressUsage.ZIP));
        lmGroupExpressCom.setUserAddress(createAddress(getUser(), AddressUsage.USER));
        lmGroupExpressCom.setSplinterAddress(createAddress(getSplinter(), AddressUsage.SPLINTER));

        // Set Expresscom
        com.cannontech.database.db.device.lm.LMGroupExpressCom expresscom = lmGroupExpressCom.getLMGroupExpressComm();
        if (lmGroupExpressCom.getPaoType() == PaoType.LM_GROUP_RFN_EXPRESSCOMM) {
            expresscom.setRouteID(0);
        } else {
            expresscom.setRouteID(getRouteId());
        }
        if (getSerialNumber() == null || getSerialNumber().trim().isBlank()) {
            expresscom.setSerialNumber("0");
        } else {
            expresscom.setSerialNumber(getSerialNumber());
        }
        
        
        // Serial is not store in address usage so ignore it
        getAddressUsage().remove(AddressUsage.SERIAL);
        // SPID is a mandatory field and should be saved in address usage
        getAddressUsage().add(AddressUsage.SERVICE);
        String addressUsageAbbreviation =
            getAddressUsage().stream().map(e -> e.getAbbreviation()).map(String::valueOf).collect(Collectors.joining());
        expresscom.setAddressUsage(addressUsageAbbreviation);

        String loads =
            getRelayUsage().stream().map(e -> e.getLoadNumber()).map(String::valueOf).collect(Collectors.joining());
        expresscom.setRelayUsage(loads);
        expresscom.setProtocolPriority(getProtocolPriority().getControlPriorityValue());
        lmGroupExpressCom.setLMGroupExpressComm(expresscom);
    }
    

    /**
     * Create addressing for passed addressing type
     */
    private LMGroupExpressComAddress createAddress(Integer address, AddressUsage type) {
        if (address == null || address <= 0) {
            return com.cannontech.database.db.device.lm.LMGroupExpressComAddress.NONE_ADDRESS;
        }
        LMGroupExpressComAddress expresscomAddress = new LMGroupExpressComAddress(type.toString());
        expresscomAddress.setAddress(address);
        return expresscomAddress;
    }
}