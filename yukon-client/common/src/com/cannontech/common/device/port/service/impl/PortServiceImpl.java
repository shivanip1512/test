package com.cannontech.common.device.port.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
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
    public Integer create(PortBase port) {
        DirectPort directPort = PortFactory.createPort(port.getType());
        port.buildDBPersistent(directPort);
        dbPersistentDao.performDBChange(directPort, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(directPort.getPAObjectID(), directPort.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        // TODO : Add eventLog in new Jira
        return directPort.getPAObjectID();
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
    public List<PortBase> getAllPorts() {
        List<LiteYukonPAObject> listOfPorts = dbCache.getAllPorts();
        List<PortBase> listOfPortBase = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(listOfPorts)) {
            listOfPorts.forEach(liteYukonPaoObject -> {
                PortBase portBase = new PortBase();
                portBase.setType(liteYukonPaoObject.getPaoType());
                portBase.setName(liteYukonPaoObject.getPaoName());
                portBase.setDisable(liteYukonPaoObject.getDisableFlag().equals("Y") ? true : false);
                listOfPortBase.add(portBase);
            });
        } else {
            throw new NotFoundException("Ports not found");
        }
        return listOfPortBase;
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
