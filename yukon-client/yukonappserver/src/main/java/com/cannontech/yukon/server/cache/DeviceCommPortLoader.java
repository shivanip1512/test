package com.cannontech.yukon.server.cache;

import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.database.JdbcTemplateHelper;

public class DeviceCommPortLoader {

    

    @SuppressWarnings("unchecked")
    public static List<Integer> getDevicesByCommPort(int portId) {
        String stmt =
            "SELECT DeviceId FROM DeviceDirectCommSettings WHERE PortId = ?";
        
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
        return yukonTemplate.queryForList(stmt, new Object[]{portId}, Integer.class); 
      

    }

    @SuppressWarnings("unchecked")
    public static List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        String sqlStmt = "SELECT DeviceId FROM DeviceAddress WHERE MasterAddress = ?  AND SlaveAddress = ?";  
        
        
        JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();            
        
        return yukonTemplate.queryForList(sqlStmt, new Object[]{masterAddress, slaveAddress}, Integer.class);          
      
    
    }

}


