package com.cannontech.web.capcontrol.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.ScheduleCommand;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.database.db.pao.PaoScheduleAssignment;

public class PaoScheduleServiceHelper {
    @Autowired private ConfigurationSource configurationSource;

    public List<PaoScheduleAssignment> getAssignmentsByDMVFilter(List<PaoScheduleAssignment> assignments) {

        boolean usesDmvTest = configurationSource.isLicenseEnabled(MasterConfigLicenseKey.DEMAND_MEASUREMENT_VERIFICATION_ENABLED);

        if (!usesDmvTest) {
            assignments = assignments.stream().filter(
                c -> !c.getCommandName().startsWith(ScheduleCommand.DmvTest.getCommandName())).collect(
                    Collectors.toList());
        }
        return assignments;

    }

}
