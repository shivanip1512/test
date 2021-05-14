package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class LmHardwareBaseDaoAdapter implements LmHardwareBaseDao {

    @Override
    public LMHardwareBase getById(int inventoryId) throws NotFoundException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void clearLMHardwareInfo(Integer inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getSerialNumberForDevice(int deviceId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public String getSerialNumberForInventoryId(int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<String> getSerialNumberForInventoryIds(Collection<Integer> inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public PaoIdentifier getDeviceIdBySerialNumber(String serialNumber) {
        throw new UnsupportedOperationException("not implemented");
    }
    
}