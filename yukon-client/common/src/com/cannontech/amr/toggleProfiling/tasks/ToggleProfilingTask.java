package com.cannontech.amr.toggleProfiling.tasks;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.jobs.support.YukonTask;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class ToggleProfilingTask implements YukonTask {

    private Logger logger = YukonLogManager.getLogger(ToggleProfilingTask.class);

    // Injected variables
    private LiteYukonUser liteYukonUser = null;
    private int deviceId;
    private boolean newToggleVal;
    private int channelNum;

    // Injected services and daos
    private PaoDao paoDao = null;
    private DBPersistentDao dbPersistentDao = null;

    public void start() {
        startTask();
    }
    
    private void startTask(){
        logger.info("Starting toggle profiling task scheduled by " + liteYukonUser.getUsername() + ".");
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        
        // get load profile collection of device
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        // do it
        deviceLoadProfile.setLoadProfileIsOnForChannel(channelNum, newToggleVal);
                
        // persist change
        dbPersistentDao.performDBChange(yukonPaobject, DBChangeMsg.CHANGE_TYPE_UPDATE);
    }

    public void stop() throws UnsupportedOperationException {
        // TODO Auto-generated method stub
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

    public void setRunAsUser(LiteYukonUser user) {
        this.liteYukonUser = user;
    }
    
    public LiteYukonUser getRunAsUser() {
        return liteYukonUser;
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
