package com.cannontech.common.device.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.definition.service.PaoDefinitionService.PointTemplateTransferPair;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
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
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class DeviceUpdateServiceImpl implements DeviceUpdateService {

    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private DbChangeManager dbChangeManager;
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DlcAddressRangeService dlcAddressRangeService;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PaoDefinitionService paoDefinitionService;
    @Autowired private PointCreationService pointCreationService;
    @Autowired private PointService pointService;
    @Autowired private RouteDiscoveryService routeDiscoveryService;

    private final Logger log = YukonLogManager.getLogger(DeviceUpdateServiceImpl.class);

    @Override
    public void changeAddress(YukonDevice device, int newAddress) throws IllegalArgumentException {

        if (!dlcAddressRangeService.isValidEnforcedAddress(device.getPaoIdentifier().getPaoType(), newAddress)) {
            throw new IllegalArgumentException("Address not in valid range for device type: " + newAddress);
        }

        deviceDao.changeAddress(device, newAddress);
    }

    @Override
    public void changeRoute(YukonDevice device, String newRouteName) throws IllegalArgumentException {

        Integer routeId = paoDao.getRouteIdForRouteName(newRouteName);

        if (routeId == null) {
            throw new IllegalArgumentException("Invalid route name: " + newRouteName);
        }

        deviceDao.changeRoute(device, routeId);
    }

    @Override
    public void changeRoute(YukonDevice device, int newRouteId) throws IllegalArgumentException {

        deviceDao.changeRoute(device, newRouteId);
    }

    @Override
    public void routeDiscovery(final YukonDevice device, List<Integer> routeIds, final LiteYukonUser liteYukonUser) {

        // callback to set routeId and do putconfig when route is discovered
        SimpleCallback<Integer> routeFoundCallback = new SimpleCallback<Integer>() {

            @Override
            public void handle(Integer routeId) throws Exception {

                if (routeId == null) {

                    log.warn("Route was not found for device '"
                        + paoDao.getYukonPAOName(device.getPaoIdentifier().getPaoId()) + "' ("
                        + device.getPaoIdentifier() + ").");

                } else {

                    // update route
                    changeRoute(device, routeId);

                    // putconfig command
                    if (DeviceTypesFuncs.isMCT410(device.getPaoIdentifier().getPaoType())) {

                        CommandRequestDevice configCmd = new CommandRequestDevice();
                        configCmd.setDevice(new SimpleDevice(device.getPaoIdentifier()));
                        configCmd.setCommandCallback(new CommandCallbackBase("putconfig emetcon intervals"));

                        CommandCompletionCallbackAdapter<CommandRequestDevice> dummyCallback =
                            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {
                            };

                        commandRequestDeviceExecutor.execute(Collections.singletonList(configCmd), dummyCallback,
                            DeviceRequestType.ROUTE_DISCOVERY_PUTCONFIG_COMMAND, liteYukonUser);
                    }
                }
            }
        };

        // start route discovery
        routeDiscoveryService.routeDiscovery(device, routeIds, routeFoundCallback, liteYukonUser);
    }

    @Override
    @Transactional
    public SimpleDevice changeDeviceType(YukonDevice currentDevice, PaoDefinition newDefinition) {

        DeviceBase yukonPAObject = (DeviceBase) PAOFactory.createPAObject(currentDevice.getPaoIdentifier().getPaoId());

        // Load the device to change
        try {
            dbPersistentDao.retrieveDBPersistent(yukonPAObject);
        } catch (PersistenceException e) {
            throw new DataRetrievalFailureException("Could not load device from db", e);
        }

        // Change the device's type
        DeviceBase changedDevice = this.changeDeviceType(yukonPAObject, newDefinition);

        // Save the changes
        try {
            dbPersistentDao.performDBChangeWithNoMsg(changedDevice, TransactionType.UPDATE);
        } catch (PersistenceException e) {
            throw new PersistenceException("Could not save device type change", e);
        }

        // meters with a device config should only retain the config if it's appropriate for the new type
        YukonDevice device = deviceDao.getYukonDevice(changedDevice.getPAObjectID());
        LightDeviceConfiguration config = deviceConfigurationDao.findConfigurationForDevice(device);
        if (config != null) {
            boolean configSupported =
                deviceConfigurationDao.isTypeSupportedByConfiguration(config, device.getPaoIdentifier().getPaoType());
            if (!configSupported) {
                try {
                    deviceConfigurationService.unassignConfig(device);
                } catch (InvalidDeviceTypeException e) {
                    log.error("Unable to remove device config on type change.", e);
                }
            }
        }

        DBChangeMsg[] changeMsgs = changedDevice.getDBChangeMsgs(DbChangeType.UPDATE);
        // Send DBChangeMsgs
        for (DBChangeMsg msg : changeMsgs) {
            dbChangeManager.processDbChange(msg);
        }

        return new SimpleDevice(changedDevice.getDevice().getDeviceID(), changedDevice.getPaoType());
    }

    @Override
    @Transactional
    public DeviceBase changeDeviceType(DeviceBase currentDevice, PaoDefinition newDefinition) {

        DeviceBase oldDevice = null;

        // get a deep copy of the current device
        try {
            oldDevice = (DeviceBase) CtiUtilities.copyObject(currentDevice);
            dbPersistentDao.performDBChangeWithNoMsg(currentDevice, TransactionType.DELETE_PARTIAL);
        } catch (Exception e) {
            CTILogger.error(e);
            CTILogger.info("*** An exception occured when trying to change type of " + currentDevice
                + ", action aborted.");

            return currentDevice;
        }

        // create a brand new DeviceBase of the new type
        DeviceBase newDevice = DeviceFactory.createDevice(newDefinition.getType());

        // set all the device specific stuff here
        newDevice.setDevice(oldDevice.getDevice());
        newDevice.setPAOName(oldDevice.getPAOName());
        newDevice.setDisableFlag(oldDevice.getPAODisableFlag());
        newDevice.setPAOStatistics(oldDevice.getPAOStatistics());

        // remove then add the new elements for PAOExclusion
        newDevice.getPAOExclusionVector().removeAllElements();
        newDevice.getPAOExclusionVector().addAll(oldDevice.getPAOExclusionVector());

        if (newDevice instanceof CarrierBase && oldDevice instanceof CarrierBase) {
            ((CarrierBase) newDevice).getDeviceCarrierSettings().setAddress(
                ((CarrierBase) oldDevice).getDeviceCarrierSettings().getAddress());

            ((CarrierBase) newDevice).getDeviceRoutes().setRouteID(
                ((CarrierBase) oldDevice).getDeviceRoutes().getRouteID());

        } else if (newDevice instanceof IGroupRoute && oldDevice instanceof IGroupRoute) {
            ((IGroupRoute) newDevice).setRouteID(((IGroupRoute) oldDevice).getRouteID());
        } else if (newDevice instanceof IDLCBase && oldDevice instanceof IDLCBase) {
            ((IDLCBase) newDevice).getDeviceIDLCRemote().setAddress(
                ((IDLCBase) oldDevice).getDeviceIDLCRemote().getAddress());
        }

        if (newDevice instanceof RemoteBase && oldDevice instanceof RemoteBase) {
            ((RemoteBase) newDevice).getDeviceDirectCommSettings().setPortID(
                ((RemoteBase) oldDevice).getDeviceDirectCommSettings().getPortID());
        }

        if (newDevice instanceof IDeviceMeterGroup && oldDevice instanceof IDeviceMeterGroup) {
            ((IDeviceMeterGroup) newDevice).setDeviceMeterGroup(((IDeviceMeterGroup) oldDevice).getDeviceMeterGroup());
        }

        if (newDevice instanceof RfnBase && oldDevice instanceof RfnBase) {
            RfnAddress rfnAddress = (((RfnBase) oldDevice).getRfnAddress());
            List<RfnManufacturerModel> rfnManufacturerModels = RfnManufacturerModel.getForType(newDefinition.getType());

            Map<String, RfnManufacturerModel> models =
                Maps.uniqueIndex(rfnManufacturerModels, new Function<RfnManufacturerModel, String>() {
                    @Override
                    public String apply(RfnManufacturerModel from) {
                        return from.getModel();
                    }
                });

            /*
             * For RFN-410fD the we can choose from 2 models FocusAXR-SD and FocusAXD-SD.
             * If RFN-420fD is changed to RFN-410fD model should remain the same as the model on RFN-420fD
             * which is FocusAXR-SD
             */
            if (!models.containsKey(rfnAddress.getModel())) {
                // update model only if the model for the new device is not a valid model choice for the old
                // device
                rfnAddress.setModel(rfnManufacturerModels.get(0).getModel());
            }
            ((RfnBase) newDevice).setRfnAddress(rfnAddress);
        }

        if (newDevice instanceof TwoWayDevice && oldDevice instanceof TwoWayDevice) {
            ((TwoWayDevice) newDevice).setDeviceScanRateMap(((TwoWayDevice) oldDevice).getDeviceScanRateMap());
        }

        if (newDevice instanceof CapBankController && oldDevice instanceof CapBankController) {
            ((CapBankController) newDevice).setDeviceCBC(((CapBankController) oldDevice).getDeviceCBC());
        }

        if (newDevice instanceof CapBankController702x && oldDevice instanceof CapBankController702x) {
            ((CapBankController702x) newDevice).setDeviceAddress(((CapBankController702x) oldDevice).getDeviceAddress());
            ((CapBankController702x) newDevice).setDeviceCBC(((CapBankController702x) oldDevice).getDeviceCBC());
        }

        if (newDevice instanceof CapBankControllerDNP && oldDevice instanceof CapBankControllerDNP) {
            ((CapBankControllerDNP) newDevice).setDeviceAddress(((CapBankControllerDNP) oldDevice).getDeviceAddress());
            ((CapBankControllerDNP) newDevice).setDeviceCBC(((CapBankControllerDNP) oldDevice).getDeviceCBC());
        }

        if (newDevice instanceof MCTBase && oldDevice instanceof MCTBase) {
            ((MCTBase) newDevice).setDeviceLoadProfile(((MCTBase) oldDevice).getDeviceLoadProfile());
            ((MCTBase) newDevice).setConfigMapping(((MCTBase) oldDevice).getConfigMapping());

            if (newDevice instanceof MCT400SeriesBase
                && oldDevice instanceof MCT400SeriesBase
                && (DeviceTypesFuncs.isMCT410(newDevice.getPaoType()) && DeviceTypesFuncs.isMCT410(oldDevice.getPaoType()))) {
                ((MCT400SeriesBase) newDevice).setDeviceMCT400Series(((MCT400SeriesBase) oldDevice).getDeviceMCT400Series());
            }
        }

        try {
            dbPersistentDao.performDBChangeWithNoMsg(newDevice, TransactionType.ADD_PARTIAL);

            this.removePoints(oldDevice, newDefinition);
            this.addPoints(oldDevice, newDefinition);
            this.transferPoints(oldDevice, newDefinition);

        } catch (PersistenceException e) {
            CTILogger.error(e.getMessage(), e);

        }
        return newDevice;
    }

    /**
     * Helper method to remove unsupported points from a device that is being
     * changed into another device type
     * Sends DBChangeMsg for each deleted point.
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws PersistenceException 
     */
    private void removePoints(DeviceBase device, PaoDefinition newDefinition) throws PersistenceException {

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Set<PointIdentifier> removeTemplates = paoDefinitionService.getPointTemplatesToRemove(yukonDevice, newDefinition);

        for (PointIdentifier identifier : removeTemplates) {
            LitePoint litePoint = pointService.getPointForPao(yukonDevice, identifier);

            log.debug("Remove point: deviceId=" + device.getPAObjectID() + litePoint.getPointName() + " type="
                + litePoint.getPointType() + " offset=" + litePoint.getPointOffset());

            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);
            dbPersistentDao.performDBChange(point, TransactionType.DELETE);
        }
    }

    /**
     * Helper method to add supported points to a device that is being changed
     * into another device type
     * Sends DBChangeMsg for each added point.
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws PersistenceException
     */
    private void addPoints(DeviceBase device, PaoDefinition newDefinition) throws PersistenceException {

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Set<PointTemplate> addTemplates = paoDefinitionService.getPointTemplatesToAdd(yukonDevice, newDefinition);
        for (PointTemplate template : addTemplates) {

            log.debug("Add point: deviceId=" + device.getPAObjectID() + " point name=" + template.getName() + " type="
                + template.getPointType().getPointTypeId() + " offset=" + template.getOffset());

            PointBase point = pointCreationService.createPoint(yukonDevice.getPaoIdentifier(), template);
            dbPersistentDao.performDBChange(point, TransactionType.INSERT);
        }
    }

    /**
     * Helper method to transfer supported points from a device that is being
     * changed into another device type
     * Sends DBChangeMsg for each transfered (aka changed) point. (ala call to changePointType) 
     * @param device - Device to change type
     * @param newDefinition - Definition of new device type
     * @throws PersistenceException
     */
    private void transferPoints(DeviceBase device, PaoDefinition newDefinition) throws PersistenceException {

        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(device);
        Iterable<PointTemplateTransferPair> transferTemplates =
            paoDefinitionService.getPointTemplatesToTransfer(yukonDevice, newDefinition);

        for (PointTemplateTransferPair pair : transferTemplates) {

            log.debug("Transfer point: deviceId=" + device.getPAObjectID() + " oldType="
                + pair.oldDefinitionTemplate.getPointType().getPointTypeId() + " old offset="
                + pair.oldDefinitionTemplate.getOffset() + " new type="
                + pair.newDefinitionTemplate.getPointType().getPointTypeId() + " new offset="
                + pair.newDefinitionTemplate.getOffset());

            LitePoint litePoint = pointService.getPointForPao(yukonDevice, pair.oldDefinitionTemplate);
            PointBase point = (PointBase) LiteFactory.convertLiteToDBPers(litePoint);

            dbPersistentDao.retrieveDBPersistent(point);
            point = PointUtil.changePointType(point, pair.newDefinitionTemplate);
            // changePointType(...) - Handles sending of DBChangeMsg for each transferred (aka changed) point. 
        }
    }
}