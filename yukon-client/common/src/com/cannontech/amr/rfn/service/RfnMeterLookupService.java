package com.cannontech.amr.rfn.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.rfn.dao.RfnMeterDao;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.model.RfnMeterIdentifier;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

public class RfnMeterLookupService {

    private static final Logger log = YukonLogManager.getLogger(RfnMeterLookupService.class);
    private RfnMeterDao rfnMeterDao;
    private ConfigurationSource configurationSource;
    private ImmutableBiMap<String, String> modelTranslations;
    
    /**
     * Will attempt to retrieve a meter based on meter identifier, if not found will attempt to 
     * retrieve it based on new versions of model names defined in master.cfg property "RFN_METER_MODEL_CONVERSION".
     * If still not found, will throw a NotFountException.
     * @param meterIdentifier
     * @return RfnMeter
     * @throws NotFoundException
     */
    public RfnMeter getMeter(RfnMeterIdentifier meterIdentifier) throws NotFoundException {
        NotFoundException originalNotFoundException;
        try {
            RfnMeter result = rfnMeterDao.getMeterForExactIdentifier(meterIdentifier);
            return result;
        } catch (NotFoundException e) {
            log.debug("exception getting meter 1", e);
            originalNotFoundException = e;
        }

        // chances are the meter doesn't exist, but it may have a renamed model

        // first check if we have the old name in the database
        if (modelTranslations.containsValue(meterIdentifier.getSensorModel())) {
            // the model name we're looking up is the "new" format, see if 
            // we have it in the database with the "old" format
            String oldModelName = modelTranslations.inverse().get(meterIdentifier.getSensorModel());
            RfnMeterIdentifier identifierWithOldModel = new RfnMeterIdentifier(meterIdentifier.getSensorSerialNumber(), meterIdentifier.getSensorManufacturer(), oldModelName);
            try {
                RfnMeter oldMeter = rfnMeterDao.getMeterForExactIdentifier(identifierWithOldModel);
                // if we find it, we need to update the database to represent the new model name
                RfnMeter newMeter = new RfnMeter(oldMeter.getPaoIdentifier(), meterIdentifier);
                rfnMeterDao.updateMeter(newMeter);
                log.info("updating older " + oldMeter + " to " + newMeter + " when " + meterIdentifier + " was requested");
                return newMeter;
            } catch (NotFoundException e) {
                log.debug("exception getting meter 2", e);
                // okay, keep looking
            }
        }

        // now check if we have the new name in the database
        if (modelTranslations.containsKey(meterIdentifier.getSensorModel())) {
            // the model name we're looking up is the "old" format, see if 
            // we have it in the database with the "new" format

            // the only time this would happen is if messages were received out of order,
            // such that we'd already updated the name into the new format but then
            // received an older message that still referenced the meter with the old format
            String newModelName = modelTranslations.get(meterIdentifier.getSensorModel());
            RfnMeterIdentifier identifierWithNewModel = new RfnMeterIdentifier(meterIdentifier.getSensorSerialNumber(), meterIdentifier.getSensorManufacturer(), newModelName);
            try {
                RfnMeter result = rfnMeterDao.getMeterForExactIdentifier(identifierWithNewModel);
                // if we find it, we'll use it, but we don't want to change the name back
                log.info("returning newer " + result + " when " + meterIdentifier + " was requested");
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
    public void setRfnMeterDao(RfnMeterDao rfnMeterDao) {
        this.rfnMeterDao = rfnMeterDao;
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