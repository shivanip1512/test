package com.cannontech.web.dr;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.rfn.service.RfnPerformanceVerificationService;
import com.cannontech.jobs.support.YukonTaskBase;

public class RfnPerformanceVerificationTask extends YukonTaskBase {
    @Autowired private RfnPerformanceVerificationService performanceVerificationService;
    
    @Override
    public void start() {
        performanceVerificationService.archiveVerificationMessage();
        performanceVerificationService.sendPerformanceVerificationMessage();
    }
}
