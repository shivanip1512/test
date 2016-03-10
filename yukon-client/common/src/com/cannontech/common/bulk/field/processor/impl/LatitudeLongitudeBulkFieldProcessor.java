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

public class LatitudeLongitudeBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private EndpointEventLogService endpointEventLogService;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) throws ProcessingException {
        if (value.getLatitude() == null || ((value.getLatitude() < -90.0) || (value.getLatitude() > 90.0))) {
            throw new ProcessingException(
                "Valid Latitude (Must be between -90 and 90) not specified for device with paoId "
                    + device.getPaoIdentifier());
        }
        if (value.getLongitude() == null || ((value.getLongitude() < -180.0) || (value.getLongitude() > 180.0))) {
            throw new ProcessingException(
                "Valid Longitude (Must be between -180 and 180) not specified for device with paoId "
                    + device.getPaoIdentifier());
        }
        
        try {
            PaoLocation location =
                new PaoLocation(device.getPaoIdentifier(), value.getLatitude(), value.getLongitude(),
                    Origin.BULK_IMPORT, new Instant());
            paoLocationDao.save(location);
            endpointEventLogService.locationUpdated(device.getPaoIdentifier(), location,
                YukonUserContext.system.getYukonUser());
        } catch (DataAccessException e) {
            throw new ProcessingException("Could not set location of device with paoId " + device.getPaoIdentifier()
                + ": " + e.getMessage(), e);
        }
    }
}
