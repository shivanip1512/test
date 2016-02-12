package com.cannontech.dr.rfn.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.amr.rfn.dao.MockRfnDeviceDaoImpl;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.dr.rfn.service.impl.RfnMeterDataSimulatorServiceImpl.TimestampValue;

public class RfnMeterDataSimulatorServiceImplTest {
    private RfnDeviceDao rfnDeviceDao = new MockRfnDeviceDaoImpl();
    
    public void testValue() {
        RfnMeterDataSimulatorServiceImpl simulator = new RfnMeterDataSimulatorServiceImpl();
        PaoType paoType = PaoType.RFN420FL;
        
        List<BuiltInAttribute> att = new ArrayList<BuiltInAttribute>();
       
        att.add(BuiltInAttribute.DELIVERED_KWH);
        att.add(BuiltInAttribute.DELIVERED_KWH);
        att.add(BuiltInAttribute.DELIVERED_KWH);
/*        att.add(BuiltInAttribute.RECEIVED_KWH);
        att.add(BuiltInAttribute.DELIVERED_KWH);
        att.add(BuiltInAttribute.RECEIVED_KVAH);
        att.add(BuiltInAttribute.DELIVERED_KWH);
*/
        
        
        List<RfnDevice> rfnDeviceList = rfnDeviceDao.getDevicesByPaoType(paoType);
        
        for(RfnDevice device: rfnDeviceList) {
       
            for(BuiltInAttribute att1:att) {
                    TimestampValue output = simulator.getValueAndTimestampForPoint(device, att1);
                    System.out.println(att1);
                    System.out.println(output.getTimestamp());
                    System.out.println(output.getValue());
                    System.out.println("--------------------");
            }
        
        }
    }
    
    public static void main(String args[]) {
      new RfnMeterDataSimulatorServiceImplTest().testValue();   
    }

}
