package com.cannontech.amr.monitors.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Range;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.monitors.PointMonitorListenerFactory;
import com.cannontech.core.monitors.PointMonitorProcessor;
import com.google.common.collect.Iterables;

public class OutagePointMonitorProcessorFactory extends MonitorProcessorFactoryBase<OutageMonitor> {

    public OutageMonitorDao outageMonitorDao;
    public AttributeService attributeService;
    private DeviceGroupService deviceGroupService;
    private RawPointHistoryDao rawPointHistoryDao;
    private OutageMonitorService outageMonitorService;
    private PointMonitorListenerFactory pointMonitorListenerFactory;
    
    @Override
    protected List<OutageMonitor> getAllMonitors() {
        return outageMonitorDao.getAll();
    }

    protected RichPointDataListener createPointListener(final OutageMonitor outageMonitor) {
        final DeviceGroup monitorGroup = deviceGroupService.findGroupName(outageMonitor.getGroupName());
        final StoredDeviceGroup resultGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
        PointMonitorProcessor monitorProcessor = new PointMonitorProcessor() {

            @Override
            public boolean evaluate(RichPointData richPointData) {

                // the richPointData matches the attribute we're looking for
                if (!attributeService.isPointAttribute(richPointData.getPaoPointIdentifier(), BuiltInAttribute.BLINK_COUNT)) {
                    return false;
                }

                int pointId = richPointData.getPointValue().getId();
                Date currentTimeStamp = richPointData.getPointValue().getPointDataTimeStamp();
                // get the one reading prior to this stopDate is exclusive, so we simply pass the current reading's time
				Range<Date> prevDateRange = new Range<Date>(null, true, currentTimeStamp, false);
                List<PointValueHolder> previousPointDatas =
                    rawPointHistoryDao.getLimitedPointData(pointId, prevDateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false, Order.REVERSE, 1);
                if (previousPointDatas.isEmpty()) {
                    return handleNoPriorReading(richPointData.getPointValue());
                }

                PointValueHolder previousValue = Iterables.getOnlyElement(previousPointDatas);

                // check if reading is unchanged
                if (previousValue.getValue() == richPointData.getPointValue().getValue()) {
                    // no need to flag an outage, nothing changed since last time
                    return false;
                }
                
             // check if the previous value can be used as prior value for calculation (saves trip to the database)
                Duration minOutagePeriod = Duration.standardDays(outageMonitor.getTimePeriodDays());
                Instant currentReadingInstant = new Instant(richPointData.getPointValue().getPointDataTimeStamp());
                Instant latestTimeOfPriorReading = currentReadingInstant.minus(minOutagePeriod);

                Instant previousReadingInstant = new Instant(previousValue.getPointDataTimeStamp());
                if (previousReadingInstant.isBefore(latestTimeOfPriorReading)) {
                    return checkThreshold(previousValue, richPointData.getPointValue());
                }
                
                // check if the change from the previous is greater than our threshold anyways
                if (checkThreshold(previousValue, richPointData.getPointValue())) {
                    return true;
                }

                // go back to the database
                Range<Date> priorDateRange = new Range<Date>(null, true, latestTimeOfPriorReading.toDate(), false);
                List<PointValueHolder> priorPointDatas =
                    rawPointHistoryDao.getLimitedPointData(pointId,  priorDateRange.translate(CtiUtilities.INSTANT_FROM_DATE), false, Order.REVERSE, 1);
				if (priorPointDatas.isEmpty()) {
                    return handleNoPriorReading(richPointData.getPointValue());
                }

                PointValueHolder priorValue = Iterables.getOnlyElement(priorPointDatas);
                return checkThreshold(priorValue, richPointData.getPointValue());
            }

            private boolean handleNoPriorReading(PointValueHolder pointValue) {

                return false;
            }

            private boolean checkThreshold(PointValueHolder previousValue, PointValueHolder pointValue) {
                double thisReading = pointValue.getValue();
                double previousReading = previousValue.getValue();
                double delta = thisReading - previousReading;
                
                boolean thresholdExceeded = delta >= outageMonitor.getNumberOfOutages();
                return thresholdExceeded;
            }

            @Override
            public DeviceGroup getGroupToMonitor() {
                return monitorGroup;
            }

            @Override
            public StoredDeviceGroup getResultsGroup() {
                return resultGroup;
            }
        };
        RichPointDataListener pointDataListener = pointMonitorListenerFactory.createListener(monitorProcessor);
        return pointDataListener;
    }

    @Autowired
    public void setRawPointHistoryDao(RawPointHistoryDao rawPointHistoryDao) {
        this.rawPointHistoryDao = rawPointHistoryDao;
    }

    @Autowired
    public void setOutageMonitorService(OutageMonitorService outageMonitorService) {
        this.outageMonitorService = outageMonitorService;
    }

    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @Autowired
    public void setOutageMonitorDao(OutageMonitorDao outageMonitorDao) {
        this.outageMonitorDao = outageMonitorDao;
    }

    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    @Autowired
    public void setPointMonitorListenerFactory(
            PointMonitorListenerFactory pointMonitorListenerFactory) {
        this.pointMonitorListenerFactory = pointMonitorListenerFactory;
    }
}
