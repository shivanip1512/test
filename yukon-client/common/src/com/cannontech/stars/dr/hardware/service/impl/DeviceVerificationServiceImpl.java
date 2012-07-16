package com.cannontech.stars.dr.hardware.service.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.stars.dr.hardware.model.LMHardwareBase;
import com.cannontech.stars.dr.hardware.service.DeviceVerificationService;
import com.cannontech.yukon.BasicServerConnection;

public class DeviceVerificationServiceImpl implements DeviceVerificationService {
    private static final AtomicLong messageIdCounter = new AtomicLong();
    private LmHardwareBaseDao hardwareBaseDao;
    private BasicServerConnection porterConnection;
    

    public boolean verify(final LiteYukonUser user, final String serialNumber) {
        try {
            LMHardwareBase hardware = hardwareBaseDao.getBySerialNumber(serialNumber);
            int routeId = hardware.getRouteId();
            
            StringBuilder sb = new StringBuilder();
            sb.append("control shed 5m relay 1 serial ");
            sb.append(serialNumber);
            sb.append(" select route id ");
            sb.append(routeId);

            String command = sb.toString();
            boolean result = executeCommand(command);
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    
    private boolean executeCommand(final String command) {
        if (porterConnection != null ) {
            Request req = new Request(0, command, messageIdCounter.incrementAndGet());
            porterConnection.write(req);
            CTILogger.info("Sent command to PIL:  " + command);
            return true;
        }

        CTILogger.info("Failed to retrieve a connection");
        return false;
    }

    public void setHardwareBaseDao(LmHardwareBaseDao hardwareBaseDao) {
        this.hardwareBaseDao = hardwareBaseDao;
    }
    
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }
}
