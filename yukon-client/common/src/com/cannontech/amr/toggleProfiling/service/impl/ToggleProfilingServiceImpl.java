package com.cannontech.amr.toggleProfiling.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.amr.toggleProfiling.service.ToggleProfilingService;
import com.cannontech.amr.toggleProfiling.tasks.ToggleProfilingTask;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.jobs.dao.JobStatusDao;
import com.cannontech.jobs.model.JobState;
import com.cannontech.jobs.model.JobStatus;
import com.cannontech.jobs.model.ScheduledOneTimeJob;
import com.cannontech.jobs.model.YukonJob;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.support.YukonJobDefinition;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class ToggleProfilingServiceImpl implements ToggleProfilingService {

    private Logger logger = YukonLogManager.getLogger(ToggleProfilingServiceImpl.class);
    
    private PaoDao paoDao = null;
    private DBPersistentDao dbPersistentDao = null;
    private JobManager jobManager = null;
    private JobStatusDao jobStatusDao = null;
    private YukonJobDefinition<ToggleProfilingTask> toggleProfilingDefinition = null;
    
    public void toggleProfilingForDevice(int deviceId, int channelNum, boolean newToggleVal) {
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        // set
        deviceLoadProfile.setLoadProfileIsOnForChannel(channelNum, newToggleVal);
                
        // persist change
        dbPersistentDao.performDBChange(yukonPaobject, DBChangeMsg.CHANGE_TYPE_UPDATE);

    }
    
    public void scheduleToggleProfilingForDevice(int deviceId, int channelNum, boolean newToggleVal, Date toggleDate, LiteYukonUser user) {
        
        ToggleProfilingTask toggleProfilingTask = toggleProfilingDefinition.createBean();
        toggleProfilingTask.setDeviceId(deviceId);
        toggleProfilingTask.setChannelNum(channelNum);
        toggleProfilingTask.setNewToggleVal(newToggleVal);
        toggleProfilingTask.setRunAsUser(user);

        jobManager.scheduleJob(toggleProfilingDefinition,
                               toggleProfilingTask,
                               toggleDate,
                               user);

        logger.info("Toggle profiling scheduled for deviceId/channel " + deviceId + "/" + channelNum + " on " + toggleDate + ".");

    }
    
    public boolean getToggleValueForDevice(int deviceId, int channelNum) {
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        boolean toggleValue = deviceLoadProfile.loadProfileIsOnForChannel(channelNum);
        
        return toggleValue;
    }
    
    public boolean getToggleValueForDevice(DeviceLoadProfile deviceLoadProfile, int channelNum) {
        
        boolean toggleValue = deviceLoadProfile.loadProfileIsOnForChannel(channelNum);
        
        return toggleValue;
    }
    
    public DeviceLoadProfile getDeviceLoadProfile(int deviceId) {
        
        LiteYukonPAObject device = paoDao.getLiteYukonPAO(deviceId);
        YukonPAObject yukonPaobject = (YukonPAObject)dbPersistentDao.retrieveDBPersistent(device);
        DeviceLoadProfile deviceLoadProfile = ((MCTBase)yukonPaobject).getDeviceLoadProfile();
        
        return deviceLoadProfile;
    }

    private ScheduledOneTimeJob findScheduledJob(int deviceId, int channel) {
        
        //TODO
        // Would like job manager to only return un-run jobs when asking for them by definition
        // Should add functionality to JobStatusDao to make this easier to do. 
        // - need a better way to get status of single job (need a generic way to match jobs first actually)
        // For now, will do that work here.
        
        ScheduledOneTimeJob myJob = null;
        Set<ScheduledOneTimeJob> jobs = jobManager.getOneTimeJobsByDefinition(toggleProfilingDefinition);
        for (ScheduledOneTimeJob job : jobs) {
            if (!job.isDisabled()) {
                ToggleProfilingTask tempTask = (ToggleProfilingTask)jobManager.instantiateTask(job);
                if (tempTask.getDeviceId() == deviceId && tempTask.getChannelNum() == channel) {
                    
                    // have found our job, now make sure it hasn't already completed/failed/disabled..
                    // that is, its not still scheduled to run unless it has no state or it is in restarted state
                    JobStatus<YukonJob> jobStatus = null; 
                    Date jobStartTime = job.getStartTime();
                    List<JobStatus<YukonJob>> statii = jobStatusDao.getAllStatus(DateUtils.addSeconds(jobStartTime, -1), DateUtils.addSeconds(jobStartTime, 1));
                    for (JobStatus<YukonJob> s : statii) {
                        jobStatus = s;
                    }
                    
                    if (jobStatus == null || jobStatus.getJobState() == JobState.RESTARTED) {
                        return job;
                    }
                }
            }
        }
        return myJob;
    }
    
    public Map<String, Object> getToggleJobInfo(int deviceId, int channel) {
        
        Map<String, Object> myJobInfo = null;
        ScheduledOneTimeJob job = findScheduledJob(deviceId, channel);
        
        if (job != null) {
            ToggleProfilingTask tempTask = (ToggleProfilingTask)jobManager.instantiateTask(job);
            myJobInfo = new HashMap<String, Object>();
            myJobInfo.put("startTime", job.getStartTime());
            myJobInfo.put("newToggleVal", tempTask.getNewToggleVal());
            return myJobInfo;
        }
        
        return myJobInfo;
    }
    
    public void disableScheduledJob(int deviceId, int channel) {
        
        ScheduledOneTimeJob job = findScheduledJob(deviceId, channel);
        if (job != null) {
            // abort it just in case its running, then disable it
            jobManager.abortJob(job);
            jobManager.disableJob(job);
        }
    }
    
    @Required
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    @Required
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

    @Required
    public void setJobManager(JobManager jobManager) {
        this.jobManager = jobManager;
    }

    @Required
    public void setToggleProfilingDefinition(
            YukonJobDefinition<ToggleProfilingTask> toggleProfilingDefinition) {
        this.toggleProfilingDefinition = toggleProfilingDefinition;
    }

    @Required
    public void setJobStatusDao(JobStatusDao jobStatusDao) {
        this.jobStatusDao = jobStatusDao;
    }

}
