package com.cannontech.watchdogs.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.watchdog.base.WatchdogBase;
import com.cannontech.watchdog.base.Watchdogs;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.google.common.collect.Maps;

public abstract class ServiceStatusWatchdogImpl extends WatchdogBase implements ServiceStatusWatchdog {
    public static final String WARNING_TYPE = "WarningType";
    public static final String SERVICE_STATUS = "Status";

    Logger log = YukonLogManager.getLogger(ServiceStatusWatchdogImpl.class);
    
    private List<YukonServices> runningServices = new ArrayList<>();

    private static final List<YukonServices> optionalServices = Arrays.asList(
        YukonServices.MACS,
        YukonServices.REALTIMESCANNER,
        YukonServices.FDR,
        YukonServices.CALCLOGIC
        );

    public abstract YukonServices getServiceName();

    @Override
    public Watchdogs getName() {
        return Watchdogs.SERVICESTATUS;
    }

    @Override
    public boolean isServiceRunning(YukonServices service) {
        return true;
    }

    public enum ServiceStatus {
        NOT_STARTED, 
        STOPPED,
        RUNNING,
        UNKNOWN;
    }

    /**
     * This will generate WatchdogWarnings , containing WatchdogWarningType and {key , value} pair of arguments . 
     * If a service have different set of arguments , then that particular service watcher has to override this method and 
     * provide its own implementation .
     */
    public List<WatchdogWarnings> generateWarning(WatchdogWarningType type, ServiceStatus connectionStatus) {
        List<WatchdogWarnings> warnings = new ArrayList<>();

        if (shouldSendWarning(connectionStatus)) {
            Map<String, Object> arguments = Maps.newHashMap();
            arguments.put(WARNING_TYPE, type.name());
            arguments.put(SERVICE_STATUS, ServiceStatus.STOPPED.name());
            WatchdogWarnings watchdogWarning = new WatchdogWarnings(type, arguments);
            warnings.add(watchdogWarning);
            warnings.stream().forEach(warning -> log.info(
                "An Watchdog Warning is generated : " + warning.getWarningType().getWatchdogCategory() + " is "
                    + warning.getArguments().get(SERVICE_STATUS)));
        }
        return warnings;
    }
    
    /**
     * Checks if a warning have to be send or not.
     * Case 1: If a service is optional service, then it should have been seen running aleast once and current
     * status should be stopped.
     * Case 2: If a service is not optional service and its status is stopped then send warning.
     */
    private boolean shouldSendWarning(ServiceStatus connectionStatus) {
        YukonServices service = getServiceName();
        if (isServiceOptional(service)) {
            if (connectionStatus == ServiceStatus.RUNNING && !haveSeenRunning(service)) {
                runningServices.add(service);
            }
            if (!haveSeenRunning(service)) {
                return false;
            }
        }
        if (connectionStatus == ServiceStatus.STOPPED) {
            return true;
        }
        return false;
    }

    /**
     * Check if the passed service is optional service or not.
     * Optional service means it may run or may not run.
     */
    private boolean isServiceOptional(YukonServices serviceName) {
        return optionalServices.contains(serviceName);
    }

    /**
     * Checks if the service was seen running atleast once.
     */
    private boolean haveSeenRunning(YukonServices serviceName) {
        return runningServices.contains(serviceName);
    }

    /**
     * Get the status of windows services for the passed service name. 
     */
    public ServiceStatus getStatusFromWindows(String serviceName) {
        try {
            Process process = new ProcessBuilder("sc.exe", "query", serviceName).start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));) {
                String line;
                String scOutput = "";

                // Append the buffer lines into one string
                while ((line = reader.readLine()) != null) {
                    scOutput += line + "\n";
                }
                log.debug("Status for service " + serviceName + " is " + scOutput);

                if (scOutput.contains("STATE")) {
                    if (scOutput.contains("RUNNING")) {
                        return ServiceStatus.RUNNING;
                    } else {
                        return ServiceStatus.STOPPED;
                    }
                } else {
                    return ServiceStatus.UNKNOWN;
                }
            }
        } catch (IOException e) {
            log.error("Exception when getting service status "+e);
        }
        return ServiceStatus.UNKNOWN;
    }
}
