package com.cannontech.common.rfn.service.impl;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.rfn.model.RfnGateway;
import com.cannontech.common.rfn.service.RfnGatewayDataCache;
import com.cannontech.common.rfn.service.RfnGatewayService;

public class RfnGatewayServiceImpl implements RfnGatewayService {
    @Autowired RfnGatewayDataCache dataCache;
    
    @Override
    public Set<RfnGateway> getAllGateways() {
        //TODO: Get all base RfnDevices
        //TODO: Get RfnGatewayData from cache
        //TODO: Get PaoLocation from PaoLocationDao
        return null;
    }

    @Override
    public RfnGateway getGatewayByPaoId(PaoIdentifier paoIdentifier) {
        //TODO: Get base RfnDevices
        //TODO: Get RfnGatewayData from cache
        //TODO: Get PaoLocation from PaoLocationDao
        return null;
    }
    
    @Override
    public boolean createGateway(String name, String ipAddress, String username, String password) {
        //TODO: Send GatewayCreateRequest on yukon.qr.common.rfn.GatewayUpdateRequest queue
        //TODO: Parse GatewayUpdateResponse on temp queue
        //TODO: Create pao in Yukon DB, (optionally) add location info w/ PaoLocationDao
        return false;
    }
    
    @Override
    public boolean updateGateway(RfnGateway gateway) {
        //TODO: Determine if change is local Yukon DB change (i.e. name) or remote Network Manager change.
        //TODO: If necessary, send GatewayUpdateRequest on yukon.qr.common.rfn.GatewayUpdateRequest queue
        //TODO: If necessary, parse GatewayUpdateResponse on temp queue
        //TODO: Update yukon database, cache
        return false;
    }

    @Override
    public boolean deleteGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayDeleteRequest on yukon.qr.common.rfn.GatewayUpdateRequest queue
        //TODO: Parse GatewayUpdateResponse on temp queue
        //TODO: Delete from yukon database, cache
        return false;
    }

    @Override
    public boolean testConnection(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectionTestRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayConnectionTestResponse on temp queue
        return false;
    }

    @Override
    public boolean connectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean disconnectGateway(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayConnectRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }
    
    @Override
    public boolean collectData(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayCollectionRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        return false;
    }

    @Override
    public boolean setCollectionSchedule(PaoIdentifier paoIdentifier, String cronExpression) {
        //TODO: Send GatewayScheduleRequest on yukon.qr.common.rf.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        //TODO: Update cache
        return false;
    }

    @Override
    public boolean deleteCollectionSchedule(PaoIdentifier paoIdentifier) {
        //TODO: Send GatewayScheduleDeleteRequest on yukon.qr.obj.common.rfn.GatewayActionRequest queue
        //TODO: Parse GatewayActionResponse from temp queue
        //TODO: Update cache
        return false;
    }

}
