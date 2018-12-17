package com.cannontech.common.bulk.field.processor.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.common.validator.YukonValidationUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class LatitudeLongitudeBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    @Autowired private PaoDao paoDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private EndpointEventLogService endpointEventLogService;
    @Autowired private IDatabaseCache cache;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) throws ProcessingException {
        // If both values are empty, remove the lat/long for the device.
        if (value.getLatitude() == null && value.getLongitude() == null) {
            try {
                paoLocationDao.delete(device.getPaoIdentifier().getPaoId());
                return;
            } catch (DataAccessException e) {
                throw new ProcessingException("Could not delete location of device with paoId " + device.getPaoIdentifier()
                    + ": " + e.getMessage(), "deleteLocation", e, device.getPaoIdentifier() );
            }
        }

            locationValidation(device.getPaoIdentifier(), value.getLatitude(), value.getLongitude());

        try {
            PaoLocation location =
                new PaoLocation(device.getPaoIdentifier(), value.getLatitude(), value.getLongitude(),
                    Origin.BULK_IMPORT, new Instant());
            paoLocationDao.save(location);

            String deviceName = value.getName();    // this will only ever be populated if Name is NOT the identifier field (first column)
            if (StringUtils.isBlank(deviceName)) {
                // try to load from cache
                LiteYukonPAObject litePao = cache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId());
                if (litePao != null) {
                    deviceName = litePao.getPaoName();
                } else {
                    deviceName = paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId());
                }
            }
            endpointEventLogService.locationUpdated(deviceName, location, YukonUserContext.system.getYukonUser());
        } catch (DataAccessException e) {
            throw new ProcessingException("Could not set location of device with paoId " + device.getPaoIdentifier()
                + ": " + e.getMessage(), "setLocation", e, device.getPaoIdentifier());
        }
    }

    public void locationValidation(PaoIdentifier paoIdentifier, Double latitude, Double longitude) {

        boolean isLatitudeValid = YukonValidationUtils.isLatitudeInRange(latitude);
        if (!isLatitudeValid) {
            throw new ProcessingException(
                "Valid Latitude (Must be between -90 and 90) not specified for device with paoId " + paoIdentifier,
                "invalidLatitude", "-90", "90", paoIdentifier);
        }

        boolean isLongitudeValid = YukonValidationUtils.isLongitudeInRange(longitude);
        if (!isLongitudeValid) {
            throw new ProcessingException(
                "Valid Longitude (Must be between -180 and 180) not specified for device with paoId " + paoIdentifier,
                "invalidLongitude", "-180", "180", paoIdentifier);
        }
    }
}
