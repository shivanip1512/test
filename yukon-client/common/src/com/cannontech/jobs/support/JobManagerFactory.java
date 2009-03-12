package com.cannontech.jobs.support;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.jobs.service.JobManager;

public class JobManagerFactory implements FactoryBean {

    private ConfigurationSource configurationSource;
    private JobManager jobManagerActual;
    private JobManager jobManagerStub;

    @Override
    public Object getObject() throws Exception {
        JobManager jobManager;

        // Retrieve from Master Config whether to disable the Job Manager
        String jobManagerDisabledStr = configurationSource.getString(JobManager.JOB_MANAGER_DISABLED_KEY);
        boolean jobManagerDisabled = false;
        if (StringUtils.isNotBlank(jobManagerDisabledStr)) {
            jobManagerDisabled = Boolean.valueOf(jobManagerDisabledStr.trim());
        }

        if (jobManagerDisabled) {
            jobManager = jobManagerStub;
        } else {
            jobManager = jobManagerActual;
        }
        //initialize it before returning the instance
        jobManager.initialize();

        return jobManager;
    }

    @Override
    public Class<JobManager> getObjectType() {
        return JobManager.class;
    }

    @Override
    public boolean isSingleton() {
        // returns the same Stub or actual Job Manager instance in the given
        // web server context, so return true;
        // then Spring can cache it and avoid subsequent getObject() calls
        return true;
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setJobManagerActual(JobManager jobManagerActual) {
        this.jobManagerActual = jobManagerActual;
    }

    public void setJobManagerStub(JobManager jobManagerStub) {
        this.jobManagerStub = jobManagerStub;
    }

}
