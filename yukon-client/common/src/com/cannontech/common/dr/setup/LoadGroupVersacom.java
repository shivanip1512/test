package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(value={ "routeName"}, allowGetters= true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LoadGroupVersacom extends LoadGroupBase<LMGroupVersacom> implements LoadGroupRoute {
    // Communication Route Id
    private Integer routeId;
    private String routeName;
    // Addressing
    private Integer utilityAddress;
    private Integer sectionAddress;
    private String classAddress;
    private String divisionAddress;
    private String serialAddress;

    // Addressing Usage
    private List<VersacomAddressUsage> addressUsage;
    private List<Relays> relayUsage;

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

    public Integer getUtilityAddress() {
        return utilityAddress;
    }

    public void setUtilityAddress(Integer utilityAddress) {
        this.utilityAddress = utilityAddress;
    }

    public Integer getSectionAddress() {
        return sectionAddress;
    }

    public void setSectionAddress(Integer sectionAddress) {
        this.sectionAddress = sectionAddress;
    }

    public String getClassAddress() {
        return classAddress;
    }

    public void setClassAddress(String classAddress) {
        this.classAddress = classAddress;
    }

    public String getDivisionAddress() {
        return divisionAddress;
    }

    public void setDivisionAddress(String divisionAddress) {
        this.divisionAddress = divisionAddress;
    }

    public List<VersacomAddressUsage> getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(List<VersacomAddressUsage> addressUsage) {
        this.addressUsage = addressUsage;
    }

    public List<Relays> getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(List<Relays> relayUsage) {
        this.relayUsage = relayUsage;
    }

    public String getSerialAddress() {
        return serialAddress;
    }

    public void setSerialAddress(String serialAddress) {
        this.serialAddress = serialAddress;
    }
    
    @Override
    public void buildDBPersistent(LMGroupVersacom group) {
        super.buildDBPersistent(group);
        // Set lmGroupVersacom fields
        com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom =
                           new com.cannontech.database.db.device.lm.LMGroupVersacom();
        // Build value of address usage
        /*List<VersacomAddressUsage> addressUsageList = getAddressUsage();
        StringBuilder addressUsageStr = new StringBuilder();
        for (VersacomAddressUsage addressusage : addressUsageList) {
            addressUsageStr.append(addressusage.getAbbreviation());
        }*/
        
        String addressUsageStr = getAddressUsage().stream()
                                                  .map(e -> e.getAbbreviation())
                                                  .map(String::valueOf)
                                                  .collect(Collectors.joining());
        lmGroupVersacom.setAddressUsage(addressUsageStr);
        String classAddressString = getClassAddress();
        if (!classAddressString.isEmpty()) {
            Integer classAddress = StringUtils.convertBinaryToInteger(classAddressString);
            lmGroupVersacom.setClassAddress(classAddress);
        }

        String divisionAddressString = getDivisionAddress();
        if (!divisionAddressString.isEmpty()) {
            Integer divisionAddress = StringUtils.convertBinaryToInteger(divisionAddressString);
            lmGroupVersacom.setDivisionAddress(divisionAddress);
        }
        /*List<Relays> relayUsage = getRelayUsage();
        StringBuilder relayUsageStr = new StringBuilder();
        for (Relays relay : relayUsage) {
            // Need to check below code.
            relayUsageStr.append(relay.getRelayNumber().toString());
        }*/
        
        String relayUsageStr = getRelayUsage().stream()
                                              .map(e -> e.getRelayNumber())
                                              .map(String::valueOf)
                                              .collect(Collectors.joining());
        lmGroupVersacom.setRelayUsage(relayUsageStr);
        lmGroupVersacom.setRouteID(getRouteId());
        lmGroupVersacom.setSectionAddress(getSectionAddress());
        lmGroupVersacom.setSerialAddress(getSerialAddress());
        lmGroupVersacom.setUtilityAddress(getUtilityAddress());
        lmGroupVersacom.setDeviceID(getId());
        group.setLmGroupVersacom(lmGroupVersacom);
    }
    
    @Override
    public void buildModel(LMGroupVersacom loadGroup) {
        super.buildModel(loadGroup);

        setRouteId(loadGroup.getRouteID());
        com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom = 
                loadGroup.getLmGroupVersacom();
        setUtilityAddress(lmGroupVersacom.getUtilityAddress());
        setSectionAddress(lmGroupVersacom.getSectionAddress());
        // Convert Class and Division Integer value to Binary String.
        Integer classAddress = lmGroupVersacom.getClassAddress();
        if (classAddress != null) {
            setClassAddress(StringUtils.convertIntegerToBinary(classAddress));
        }
        Integer divisionAddress = lmGroupVersacom.getDivisionAddress();
        if (divisionAddress != null) {
            setDivisionAddress(StringUtils.convertIntegerToBinary(divisionAddress));
        }
        setSerialAddress(lmGroupVersacom.getSerialAddress());
        String addressUsageStr = lmGroupVersacom.getAddressUsage().trim();
        List<VersacomAddressUsage> addressUsage = new ArrayList<>();
        for (int i = 0; i < addressUsageStr.length(); i++) {
            addressUsage.add(VersacomAddressUsage.getDisplayValue(addressUsageStr.charAt(i)));
        }

        // Set values inside versacomAddressUsage
        setAddressUsage(addressUsage);
        String relayUsage = lmGroupVersacom.getRelayUsage().trim();
        List<Relays> relays = new ArrayList<>();
        for (int i = 0; i < relayUsage.length(); i++) {
            relays.add(Relays.getDisplayValue(relayUsage.charAt(i)));
        }
        setRelayUsage(relays);
    }

}
