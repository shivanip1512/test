package com.cannontech.common.device.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.definition.service.PaoDefinitionService.PointTemplateTransferPair;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController702x;
import com.cannontech.database.data.capcontrol.CapBankControllerDNP;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceFactory;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.RfnBase;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.device.range.DeviceAddressRange;
import com.cannontech.device.range.RangeBase;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

public class DeviceUpdateServiceImpl implements DeviceUpdateService {

    private DeviceDao deviceDao = null;
    private PaoDao paoDao = null;
    private RouteDiscoveryService routeDiscoveryService = null;
    private CommandRequestDeviceExecutor commandRequestDeviceExecutor = null;
    private PaoDefinitionService paoDefinitionService = null;
    private PointService pointService = null;
    private PointCreationService pointCreationService = null;
    private PaoGroupsWrapper paoGroupsWrapper = null;
    private DBPersistentDao dbPersistentDao = null;
    
    private Logger log = YukonLogManager.getLogger(DeviceUpdateServiceImpl.class);
    
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException {
    	
    	RangeBase rangeBase = DeviceAddressRange.getRangeBase(device.getPaoIdentifier().getPaoType());
        boolean validAddressForType = rangeBase.isValidRange(newAddress);

        if (!validAddressForType) {
            throw new IllegalArgumentException("Address not in valid range for device type: " + newAddress);
        }

        deviceDao.changeAddress(device, newAddress);
    }

    public void changeRoute(YukonDevice device, String newRouteName) throws IllegalArgumentException {

        Integer routeId = paoDao.getRouteIdForRouteName(newRouteName);

        if (routeId == null) {
            throw new IllegalArgumentException("Invalid route name: " + newRouteName);
        }

        deviceDao.changeRoute(device, routeId);
    }
    
    public void changeRoute(YukonDevice device, int newRouteId) throws IllegalArgumentException {

        deviceDao.changeRoute(device, newRouteId);
    }

    public void changeMeterNumber(YukonDevice device, String newMeterNumber) throws IllegalArgumentException {
    
        if (StringUtils.isBlank(newMeterNumber)) {
            throw new IllegalArgumentException("Blank meter number.");
        }
        
        deviceDao.changeMeterNumber(device, newMeterNumber);
    }
    
