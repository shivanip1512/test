package com.cannontech.web.support.development.database.service.impl;

import java.util.List;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CCU711;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.database.data.port.TerminalServerDirectPort;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.port.PortSettings;
import com.cannontech.database.db.port.PortTerminalServer;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.web.support.development.database.objects.DevAMR;
import com.cannontech.web.support.development.database.objects.DevCCU;
import com.cannontech.web.support.development.database.objects.DevCommChannel;
import com.cannontech.web.support.development.database.objects.DevMeter;
import com.cannontech.web.support.development.database.objects.DevPaoType;
import com.cannontech.web.support.development.database.objects.DevRfnTemplateMeter;
import com.google.common.collect.Lists;

public class DevAMRCreationService extends DevObjectCreationBase {
    
    public List<YukonPao> createTestMeters(List<PaoType> types, int numToCreate) {
        DevAMR devAMR = new DevAMR();
        devAMR.setCreateCartObjects(false);
        devAMR.setCreateRfnTemplates(false);
        devAMR.setNumAdditionalMeters(numToCreate);
        
        List<DevPaoType> devPaoTypes = Lists.newArrayListWithCapacity(types.size());
        for (PaoType paoType: types) {
            DevPaoType devPaoType = new DevPaoType(paoType);
            devPaoType.setCreate(true);
            devPaoTypes.add(devPaoType);
        }
        devAMR.setMeterTypes(devPaoTypes);
        return createAllMeters(devAMR);
    }
    
    @Override
    protected void createAll() {
        createAllCommChannels();
        createAllCCUs();
        createAllMeters(devDbSetupTask.getDevAMR());
    }

    @Override
    protected void logFinalExecutionDetails() {
        log.info("AMR:");
    }

