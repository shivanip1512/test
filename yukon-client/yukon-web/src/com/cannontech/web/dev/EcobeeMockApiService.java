package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.ecobee.message.EcobeeJobStatus;
import com.cannontech.dr.ecobee.message.EcobeeReportJob;
import com.cannontech.dr.ecobee.message.HierarchyResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.RuntimeReportJobStatusResponse;
import com.cannontech.dr.ecobee.message.partial.SetNode;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class EcobeeMockApiService {
    private static final Duration fiveMinutes = Duration.standardMinutes(5);
    private Cache<String,String[]> jobURLCache = CacheBuilder.newBuilder().build();
    private Cache<String,EcobeeJobStatus> jobStatusCache = CacheBuilder.newBuilder().build();
    @Autowired private EcobeeDataConfiguration ecobeeDataConfiguration;
    
    public HierarchyResponse getHierarchyList() {
        List<SetNode> setNodes = new ArrayList<SetNode>();
        long thermostat = 222222;
        String node = "Node";
        for (int i = 0; i < 300; i++, thermostat++) {
            List<String> thermostats = new ArrayList<String>();
            thermostats.add(new Long(thermostat).toString());
            SetNode setNode = new SetNode(node + i, "\\data", null, thermostats);
            setNodes.add(setNode);
        }

        HierarchyResponse hierarchyResponse = new HierarchyResponse(setNodes, new Status(ecobeeDataConfiguration.getHierarchy(), "Hierarchy Tested"));
        return hierarchyResponse;
    }

    public RuntimeReportJobResponse createRuntimeReportJob(RuntimeReportJobRequest request) {
        // Generate random jodId for each request.
        String jobId = UUID.randomUUID().toString().replace("-", "");
        
        // Set initial jobStatus = QUEUED and put it it the cache.
        EcobeeJobStatus jobStatus = EcobeeJobStatus.QUEUED;
        jobStatusCache.invalidateAll();
        jobStatusCache.put(jobId, EcobeeJobStatus.QUEUED);
        Status status = new Status(0, "");
        RuntimeReportJobResponse runtimeReportJobResponse = new RuntimeReportJobResponse(jobId, jobStatus, status);
        //Run a thread to execute task of building csv files (URLs)
        new Thread(() -> {
            runTask(jobId, request);
        }).start();
        return runtimeReportJobResponse;
    }

    public RuntimeReportJobStatusResponse getRuntimeJobStatus(String jobId) {
        // Return one EcobeeReportJob for one jobId (as per the current ecobee implementation).
        List<EcobeeReportJob> jobs = new ArrayList<>(1);
        EcobeeJobStatus jobStatus = jobStatusCache.getIfPresent(jobId);
        // Get the array of URLs from cache.
        String[] files = jobURLCache.getIfPresent(jobId);
        jobs.add(new EcobeeReportJob(jobId, jobStatus, "Simulated test message", files));
        Status status = new Status(0, "");
        return new RuntimeReportJobStatusResponse(jobs, status);
    }

    private void runTask(String jobId, RuntimeReportJobRequest request) {
        String[] fileURLs = ArrayUtils.EMPTY_STRING_ARRAY;
        // Write logic to generate data URLs.
        jobURLCache.invalidateAll();
        jobURLCache.put(jobId, fileURLs);
        // Set jobStatus = COMPLETED , after generating URLs
        jobStatusCache.put(jobId, EcobeeJobStatus.COMPLETED);
    }
}
