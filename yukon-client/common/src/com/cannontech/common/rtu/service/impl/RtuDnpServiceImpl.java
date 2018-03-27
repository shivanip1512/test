package com.cannontech.common.rtu.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
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
        LightDeviceConfiguration config =
            configurationDao.findConfigurationForDevice(new SimpleDevice(pao.getPaoIdentifier()));
        DeviceConfiguration deviceConfig =
            configurationDao.getDeviceConfiguration(config.getConfigurationId());
        DNPConfiguration dnp = configurationDao.getDnpConfiguration(deviceConfig);
        rtu.setDnpConfigId(dnp.getConfigurationId());
        List<DisplayableDevice> devices = deviceDao.getChildDevices(id);
        Comparator<DisplayableDevice> comparator = (o1, o2) -> o1.getName().compareTo(o2.getName());
        Collections.sort(devices, comparator);
        rtu.setChildDevices(devices);
        return rtu;
    }
    
    @Override
    public SearchResults<RtuPointDetail> getRtuPointDetail(int rtuId, RtuPointsFilter filter, Direction direction,
            SortBy sortBy, PagingParameters paging) {

        if (filter.getDeviceIds() != null) {
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
    public int save(RtuDnp rtuDnp) {
        if (rtuDnp.getId() == null) {
            create(rtuDnp);
        }
        
        int paoId = rtuDnp.getId();
        
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(paoId);
        DNPBase dnpBase = (DNPBase) dbPersistentDao.retrieveDBPersistent(pao);
        
        setDNPFields(dnpBase, rtuDnp);
        
        // assign DNP config.
        SimpleDevice device = SimpleDevice.of(paoId, dnpBase.getPaoType());
        LightDeviceConfiguration config =
            configurationDao.getLightConfigurationById(rtuDnp.getDnpConfigId());
        try {
            deviceConfigService.assignConfigToDevice(config, device, YukonUserContext.system.getYukonUser(),
                rtuDnp.getName());
        } catch (InvalidDeviceTypeException e) {
            /*
             * This should have already been validated.
             * Even if not, the old (valid) device config will still be in use.
             * Log it and move on.
             */
            log.error("An error occurred attempting to assign a DNP configuration to RTU-DNP " + "'" + rtuDnp.getName()
                + "'. Please assign this device a configuration manually.", e);
        }

        dbPersistentDao.performDBChange(dnpBase, TransactionType.UPDATE);

        return dnpBase.getPAObjectID();
    }

    private DNPBase create(RtuDnp rtuDnp) {
        DNPBase dnpBase = new RTUDnp();
        setDNPFields(dnpBase, rtuDnp);
        
        dbPersistentDao.performDBChange(dnpBase, TransactionType.INSERT);
        DNPBase rtuCreated = (DNPBase) dbPersistentDao.retrieveDBPersistent(dnpBase);
        rtuDnp.setId(rtuCreated.getPAObjectID());
        return rtuCreated;
    }

    private void setDNPFields (DNPBase dnpBase, RtuDnp rtuDnp) {
        dnpBase.setPAOName(rtuDnp.getName());
        dnpBase.setDisableFlag(rtuDnp.isDisableFlag() ? 'Y' : 'N');
        
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
}
