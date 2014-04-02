package com.cannontech.jobs.support;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.FactoryBean;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.jobs.service.JobManager;
import com.cannontech.jobs.service.impl.JobManagerImpl;
import com.cannontech.jobs.service.impl.JobManagerStub;

public class JobManagerFactory implements FactoryBean {

    private ConfigurationSource configurationSource;
    private JobManagerImpl jobManagerActual;
    private JobManagerStub jobManagerStub;
    private JobManager jobManager;

    @Override
    public Object getObject() throws Exception {
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

    @PostConstruct
    public void initialize() {
        // Retrieve from Master Config whether to disable the Job Manager
        String jobManagerDisabledStr = configurationSource.getString(JobManager.JOB_MANAGER_DISABLED_KEY);
        boolean jobManagerDisabled = false;
        if (StringUtils.isNotBlank(jobManagerDisabledStr)) {
            jobManagerDisabled = Boolean.valueOf(jobManagerDisabledStr.trim());
        }

        // initialize and keep correct impl for the context, to return later
        if (jobManagerDisabled) {
            jobManagerStub.initialize();
            jobManager = jobManagerStub;
        } else {
            jobManagerActual.initialize();
            jobManager = jobManagerActual;
        }
    }

    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    public void setJobManagerActual(JobManagerImpl jobManagerActual) {
        this.jobManagerActual = jobManagerActual;
    }

    public void setJobManagerStub(JobManagerStub jobManagerStub) {
        this.jobManagerStub = jobManagerStub;
    }

}
