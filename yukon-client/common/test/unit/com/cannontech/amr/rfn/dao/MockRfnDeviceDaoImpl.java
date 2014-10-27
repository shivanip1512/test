package com.cannontech.amr.rfn.dao;

import static com.cannontech.amr.meter.dao.MockMeterDaoImpl.METER_RFN410FL;
import static com.cannontech.common.pao.PaoType.LCR6200_RFN;

import java.util.HashMap;
import java.util.Map;

import com.cannontech.amr.rfn.dao.impl.RfnDeviceDaoImpl;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.NotFoundException;

public class MockRfnDeviceDaoImpl extends RfnDeviceDaoImpl {
    
    public static final YukonPao LCR_RFN6200 = new PaoIdentifier(30, LCR6200_RFN);

    private Object[][] paoToRfnDeviceData  = 
        {
         // {YukonPAO , sensorSerialNumber, sensorManufacturer, sensorModel}
            {METER_RFN410FL, "410987654", "LGYR", "FocuskWh"},
            {LCR_RFN6200, "12345", "CPS", "1082"},
        };
    
    private Map<YukonPao, RfnDevice> paoToRfnDeviceMap = new HashMap<>(); 
    
    public MockRfnDeviceDaoImpl() {
        init();
    }
    
    private void init() {
        // Building up paoToRfnDeviceMap
        for (Object[] paoToRfnDeviceEntry : paoToRfnDeviceData) {
            YukonPao pao = (YukonPao) paoToRfnDeviceEntry[0];
            RfnIdentifier rfnIdentifier = new RfnIdentifier(String.valueOf(paoToRfnDeviceEntry[1]), 
                    String.valueOf(paoToRfnDeviceEntry[2]), String.valueOf(paoToRfnDeviceEntry[3]));
            RfnDevice rfnDevice = new RfnDevice(rfnIdentifier.getSensorSerialNumber(), pao, rfnIdentifier);
            paoToRfnDeviceMap.put(pao, rfnDevice);
        }
    }
    
    @Override
    public RfnDevice getDevice(YukonPao pao) throws NotFoundException {
        if (paoToRfnDeviceMap.containsKey(pao)) {
            return paoToRfnDeviceMap.get(pao);
        }
        
        throw new NotFoundException("The pao " + pao + " does not exist in the test data.");
    }
    
    @Override
    public RfnDevice getDeviceForId(int paoId) throws NotFoundException {
        for (Object[] paoToRfnDeviceEntry : paoToRfnDeviceData) {
            YukonPao pao = (YukonPao) paoToRfnDeviceEntry[0];
            if (pao.getPaoIdentifier().getPaoId() == paoId) {
                return paoToRfnDeviceMap.get(pao);
            }
        }
        
        throw new NotFoundException("The paoId " + paoId + " does not exist in the test data.");
    }
    
}