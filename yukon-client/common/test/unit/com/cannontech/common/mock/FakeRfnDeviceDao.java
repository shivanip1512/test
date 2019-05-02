package com.cannontech.common.mock;

import java.util.List;
import java.util.Map;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.dao.NotFoundException;

public class FakeRfnDeviceDao implements RfnDeviceDao {
    @Override
    public RfnDevice getDeviceForExactIdentifier(RfnIdentifier rfnIdentifier)
            throws NotFoundException {
        throw new MethodNotImplementedException();
    }

    @Override
    public RfnDevice getDevice(YukonPao pao) {
        throw new MethodNotImplementedException();
    }

    @Override
    public <T extends YukonPao> Map<T, RfnIdentifier> getRfnIdentifiersByPao(Iterable<T> paos) {
        throw new MethodNotImplementedException();
    }

    @Override
    public RfnDevice getDeviceForId(int deviceId) throws NotFoundException {
        YukonPao pao = new YukonPao() {
            @Override
            public PaoIdentifier getPaoIdentifier() {
                return new PaoIdentifier(1, PaoType.RFN_GATEWAY);
            }
        };
        RfnIdentifier rfnId = new RfnIdentifier("10000", "CPS", "RF_GATEWAY");
        return new RfnDevice("10000", pao, rfnId);
    }

    @Override
    public void updateDevice(RfnDevice device) throws NotFoundException {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> getDevicesByPaoType(PaoType paoType) {
        throw new MethodNotImplementedException();
    }
    
    @Override
    public List<RfnDevice> getDevicesByPaoTypes(Iterable<PaoType> paoTypes) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, RfnDevice> getPaoIdMappedDevicesByPaoType(PaoType paoType) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> getDevicesByPaoIds(Iterable<Integer> paoIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public boolean deviceExists(RfnIdentifier rfnIdentifier) {
        throw new MethodNotImplementedException();
    }

    @Override
    public RfnDevice updateGatewayType(RfnDevice device) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> searchDevicesByPaoTypes(Iterable<PaoType> paoTypes, RfnDeviceSearchCriteria criteria) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void createGatewayToDeviceMapping(int gatewayId, List<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getDevicesForGateway(int gatewayId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void clearNmToRfnDeviceData() {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers) {
        throw new MethodNotImplementedException();
    }



}
