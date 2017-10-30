package com.cannontech.common.bulk.field.processor.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.EndpointEventLogService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.location.Origin;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class LatitudeLongitudeBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

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
        
        // Otherwise, check for valid values and update lat/long
        if (value.getLatitude() == null || ((value.getLatitude() < -90.0) || (value.getLatitude() > 90.0))) {
            throw new ProcessingException(
                "Valid Latitude (Must be between -90 and 90) not specified for device with paoId "
                    + device.getPaoIdentifier(), "invalidLatitude", "-90", "90", device.getPaoIdentifier());
        }
        if (value.getLongitude() == null || ((value.getLongitude() < -180.0) || (value.getLongitude() > 180.0))) {
            throw new ProcessingException(
                "Valid Longitude (Must be between -180 and 180) not specified for device with paoId "
                    + device.getPaoIdentifier(), "invalidLongitude", "-180", "180", device.getPaoIdentifier());
        }
        
        try {
            PaoLocation location =
                new PaoLocation(device.getPaoIdentifier(), value.getLatitude(), value.getLongitude(),
                    Origin.BULK_IMPORT, new Instant());
            paoLocationDao.save(location);
            endpointEventLogService.locationUpdated(cache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName(), location,
                YukonUserContext.system.getYukonUser());
        } catch (DataAccessException e) {
            throw new ProcessingException("Could not set location of device with paoId " + device.getPaoIdentifier()
                + ": " + e.getMessage(), "setLocation", e, device.getPaoIdentifier());
        }
    }
}
