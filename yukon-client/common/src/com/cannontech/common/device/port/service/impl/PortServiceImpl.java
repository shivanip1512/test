package com.cannontech.common.device.port.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.model.PaoModelFactory;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.port.BaudRate;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.dao.PortDao;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.common.events.loggers.CommChannelEventLogService;
import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.yukon.IDatabaseCache;

public class PortServiceImpl implements PortService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoCreationHelper paoCreationHelper;
    @Autowired private CommChannelEventLogService commChannelEventLogService;
    @Autowired private PortDao portDao;
    private static final String baseKey = "yukon.common.";

    @Override
    @Transactional
    public PortBase<? extends DirectPort> create(PortBase port, LiteYukonUser liteYukonUser) {
        DirectPort directPort = PortFactory.createPort(port.getDeviceType());
        port.buildDBPersistent(directPort);
        dbPersistentDao.performDBChange(directPort, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(directPort.getPAObjectID(), directPort.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        port.buildModel(directPort);

        //event log comm channel creation
        commChannelEventLogService.commChannelCreated(port.getDeviceName(),
                                                      port.getDeviceType(),
                                                      port.getBaudRate(),
                                                      liteYukonUser);

        return port;
    }

    @Override
    public PortBase<? extends DirectPort> retrieve(int portId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(portId);
        if (pao == null) {
            throw new NotFoundException("Port Id not found");
        }

        if (!pao.getPaoType().isPort()) {
            throw new NotFoundException("Invalid Port Id : " + portId);
        }
        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(pao);
        PortBase portBase = (PortBase) PaoModelFactory.getModel(directPort.getPaoType());
        portBase.buildModel(directPort);
        return portBase;
    }

    @Override
    @Transactional
    public PortBase<? extends DirectPort> update(int portId, PortBase port, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> litePort = dbCache.getAllPorts()
                                                      .stream()
                                                      .filter(group -> group.getLiteID() == portId)
                                                      .findFirst();
        if (litePort.isEmpty()) {
            throw new NotFoundException("Id not found " + portId);
        }
        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(litePort.get());
        port.buildDBPersistent(directPort);
        dbPersistentDao.performDBChange(directPort, TransactionType.UPDATE);
        port.buildModel(directPort);

      //event log comm channel update
        commChannelEventLogService.commChannelUpdated(port.getDeviceName(),
                                                      port.getDeviceType(),
                                                      port.getBaudRate(),
                                                      liteYukonUser);

        return port;
    }
   
    @Override
    @Transactional
    public int delete(int portId, LiteYukonUser liteYukonUser) {
        Optional<LiteYukonPAObject> litePort = dbCache.getAllPorts()
                                                      .stream()
                                                      .filter(group -> group.getLiteID() == portId)
                                                      .findFirst();
        if (litePort.isEmpty()) {
            throw new NotFoundException("Port Id not found");
        }
        
        if (DirectPort.hasDevice(portId)) {
            throw new NotFoundException(
                    "You cannot delete the comm port '" + litePort.get().getPaoName() + "' because it is used by a device");
        }

        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(litePort.get());
        dbPersistentDao.performDBChange(directPort, TransactionType.DELETE);

        //event log comm channel deletion
        commChannelEventLogService.commChannelDeleted(directPort.getPortName(),
                                                      directPort.getPaoType(),
                                                      BaudRate.getForRate(directPort.getPortSettings().getBaudRate()),
                                                      liteYukonUser);

        return directPort.getPAObjectID();
    }

    @Override
    public List<PortBase> getAllPorts() {
        List<LiteYukonPAObject> listOfPorts = dbCache.getAllPorts();
        List<PortBase> listOfPortBase = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listOfPorts)) {
            listOfPorts.forEach(liteYukonPaoObject -> {
                PortBase portBase = new PortBase();
                portBase.buildModel(liteYukonPaoObject);
                listOfPortBase.add(portBase);
            });
        }
        return listOfPortBase;
    }

    @Override
    public SearchResults<DeviceBaseModel> getDevicesAssignedPort(int portId, CommChannelSortBy sortBy, PagingParameters paging, Direction direction) {
        return portDao.getDevicesAssignedPort(portId, sortBy, paging, direction);
    }
    
    // enum for sorting commchannels and linked devices
    public enum CommChannelSortBy implements DisplayableEnum {
        name("PAOName"),
        type("type"),
        status("DisableFlag");

        private CommChannelSortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;

        public String getDbString() {
            return dbString;
        }

        @Override
        public String getFormatKey() {
            return baseKey + name();
        }
    }
}
