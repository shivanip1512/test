package com.cannontech.amr.toggleProfiling.tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.jobs.support.YukonTaskBase;

public class ToggleProfilingTask extends YukonTaskBase {

    private Logger logger = YukonLogManager.getLogger(ToggleProfilingTask.class);

    // Injected variables
    private int deviceId;
    private boolean newToggleVal;
    private int channelNum;

    // Injected services and daos
    private PaoDao paoDao = null;
    private DBPersistentDao dbPersistentDao = null;

    @Override
    public void start() {
        startTask();
    }
    
    private void startTask(){
        logger.info("Starting toggle profiling task scheduled by " + getUserContext().getYukonUser().getUsername() + ".");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        
        // get load profile collection of device
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        // do it
        deviceLoadProfile.setLoadProfileIsOnForChannel(channelNum, newToggleVal);
                
        // persist change
        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);
    }

    // Setters for injected parameters
    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean getNewToggleVal() {
        return newToggleVal;
    }

    public void setNewToggleVal(boolean newToggleVal) {
        this.newToggleVal = newToggleVal;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    // Setters for injected services and daos
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }
    
}
