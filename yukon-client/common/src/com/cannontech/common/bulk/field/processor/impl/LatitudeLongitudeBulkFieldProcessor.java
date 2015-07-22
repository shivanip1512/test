package com.cannontech.common.bulk.field.processor.impl;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.events.loggers.BulkImportEventLogService;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.rfn.message.location.Origin;

public class LatitudeLongitudeBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private BulkImportEventLogService bulkImportEventLogService;
    
    @Override
    public void updateField(SimpleDevice device, YukonDeviceDto value) throws ProcessingException {
        if (value.getLatitude() == null) {
            throw new ProcessingException("Latitude not specified for device with paoId " + device.getPaoIdentifier());
        }
        if (value.getLongitude() == null) {
            throw new ProcessingException("Longitude not specified for device with paoId " + device.getPaoIdentifier());
        }
        try {
            PaoLocation location =
                new PaoLocation(device.getPaoIdentifier(), value.getLatitude(), value.getLongitude(),
                    Origin.BULK_IMPORT, new Instant());
            paoLocationDao.save(location);
            bulkImportEventLogService.locationUpdated(device.getPaoIdentifier(),
                String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()),
                location.getOrigin().name());
        } catch (DataAccessException e) {
            throw new ProcessingException("Could not set location of device with paoId " + device.getPaoIdentifier()
                + ": " + e.getMessage(), e);
        }
    }
}