    public void routeDiscovery(final YukonDevice device, List<Integer> routeIds, final LiteYukonUser liteYukonUser) {
        
        // callback to set routeId and do putconfig when route is discovered
        SimpleCallback<Integer> routeFoundCallback = new SimpleCallback<Integer> () {
            
            @Override
            public void handle(Integer routeId) throws Exception {
                
                if (routeId == null) {
                    
                    log.warn("Route was not found for device '" + paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()) + "' (" + device.getPaoIdentifier() + ").");
                
                } else {
                    
                    // update route
                    changeRoute(device, routeId);

                    // putconfig command
                    if (DeviceTypesFuncs.isMCT410(device.getPaoIdentifier().getPaoType().getDeviceTypeId())) {

                        CommandRequestDevice configCmd = new CommandRequestDevice();
                        configCmd.setDevice(new SimpleDevice(device.getPaoIdentifier()));
                        configCmd.setCommand("putconfig emetcon intervals");

                        CommandCompletionCallbackAdapter<CommandRequestDevice> dummyCallback = new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
                        };
                        
                        commandRequestDeviceExecutor.execute(Collections.singletonList(configCmd), dummyCallback, DeviceRequestType.ROUTE_DISCOVERY_PUTCONFIG_COMMAND, liteYukonUser);
                    }
                }
            }
        };
        
        // start route discovery
        routeDiscoveryService.routeDiscovery(device, routeIds, routeFoundCallback, liteYukonUser);
    }
    
    @Transactional
    public SimpleDevice changeDeviceType(YukonDevice currentDevice,
            PaoDefinition newDefinition) {
        
        DeviceBase yukonPAObject = (DeviceBase) PAOFactory.createPAObject(currentDevice.getPaoIdentifier().getPaoId());
        
        // Load the device to change
        try {
            Transaction<?> t = Transaction.createTransaction(Transaction.RETRIEVE,
                                                          yukonPAObject);

            yukonPAObject = (DeviceBase) t.execute();
        } catch (TransactionException e) {
            throw new DataRetrievalFailureException("Could not load device from db", e);
        }

        // Change the device's type
        DeviceBase changedDevice = this.changeDeviceType(yukonPAObject, newDefinition);
        
        // Save the changes
        try {
            Transaction<?> t = Transaction.createTransaction(Transaction.UPDATE, changedDevice);
            t.execute();
        } catch(TransactionException e) {
            throw new PersistenceException("Could not save device type change", e);
        }
        
        DBChangeMsg[] changeMsgs = changedDevice.getDBChangeMsgs(DbChangeType.UPDATE);
        // Send DBChangeMsgs
        for (DBChangeMsg msg : changeMsgs) {
            dbPersistentDao.processDBChange(msg);
        }
        
        return new SimpleDevice(changedDevice.getDevice().getDeviceID(), paoGroupsWrapper.getDeviceType(changedDevice.getPAOType()));
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public DeviceBase changeDeviceType(DeviceBase currentDevice, PaoDefinition newDefinition) {

        DeviceBase oldDevice = null;

        // get a deep copy of the current device
        try {
            oldDevice = (DeviceBase) CtiUtilities.copyObject(currentDevice);

            Transaction t = Transaction.createTransaction(Transaction.DELETE_PARTIAL,
                                                          ((DBPersistent) currentDevice));

            currentDevice = (DeviceBase) t.execute();

        } catch (Exception e) {
            CTILogger.error(e);
            CTILogger.info("*** An exception occured when trying to change type of "
                           + currentDevice + ", action aborted.");

            return currentDevice;
        }

        // create a brand new DeviceBase of the new type
        DeviceBase newDevice = DeviceFactory.createDevice(newDefinition.getType().getDeviceTypeId());

        // set all the device specific stuff here
        newDevice.setDevice(oldDevice.getDevice());
        newDevice.setPAOName(oldDevice.getPAOName());
        newDevice.setDisableFlag(oldDevice.getPAODisableFlag());
        newDevice.setPAOStatistics(oldDevice.getPAOStatistics());

        // remove then add the new elements for PAOExclusion
        newDevice.getPAOExclusionVector().removeAllElements();
        newDevice.getPAOExclusionVector().addAll(oldDevice.getPAOExclusionVector());

        if (newDevice instanceof CarrierBase && oldDevice instanceof CarrierBase) {
            ((CarrierBase) newDevice).getDeviceCarrierSettings()
            .setAddress(((CarrierBase) oldDevice).getDeviceCarrierSettings()
                        .getAddress());

            ((CarrierBase) newDevice).getDeviceRoutes()
            .setRouteID(((CarrierBase) oldDevice).getDeviceRoutes()
                        .getRouteID());

        } else if (newDevice instanceof IGroupRoute && oldDevice instanceof IGroupRoute) {
            ((IGroupRoute) newDevice).setRouteID(((IGroupRoute) oldDevice).getRouteID());
        } else if (newDevice instanceof IDLCBase && oldDevice instanceof IDLCBase) {
            ((IDLCBase) newDevice).getDeviceIDLCRemote()
            .setAddress(((IDLCBase) oldDevice).getDeviceIDLCRemote()
                        .getAddress());
        }

        if (newDevice instanceof RemoteBase && oldDevice instanceof RemoteBase) {
            ((RemoteBase) newDevice).getDeviceDirectCommSettings()
            .setPortID(((RemoteBase) oldDevice).getDeviceDirectCommSettings()
                       .getPortID());
        }

        if (newDevice instanceof IDeviceMeterGroup && oldDevice instanceof IDeviceMeterGroup) {
            ((IDeviceMeterGroup) newDevice).setDeviceMeterGroup(((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup());
        }
        
        if (newDevice instanceof RfnBase && oldDevice instanceof RfnBase) {
            ((RfnBase) newDevice).setRfnAddress((((RfnBase) oldDevice).getRfnAddress()));
        }

        if (newDevice instanceof TwoWayDevice && oldDevice instanceof TwoWayDevice) {
            ((TwoWayDevice) newDevice).setDeviceScanRateMap(((TwoWayDevice) oldDevice).getDeviceScanRateMap());
        }

        if (newDevice instanceof CapBankController && oldDevice instanceof CapBankController) {
            ((CapBankController) newDevice).setDeviceCBC(((CapBankController) oldDevice).getDeviceCBC());
        }

        if (newDevice instanceof CapBankController702x
                && oldDevice instanceof CapBankController702x) {
            ((CapBankController702x) newDevice).setDeviceAddress(((CapBankController702x) oldDevice).getDeviceAddress());
            ((CapBankController702x) newDevice).setDeviceCBC(((CapBankController702x) oldDevice).getDeviceCBC());
        }
        
        if (newDevice instanceof CapBankControllerDNP
                && oldDevice instanceof CapBankControllerDNP) {
            ((CapBankControllerDNP) newDevice).setDeviceAddress(((CapBankControllerDNP) oldDevice).getDeviceAddress());
            ((CapBankControllerDNP) newDevice).setDeviceCBC(((CapBankControllerDNP) oldDevice).getDeviceCBC());
        }
        
        if (newDevice instanceof MCTBase && oldDevice instanceof MCTBase ) {
            ((MCTBase)newDevice).setDeviceLoadProfile(((MCTBase)oldDevice).getDeviceLoadProfile());
            ((MCTBase)newDevice).setConfigMapping(((MCTBase)oldDevice).getConfigMapping());

            if ( newDevice instanceof MCT400SeriesBase && oldDevice instanceof MCT400SeriesBase &&
                    (DeviceTypesFuncs.isMCT410(PAOGroups.getDeviceType(newDevice.getPAOType())) && 
                     DeviceTypesFuncs.isMCT410(PAOGroups.getDeviceType(oldDevice.getPAOType())))) {
                ((MCT400SeriesBase) newDevice).setDeviceMCT400Series(((MCT400SeriesBase)oldDevice).getDeviceMCT400Series());
            }
        }

        try {
            Transaction t = Transaction.createTransaction(Transaction.ADD_PARTIAL,
                                                          ((DBPersistent) newDevice));
            newDevice = (DeviceBase) t.execute();

            this.removePoints(oldDevice, newDefinition);
            this.addPoints(oldDevice, newDefinition);
            this.transferPoints(oldDevice, newDefinition);

        } catch (TransactionException e) {
            CTILogger.error(e.getMessage(), e);

        }

        return newDevice;
    }
    
    /**
     * Helper method to remove unsupported points from a device that is being
     * changed into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void removePoints(DeviceBase device, PaoDefinition newDefinition)
    throws TransactionException {

    	SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Set<PointIdentifier> removeTemplates = paoDefinitionService.getPointTemplatesToRemove(yukonDevice, newDefinition);

        SimpleDevice meter = deviceDao.getYukonDeviceForDevice(device);

        for (PointIdentifier identifier : removeTemplates) {
            LitePoint litePoint = pointService.getPointForPao(meter, identifier);

            log.debug("Remove point: deviceId=" + device.getPAObjectID() + litePoint.getPointName() + " type=" + litePoint.getPointType() + " offset=" + litePoint.getPointOffset());
            
            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);
            Transaction<?> t = Transaction.createTransaction(Transaction.DELETE, point);
            t.execute();
        }
    }

    /**
     * Helper method to add supported points to a device that is being changed
     * into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void addPoints(DeviceBase device, PaoDefinition newDefinition)
    throws TransactionException {

    	SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Set<PointTemplate> addTemplates = paoDefinitionService.getPointTemplatesToAdd(yukonDevice, newDefinition);
        for (PointTemplate template : addTemplates) {
        	
        	log.debug("Add point: deviceId=" + device.getPAObjectID() + " point name=" + template.getName() + " type=" + template.getType() + " offset=" + template.getOffset());
        	
            PointBase point = pointCreationService.createPoint(device.getDevice().getDeviceID(), template);

            Transaction<?> t = Transaction.createTransaction(Transaction.INSERT, point);
            t.execute();
        }

    }

    /**
     * Helper method to transfer supported points from a device that is being
     * changed into another device type
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws TransactionException
     */
    private void transferPoints(DeviceBase device, PaoDefinition newDefinition)
    throws TransactionException {

    	SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Iterable<PointTemplateTransferPair> transferTemplates = paoDefinitionService.getPointTemplatesToTransfer(yukonDevice,
                                                                                newDefinition);

        SimpleDevice meter = deviceDao.getYukonDeviceForDevice(device);

        for (PointTemplateTransferPair pair : transferTemplates) {
        	
        	log.debug("Transfer point: deviceId=" + device.getPAObjectID() +
        			" oldType=" + pair.oldDefinitionTemplate.getType() + 
        			" old offset=" + pair.oldDefinitionTemplate.getOffset() + 
        			" new type=" + pair.newDefinitionTemplate.getType() +
        			" new offset=" + pair.newDefinitionTemplate.getOffset());
            
            LitePoint litePoint = pointService.getPointForPao(meter, pair.oldDefinitionTemplate);
            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);

            Transaction<PointBase> t = Transaction.createTransaction(Transaction.RETRIEVE, point);
            point = t.execute();

            point = PointUtil.changePointType(point, pair.newDefinitionTemplate);
        }

    }

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setRouteDiscoveryService(
            RouteDiscoveryService routeDiscoveryService) {
        this.routeDiscoveryService = routeDiscoveryService;
    }
    
    @Autowired
    public void setCommandRequestDeviceExecutor(
            CommandRequestDeviceExecutor commandRequestDeviceExecutor) {
        this.commandRequestDeviceExecutor = commandRequestDeviceExecutor;
    }
    
    @Autowired
    public void setPaoDefinitionService(
			PaoDefinitionService paoDefinitionService) {
		this.paoDefinitionService = paoDefinitionService;
	}
    
    @Autowired
    public void setPointService(PointService pointService) {
		this.pointService = pointService;
	}
    
    @Autowired
    public void setPointCreationService(PointCreationService pointCreationService) {
		this.pointCreationService = pointCreationService;
	}
    
    @Autowired
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
		this.paoGroupsWrapper = paoGroupsWrapper;
	}
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
		this.dbPersistentDao = dbPersistentDao;
	}
}
