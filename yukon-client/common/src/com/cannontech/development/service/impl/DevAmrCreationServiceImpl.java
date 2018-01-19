package com.cannontech.development.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnManufacturerModel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CCU711;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TerminalServerSharedPort;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.port.PortTiming;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.development.model.DevAmr;
import com.cannontech.development.model.DevCCU;
import com.cannontech.development.model.DevCommChannel;
import com.cannontech.development.model.DevMeter;
import com.cannontech.development.model.DevPaoType;
import com.cannontech.development.service.DevAmrCreationService;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class DevAmrCreationServiceImpl extends DevObjectCreationBase implements DevAmrCreationService {
    
    private static final ReentrantLock _lock = new ReentrantLock();
    private static final int delayTimeInSeconds = 3;
    private static final int createMetersBeforeDelay = 100;

    @Override
    public boolean isRunning() {
        return _lock.isLocked();
    }
    
    @Override
    @Transactional
    public void executeSetup(DevAmr devAmr) {
        if(_lock.tryLock()) {
            try  {
                createAllCommChannels(devAmr);
                createAllCCUs(devAmr);
                createAllMeters(devAmr);
            } finally {
                _lock.unlock();
            }
        }
    }
    
    private List<YukonPao> createTestMeters(List<PaoType> types, int numToCreate) {
        DevAmr devAmr = new DevAmr();
        devAmr.setCreateCartObjects(false);
        devAmr.setCreateRfnTemplates(false);
        devAmr.setNumAdditionalMeters(numToCreate);
        
        List<DevPaoType> devPaoTypes = Lists.newArrayListWithCapacity(types.size());
        for (PaoType paoType: types) {
            DevPaoType devPaoType = new DevPaoType(paoType);
            devPaoType.setCreate(true);
            devPaoTypes.add(devPaoType);
        }
        devAmr.setMeterTypes(devPaoTypes);
        return createAllMeters(devAmr);
    }    
    
    private void createAllCommChannels(DevAmr devAmr) {
        if (!devAmr.isCreateCartObjects()) {
            return;
        }
        log.info("Creating Comm Channels ...");
        DevCommChannel[] commChannels = DevCommChannel.values();
        for (DevCommChannel commChannel : commChannels) {
            createCommChannel(commChannel);
        }
    }

    private void createCommChannel(DevCommChannel commChannel) throws NotFoundException {
        // if this comm channel exists already, then return
        LiteYukonPAObject existingCommChannel = paoDao.findUnique(commChannel.getName(), PaoType.TSERVER_SHARED);
        if (existingCommChannel != null) {
            log.info("Comm Channel with name " + commChannel.getName() + " already exists. Skipping.");
            return;
        }

        DirectPort directPort = PortFactory.createPort(commChannel.getPaoType());
        directPort.getCommPort().setCommonProtocol("IDLC");

        PortSettings portSettings = new PortSettings();
        portSettings.setPortID(directPort.getCommPort().getPortID());
        portSettings.setBaudRate(commChannel.getBaudRate());

        String ipAddress = commChannel.getIpAddress();
        String cparmIpAddress = configurationSource.getString("DEVELOPMENT_DB_IP_ADDRESS_" + commChannel.name());
        if (cparmIpAddress != null) {
            ipAddress = cparmIpAddress;
        }

        PortTerminalServer portTerminalServer =
            new PortTerminalServer(directPort.getCommPort().getPortID(), ipAddress, commChannel.getPort());

        TerminalServerSharedPort terminalServerSharedPort = new TerminalServerSharedPort();
        terminalServerSharedPort.setCommPort(directPort.getCommPort());
        terminalServerSharedPort.setPortSettings(portSettings);
        terminalServerSharedPort.setPortTerminalServer(portTerminalServer);
        terminalServerSharedPort.setPAOName(commChannel.getName());
        terminalServerSharedPort.setPortID(directPort.getCommPort().getPortID());
        terminalServerSharedPort.setPortName(commChannel.getName());
        terminalServerSharedPort.setPortTiming(new PortTiming(portTerminalServer.getPortID(),commChannel.getPortTimingPreTxWait(),0,0,0,0));

        // Create the default points for the port
        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(terminalServerSharedPort);
        PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
        SimpleDevice terminalServer = new SimpleDevice(terminalServerSharedPort.getPAObjectID(), terminalServerSharedPort.getPaoType());
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(terminalServer);
        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }
        
        // Insert the port and points into the database
        try {
            dbPersistentDao.performDBChange(smartDB, TransactionType.INSERT);
            log.info("Comm Channel with name " + commChannel.getName() + " created.");
        } catch(PersistenceException e) {
            log.warn("Caught PersistenceException while inserting Dev Port", e);
        }
    }

    private void createAllCCUs(DevAmr devAmr) {
        if (!devAmr.isCreateCartObjects()) {
            return;
        }
        log.info("Creating CCUs ...");
        DevCCU[] ccus = DevCCU.values();
        for (DevCCU ccu : ccus) {
            createCCU(ccu);
        }
    }

    private void createCCU(DevCCU devCCU) {
        try {
            // if this device exists already, then return
            LiteYukonPAObject liteYukonPAObject =
                deviceDao.getLiteYukonPAObject(devCCU.getName(), PaoType.CCU711);
            if (liteYukonPAObject != null) {
                log.info("CCU with name " + devCCU.getName() + " already exists. Skipping.");
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this ccu
        }

        LiteYukonPAObject commChan = paoDao.findUnique(devCCU.getCommChannel().getName(), PaoType.TSERVER_SHARED);

        Integer portID = new Integer(commChan.getYukonID());

        CCU711 ccu = new CCU711();
        ccu.setPAOName(devCCU.getName());

        ((RemoteBase) ccu).getDeviceDirectCommSettings().setPortID(portID);

        DirectPort port = (DirectPort) LiteFactory.createDBPersistent(commChan);
        dbPersistentDao.performDBChange(port, TransactionType.RETRIEVE);

        ((RemoteBase) ccu).getDeviceDialupSettings().setBaudRate(port.getPortSettings().getBaudRate());
        ((IDLCBase) ccu).getDeviceIDLCRemote().setPostCommWait(new Integer(0));
        ((IDLCBase) ccu).getDeviceIDLCRemote().setAddress(new Integer(devCCU.getAddress()));

        ((DeviceBase) ccu).setDeviceID(paoDao.getNextPaoId());

        CCURoute route = (CCURoute) RouteFactory.createRoute(PaoType.ROUTE_CCU);
        Integer routeID = paoDao.getNextPaoId();

        // make sure the name will fit in the DB!!
        route.setRouteName((((DeviceBase) ccu).getPAOName().length() <= TextFieldDocument.MAX_ROUTE_NAME_LENGTH
                ? ((DeviceBase) ccu).getPAOName()
                : ((DeviceBase) ccu).getPAOName()
                                    .substring(0, TextFieldDocument.MAX_ROUTE_NAME_LENGTH)));

        // set default values for route tables
        route.setDeviceID(((DeviceBase) ccu).getDevice().getDeviceID());
        route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());

        route.setCarrierRoute(new CarrierRoute(routeID));

        SmartMultiDBPersistent newVal = createSmartDBPersistent(ccu);
        newVal.addDBPersistent(route);

        dbPersistentDao.performDBChange(newVal, TransactionType.INSERT);
        log.info("CCU with name " + devCCU.getName() + " created.");
    }

    // This creates meter and RF Relay
    private List<YukonPao> createAllMeters(DevAmr devAmr) {
        log.info("Creating Meters ...");
        List<YukonPao> createdMeters = Lists.newArrayList();
        createdMeters.addAll(createCartMeters(devAmr));
        createdMeters.addAll(createRfnTemplateMeters(devAmr));
        int addressCount = 0;
        int delayCount = createMetersBeforeDelay;
        int address = devAmr.getAddressRangeMin();
        for (DevPaoType meterType: devAmr.getMeterTypes()) {
            if (meterType.isCreate()) {
                for (int i = 0; i < devAmr.getNumAdditionalMeters(); i++) {
                    if (addressCount == delayCount) {
                        try {
                            log.info("----Delaying " + delayTimeInSeconds + " seconds.");
                            TimeUnit.SECONDS.sleep(delayTimeInSeconds);
                            delayCount += createMetersBeforeDelay;
                            log.info("----Delaying done. Next delay after " + delayCount + " devices created.");
                        } catch (InterruptedException e) {}
                    }
                    address = devAmr.getAddressRangeMin() + addressCount;
                    String meterName = meterType.getPaoType().getPaoTypeName() + " " + address;
                    YukonPao meter;
                    if (meterType.getPaoType().isRfn()) {
                        if (meterType.getPaoType().isRfRelay()) {
                            RfnIdentifier rfId = new RfnIdentifier(String.valueOf(address), "EATON", "RFRelay");
                            meter = deviceCreationService.createRfnDeviceByDeviceType(PaoType.RFN_RELAY, meterName, rfId, true);
                        } else {
                            RfnManufacturerModel templateSettings = RfnManufacturerModel.getForType(meterType.getPaoType())
                                                                                        .get(0);
                            RfnIdentifier rfId = new RfnIdentifier(String.valueOf(address),
                                                                   templateSettings.getManufacturer(),
                                                                   templateSettings.getModel());
                            meter = createRfnMeter(devAmr, meterType.getPaoType(), meterName, rfId, true);
                        }
                    } else {
                        meter = createPlcMeter(devAmr, meterType.getPaoType(), meterName, address, devAmr.getRouteId(), true);
                    }
                    if (meter != null) {
                        createdMeters.add(meter);
                    }
                    addressCount++;
                }
            }
        }
        log.info("Done Creating Meters ...");
        return createdMeters;
    }
    
    private List<YukonPao> createCartMeters(DevAmr devAmr) {
        if (!devAmr.isCreateCartObjects()) {
            return Lists.newArrayList();
        }

        List<YukonPao> createdMeters = Lists.newArrayList();
        log.info("Creating Software Cart Meters ...");
        for (DevMeter meter : DevMeter.values()) {
            int routeId = getRouteIdForMeter(meter);
            YukonPao createdMeter = createPlcMeter(devAmr, meter.getPaoType(), meter.getName(), meter.getAddress(), routeId, true);
            if (createdMeter != null) {
                createdMeters.add(createdMeter);
            }
        }
        return createdMeters;
    }
    
    private List<YukonPao> createRfnTemplateMeters(DevAmr devAmr) {
        if (!devAmr.isCreateRfnTemplates()) {
            return Lists.newArrayList();
        }

        List<YukonPao> createdMeters = Lists.newArrayList();
        log.info("Creating Rfn Template Meters ...");
        for (RfnManufacturerModel rfnManufacturerModel : RfnManufacturerModel.values()) {
            String templateName = "*RfnTemplate_" + rfnManufacturerModel.getManufacturer() + "_" + rfnManufacturerModel.getModel();
            YukonPao rfnMeter = createRfnMeter(devAmr, rfnManufacturerModel.getType(), templateName, RfnIdentifier.BLANK, true);
            if (rfnMeter != null) {
                createdMeters.add(rfnMeter);
            }
        }
        return createdMeters;
    }
    
    private boolean canCreateMeter(DevAmr devAmr, String name, Integer address) {
//        checkIsCancelled();
        if (address != null && address >= devAmr.getAddressRangeMax()) {
            log.info("Meter with name " + name + " has address greater than max address range of "
                    + devAmr.getAddressRangeMax() + " . Skipping.");
            devAmr.incrementFailureCount();
            return false;
        }
        try {
            // if this device exists already, then return
            SimpleDevice existingMeter = deviceDao.findYukonDeviceObjectByName(name);
            if (existingMeter != null) {
                log.info("Meter with name " + name + " already exists. Skipping.");
                devAmr.incrementFailureCount();
                return false;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this meter
        }
        return true;
    }

    private YukonPao createPlcMeter(DevAmr devAmr, PaoType type, String name, int address, int routeId, boolean createPoints) {
        if (!canCreateMeter(devAmr, name, address)) {
            return null;
        }
        
        if (routeId <= 0) {
            routeId = getDefaultRouteId();
        }

        YukonDevice yukonDevice = deviceCreationService.createCarrierDeviceByDeviceType(type, name, address, routeId, createPoints);
        deviceDao.changeMeterNumber(yukonDevice, Integer.toString(address));
        devAmr.incrementSuccessCount();
        log.info("Plc Meter with name " + name + " created.");
        return yukonDevice;
    }
    
    private YukonPao createRfnMeter(DevAmr devAmr, PaoType type, String name, RfnIdentifier rfId, boolean createPoints) {
        if (!canCreateMeter(devAmr, name, null)) {
            return null;
        }

        YukonDevice yukonDevice = deviceCreationService.createRfnDeviceByDeviceType(type, name, rfId, createPoints);
        devAmr.incrementSuccessCount();
        log.info("Rfn Meter with name " + name + " created.");
        return yukonDevice;
    }

    private int getDefaultRouteId() {
        Integer routeId = paoDao.getRouteIdForRouteName(DevCCU.SIM_711.getName());
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + DevCCU.SIM_711.getName());
        }
        return routeId;
    }
    
    private int getRouteIdForMeter(DevMeter meter) {
        Integer routeId = paoDao.getRouteIdForRouteName(meter.getCcu().getName());
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + DevCCU.SIM_711.getName());
        }
        return routeId;
    }

}
