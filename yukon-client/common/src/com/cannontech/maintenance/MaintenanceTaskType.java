package com.cannontech.maintenance;

import java.util.Arrays;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public enum MaintenanceTaskType {

    DUPLICATE_POINT_DATA_PRUNING(MaintenanceScheduler.POINT_DATA_CLEANUP_SCHEDULER),
    POINT_DATA_PRUNING(MaintenanceScheduler.POINT_DATA_CLEANUP_SCHEDULER),
    DR_RECONCILIATION(MaintenanceScheduler.RFN_MESSAGE_SCHEDULER);

    private static ImmutableMap<MaintenanceScheduler, Set<MaintenanceTaskType>> schedulerToTaskMapping;
    
    static {
        buildMapping();
    }
    private MaintenanceScheduler scheduler;
    
    private MaintenanceTaskType(MaintenanceScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public MaintenanceScheduler getScheduler() {
        return scheduler;
    }
    
    private static void buildMapping() {
        ImmutableMap.Builder<MaintenanceScheduler, Set<MaintenanceTaskType>> schedulerToTaskMappingBuilder =
            ImmutableMap.builder();

        Arrays.stream(MaintenanceScheduler.values()).forEach(scheduler -> {
            ImmutableSet.Builder<MaintenanceTaskType> taskBuilder = ImmutableSet.builder();
            Arrays.stream(values()).forEach(task -> {
                if (task.getScheduler() == scheduler) {
                    taskBuilder.add(task);
                }
            });
            schedulerToTaskMappingBuilder.put(scheduler, taskBuilder.build());
        });
        schedulerToTaskMapping = schedulerToTaskMappingBuilder.build();
    }
    
    public static Set<MaintenanceTaskType> getMaintenanceTaskForScheduler(MaintenanceScheduler scheduler) {
        return schedulerToTaskMapping.get(scheduler);
    }
}