    private void createAllCommChannels() {
        if (!devDbSetupTask.getDevAMR().isCreateCartObjects()) {
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
        LiteYukonPAObject existingCommChannel = paoDao.findUnique(commChannel.getName(), PaoCategory.PORT, PaoClass.PORT);
        if (existingCommChannel != null) {
            log.info("Comm Channel with name " + commChannel.getName() + " already exists. Skipping.");
            return;
        }

        DirectPort directPort = PortFactory.createPort(commChannel.getPaoType().getDeviceTypeId());
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

        TerminalServerDirectPort terminalServerSharedPort = new TerminalServerDirectPort();
        terminalServerSharedPort.setCommPort(directPort.getCommPort());
        terminalServerSharedPort.setPortSettings(portSettings);
        terminalServerSharedPort.setPortTerminalServer(portTerminalServer);
        terminalServerSharedPort.setPAOCategory(PaoCategory.PORT.toString());
        terminalServerSharedPort.setPAOClass(PaoClass.PORT.toString());
        terminalServerSharedPort.setPAOName(commChannel.getName());
        terminalServerSharedPort.setPortType(PaoType.TSERVER_SHARED.getPaoTypeName());
        terminalServerSharedPort.setPortID(directPort.getCommPort().getPortID());
        terminalServerSharedPort.setPortName(commChannel.getName());

        dbPersistentDao.performDBChange(terminalServerSharedPort, TransactionType.INSERT);
        log.info("Comm Channel with name " + commChannel.getName() + " created.");
    }

    private void createAllCCUs() {
        if (!devDbSetupTask.getDevAMR().isCreateCartObjects()) {
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
                deviceDao.getLiteYukonPAObject(devCCU.getName(),
                                               PaoCategory.DEVICE.getCategoryId(),
                                               PaoClass.TRANSMITTER.getPaoClassId(),
                                               PaoType.CCU711.getDeviceTypeId());
            if (liteYukonPAObject != null) {
                log.info("CCU with name " + devCCU.getName() + " already exists. Skipping.");
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this ccu
        }

        LiteYukonPAObject commChan =
            paoDao.getLiteYukonPAObject(devCCU.getCommChannel().getName(),
                                        PaoCategory.PORT.getCategoryId(), PaoClass.PORT.getPaoClassId(),
                                        PaoType.TSERVER_SHARED.getDeviceTypeId());

        Integer portID = new Integer(commChan.getYukonID());

        CCU711 ccu = new CCU711();
        ccu.applyTypeToPao(PaoType.CCU711);
        ccu.setPAOName(devCCU.getName());

        ((RemoteBase) ccu).getDeviceDirectCommSettings().setPortID(portID);

        DirectPort port = (DirectPort) LiteFactory.createDBPersistent((LiteBase) commChan);
        dbPersistentDao.performDBChange(port, TransactionType.RETRIEVE);

        ((RemoteBase) ccu).getDeviceDialupSettings().setBaudRate(port.getPortSettings().getBaudRate());
        ((IDLCBase) ccu).getDeviceIDLCRemote().setPostCommWait(new Integer(0));
        ((IDLCBase) ccu).getDeviceIDLCRemote().setAddress(new Integer(devCCU.getAddress()));

        ((DeviceBase) ccu).setDeviceID(paoDao.getNextPaoId());

        String routeType = RouteTypes.STRING_CCU;

        RouteBase route = RouteFactory.createRoute(routeType);
        Integer routeID = paoDao.getNextPaoId();

        // make sure the name will fit in the DB!!
        route.setRouteName((((DeviceBase) ccu).getPAOName().length() <= TextFieldDocument.MAX_ROUTE_NAME_LENGTH
                ? ((DeviceBase) ccu).getPAOName()
                : ((DeviceBase) ccu).getPAOName()
                                    .substring(0, TextFieldDocument.MAX_ROUTE_NAME_LENGTH)));

        // set default values for route tables
        route.setDeviceID(((DeviceBase) ccu).getDevice().getDeviceID());
        route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());

        if (routeType.equalsIgnoreCase(RouteTypes.STRING_CCU)) {
            ((CCURoute) route).setCarrierRoute(new CarrierRoute(routeID));
        }

        SmartMultiDBPersistent newVal = createSmartDBPersistent((DeviceBase) ccu);
        newVal.addDBPersistent(route);

        dbPersistentDao.performDBChange(newVal, TransactionType.INSERT);
        log.info("CCU with name " + devCCU.getName() + " created.");
    }

    private List<YukonPao> createAllMeters(DevAMR devAMR) {
        log.info("Creating Meters ...");
        List<YukonPao> createdMeters = Lists.newArrayList();
        createdMeters.addAll(createCartMeters(devAMR));
        createdMeters.addAll(createRfnTemplateMeters(devAMR));
        int addressCount = 0;
        int address = devAMR.getAddressRangeMin();
        for (DevPaoType meterType: devAMR.getMeterTypes()) {
            if (meterType.isCreate()) {
                for (int i = 0; i < devAMR.getNumAdditionalMeters(); i++) {
                    address = devAMR.getAddressRangeMin() + addressCount;
                    String meterName = meterType.getPaoType().getPaoTypeName() + " " + address;
                    YukonPao meter;
                    if (meterType.getPaoType().isRfn()) {
                        meter = createRfnMeter(devAMR, meterType.getPaoType(), meterName, null, null, null, true);
                    } else {
                        meter = createPlcMeter(devAMR, meterType.getPaoType(), meterName, address, devAMR.getRouteId(), true);
                    }
                    if (meter != null) createdMeters.add(meter);
                    addressCount++;
                }
            }
        }
        return createdMeters;
    }
    
    private List<YukonPao> createCartMeters(DevAMR devAMR) {
        if (!devAMR.isCreateCartObjects()) return Lists.newArrayList();

        List<YukonPao> createdMeters = Lists.newArrayList();
        log.info("Creating Software Cart Meters ...");
        for (DevMeter meter : DevMeter.values()) {
            int routeId = getRouteIdForMeter(meter);
            YukonPao createdMeter = createPlcMeter(devAMR, meter.getPaoType(), meter.getName(), meter.getAddress(), routeId, true);
            if (createdMeter != null) createdMeters.add(createdMeter);
        }
        return createdMeters;
    }
    
    private List<YukonPao> createRfnTemplateMeters(DevAMR devAMR) {
        if (!devAMR.isCreateRfnTemplates()) return Lists.newArrayList();
        
        List<YukonPao> createdMeters = Lists.newArrayList();
        log.info("Creating Rfn Template Meters ...");
        for (DevRfnTemplateMeter meter : DevRfnTemplateMeter.values()) {
            YukonPao rfnMeter = createRfnMeter(devAMR, meter.getPaoType(), meter.getName(), null, null, null, true);
            if (rfnMeter != null) createdMeters.add(rfnMeter);
        }
        return createdMeters;
    }
    
    private boolean canCreateMeter(DevAMR devAMR, String name, Integer address) {
        checkIsCancelled();
        if (address != null && address >= devAMR.getAddressRangeMax()) {
            log.info("Meter with name " + name + " has address greater than max address range of "
                    + devAMR.getAddressRangeMax() + " . Skipping.");
            devAMR.incrementFailureCount();
            return false;
        }
        try {
            // if this device exists already, then return
            SimpleDevice existingMeter = deviceDao.findYukonDeviceObjectByName(name);
            if (existingMeter != null) {
                log.info("Meter with name " + name + " already exists. Skipping.");
                devAMR.incrementFailureCount();
                return false;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this meter
        }
        return true;
    }

    private YukonPao createPlcMeter(DevAMR devAMR, PaoType type, String name, int address, int routeId, boolean createPoints) {
        if (!canCreateMeter(devAMR, name, address)) return null;
        
        if (routeId <= 0) {
            routeId = getDefaultRouteId();
        }

        YukonDevice yukonDevice = deviceCreationService.createCarrierDeviceByDeviceType(type.getDeviceTypeId(), name, address, routeId, createPoints);
        deviceDao.changeMeterNumber(yukonDevice, Integer.toString(address));
        devAMR.incrementSuccessCount();
        log.info("Plc Meter with name " + name + " created.");
        return yukonDevice;
    }
    
    private YukonPao createRfnMeter(DevAMR devAMR, PaoType type, String name, String model, String manufacturer, String serialNumber, boolean createPoints) {
        if (!canCreateMeter(devAMR, name, null)) return null;
        
        YukonDevice yukonDevice = deviceCreationService.createRfnDeviceByDeviceType(type, name, model, manufacturer, serialNumber, createPoints);
        devAMR.incrementSuccessCount();
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
