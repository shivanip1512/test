package com.cannontech.watchdogs.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ThreadCachingScheduledExecutorService;
import com.cannontech.watchdog.base.WatchdogBase;
import com.cannontech.watchdog.base.YukonServices;
import com.cannontech.watchdog.model.WatchdogWarningType;
import com.cannontech.watchdog.model.WatchdogWarnings;
import com.cannontech.watchdog.model.Watchdogs;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public abstract class ServiceStatusWatchdogImpl extends WatchdogBase implements ServiceStatusWatchdog {
    Logger log = YukonLogManager.getLogger(ServiceStatusWatchdogImpl.class);
    @Autowired private @Qualifier("main") ThreadCachingScheduledExecutorService executor;

    public static final String WARNING_TYPE = "WarningType";
    public static final String SERVICE_STATUS = "Status";

    private List<YukonServices> runningServices = new ArrayList<>();

    Multiset<YukonServices> stoppingServicesCount = HashMultiset.create();

    private List<YukonServices> serviceNotificationSent = Collections.synchronizedList(new ArrayList<YukonServices>());

    private static final List<YukonServices> optionalServices = Arrays.asList(
        YukonServices.MACS,
        YukonServices.REALTIMESCANNER,
        YukonServices.FDR,
        YukonServices.CALCLOGIC
        );

    public abstract YukonServices getServiceName();

    @Override
    public Watchdogs getName() {
        return Watchdogs.SERVICE_STATUS;
    }

    @Override
    public boolean isServiceRunning() {
        return true;
    }

    public enum ServiceStatus {
        NOT_STARTED, 
        STOPPED,
        RUNNING,
        UNKNOWN;
    }

    public void doWatchAction() {
        watchAndNotify();
    }

    @Override
    public void start() {
        executor.scheduleAtFixedRate(this::doWatchAction, 1, 5, TimeUnit.MINUTES);
    }

    /**
     * This will generate WatchdogWarnings , containing WatchdogWarningType and {key , value} pair of arguments . 
     * If a service have different set of arguments , then that particular service watcher has to override this method and 
     * provide its own implementation .
     */
    public List<WatchdogWarnings> generateWarning(WatchdogWarningType type, ServiceStatus connectionStatus) {
        List<WatchdogWarnings> warnings = new ArrayList<>();

        if (shouldSendWarning(connectionStatus)) {
            Map<String, Object> arguments = Maps.newLinkedHashMap();
            arguments.put(WARNING_TYPE, type.name());
            arguments.put(SERVICE_STATUS, ServiceStatus.STOPPED.name());
            WatchdogWarnings watchdogWarning = new WatchdogWarnings(type, arguments);
            warnings.add(watchdogWarning);
            warnings.stream().forEach(warning -> log.info(
                "A Watchdog Warning is generated : " + warning.getWarningType().getName() + " is "
                    + warning.getArguments().get(SERVICE_STATUS)));
        }
        return warnings;
    }
    
    /**
     * Checks if a warning have to be send or not.
     * If Service is stopped first time, not sending notification but showing service status in log file.
     * Case 1: If a service is optional service, then it should have been seen running atleast once and current
     * status should be stopped.
     * Case 2: If a service is not optional service and it was running earlier and have stopped then send warning.
     */
    private boolean shouldSendWarning(ServiceStatus connectionStatus) {
        YukonServices service = getServiceName();

        if (connectionStatus == ServiceStatus.STOPPED) {
            if (stoppingServicesCount.count(service) < 2) {
                stoppingServicesCount.add(service);
            }
            if (stoppingServicesCount.count(service) == 1) {
                log.info("Retrying to get " + service + "service status , not sending notification at this time");
                return false;
            }
        } else if (stoppingServicesCount.count(service) >= 1) {
            stoppingServicesCount.setCount(service, 0);
        }

        if (isServiceOptional(service)) {
            if (connectionStatus == ServiceStatus.RUNNING && !haveSeenRunning(service)) {
                runningServices.add(service);
            }
            if (!haveSeenRunning(service)) {
                log.debug(service +" Service is not seen running yet, so not sending notification.");
                return false;
            }
        }
        // Tracks services to which notification should not be send to,
        // Add services when a notification goes out and remove them when they are back to a valid state
        if (connectionStatus == ServiceStatus.STOPPED && (!serviceNotificationSent.contains(service))) {
            serviceNotificationSent.add(service);
            return true;
        } else if (connectionStatus == ServiceStatus.RUNNING && (serviceNotificationSent.contains(service))) {
            serviceNotificationSent.remove(service);
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
