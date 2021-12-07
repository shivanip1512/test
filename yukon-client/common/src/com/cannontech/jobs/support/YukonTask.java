package com.cannontech.jobs.support;

import org.springframework.web.client.RestClientException;

import com.cannontech.dr.ecobee.EcobeeAuthenticationException;
import com.cannontech.jobs.model.YukonJob;

public interface YukonTask {
    public void start() throws RestClientException, EcobeeAuthenticationException;
    public void stop() throws UnsupportedOperationException;
    public void setJob(YukonJob job);
}
