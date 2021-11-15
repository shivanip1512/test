package com.cannontech.dr.eatonCloud.service;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.util.Range;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.db.point.stategroup.OnOff;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.dr.eatonCloud.model.EatonCloudRelayDataLogs;
import com.cannontech.dr.service.RuntimeCalcSchedulerService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.DatedShedtimeStatus;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.dr.service.impl.ShedtimeStatus;
import com.cannontech.simulators.message.request.EatonCloudRuntimeCalcSimulatonRequest;

public class EatonCloudRuntimeCalcService extends RuntimeCalcSchedulerService {

    private static final Logger log = YukonLogManager.getLogger(EatonCloudRuntimeCalcService.class);

    public void startSimulation(EatonCloudRuntimeCalcSimulatonRequest request) {
        log.info("Simulated Runtime Calculation Started");
        calculateDataLogs();
        log.info("Simulated Runtime Calculation Completed");
    }

    /*
     * Relay X Run Time Data Log X Minutes and Relay X Shed Time Data Log X Minutes are not created automatically, use collection
     * action to add points
     */
    {
        dataLogAttributes =  EatonCloudRelayDataLogs.getDataLogAttributes();
        relayStatusAttributes =  EatonCloudRelayDataLogs.getRelayStatusAttributes();
        types = PaoType.getCloudTypes();
    }

    @Override
    protected DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        RuntimeStatus status = RuntimeStatus.STOPPED;

        if (pointValue.getValue() == OnOff.ON.getRawState()) {
            status = RuntimeStatus.RUNNING;
        }

        return new DatedRuntimeStatus(status, date);
    }

    @Override
    protected DatedShedtimeStatus getShedtimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        ShedtimeStatus status = ShedtimeStatus.RESTORED;

        if (pointValue.getValue() == TrueFalse.TRUE.getRawState()) {
            status = ShedtimeStatus.SHED;
        }

        return new DatedShedtimeStatus(status, date);
    }

    @Override
    protected void calculateRelayDataLogs(YukonPao device, Map<PaoPointIdentifier, Integer> dataLogIdLookup,
            Map<PaoPointIdentifier, Integer> relayStatusIdLookup, Range<Instant> logRange,
            Map<Integer, List<PointValueHolder>> relayStatusData) {
        for (var relayInfo : EatonCloudRelayDataLogs.values()) {
            calculateRelayDataLogs(device, logRange, relayStatusData, relayInfo.getRelayStatusAttribute(), relayInfo.isRuntime(),
                    relayInfo.getRelayNumber(), relayInfo.getDataLogIntervals(), relayStatusIdLookup, dataLogIdLookup);
        }

    }
}
