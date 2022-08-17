package com.cannontech.common.rtu.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.DisplayableDevice;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.rtu.dao.RtuDnpDao;
import com.cannontech.common.rtu.dao.RtuDnpDao.SortBy;
import com.cannontech.common.rtu.model.RtuDnp;
import com.cannontech.common.rtu.model.RtuPointDetail;
import com.cannontech.common.rtu.model.RtuPointsFilter;
import com.cannontech.common.rtu.service.RtuDnpService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.RTUDnp;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class RtuDnpServiceImpl implements RtuDnpService {

    @Autowired private IDatabaseCache dbCache;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DeviceConfigurationDao configurationDao;
    @Autowired private DeviceDao deviceDao;
    @Autowired private RtuDnpDao rtuDnpDao;
    @Autowired private DeviceConfigurationService deviceConfigService;
    
    private static final Logger log = YukonLogManager.getLogger(RtuDnpServiceImpl.class);
    
    @Override
    public RtuDnp getRtuDnp(int id) {
        RtuDnp rtu = new RtuDnp();
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        rtu.setName(pao.getPaoName());
        rtu.setId(pao.getPaoIdentifier().getPaoId());
        rtu.setPaoType(pao.getPaoType());
        rtu.setDisableFlag(pao.getDisableFlag().equals(YNBoolean.YES.getDatabaseRepresentation()));
        DBPersistent dbPersistent = dbPersistentDao.retrieveDBPersistent(pao);
        DNPBase dnpBase = (DNPBase) dbPersistent;
        rtu.setDeviceScanRateMap(dnpBase.getDeviceScanRateMap());
        rtu.setDeviceWindow(dnpBase.getDeviceWindow());
        rtu.setDeviceAddress(dnpBase.getDeviceAddress());
        rtu.setDeviceDirectCommSettings(dnpBase.getDeviceDirectCommSettings());
        rtu.setIpAddress(dnpBase.getIpAddress());
        rtu.setPort(dnpBase.getPort());
        LightDeviceConfiguration config =
            configurationDao.findConfigurationForDevice(new SimpleDevice(pao.getPaoIdentifier()));
        Integer configId = null;
        if (config != null) {
            DeviceConfiguration deviceConfig =
                    configurationDao.getDeviceConfiguration(config.getConfigurationId());
            configId = deviceConfig.getConfigurationId();
        } else {
            DeviceConfiguration configuration = configurationDao.getDefaultDNPConfiguration();
            configId = configuration.getConfigurationId();
        }
        rtu.setDnpConfigId(configId);
        List<DisplayableDevice> devices = deviceDao.getChildDevices(id);
        Comparator<DisplayableDevice> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
        Collections.sort(devices, comparator);
        rtu.setChildDevices(devices);
        return rtu;
    }
    
    @Override
    public SearchResults<RtuPointDetail> getRtuPointDetail(int rtuId, RtuPointsFilter filter, Direction direction,
            SortBy sortBy, PagingParameters paging) {

        if (filter.getDeviceIds() != null && !filter.getDeviceIds().isEmpty()) {
            return rtuDnpDao.getRtuPointDetail(filter.getDeviceIds(), filter.getPointNames(), filter.getTypes(),
                direction, sortBy, paging);
        } else {
            return rtuDnpDao.getRtuPointDetail(getParentAndChildDevices(rtuId), filter.getPointNames(), filter.getTypes(),
                direction, sortBy, paging);
        }
    }

    @Override
    public List<RtuPointDetail> getRtuPointDetail(int rtuId) {
        return getRtuPointDetail(rtuId, new RtuPointsFilter(), Direction.desc, SortBy.DEVICE_NAME,
            PagingParameters.EVERYTHING).getResultList();

    }

    @Override
    public List<Integer> getParentAndChildDevices(int rtuId){
        List<Integer> paoIds = new ArrayList<>();
        paoIds.add(rtuId);
        paoIds.addAll(deviceDao.getChildDevices(rtuId).stream()
                                                      .map(p -> p.getPaoIdentifier().getPaoId())
                                                      .collect(Collectors.toList()));
        return paoIds;
    }

    @Override
    public int save(RtuDnp rtuDnpModel) {
        RTUDnp rtuDnp = buildRtuDbPersistent(rtuDnpModel);
        
        Integer paoId = rtuDnp.getPAObjectID();
        if (paoId == null) {
            dbPersistentDao.performDBChange(rtuDnp, TransactionType.INSERT);
        }
        
        // assign DNP config.
        SimpleDevice device = SimpleDevice.of(rtuDnp.getPAObjectID(), rtuDnp.getPaoType());
        LightDeviceConfiguration config =
            configurationDao.getLightConfigurationById(rtuDnpModel.getDnpConfigId());
        try {
            deviceConfigService.assignConfigToDevice(config, device, YukonUserContext.system.getYukonUser(),
                rtuDnpModel.getName());
        } catch (InvalidDeviceTypeException e) {
            /*
             * This should have already been validated.
             * Even if not, the old (valid) device config will still be in use.
             * Log it and move on.
             */
            log.error("An error occurred attempting to assign a DNP configuration to RTU-DNP " + "'" + rtuDnpModel.getName()
                + "'. Please assign this device a configuration manually.", e);
        }

        dbPersistentDao.performDBChange(rtuDnp, TransactionType.UPDATE);

        return rtuDnp.getPAObjectID();
    }

    private void setScanRate(DNPBase dnpBase, RtuDnp rtuDnp, String typeIntegrity) {
        if (rtuDnp.getDeviceScanRateMap().containsKey(typeIntegrity)) {
            DeviceScanRate scanRate = new DeviceScanRate();
            if(rtuDnp.getId() != null) {
                scanRate.setDeviceID(rtuDnp.getId());
            }
            scanRate.setScanType(typeIntegrity);
            DeviceScanRate newScanRate = rtuDnp.getDeviceScanRateMap().get(typeIntegrity);
            scanRate.setAlternateRate(newScanRate.getAlternateRate());
            scanRate.setScanGroup(newScanRate.getScanGroup());
            scanRate.setIntervalRate(newScanRate.getIntervalRate());
            dnpBase.getDeviceScanRateMap().put(typeIntegrity, scanRate);
        }
    }

    private RTUDnp buildRtuDbPersistent(RtuDnp rtuDnp) {
        RTUDnp dnpBase = new RTUDnp();  
        if (rtuDnp.getId() != null) {
            // set dnpBase to existing data.
            LiteYukonPAObject pao = dbCache.getAllPaosMap().get(rtuDnp.getId());
            dnpBase = (RTUDnp) dbPersistentDao.retrieveDBPersistent(pao);
        }
        
        dnpBase.setPAOName(rtuDnp.getName());
        dnpBase.setDisabled(rtuDnp.isDisableFlag());
        
        dnpBase.getDeviceAddress().setMasterAddress(rtuDnp.getDeviceAddress().getMasterAddress());
        dnpBase.getDeviceAddress().setSlaveAddress(rtuDnp.getDeviceAddress().getSlaveAddress());
        dnpBase.getDeviceAddress().setPostCommWait(rtuDnp.getDeviceAddress().getPostCommWait());
        dnpBase.getDeviceDirectCommSettings().setPortID(rtuDnp.getDeviceDirectCommSettings().getPortID());
        
        dnpBase.getDeviceWindow().setType(rtuDnp.getDeviceWindow().getType());
        dnpBase.getDeviceWindow().setWinOpen(rtuDnp.getDeviceWindow().getWinOpen());
        dnpBase.getDeviceWindow().setWinClose(rtuDnp.getDeviceWindow().getWinClose());
        
        DeviceConfiguration configuration = configurationDao.getDeviceConfiguration(rtuDnp.getDnpConfigId());
        DNPConfiguration dnpConfig = configurationDao.getDnpConfiguration(configuration);
        dnpBase.setDnpConfiguration(dnpConfig);
        
        dnpBase.setDeviceScanRateMap(new HashMap<String, DeviceScanRate>());
        setScanRate(dnpBase, rtuDnp, DeviceScanRate.TYPE_INTEGRITY);
        setScanRate(dnpBase, rtuDnp, DeviceScanRate.TYPE_EXCEPTION);
        PaoType paoType = rtuDnp.getPaoType();
        int portId = dnpBase.getDeviceDirectCommSettings().getPortID();

        if (paoType.isTcpPortEligible() && DeviceTypesFuncs.isTcpPort(portId)) {
            dnpBase.setIpAddress(rtuDnp.getIpAddress());
            dnpBase.setPort(rtuDnp.getPort());
        }

        return dnpBase;
    }
}
