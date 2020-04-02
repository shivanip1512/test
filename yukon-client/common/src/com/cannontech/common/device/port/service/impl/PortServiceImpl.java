package com.cannontech.common.device.port.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.port.PortBase;
import com.cannontech.common.device.port.PortDetailBase;
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
    public Integer create(PortBase portInfo) {
        DirectPort port = (DirectPort ) PortFactory.createPort(portInfo.getType());
        portInfo.buildDBPersistent(port);
        dbPersistentDao.performDBChange(port, TransactionType.INSERT);
        SimpleDevice device = SimpleDevice.of(port.getPAObjectID(), port.getPaoType());
        paoCreationHelper.addDefaultPointsToPao(device);
        return port.getPAObjectID();
    }

    @Override
    public PortDetailBase retrieve(int portId) {
        LiteYukonPAObject pao = dbCache.getAllPaosMap().get(portId);
        if (pao == null) {
            throw new NotFoundException("Port Id not found");
        }
        DirectPort directPort = (DirectPort) dbPersistentDao.retrieveDBPersistent(pao);
        PortDetailBase portDetailBase = getModel(directPort.getPaoType());
        portDetailBase.buildModel(directPort);
        return portDetailBase;
    }

    private PortDetailBase getModel(PaoType paoType) {
        PortDetailBase portDetailBase = null;
        switch (paoType) {
        case TCPPORT :
            portDetailBase = new TcpPortDetail();
            break;
        // TODO : Add for other Ports here.
        }
        
        return portDetailBase;
    }
}
