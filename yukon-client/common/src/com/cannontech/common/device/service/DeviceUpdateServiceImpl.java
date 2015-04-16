package com.cannontech.common.device.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.pao.service.PointService;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.rfn.service.RfnDeviceLookupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
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
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointUtil;
import com.cannontech.database.db.device.RfnAddress;
import com.cannontech.database.db.point.Point;
import com.cannontech.device.range.DlcAddressRangeService;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
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
    @Autowired private RfnDeviceLookupService rfnDeviceLookupService;
    @Autowired private PointDao pointDao;
    @Autowired private PaoCreationHelper paoCreationHelper;

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
    public SimpleDevice changeDeviceType(YukonDevice currentDevice, PaoDefinition newDefinition, ChangeDeviceTypeInfo info) {

        DeviceBase yukonPAObject = (DeviceBase) PAOFactory.createPAObject(currentDevice.getPaoIdentifier().getPaoId());

        // Load the device to change
        try {
            dbPersistentDao.retrieveDBPersistent(yukonPAObject);
        } catch (PersistenceException e) {
            throw new DataRetrievalFailureException("Could not load device from db", e);
        }

        // Change the device's type
        DeviceBase changedDevice = this.changeDeviceType(yukonPAObject, newDefinition, info);

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
    public DeviceBase changeDeviceType(DeviceBase currentDevice, PaoDefinition newDefinition, ChangeDeviceTypeInfo info) {
        
        log.debug("Changing device type from " + currentDevice.getPaoType() + " to " + newDefinition.getType());
        log.debug("Device id=" + currentDevice.getDevice().getDeviceID());
        
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

        if (newDevice instanceof MCTBase && oldDevice instanceof RfnBase) {
            
            if (info == null || info.getRouteId() == 0){
                throw new ProcessingException("Address and route id are required");
            }
            if (!dlcAddressRangeService.isValidEnforcedAddress(newDefinition.getType(), info.getAddress())) {
                throw new ProcessingException("Invalid address: " + info.getAddress() + ".");
            }
            ((MCTBase) newDevice).setAddress(info.getAddress());
            ((MCTBase) newDevice).getDeviceRoutes().setRouteID(info.getRouteId());
        }

        if (newDevice instanceof RfnBase && oldDevice instanceof MCTBase) {
            
            if (info == null || StringUtils.isEmpty(info.getSerialNumber())
                || StringUtils.isEmpty(info.getManufacturer()) || StringUtils.isEmpty(info.getModel())) {
                throw new ProcessingException("Serial Number, Manufacturer and Model are required");
            }
            
            RfnIdentifier rfnIdentifier =
                new RfnIdentifier(info.getSerialNumber(), info.getManufacturer(), info.getModel());
            try {
                rfnDeviceLookupService.getDevice(rfnIdentifier);
                // device found, unable to change device type
                throw new ProcessingException("Device: " + rfnIdentifier + " already exists.");
            } catch (NotFoundException e) {
                // ignore
            }
            ((RfnBase) newDevice).getRfnAddress().setManufacturer(rfnIdentifier.getSensorManufacturer());
            ((RfnBase) newDevice).getRfnAddress().setModel(rfnIdentifier.getSensorModel());
            ((RfnBase) newDevice).getRfnAddress().setSerialNumber(rfnIdentifier.getSensorSerialNumber());
        }

        try {
            dbPersistentDao.performDBChangeWithNoMsg(newDevice, TransactionType.ADD_PARTIAL);
            updatePoints(oldDevice, newDefinition.getType());

        } catch (PersistenceException e) {
            CTILogger.error(e.getMessage(), e);

        }
        return newDevice;
    }

    @Override
    public PointsToProcess getPointsToProccess(DeviceBase oldDevice, PaoType newType) {

        Map<PaoType, Map<Attribute, AttributeDefinition>> definitionMap =
            paoDefinitionDao.getPaoAttributeAttrDefinitionMap();
        Map<Attribute, AttributeDefinition> oldDeviceAttributeMap = definitionMap.get(oldDevice.getPaoType());
        Map<Attribute, AttributeDefinition> newDeviceAttributeMap = definitionMap.get(newType);

        List<PointBase> existingPoints = pointDao.getPointsForPao(oldDevice.getPAObjectID());
        Set<PointTemplate> initPointTemplates = paoDefinitionDao.getInitPointTemplates(newType);
     
        Set<PointBase> pointsToDelete = new HashSet<>();
        Set<PointTemplate> pointsToAdd = new HashSet<>(initPointTemplates);
        Map<Integer, PointToTemplate> pointsToTransfer = new HashMap<>();

        ListMultimap<PointIdentifier, PointBase> identifierToPoint = ArrayListMultimap.create();
        for (PointBase point : existingPoints) {
            PointIdentifier pointIdentifier =
                new PointIdentifier(PointType.getForString(point.getPoint().getPointType()),
                    point.getPoint().getPointOffset());
            identifierToPoint.put(pointIdentifier, point);

            if (log.isDebugEnabled()) {
                log.debug("Existing point: id=" + point.getPoint().getPointID() + " name="
                    + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                    + point.getPoint().getPointOffset());
            }
        }

        // match by attribute
        for (Attribute attribute : oldDeviceAttributeMap.keySet()) {
            AttributeDefinition oldDefinition = oldDeviceAttributeMap.get(attribute);
            AttributeDefinition newDefinition = newDeviceAttributeMap.get(attribute);
            List<PointBase> points = identifierToPoint.get(oldDefinition.getPointTemplate().getPointIdentifier());

            if (log.isDebugEnabled()) {
                log.debug("Attribute=" + attribute + "------------------------");
                log.debug("old definition=" + oldDefinition.getPointTemplate());
                if (newDefinition != null) {
                    log.debug("new definition=" + newDefinition.getPointTemplate());
                }
                for(PointBase point: points){
                    log.debug("Existing point: id=" + point.getPoint().getPointID() + " name="
                        + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                        + point.getPoint().getPointOffset());
                }
            }

            /**
             * RF Meters calculated points are created with the same point type and offset so it is possible
             * to get more then one point with the same point identifier. Attempt to match by point name if
             * points.size() > 1.
             */
            if (points.size() == 1) {
                if (newDefinition != null
                    && isTransferable(newDefinition.getPointTemplate().getName(), pointsToTransfer)) {
                    PointBase point = points.get(0);
                    PointToTemplate pointToTemplate = new PointToTemplate(point, newDefinition.getPointTemplate());
                    pointsToTransfer.put(point.getPoint().getPointID(), pointToTemplate);
                    identifierToPoint.removeAll(oldDefinition.getPointTemplate().getPointIdentifier());
                    // removing the point from the list of points to be added
                    pointsToAdd.remove(pointToTemplate.getTemplate());

                    log.debug("Transfering point.");
                } else {
                    pointsToDelete.addAll(points);
                    log.debug("Deleting point.");
                }
            }
        }

        Set<PointTemplate> supportedTemplates = paoDefinitionDao.getAllPointTemplates(newType);
        
        // match "leftover" points by name, since some points don't have attributes
        // Example : Changing device type from MCT410CL to MCT410IL, point: Peak kW (Channel 2)

        // identifierToPoint contains only unmatched points
        for (PointIdentifier identifier : identifierToPoint.keySet()) {
            List<PointBase> points = identifierToPoint.get(identifier);

            for (PointBase point : points) {
                if (log.isDebugEnabled()) {
                    log.debug("Unmatched point by attribute : id=" + point.getPoint().getPointID() + " name="
                        + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                        + point.getPoint().getPointOffset());
                }
                PointTemplate oldTemplate = null;
                try {
                    boolean transfer = false;
                    oldTemplate = paoDefinitionDao.getPointTemplateByTypeAndOffset(oldDevice.getPaoType(), identifier);
                    for (PointTemplate newTemplate : supportedTemplates) {
                        if (oldTemplate.getName().equals(newTemplate.getName())
                            && isTransferable(newTemplate.getName(), pointsToTransfer)) {
                            PointToTemplate pointToTemplate = new PointToTemplate(point, newTemplate);
                            pointsToTransfer.put(point.getPoint().getPointID(), pointToTemplate);
                            // removing the point from the list of points to be added
                            pointsToAdd.remove(pointToTemplate.getTemplate());
                            transfer = true;
                            log.debug("Matched by name - transfering point");
                        }
                    }
                    if (!transfer) {
                        pointsToDelete.add(point);
                        log.debug("Match by name failed - deleting point");
                    }
                } catch (NotFoundException e) {
                    // this point must be a user created point since the template is not found, do nothing
                    log.debug("User created point - ignoring");
                }
            }
        }

        removeTransferablePointsFromDeletedList(pointsToDelete, pointsToTransfer);
        return new PointsToProcess(pointsToDelete, pointsToAdd, pointsToTransfer);
    }

    private void updatePoints(DeviceBase oldDevice, PaoType newType) throws PersistenceException {

        log.debug("Updating points");
        PointsToProcess pointsToProcess = getPointsToProccess(oldDevice, newType);
        deletePoints(pointsToProcess.getPointsToDelete());
        updatePointName(pointsToProcess.getPointsToTransfer());
        addPoints(pointsToProcess.getPointsToAdd(), oldDevice);
        changePointType(pointsToProcess.getPointsToTransfer());
    }

    /**
     * One point can be mapped to 2 different attributes
     * Example: Changing device type from MCT410IL to MCT370
     * 
     * same point deleted:
     * Attribute=DELIVERED_KWH
     * old definition: type = PulseAccumulator, offset = 1, name = 'kWh'
     * new definition: none
     * 
     * same point transfered:
     * Attribute=USAGE
     * old definition: type = PulseAccumulator, offset = 1, name = 'kWh
     * new definition: Analog, offset = 1, name = 'Total kWh'
     * 
     * Removes transferable points from deleted list
     */

    private void removeTransferablePointsFromDeletedList(Set<PointBase> pointsToDelete, Map<Integer, PointToTemplate> pointsToTransfer) {

        Iterator<PointBase> it = pointsToDelete.iterator();
        while (it.hasNext()) {
            PointToTemplate pointToTemplate = pointsToTransfer.get(it.next().getPoint().getPointID());
            if (pointToTemplate != null) {
                it.remove();
            }
        }
    }
    
    /**
     * MCT430A3 to MCT410IL
     * 
     * Existing point: id=2745513 name=IED Blink Count type=Analog offset=40
     * Existing point: id=2745510 name=Blink Count type=PulseAccumulator offset=20
     * 
     * Attribute=BLINK_COUNT
     * MCT430A3 -> type = Analog, offset = 40, name = 'IED Blink Count'
     * MCT410IL -> type = PulseAccumulator, offset = 20, name = 'Blink Count'
     * 
     * Transfering 'IED Blink Count' to 'Blink Count'
     * 
     * Blink Count is unmatched by attribute. Matching "Blink Count" by name:
     * Blink Count found : id=2745510 name=Blink Count type=PulseAccumulator offset=20
     * 
     * Transfering 'Blink Count' to 'Blink Count'
     * 
     * Problem: can't create 2 "Blink Count"s
     * 
     * Returns true is point is transferable.
     * If false is returned the the point should be deleted.
     */

    private boolean isTransferable(String templateName, Map<Integer, PointToTemplate> pointsToTransfer) {

        for (PointToTemplate pointToTemplate : pointsToTransfer.values()) {
            if (pointToTemplate.getTemplate().getName().equals(templateName)) {
                if (log.isDebugEnabled()) {
                    log.debug("Point is already transfered " + pointToTemplate.getTemplate());
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Deletes points
     * 
     * @throws PersistenceException
     */
    private void deletePoints(Set<PointBase> pointsToDelete) throws PersistenceException {

        if (!pointsToDelete.isEmpty()) {
            log.debug("Points to delete-----------------------");
            for (PointBase point : pointsToDelete) {
                if (log.isDebugEnabled()) {
                    log.debug("Deleting point: id=" + point.getPoint().getPointID() + " name="
                        + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                        + point.getPoint().getPointOffset());
                }
                dbPersistentDao.performDBChange(point, TransactionType.DELETE);
            }
            log.debug("Points deleted-----------------------");
        }
    }

    /**
     * Creates points base on list of points provided that exist on a template device.
     * 
     * @throws PersistenceException
     */
    private void addPoints(Set<PointTemplate> pointsToAdd, DeviceBase oldDevice) throws PersistenceException {

        if (!pointsToAdd.isEmpty()) {
            List<PointTemplate> calcPoints =
                Lists.newArrayList(Iterables.filter(pointsToAdd, new Predicate<PointTemplate>() {
                    @Override
                    public boolean apply(PointTemplate t) {
                        return t.getPointType() == PointType.CalcAnalog || t.getPointType() == PointType.CalcStatus;
                    }
                }));
            pointsToAdd.removeAll(calcPoints);
            ArrayList<PointTemplate> sortedTemplates = new ArrayList<PointTemplate>();
            sortedTemplates.addAll(pointsToAdd);
            sortedTemplates.addAll(calcPoints);

            log.debug("Points to add-----------------------");
            SimpleDevice device = deviceDao.getYukonDeviceForDevice(oldDevice);
            for (PointTemplate template : sortedTemplates) {

                PointBase point = pointCreationService.createPoint(device.getPaoIdentifier(), template);
                if (log.isDebugEnabled()) {
                    log.debug("Adding point: id=" + point.getPoint().getPointID() + " name="
                        + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                        + point.getPoint().getPointOffset());
                }
                dbPersistentDao.performDBChange(point, TransactionType.INSERT);
            }
            log.debug("Points added--------------------------");
        }
    }

    /**
     * Changes pointBase to the newPointTemplate type.
     * Sends DBChangeMsg for each point that actually has 'changes'.
     * 
     * @throws PersistenceException
     */
    private void changePointType(Map<Integer, PointToTemplate> pointsToUpdate) throws PersistenceException {

        if (!pointsToUpdate.isEmpty()) {
            List<PointToTemplate> calcPoints =
                    Lists.newArrayList(Iterables.filter(pointsToUpdate.values(), new Predicate<PointToTemplate>() {
                        @Override
                        public boolean apply(PointToTemplate t) {
                            return t.getTemplate().getPointType() == PointType.CalcAnalog
                                    || t.getTemplate().getPointType() == PointType.CalcStatus;
                        }
                    }));
            
            List<PointToTemplate> points = new ArrayList<PointToTemplate>(pointsToUpdate.values());
            points.removeAll(calcPoints);
            points.addAll(calcPoints);
            log.debug("Points to update-----------------------");
            // Change point type
            for (PointToTemplate pointToTemplate : points) {
                if (log.isDebugEnabled()) {
                    PointBase point = pointToTemplate.getPoint();
                    log.debug("Updating point: id=" + point.getPoint().getPointID() + " name="
                        + point.getPoint().getPointName() + " type=" + point.getPoint().getPointType() + " offset="
                        + point.getPoint().getPointOffset() + "--Change to=" + pointToTemplate.getTemplate());
                }
                PointUtil.changePointType(pointToTemplate.getPoint(), pointToTemplate.getTemplate());
            }
            log.debug("Points updated---------------------");
        }
    }

    /**
     * Updates point names
     * Example: MCT410IL -> RFN420CD
     * MCT410IL -> type = PulseAccumulator, offset = 20, name = 'Blink Count'
     * RFN420CD -> type = CalcAnalog, offset = 0, name = 'Total Outage Count'
     * 1. rename point Blink Count to Total Outage Count and update the offset, now the new Blink count can
     * be added.
     * 2. add new point type = Analog, offset = 20, name = 'Blink Count'
     * 3. update point type last since new 'Blink Count' is used for calculation
     * 
     * Example: MCT410IL -> MCT430S4
     * ATTRIBUTE:BLINK_COUNT
     * MCT410IL-> type = PulseAccumulator, offset = 20, name = 'Blink Count'
     * MCT430S4-> type = Analog, offset = 40, name = 'IED Blink Count'
     * 
     * add
     * MCT430S4-> type = PulseAccumulator, offset = 20, name = 'Blink Count'
     * 
     * Updates point name
     * @throws PersistenceException
     */
    private void updatePointName(Map<Integer, PointToTemplate> pointsToUpdate) throws PersistenceException {
        if (!pointsToUpdate.isEmpty()) {
            for (PointToTemplate pointToTemplate : pointsToUpdate.values()) {
                Point point = pointToTemplate.getPoint().getPoint();
                String newPointName = pointToTemplate.getTemplate().getName();
                if (!point.getPointName().equals(newPointName)) {
                    log.debug("Updating point name: id=" + point.getPointID() + " name="
                        + point.getPointName() + " type=" + point.getPointType() + " offset=" + point.getPointOffset()
                        + "--Change to=" + pointToTemplate.getTemplate());
                    point.setPointName(newPointName);
                    try {
                        dbPersistentDao.performDBChangeWithNoMsg(point, TransactionType.UPDATE);
                    } catch (PersistenceException e) {
                        log.warn("Failed to update the point name", e);
                        // ignore
                    }
                }
            }
        }
    }
}