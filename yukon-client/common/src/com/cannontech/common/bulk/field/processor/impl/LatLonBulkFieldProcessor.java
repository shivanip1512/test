package com.cannontech.common.bulk.field.processor.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;

import com.cannontech.common.bulk.field.impl.YukonDeviceDto;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.PaoLocation;
import com.cannontech.core.dao.PaoLocationDao;

public class LatLonBulkFieldProcessor extends BulkYukonDeviceFieldProcessor {

    private PaoLocationDao paoLocationDao;

    @Override
    public void updateField(SimpleDevice identifier, YukonDeviceDto value)
            throws ProcessingException {
        if (value.getLatitude() == null) {
            throw new ProcessingException("Latitude not specified for device with paoId "
                                               + identifier.getPaoIdentifier());
        }
        if (value.getLongitude() == null) {
            throw new ProcessingException("Longitude not specified for device with paoId "
                                               + identifier.getPaoIdentifier());
        }
        try {
            PaoLocation location = new PaoLocation();
            location.setPaoIdentifier(identifier.getPaoIdentifier());
            location.setLatitude(value.getLatitude());
            location.setLongitude(value.getLongitude());
            paoLocationDao.save(location);
        } catch (DataAccessException e) {
            throw new ProcessingException("Could not set location of device with paoId "
                                          + identifier.getPaoIdentifier() + ": "
                                          + e.getMessage(),
                                          e);
        }
    }
    
    @Required
    public void setPaoLocationDao(PaoLocationDao paoLocationDao) {
        this.paoLocationDao = paoLocationDao;
    }
}
