package com.cannontech.amr.toggleProfiling.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.toggleProfiling.service.ProfilingService;
import com.cannontech.amr.toggleProfiling.tasks.ToggleProfilingTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

public class ProfilingServiceImpl implements ProfilingService {

    private Logger logger = YukonLogManager.getLogger(ProfilingServiceImpl.class);

    @Autowired private IDatabaseCache databaseCache; 
    @Autowired private DBPersistentDao dbPersistentDao = null;
    @Autowired private JobManager jobManager = null;
    private YukonJobDefinition<ToggleProfilingTask> toggleProfilingDefinition = null;

    @Override
    public void startProfilingForDevice(int deviceId, int channelNum) {
        toggleProfilingForDevice(deviceId, channelNum, true);
    }

    @Override
    public void stopProfilingForDevice(int deviceId, int channelNum) {
        toggleProfilingForDevice(deviceId, channelNum, false);
    }

    private void toggleProfilingForDevice(int deviceId, int channelNum, boolean newToggleVal) {
        
        LiteYukonPAObject device = databaseCache.getAllPaosMap().get(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        // set
        deviceLoadProfile.setLoadProfileIsOnForChannel(channelNum, newToggleVal);
                
        // persist change
        dbPersistentDao.performDBChange(yukonPaobject, Transaction.UPDATE);

    }

    @Override
    public void scheduleStartProfilingForDevice(int deviceId, int channelNum, Date startDate,
                                                YukonUserContext userContext) {
        scheduleToggleProfilingForDevice(deviceId, channelNum, true, startDate, userContext);
    }

    @Override
    public void scheduleStopProfilingForDevice(int deviceId, int channelNum, Date stopDate,
                                               YukonUserContext userContext) {
        scheduleToggleProfilingForDevice(deviceId, channelNum, false, stopDate, userContext);
    }

    private void scheduleToggleProfilingForDevice(int deviceId, int channelNum,
                                                  boolean newToggleVal, Date toggleDate,
                                                  YukonUserContext userContext) {
        ToggleProfilingTask toggleProfilingTask = toggleProfilingDefinition.createBean();
        toggleProfilingTask.setDeviceId(deviceId);
        toggleProfilingTask.setChannelNum(channelNum);
        toggleProfilingTask.setNewToggleVal(newToggleVal);

        jobManager.scheduleJob(toggleProfilingDefinition,
                               toggleProfilingTask,
                               toggleDate,
                               userContext);

        logger.info("Toggle profiling scheduled for deviceId/channel " + deviceId + "/" + channelNum + " on " + toggleDate + ".");

    }

    @Override
    public boolean isProfilingOnNow(int deviceId, int channelNum) {
        
        LiteYukonPAObject device = databaseCache.getAllPaosMap().get(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        boolean toggleValue = deviceLoadProfile.loadProfileIsOnForChannel(channelNum);
        
        return toggleValue;
    }

    @Override
    public DeviceLoadProfile getDeviceLoadProfile(int deviceId) {
        
        LiteYukonPAObject device = databaseCache.getAllPaosMap().get(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        return deviceLoadProfile;
    }

    private ScheduledOneTimeJob findScheduledJob(int deviceId, int channel, boolean newToggleVal) {
        
        ScheduledOneTimeJob myJob = null;
        Set<ScheduledOneTimeJob> jobs = jobManager.getUnRunOneTimeJobsByDefinition(toggleProfilingDefinition);
        for (ScheduledOneTimeJob job : jobs) {
            
            ToggleProfilingTask tempTask = (ToggleProfilingTask)jobManager.instantiateTask(job);
            if (tempTask.getDeviceId() == deviceId && tempTask.getChannelNum() == channel && tempTask.getNewToggleVal() == newToggleVal) {
                myJob = job;
                break;
            }
        }
        return myJob;
    }

    @Override
    public List<Map<String, Object>> getToggleJobInfos(int deviceId, int channel) {
        List<Map<String, Object>> myJobInfos = new ArrayList<Map<String, Object>>();
        
        // toggle on job
        ScheduledOneTimeJob job = findScheduledJob(deviceId, channel, true);
        if (job != null) {
            ToggleProfilingTask tempTask = (ToggleProfilingTask)jobManager.instantiateTask(job);
            Map<String, Object> myJobInfo = new HashMap<String, Object>();
            myJobInfo.put("startTime", job.getStartTime());
            myJobInfo.put("newToggleVal", tempTask.getNewToggleVal());
            myJobInfos.add(myJobInfo);
        }
        
        // toggle off job
        job = findScheduledJob(deviceId, channel, false);
        if (job != null) {
            ToggleProfilingTask tempTask = (ToggleProfilingTask)jobManager.instantiateTask(job);
            Map<String, Object> myJobInfo = new HashMap<String, Object>();
            myJobInfo.put("startTime", job.getStartTime());
            myJobInfo.put("newToggleVal", tempTask.getNewToggleVal());
            myJobInfos.add(myJobInfo);
        }
        
        return myJobInfos;
    }

    @Override
    public void disableScheduledStart(int deviceId, int channel) {
        disableScheduledJob(deviceId, channel, true);
    }

    @Override
    public void disableScheduledStop(int deviceId, int channel) {
        disableScheduledJob(deviceId, channel, false);
    }

    private void disableScheduledJob(int deviceId, int channel, boolean newToggleVal) {
        
        ScheduledOneTimeJob job = findScheduledJob(deviceId, channel, newToggleVal);
        if (job != null) {
            // abort it just in case its running, then disable it
            jobManager.abortJob(job);
            jobManager.disableJob(job);
        }
    }

    @Override
    public Instant getScheduledStart(int deviceId, int channel) {
        ScheduledOneTimeJob job = findScheduledJob(deviceId, channel, true);
        return job == null ? null : new Instant(job.getStartTime());
    }
    
    @Required
    public void setToggleProfilingDefinition(
            YukonJobDefinition<ToggleProfilingTask> toggleProfilingDefinition) {
        this.toggleProfilingDefinition = toggleProfilingDefinition;
    }
}