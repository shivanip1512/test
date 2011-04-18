package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.stars.dr.hardware.dao.LMHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;

public class LmHardwareBaseDaoAdapter implements LMHardwareBaseDao {

    @Override
    public boolean update(LMHardwareBase hardwareBase) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LMHardwareBase getById(int inventoryId) throws NotFoundException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LMHardwareBase getBySerialNumber(String serialNumber) throws DataAccessException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<LMHardwareBase> getByLMHardwareTypeId(int typeId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<LMHardwareBase> getByRouteId(int routeId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<LMHardwareBase> getByConfigurationId(int configurationId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<LMHardwareBase> getAll() {
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

}