package com.cannontech.multispeak.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspMeterDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableList;

public class MultispeakMeterServiceBase {
    
    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceBase.class);
    
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;
    @Autowired protected ConfigurationSource configurationSource;
    @Autowired protected DeviceDao deviceDao;
    @Autowired protected DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired protected DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired protected MeterDao meterDao;
    @Autowired private MspMeterDao mspMeterDao; // caution! this is a v3 dao, only intended for calling mspMeterDaoBase methods
    @Autowired protected MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs; // caution! this is a v3 dao, only intended for calling multispeakFuncsBase methods
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao substationToRouteMappingDao;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private LocationService locationService;
    @Autowired private PaoLocationDao paoLocationDao;
    
    public static final String DEVICE_NAME_EXT_REGEX = "\\s+\\[[a-zA-Z0-9]+\\]";
    /**
     * Updates the billingCycle device group.
     * The exact parent group to update is configured in MultiSpeak global settings.
     */
    public boolean updateBillingCyle(String newBilling, String meterNumber, YukonDevice yukonDevice, String mspMethod,
            MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(newBilling)) {

            // Remove from all billing membership groups
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(billingCycledeviceGroup);
            return updatePrefixGroup(newBilling, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
        }

        return false;
    }

    /**
     * Updates the CIS Substation device group.
     * This group (should be) completely managed by MultiSpeak processing.
     */

    public boolean updateSubstationGroup(String substationName, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {

        if (!StringUtils.isBlank(substationName)) {

            // Remove from all substation membership groups
            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_SUBSTATION);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(substationNameDeviceGroup);
            return updatePrefixGroup(substationName, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
        }
        return false;
    }

    /**
     * Removes meter from all immediate descendants of deviceGroupParent. Adds
     * meter to a subgroup of deviceGroupParent called groupName. If groupName
     * does not exist, a new group will be created.
     * 
     * @return true if added to new prefix group.
     */
    protected boolean updatePrefixGroup(String groupName, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor, StoredDeviceGroup deviceGroupParent) {
        boolean alreadyInGroup = false;

        Set<StoredDeviceGroup> deviceGroups =
            deviceGroupMemberEditorDao.getGroupMembership(deviceGroupParent, yukonDevice);
        for (StoredDeviceGroup deviceGroup : deviceGroups) {
            if (deviceGroup.getName().equalsIgnoreCase(groupName)) {
                log.debug("MeterNumber(" + meterNumber + ") - Already in group:  " + groupName);
                alreadyInGroup = true;
            } else {
                int numAffected = deviceGroupMemberEditorDao.removeDevices(deviceGroup, yukonDevice);
                if (numAffected > 0) {
                    multispeakEventLogService.removeMeterFromGroup(meterNumber, deviceGroup.getFullName(), mspMethod,
                        mspVendor.getCompanyName());
                }
            }
        }

        if (!alreadyInGroup) {
            StoredDeviceGroup deviceGroup = deviceGroupEditorDao.getGroupByName(deviceGroupParent, groupName, true);
            int numAffected = deviceGroupMemberEditorDao.addDevice(deviceGroup, yukonDevice);
            if (numAffected > 0) {
                multispeakEventLogService.addMeterToGroup(meterNumber, deviceGroup.getFullName(), mspMethod,
                    mspVendor.getCompanyName());
            }
        }
        return !alreadyInGroup;
    }

    /**
     * Helper method to return a Meter object for Meter Number
     */
    public YukonMeter getMeterByMeterNumber(String mspMeter) throws NotFoundException {
        return meterDao.getForMeterNumber(mspMeter);
    }

    /**
     * Helper method to return a PLC Meter object for Address
     */
    public YukonMeter getMeterBySerialNumberOrAddress(String mspAddress) {
        return mspMeterDao.getForSerialNumberOrAddress(mspAddress);
    }

    /**
     * Returns an "unused" paoName for newPaoName.
     * If a meter already exists with newPaoName, then a numeric incremented value will be appended to the
     * newPaoName.
     */
    public String getNewUniquePaoName(String newPaoName) {

        int retryCount = 0;
        String tempPaoName = newPaoName;
        boolean found = false;
        do {
            try {
                if (retryCount > 0) {
                    tempPaoName = newPaoName + " (" + retryCount + ")";
                }
                // Try to find if a meter already exists with this paoName
                meterDao.getForPaoName(tempPaoName);
                retryCount++;
            } catch (NotFoundException e) {
                // this is good!
                found = true;
            }
        } while (!found);

        return tempPaoName;
    }

    /**
     * Returns an rfnIdentifier representing the serialNumber and manufacturer/model from templateMeter.
     * If templateMeter has blank values, then we will attempt to parse the manufacturer/model values from the
     * template name.
     * This is useful if the templateName matches the "standard" *RfnTemplate_manufacturer_model naming
     * convention
     * 
     * @param templateMeter
     * @param serialNumber
     * @return RfnIdentifier
     * @throws BadConfigurationException in case the Template meter does not contain the Manufacturer or model
     */
    public RfnIdentifier buildNewMeterRfnIdentifier(RfnMeter templateMeter, String serialNumber)
            throws BadConfigurationException {

        String manufacturer = templateMeter.getRfnIdentifier().getSensorManufacturer();
        String model = templateMeter.getRfnIdentifier().getSensorModel();

        if (StringUtils.isBlank(manufacturer) || StringUtils.isBlank(model)) {
            // if either is empty, attempt to parse from the templateMeter.paoName
            String templatePrefix =
                configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");

            // Format is *RfnTemplate_manufacturer_model
            String nameToStripRfnIdentifierFrom = templateMeter.getName();
            String rfnIdentifierPart = StringUtils.removeStart(nameToStripRfnIdentifierFrom, templatePrefix);
            String[] manufacturerModel = StringUtils.split(rfnIdentifierPart, "_");

            if (manufacturerModel.length == 2) {
                manufacturer = manufacturerModel[0];
                model = manufacturerModel[1];
            } else {
                
                // could possibly add something in here that defaults to the default meter definition, 
                // would also just have to "pick" if there are multiple setups for paoType 
//                List<RfnManufacturerModel> defaultTemplate = RfnManufacturerModel.getForType(templateMeter.getPaoType());
//                manufacturer = defaultTemplate.get(0).getManufacturer();
//                model = defaultTemplate.get(0).getModel();
                        
                throw new BadConfigurationException("Template Manufacturer/Model not configured correctly.");
            }
        }

        return new RfnIdentifier(serialNumber, manufacturer, model);
    }

    /**
     * Check if the deviceType of meter is different than the deviceType of the template meter
     * If different types of meters, then the deviceType will be changed for meter.
     * If the types are not compatible, a MspErrorObjectException will be thrown.
     * 
     * @param templateMeter - the meter to compare to, this is the type of meter the calling system thinks we
     *        should have
     * @param existingMeter - the meter to update
     * @param serialOrAddress
     * @param mspMethod
     * @param mspVendor
     * @return Returns the updated existingMeter object (in case of major paoType change)
     */
    public YukonMeter updateDeviceType(YukonMeter templateMeter, YukonMeter existingMeter, String serialOrAddress,
            String mspMethod, MultispeakVendor mspVendor) {
        PaoType originalType = existingMeter.getPaoType();
        if (templateMeter.getPaoType() != originalType) {
            // PROBLEM, types do not match!
            // Attempt to change type
            ChangeDeviceTypeService.ChangeDeviceTypeInfo changeInfo;
            if (templateMeter.getPaoType().isRfn()) {
                RfnIdentifier rfnIdentifier = buildNewMeterRfnIdentifier((RfnMeter) templateMeter, serialOrAddress);
                changeInfo = new ChangeDeviceTypeInfo(rfnIdentifier);
            } else {
                changeInfo = new ChangeDeviceTypeInfo(Integer.parseInt(serialOrAddress), 0); // route will be updated later as part of route locate
            }
            changeDeviceTypeService.changeDeviceType(new SimpleDevice(existingMeter), templateMeter.getPaoType(),
                changeInfo);
            existingMeter = meterDao.getForId(existingMeter.getDeviceId()); // reload the meter in case we've changed base classes
            multispeakEventLogService.deviceTypeUpdated(originalType, existingMeter, mspMethod, mspVendor.getCompanyName());
        }
        return existingMeter;
    }

    /**
     * Check if paoName of meter is different than new paoName.
     * If different, paoName is updated.
     */
    public void verifyAndUpdatePaoName(String newPaoName, YukonMeter existingMeter, String mspMethod,
            MultispeakVendor mspVendor) {
        String originalName = existingMeter.getName();
        if (!originalName.equalsIgnoreCase(newPaoName)) {
            // UPDATE PAONAME
            // Shouldn't fail, if PaoName already exists, a uniqueness value will be added.
            newPaoName = getNewUniquePaoName(newPaoName);
            deviceDao.changeName(existingMeter, newPaoName);
            existingMeter.setName(newPaoName); // update local object with new name.
            multispeakEventLogService.paoNameUpdated(originalName, existingMeter, mspMethod, mspVendor.getCompanyName());
        }
    }

    /**
     * Check if meterNumber of meter is different than new meterNumber.
     * If different, meterNumber is updated.
     */
    public void verifyAndUpdateMeterNumber(String newMeterNumber, YukonMeter existingMeter, String mspMethod,
            MultispeakVendor mspVendor) {
        String originalMeterNumber = existingMeter.getMeterNumber();
        if (!originalMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            // UPDATE METER NUMBER
            // Shouldn't fail, if Meter Number already exists, we end up with duplicates
            deviceDao.changeMeterNumber(existingMeter, newMeterNumber);
            existingMeter.setMeterNumber(newMeterNumber); // update local object with new meter number.
            multispeakEventLogService.meterNumberUpdated(originalMeterNumber, existingMeter, mspMethod,
                mspVendor.getCompanyName());
        }
    }

    /**
     * For Rfn, check if rfnIdentifier of meter is different than new serialNumber (and model/manufacturer of
     * templateMeter).
     * If different, rfnIdentifier is updated.
     * For Plc, check if address of meter is different than new address.
     * If different, address is updated.
     */
    public void verifyAndUpdateAddressOrSerial(String newSerialOrAddress, YukonMeter templateMeter,
            YukonMeter existingMeter, String mspMethod, MultispeakVendor mspVendor) {
        if (existingMeter.getPaoType().isRfn()) {
            RfnIdentifier newMeterRfnIdentifier =
                buildNewMeterRfnIdentifier((RfnMeter) templateMeter, newSerialOrAddress);

            // Check if different first, then update only if change needed
            if (!((RfnMeter) existingMeter).getRfnIdentifier().getSensorSerialNumber().equals(
                newMeterRfnIdentifier.getSensorSerialNumber())) {
                String originalSerialNumber = ((RfnMeter) existingMeter).getRfnIdentifier().getSensorSerialNumber();
                RfnDevice deviceToUpdate = new RfnDevice(existingMeter.getName(), existingMeter, newMeterRfnIdentifier);
                rfnDeviceDao.updateDevice(deviceToUpdate);
                existingMeter =
                    new RfnMeter(existingMeter, newMeterRfnIdentifier, existingMeter.getMeterNumber(),
                        existingMeter.getName(), existingMeter.isDisabled()); // update local object with new RfnIdentifier
                multispeakEventLogService.serialNumberOrAddressUpdated(originalSerialNumber, existingMeter, mspMethod,
                    mspVendor.getCompanyName());
                // UPDATE SERIAL NUMBER (model, manufacturer)
                // MAY FAIL IF RFNIdentifier ALREADY EXISTS FOR ANOTHER METER
            }

        } else if (templateMeter.getPaoType().isMct()) {
            // Check if different first, then update only if change needed
            if (!existingMeter.getSerialOrAddress().equals(newSerialOrAddress)) {
                String originalAddress = existingMeter.getSerialOrAddress();
                // UPDATE CARRIER ADDRESS
                // WILL NOT FAIL IF ADDRESS IS ALREADY IN USE BY ANOTHER DEVICE!
                deviceUpdateService.changeAddress(existingMeter, Integer.valueOf(newSerialOrAddress));
                ((PlcMeter) existingMeter).setAddress(newSerialOrAddress); // update local object with new address.
                multispeakEventLogService.serialNumberOrAddressUpdated(originalAddress, existingMeter, mspMethod,
                    mspVendor.getCompanyName());
            }
        }
    }

    /**
     * Helper method to remove meter from systemGroup
     */
    public void removeFromGroup(YukonMeter meter, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        int numAffected =
            deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup) deviceGroup, Collections.singletonList(meter));
        if (numAffected > 0) {
            String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
            multispeakEventLogService.removeMeterFromGroup(meter.getMeterNumber(), basePath, mspMethod,
                mspVendor.getCompanyName());
        }
    }

    /**
     * Helper method to add meter to systemGroup
     */
    public void addMeterToGroup(YukonMeter meter, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        int numAffected = deviceGroupMemberEditorDao.addDevice((StoredDeviceGroup) deviceGroup, meter);
        if (numAffected > 0) {
            String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
            multispeakEventLogService.addMeterToGroup(meter.getMeterNumber(), basePath, mspMethod,
                mspVendor.getCompanyName());
        }
    }
    
    /**
     * Checks if substationName is in Yukon.
     * If not found, or if no routes associated with substation, then return with no processing.
     * If found, and routes exist, assigns (first) route to meter.
     * Then initiates route locate.
     * If only one route found, assigns to meter but locate is skipped.
     * Returns with no processing for non PLC types.
     * @param meterToUpdate- the meter to update routing for
     * @param mspVendor
     * @param substationName - the substationName to lookup routeMappings for
     * @param meterNumber - for logging
     */
    public void verifyAndUpdateRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor, String substationName,
            String mspMethod) {

        // not valid for RFN meter types
        if (!(meterToUpdate instanceof PlcMeter)) {
            return;
        }

        String meterNumber = meterToUpdate.getMeterNumber();

        try {
            // get routes
            Substation substation = substationDao.getByName(substationName);
            List<Route> routes = substationToRouteMappingDao.getRoutesBySubstationId(substation.getId());

            if (routes.isEmpty()) {
                multispeakEventLogService.routeNotFound(substationName, ((PlcMeter) meterToUpdate).getRoute(),
                    meterNumber, mspMethod, mspVendor.getCompanyName());
            } else { // routes exist

                // initally set route to first sub mapping
                int originalRouteId = ((PlcMeter) meterToUpdate).getRouteId();
                Route initialRoute = routes.get(0);

                if (originalRouteId != initialRoute.getId()) {
                    deviceUpdateService.changeRoute(meterToUpdate, initialRoute.getId());
                    ((PlcMeter) meterToUpdate).setRouteId(initialRoute.getId());
                    ((PlcMeter) meterToUpdate).setRoute(initialRoute.getName()); // update local object with initiate route

                    if (routes.size() == 1) { // no need to run route discovery if we only have one route.
                        multispeakEventLogService.routeUpdated(initialRoute.getName(), meterNumber, mspMethod,
                            mspVendor.getCompanyName());
                    }
                }

                if (routes.size() > 1) { // run route discovery
                    LiteYukonUser liteYukonUser = UserUtils.getYukonUser();

                    ImmutableList<Integer> routeIds = PaoUtils.asPaoIdList(routes);
                    ImmutableList<String> routeNames = PaoUtils.asPaoNames(routes);

                    multispeakEventLogService.routeUpdatedByDiscovery(initialRoute.getName(), meterNumber,
                        StringUtils.join(routeNames, ","), mspMethod, mspVendor.getCompanyName());
                    deviceUpdateService.routeDiscovery(meterToUpdate, routeIds, liteYukonUser);
                }
            }

        } catch (NotFoundException e) { // bad sub name
            multispeakEventLogService.substationNotFound(substationName, meterNumber, mspMethod,
                mspVendor.getCompanyName());
            log.warn(e);
        }
    }
    
    /**
     * Update the (CIS) Substation Group.
     * If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     */
    public void checkAndUpdateSubstationName(String substationName, String meterNumber, String mspMethod,
            MultispeakVendor mspVendor, YukonMeter meterToUpdate) {
        if (StringUtils.isBlank(substationName)) {
            // change logging here.
            // No route updates made (if PLC). No substation group updates.
            multispeakEventLogService.substationNotFound("", meterNumber, mspMethod, mspVendor.getCompanyName());
        } else {
            // update the substation group
            boolean addedToGroup = updateSubstationGroup(substationName, meterNumber, meterToUpdate, mspMethod, mspVendor);

            if (meterToUpdate.getPaoType().isPlc()) {
                if (addedToGroup || ((PlcMeter)meterToUpdate).getRouteId() == 0) {
                    // If the substation changed, we should attempt to update the route info too.
                    // Update route (_after_ meter is enabled).
                    verifyAndUpdateRoute(meterToUpdate, mspVendor, substationName, mspMethod);
                }
            }
        }
    }

    /**
     * Create cisDeviceClass Device group if it is not exist and also remove meter from any other
     * device group of the same parent.
     */
    public boolean updateCISDeviceClassGroup(String meterNumber, String cisDeviceClass, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {
        if (!StringUtils.isBlank(cisDeviceClass)) {
            // Remove from all CIS deviceClass membership groups
            DeviceGroup cisDeviceClassDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_DEVICECLASS);
            StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(cisDeviceClassDeviceGroup);
            return updatePrefixGroup(cisDeviceClass, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
        }

        return false;
    }

    /**
     * Remove extension " [alphanumeric]" from DeviceName
     */

    public void removeDeviceNameExtension(YukonMeter meter, String mspMethod, MultispeakVendor mspVendor) {
        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();
        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("DeviceClass")) { 
                // Custom for WHE to remove the device class extension value from the device name.
                String newPaoName = StringUtils.removePattern(meter.getName(), DEVICE_NAME_EXT_REGEX).trim();
                verifyAndUpdatePaoName(newPaoName, meter, mspMethod, mspVendor);
            }
        }
    }

    /**
     * Remove device from Device Group (BillingCycle, CIS DeviceClass, CIS Substation & Alternate) and its
     * child device groups
     * 
     */

    public void removeDeviceFromCISGroups(YukonMeter meter, String mspMethod, MultispeakVendor mspVendor) {
        List<DeviceGroup> deviceGroupList = new ArrayList<>();

        boolean checkRemoveDeviceFromCISGroup = globalSettingDao.getBoolean(GlobalSettingType.MSP_REMOVE_DEVICE_FROM_CIS_GROUP);
        if (checkRemoveDeviceFromCISGroup) {
            DeviceGroup billingCycledeviceGroup = multispeakFuncs.getBillingCycleDeviceGroup();
            deviceGroupList.add(billingCycledeviceGroup);

            DeviceGroup substationNameDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_SUBSTATION);
            deviceGroupList.add(substationNameDeviceGroup);

            boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_ALTGROUP_EXTENSION);
            if (updateAltGroup) {
                DeviceGroup altGroupDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
                deviceGroupList.add(altGroupDeviceGroup);
            }
        }

        DeviceGroup cisDeviceClassGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.CIS_DEVICECLASS);
        deviceGroupList.add(cisDeviceClassGroup);

        deviceGroupList.forEach(deviceGroup -> {
            StoredDeviceGroup storedDeviceGroup = deviceGroupEditorDao.getStoredGroup(deviceGroup);
            Set<StoredDeviceGroup> storedDeviceGroups = deviceGroupMemberEditorDao.getGroupMembership(storedDeviceGroup, meter);
            storedDeviceGroups.forEach(group -> {
                int numAffected = deviceGroupMemberEditorDao.removeDevices(group, meter);
                if (numAffected > 0) {
                    multispeakEventLogService.removeMeterFromGroup(meter.getMeterNumber(), deviceGroup.getFullName(),
                        mspMethod, mspVendor.getCompanyName());
                }
            });
        });
    }
    
    /**
     * Update paolocation coordinates.
     * If latitude AND longitude are not valid, no action will be taken.
     * If latitude AND longitude have not changed, existing paoLocation will not be updated.
     */
    public void updatePaoLocation(String meterNumber, String meterName, PaoLocation paoLocation) {
        boolean isLatitudeValid = YukonValidationUtils.isLatitudeInRange(paoLocation.getLatitude());
        boolean isLongitudeValid = YukonValidationUtils.isLongitudeInRange(paoLocation.getLongitude());
        if (isLatitudeValid && isLongitudeValid) {
            PaoLocation existingPaoLocation = paoLocationDao.getLocation(paoLocation.getPaoIdentifier().getPaoId());
            if (existingPaoLocation == null || !existingPaoLocation.equalsCoordinates(paoLocation)) {
                // new entry or updated lat/loong
                locationService.saveLocation(paoLocation, meterName, YukonUserContext.system.getYukonUser());
            }
        } else {
            log.warn("Location info not updated for meter number " + meterNumber + ". " + paoLocation.toString());
        }
    }
}
