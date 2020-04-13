package com.cannontech.common.device.port.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.TcpPortDetail;
import com.cannontech.common.device.port.service.PortService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.impl.PaoCreationHelper;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.PortFactory;
import com.cannontech.yukon.IDatabaseCache;

public class PortServiceImpl implements PortService {

    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private PaoCreationHelper paoCreationHelper;

    @Override
    @Transactional
    public PortBase<? extends DirectPort> create(PortBase port) {
        DirectPort directPort = PortFactory.createPort(port.getType());
        port.buildDBPersistent(directPort);
        dbPersistentDao.performDBChange(directPort, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(directPort.getPAObjectID(), directPort.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        port.buildModel(directPort);
        // TODO : Add eventLog in new Jira
        return port;
    }

    @Override
    public PortBase<? extends DirectPort> retrieve(int portId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(portId);
        if (pao == null) {
            throw new NotFoundException("Port Id not found");
        }
        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(pao);
        PortBase portBase = getModel(directPort.getPaoType());
        portBase.buildModel(directPort);
        return portBase;
    }

    @Override
    @Transactional
    public PortBase<? extends DirectPort> update(int portId, PortBase port) {
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
        // TODO : Add eventLog in new Jira
        port.buildModel(directPort);
        return port;
    }
   
    @Override
    @Transactional
    public Integer delete(String portName, int id) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(id);
        if (pao == null) {
            throw new NotFoundException("Port Id not found");
        }
        if (!(pao.getLiteID() == id && pao.getPaoName().equalsIgnoreCase(portName))) {
            throw new NotFoundException("Port Id and Name combination not found");
        }

        // TODO : Can change message for exception. Right now used message from DBEditor
        if (com.cannontech.database.data.port.DirectPort.hasDevice(id)) {
            throw new NotFoundException(
                    "You cannot delete the comm port '" + pao.getPaoName() + "' because it is used by a device");
        }
        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(pao);
        dbPersistentDao.performDBChange(directPort, TransactionType.DELETE);
        return directPort.getPAObjectID();
    }
    

    private PortBase<? extends DirectPort> getModel(PaoType paoType) {
        PortBase<? extends DirectPort> portBase = null;
        switch (paoType) {
        case TCPPORT :
            portBase = new TcpPortDetail();
            break;
        // TODO : Add for other Ports here.
        }
        
        return portBase;
    }
}
