package com.cannontech.web.updater.point;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.style.ToStringCreator;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.service.PointFormattingService;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.util.Command;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;

public class CachedPointDataCorrelationServiceImpl implements CachedPointDataCorrelationService {

    private static final Logger log = YukonLogManager.getLogger(CachedPointDataCorrelationServiceImpl.class);

    @Autowired private RawPointHistoryDao rawPointHistoryDao;
    @Autowired private PointUpdateBackingService pointUpdateBackingService;
    @Autowired private AsyncDynamicDataSource asyncDataSource;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PointFormattingService pointFormattingService;
    @Autowired private DispatchClientConnection dispatch;

    @Override
    public void correlateAndLog(int pointId) {
        boolean isErrorReportingEnabled = globalSettingDao.getBoolean(GlobalSettingType.ERROR_REPORTING);
        if (isErrorReportingEnabled) {
            try {
                log.info("correlateAndLog pointId=" + pointId);
                logAndMatch(pointId);
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    /**
     * Logs cached and historical values
     */
    private void logAndMatch(int pointId) {
        PointValueQualityHolder pointUpdateBackingServiceCachedValue =
            pointUpdateBackingService.getCachedValue(pointId);
        List<PointValueQualityHolder> historicalValues = rawPointHistoryDao.getMostRecentValues(pointId, 2);
        PointValueQualityHolder asyncDataSourceValue = asyncDataSource.getPointValue(pointId);

        if (historicalValues.isEmpty()) {
            log.info("No historical values to match");
        } else {
            if (!isMatched(historicalValues.get(0), pointUpdateBackingServiceCachedValue, asyncDataSourceValue)) {
                log.info("correlateAndLog pointId=" + pointId);
                asyncDataSource.logListenerInfo(pointId);
                log.info("Value from PointUpdateBackingService cache");
                log(pointUpdateBackingServiceCachedValue);
                log.info("Value from AsyncDynamicDataSource cache");
                log(asyncDataSourceValue);

                log.info("Values from RAWPOINTHISTORY");

                for (PointValueQualityHolder historicalValue : historicalValues) {
                    log(historicalValue);
                }
                log.info(
                    "Cached values do not match historical values. Notifying Dispatch to log information for point id="
                        + pointId);
                notifyDispatch(pointId, historicalValues.toString() + "," + pointUpdateBackingServiceCachedValue + ","
                    + asyncDataSourceValue);
            } else {
                log.info("Cached values match historical values");
            }
        }
    }

    /**
     * Returns true if cached values match historical values.
     */
    boolean isMatched(PointValueQualityHolder historicalValue,
            PointValueQualityHolder pointUpdateBackingServiceCachedValue,
            PointValueQualityHolder asyncDataSourceValue) {
        if (historicalValue.getPointDataTimeStamp().getTime() < pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime()) {
            log.info("Historical values haven't been written to RPH yet");
            return true;
        }
        log.info("Comparing values:");
        log.info("historicalValue="+historicalValue.getValue());
        log.info("pointUpdateBackingServiceCachedValue="+pointUpdateBackingServiceCachedValue.getValue());
        log.info("asyncDataSourceValue="+asyncDataSourceValue.getValue());
        boolean matchedByValue = historicalValue.getValue() == pointUpdateBackingServiceCachedValue.getValue()
            && pointUpdateBackingServiceCachedValue.getValue() == asyncDataSourceValue.getValue();
        boolean matchedByDate =
            historicalValue.getPointDataTimeStamp().getTime() == pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime()
                && pointUpdateBackingServiceCachedValue.getPointDataTimeStamp().getTime() == asyncDataSourceValue.getPointDataTimeStamp().getTime();
        return matchedByValue && matchedByDate;
    }

    private void log(PointValueQualityHolder value) {
        if (value != null) {
            ToStringCreator tsc = new ToStringCreator(value);
            tsc.append("id", value.getId());
            tsc.append("value", formatValue(value));
            tsc.append("timestamp", new DateTime(value.getPointDataTimeStamp()).toString("MM-dd-yyyy HH:mm:ss"));
            tsc.append("type", value.getType());
            tsc.append("quality", value.getPointQuality());
            log.info("..." + tsc.toString());
        }
    }

    private String formatValue(PointValueQualityHolder value) {
        String valueString;
        try {
            valueString = pointFormattingService.getValueString(value, Format.SHORT, YukonUserContext.system);
        } catch (IllegalArgumentException e) {
            valueString = pointFormattingService.getValueString(value, Format.RAWVALUE, YukonUserContext.system);
        }
        return valueString;
    }
    
    private void notifyDispatch(int pointId, String javaDebug) {
        final List<Integer> data = new ArrayList<Integer>(1);
        data.add(pointId);

        Command command = new Command();
        command.setUserName(YukonUserContext.system.getYukonUser().getUsername());
        command.setOpString(javaDebug);
        command.setOperation(Command.POINT_DATA_DEBUG);
        command.setOpArgList(data);

        dispatch.queue(command);
    }
}
