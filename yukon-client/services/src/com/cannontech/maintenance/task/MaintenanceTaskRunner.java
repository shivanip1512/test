package com.cannontech.maintenance.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.maintenance.MaintenanceTaskType;

public class MaintenanceTaskRunner {
    private static final Logger log = YukonLogManager.getLogger(MaintenanceTaskRunner.class);
    // TODO will remove after discussion on this
    private long minimumExecutionTime = 300000; // Time in ms i.e. 5 minute
    // Store completed Tasks
    private Set<MaintenanceTaskType> completedMaintenanceTask = new HashSet<>();

    /**
     * This method is run at the start of a run window.
     * It returns true when all the task have completed and false if all task are not completed.
     * Completed here means that there is no further records to delete for now.
     */
    public synchronized boolean run(List<MaintenanceTask> tasks, Instant endOfRunWindow) {
        Duration timeSliceLength = getTimeSliceLength(tasks.size(), endOfRunWindow);
        while (Instant.now().isBefore(endOfRunWindow) && completedMaintenanceTask.size() != tasks.size()) {
            runTasks(tasks, timeSliceLength);
            int remainingTasks = tasks.size() - completedMaintenanceTask.size();
            if (remainingTasks == 0) {
                log.info("All scheduled maintenance task are completed.");
                return true;
            } else if (!isEnoughTimeAvailable(endOfRunWindow)) {
                log.info("Not enough time to run maintenance tasks.");
                return true;
            }
            else {
                timeSliceLength = getTimeSliceLength(remainingTasks, endOfRunWindow);
            }
            // minimumExecutionTime can be different for other tasks
            if (timeSliceLength.getMillis() < minimumExecutionTime) {
                break;
            }
        }
        completedMaintenanceTask.clear();
        return false;
    }

    private void runTasks(List<MaintenanceTask> tasks, Duration timeSliceLength) {
        for (MaintenanceTask task : tasks) {
            log.info("Running " + task.getMaintenanceTaskType().name() + " maintenance task");
            Instant endOfTimeSlice = Instant.now().plus(timeSliceLength);
            // The task runs repeatedly, until it's out of work, or the allotted time is up
            boolean taskIsDone = false;
            if (!completedMaintenanceTask.contains(task.getMaintenanceTaskType())) {
                while (!taskIsDone && isEnoughTimeAvailable(endOfTimeSlice)) {
                    taskIsDone = task.doTask(endOfTimeSlice);
                    if (taskIsDone) {
                        completedMaintenanceTask.add(task.getMaintenanceTaskType());
                        log.info("Maintenance task " + task.getMaintenanceTaskType().name() + " is completed.");
                    }
                }
            }
        }

    }

    private Duration getTimeSliceLength(int numberOfTasks, Instant endTime) {
        // Determine how long the time slices should be and return that Duration
        // All time slices should maybe add up to less than the total run window, since tasks may run a little
        // over the end of their slice

        Long totalTime = (endTime.getMillis() - Instant.now().getMillis()) / numberOfTasks;
        return Duration.standardSeconds(totalTime / 1000);
    }

    private boolean isEnoughTimeAvailable(Instant processEndTime) {
        return Instant.now().isBefore(processEndTime.minus(minimumExecutionTime));
    }
}
