package com.cannontech.maintenance.task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.cannontech.maintenance.MaintenanceTaskType;

public class MaintenanceTaskRunner {
    // TODO will remove after discussion on this
    private long minimumExecutionTime = 5;
    // Store completed Tasks
    private Set<MaintenanceTaskType> completedMaintenanceTask = new HashSet<>();

    // This method is run at the start of a run window
    public synchronized void run(List<MaintenanceTask> tasks, Instant endOfRunWindow) {
        Duration timeSliceLength = getTimeSliceLength(tasks.size(), endOfRunWindow);
        while (Instant.now().isBefore(endOfRunWindow) && completedMaintenanceTask.size() != tasks.size()) {
            runTasks(tasks, timeSliceLength);
            timeSliceLength = getTimeSliceLength(tasks.size(), endOfRunWindow);
            // minimumExecutionTime can be different for other tasks
            if (timeSliceLength.getStandardMinutes() < minimumExecutionTime) {
                break;
            }
        }
        completedMaintenanceTask.clear();
    }

    private void runTasks(List<MaintenanceTask> tasks, Duration timeSliceLength) {

        for (MaintenanceTask task : tasks) {
            Instant endOfTimeSlice = Instant.now().plus(timeSliceLength);
            // The task runs repeatedly, until it's out of work, or the allotted time is up
            boolean taskIsDone = false;
            if (!completedMaintenanceTask.contains(task.getMaintenanceTaskType())) {
                while (!taskIsDone && Instant.now().isBefore(endOfTimeSlice)) {
                    taskIsDone = task.doTask(endOfTimeSlice);
                    if (taskIsDone) {
                        completedMaintenanceTask.add(task.getMaintenanceTaskType());
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

}
