package com.cannontech.common.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.dao.model.DynamicRfnDeviceData;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.common.rfn.model.RfnDeviceSearchCriteria;
import com.cannontech.common.rfn.model.RfnModelChange;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.services.systemDataPublisher.service.model.RfnDeviceDescendantCountData;

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
        RfnIdentifier rfnId = new RfnIdentifier("10000", "EATON", "RFGateway");
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
    public Set<Integer> getDeviceIdsForRfnIdentifiers(Iterable<RfnIdentifier> rfnIdentifiers) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> getDevicesForGateway(int gatewayId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void clearDynamicRfnDeviceData() {
        throw new MethodNotImplementedException();
    }

    @Override
    public RfnDevice findDeviceBySensorSerialNumber(String sensorSerialNumber) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnIdentifier> getRfnIdentifiersForGateway(int gatewayId, int rowLimit) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Integer getDeviceIdForRfnIdentifier(RfnIdentifier rfnIdentifier) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> getDevicesForGateways(List<Integer> gatewayIdsList, Iterable<PaoType> paoTypes) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<Integer> getGatewayIdsForDevices(Set<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void saveDynamicRfnDeviceData(Set<DynamicRfnDeviceData> datas) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<DynamicRfnDeviceData> getDynamicRfnDeviceData(Iterable<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public DynamicRfnDeviceData findDynamicRfnDeviceData(Integer deviceId) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, List<DynamicRfnDeviceData>> getDynamicRfnDeviceDataByGateways(Iterable<Integer> gatewayIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Map<Integer, List<DynamicRfnDeviceData>> getDynamicRfnDeviceDataByDevices(Iterable<Integer> deviceIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public List<DynamicRfnDeviceData> getAllDynamicRfnDeviceData() {
        throw new MethodNotImplementedException();
    }

    @Override
    public Set<RfnIdentifier> getDeviceRfnIdentifiersByGatewayIds(Iterable<Integer> gatewayIds) {
        throw new MethodNotImplementedException();
    }

    @Override
    public RfnDeviceDescendantCountData findDeviceDescendantCountDataForPaoTypes(Iterable<PaoType> paoType) {
         throw new MethodNotImplementedException();
    }

    @Override
    public List<RfnDevice> getPartiallyMatchedDevices(String serialNumber, String manufacturer) {
        throw new MethodNotImplementedException();
    }

    @Override
    public void updateRfnModelChange(RfnModelChange rfnModelChange) {
        throw new MethodNotImplementedException();
    }

    @Override
    public Instant findModelChangeDataTimestamp(int deviceId) {
        throw new MethodNotImplementedException();
    }
}
