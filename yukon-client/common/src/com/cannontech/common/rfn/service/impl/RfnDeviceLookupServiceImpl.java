package com.cannontech.common.rfn.service.impl;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

public class RfnDeviceLookupServiceImpl implements RfnDeviceLookupService {

    private static final Logger log = YukonLogManager.getLogger(RfnDeviceLookupServiceImpl.class);
    private RfnDeviceDao rfnDeviceDao;
    private ConfigurationSource configurationSource;
    private ImmutableBiMap<String, String> modelTranslations;
    
    public RfnDevice getDevice(RfnIdentifier rfnIdentifier) throws NotFoundException {
        NotFoundException originalNotFoundException;
        try {
            RfnDevice result = rfnDeviceDao.getDeviceForExactIdentifier(rfnIdentifier);
            return result;
        } catch (NotFoundException e) {
            log.debug("exception getting device 1", e);
            originalNotFoundException = e;
        }

        // chances are the device doesn't exist, but it may have a renamed model

        // first check if we have the old name in the database
        if (modelTranslations.containsValue(rfnIdentifier.getSensorModel())) {
            // the model name we're looking up is the "new" format, see if 
            // we have it in the database with the "old" format
            String oldModelName = modelTranslations.inverse().get(rfnIdentifier.getSensorModel());
            RfnIdentifier identifierWithOldModel = new RfnIdentifier(rfnIdentifier.getSensorSerialNumber(), rfnIdentifier.getSensorManufacturer(), oldModelName);
            try {
                RfnDevice oldDevice = rfnDeviceDao.getDeviceForExactIdentifier(identifierWithOldModel);
                // if we find it, we need to update the database to represent the new model name
                RfnDevice newDevice = new RfnDevice(oldDevice.getName(), oldDevice.getPaoIdentifier(), rfnIdentifier);
                rfnDeviceDao.updateDevice(newDevice);
                log.info("updating older " + oldDevice + " to " + newDevice + " when " + rfnIdentifier + " was requested");
                return newDevice;
            } catch (NotFoundException e) {
                log.debug("exception getting device 2", e);
                // okay, keep looking
            }
        }

        // now check if we have the new name in the database
        if (modelTranslations.containsKey(rfnIdentifier.getSensorModel())) {
            // the model name we're looking up is the "old" format, see if 
            // we have it in the database with the "new" format

            // the only time this would happen is if messages were received out of order,
            // such that we'd already updated the name into the new format but then
            // received an older message that still referenced the device with the old format
            String newModelName = modelTranslations.get(rfnIdentifier.getSensorModel());
            RfnIdentifier identifierWithNewModel = new RfnIdentifier(rfnIdentifier.getSensorSerialNumber(), rfnIdentifier.getSensorManufacturer(), newModelName);
            try {
                RfnDevice result = rfnDeviceDao.getDeviceForExactIdentifier(identifierWithNewModel);
                // if we find it, we'll use it, but we don't want to change the name back
                log.info("returning newer " + result + " when " + rfnIdentifier + " was requested");
                return result;
            } catch (NotFoundException e) {
                log.debug("exception getting meter 3", e);
                // guess it really doesn't exist
            }
        }

        // rethrow the first NotFoundException because it probably most accurately reflected what's going on
        throw originalNotFoundException;
    }
    
    @Autowired
    public void setRfnDeviceDao(RfnDeviceDao rfnDeviceDao) {
        this.rfnDeviceDao = rfnDeviceDao;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @PostConstruct
    public void initialize() {
        Builder<String, String> builder = ImmutableBiMap.builder();
        String modelConversion = configurationSource.getString("RFN_METER_MODEL_CONVERSION", null);
        if (modelConversion != null) {
            String[] models = modelConversion.split(",");
            for (String model : models) {
                String[] modelNames = model.split(":");
                builder.put(modelNames[0], modelNames[1]);
            }
        }
        modelTranslations = builder.build();
    }
    
}