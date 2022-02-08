package com.cannontech.dr.itron.service;

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
import com.cannontech.dr.itron.ItronDataEventType;
import com.cannontech.dr.itron.model.ItronRelayDataLogs;
import com.cannontech.dr.service.RuntimeCalcSchedulerService;
import com.cannontech.dr.service.impl.DatedRuntimeStatus;
import com.cannontech.dr.service.impl.DatedShedtimeStatus;
import com.cannontech.dr.service.impl.RuntimeStatus;
import com.cannontech.dr.service.impl.ShedtimeStatus;
import com.cannontech.simulators.message.request.ItronRuntimeCalcSimulatonRequest;

public class ItronRuntimeCalcService extends RuntimeCalcSchedulerService {
    private static final Logger log = YukonLogManager.getLogger(ItronRuntimeCalcService.class);
    
    public void startSimulation(ItronRuntimeCalcSimulatonRequest request) {
        log.info("Simulated Runtime Calculation Started");
        calculateDataLogs();
        log.info("Simulated Runtime Calculation Completed");
    }
    
    {
        dataLogAttributes = ItronRelayDataLogs.getDataLogAttributes();
        relayStatusAttributes = ItronRelayDataLogs.getRelayStatusAttributes();
        types = PaoType.getItronTypes();
    }
    

    @Override
    protected DatedRuntimeStatus getRuntimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        RuntimeStatus status = RuntimeStatus.STOPPED;

        if (pointValue.getValue() == ItronDataEventType.LOAD_ON.getValue()) {
            status = RuntimeStatus.RUNNING;
        }

        return new DatedRuntimeStatus(status, date);
    }

    @Override
    protected DatedShedtimeStatus getShedtimeStatusFromPoint(PointValueHolder pointValue) {
        DateTime date = new DateTime(pointValue.getPointDataTimeStamp());
        ShedtimeStatus status = ShedtimeStatus.RESTORED;

        if (pointValue.getValue() == ItronDataEventType.SHED_START.getValue()) {
            status = ShedtimeStatus.SHED;
        }

        return new DatedShedtimeStatus(status, date);
    }

    @Override
    protected void calculateRelayDataLogs(YukonPao device, Map<PaoPointIdentifier, Integer> dataLogIdLookup,
            Map<PaoPointIdentifier, Integer> relayStatusIdLookup, Range<Instant> logRange,
            Map<Integer, List<PointValueHolder>> relayStatusData) {
        for (var relayInfo : ItronRelayDataLogs.values()) {
            calculateRelayDataLogs(device, logRange, relayStatusData, relayInfo.getRelayStatusAttribute(), relayInfo.isRuntime(),
                    relayInfo.getRelayNumber(), relayInfo.getDataLogIntervals(), relayStatusIdLookup, dataLogIdLookup);
        }

    }
}
