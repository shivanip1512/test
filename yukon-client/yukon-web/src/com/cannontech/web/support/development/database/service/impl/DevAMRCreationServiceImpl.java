package com.cannontech.web.support.development.database.service.impl;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
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
import com.cannontech.web.support.development.database.service.DevAMRCreationService;

public class DevAMRCreationServiceImpl extends DevObjectCreationBase implements DevAMRCreationService {
    
    @Override
    public void createAll() {
        createAllCommChannels();
        createAllCCUs();
        createAllMeters(devDbSetupTask.getDevAMR());
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
        try {
            // if this comm channel exists already, then return
            LiteYukonPAObject existingCommChannel = paoDao.getLiteYukonPAObject(commChannel.getName(), PaoCategory.PORT.getCategoryId(), PaoClass.PORT.getPaoClassId(), PaoType.TSERVER_SHARED.getDeviceTypeId());
            if (existingCommChannel != null) {
                log.info("Comm Channel with name " + commChannel.getName() + " already exists. Skipping.");
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this comm channel
        }

        DirectPort directPort = PortFactory.createPort(commChannel.getPaoType().getDeviceTypeId());
        directPort.getCommPort().setCommonProtocol("IDLC");

        PortSettings portSettings = new PortSettings();
        portSettings.setPortID(directPort.getCommPort().getPortID());
        portSettings.setBaudRate(commChannel.getBaudRate());

        PortTerminalServer portTerminalServer =
            new PortTerminalServer(directPort.getCommPort().getPortID(),
                                               commChannel.getIpAddress(), commChannel.getPort());

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

        dbPersistentDao.performDBChange(terminalServerSharedPort, Transaction.INSERT);
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

        ccu.setPAOCategory(PaoCategory.DEVICE.toString());
        ccu.setPAOClass(PaoClass.TRANSMITTER.toString());
        ccu.setPAOName(devCCU.getName());
        ccu.setDeviceType(PaoType.CCU711.getDbString());

        ((RemoteBase) ccu).getDeviceDirectCommSettings().setPortID(portID);

        DirectPort port = (DirectPort) LiteFactory.createDBPersistent((LiteBase) commChan);
        Transaction<DirectPort> t = Transaction.createTransaction(Transaction.RETRIEVE, port);

        try {
            port = t.execute();
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }

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

        dbPersistentDao.performDBChange(newVal, Transaction.INSERT);
        log.info("CCU with name " + devCCU.getName() + " created.");
    }

    private void createAllMeters(DevAMR devAMR) {
        if (devAMR.isCreateCartObjects()) {
            DevMeter[] meters = DevMeter.values();
            for (DevMeter meter : meters) {
                createMeter(devAMR, meter);
            }
        }
        int addressCount = 0;
        int address = devAMR.getAddressRangeMin();
        for (DevPaoType meterType: devAMR.getMeterTypes()) {
            if (meterType.isCreate()) {
                for (int i = 0; i < devAMR.getNumAdditionalMeters(); i++) {
                    address = devAMR.getAddressRangeMin() + addressCount;
                    String meterName = meterType.getPaoType().getPaoTypeName() + " " + address;
                    createMeter(devAMR, meterType.getPaoType().getDeviceTypeId(), meterName, address, devAMR.getRouteId(), true);
                    addressCount++;
                }
            }
        }
    }

    private void createMeter(DevAMR devAMR, DevMeter meter) {
        Integer routeId = paoDao.getRouteIdForRouteName(DevCCU.CCU_711_SIM.getName());
        if (routeId == null) {
            throw new RuntimeException("Couldn't find route with name " + DevCCU.CCU_711_SIM.getName());
        }
        createMeter(devAMR, meter.getPaoType().getDeviceTypeId(), meter.getName(), meter.getAddress(), routeId, true);
    }

    private void createMeter(DevAMR devAMR, int type, String name, int address, int routeId, boolean createPoints) {
        checkIsCancelled();
        if (address >= devAMR.getAddressRangeMax()) {
            log.info("Meter with name " + name + " has address greater than max address range of "
                     + devAMR.getAddressRangeMax() + " . Skipping.");
            devAMR.incrementFailureCount();
            return;
        }
        try {
            // if this device exists already, then return
            SimpleDevice existingMeter = deviceDao.findYukonDeviceObjectByName(name);
            if (existingMeter != null) {
                log.info("Meter with name " + name + " already exists. Skipping.");
                devAMR.incrementFailureCount();
                return;
            }
        } catch (NotFoundException e) {
            // ignoring and continuing to create this meter
        }

        deviceCreationService.createCarrierDeviceByDeviceType(type, name, address, routeId, createPoints);
        devAMR.incrementSuccessCount();
        log.info("Meter with name " + name + " created.");
    }
}
