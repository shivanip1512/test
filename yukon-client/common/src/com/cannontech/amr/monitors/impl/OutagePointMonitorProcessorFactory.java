package com.cannontech.amr.monitors.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.amr.outageProcessing.service.OutageMonitorService;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.model.PaoPointIdentifier;
import com.cannontech.common.device.definition.model.PointIdentifier;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.core.dao.RawPointHistoryDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.RichPointData;
import com.cannontech.core.dynamic.RichPointDataListener;
import com.cannontech.core.monitors.PointMonitorListenerFactory;
import com.cannontech.core.monitors.PointMonitorProcessor;
import com.cannontech.core.service.SystemDateFormattingService;

public class OutagePointMonitorProcessorFactory extends MonitorProcessorFactoryBase<OutageMonitor> {

    public OutageMonitorDao outageMonitorDao;
    public AttributeService attributeService;
    private DeviceGroupService deviceGroupService;
    private RawPointHistoryDao rawPointHistoryDao;
    private OutageMonitorService outageMonitorService;
    private SystemDateFormattingService systemDateFormattingService;
    private PointMonitorListenerFactory pointMonitorListenerFactory;
    
    @Override
    protected List<OutageMonitor> getAllMonitors() {
        return outageMonitorDao.getAll();
    }

    protected RichPointDataListener createPointListener(final OutageMonitor outageMonitor) {
        final DeviceGroup monitorGroup = deviceGroupService.resolveGroupName(outageMonitor.getGroupName());
        final StoredDeviceGroup resultGroup = outageMonitorService.getOutageGroup(outageMonitor.getOutageMonitorName());
        PointMonitorProcessor monitorProcessor = new PointMonitorProcessor() {

            @Override
            public boolean evaluate(RichPointData richPointValue) {

                YukonDevice yukonDevice = new SimpleDevice(richPointValue.getPaoPointIdentifier()
                                                                         .getPaoIdentifier());

                PointIdentifier pointIdentifier = richPointValue.getPaoPointIdentifier()
                                                                .getPointIdentifier();
                // get the paoPointIdentifier for the attribute
                PaoPointIdentifier paoPointIdentifier = attributeService.getPaoPointIdentifierForAttribute(yukonDevice,
                                                                                                           BuiltInAttribute.BLINK_COUNT);

                // the richPointValue matches the attribute we're looking for
                if (pointIdentifier.equals(paoPointIdentifier.getPointIdentifier())) {
                    List<PointValueHolder> latestPreviousReadings = getLatestPreviousReading(richPointValue);
                    if (!latestPreviousReadings.isEmpty()) {
                        PointValueHolder latestPreviousReading = latestPreviousReadings.get(0);
                        double thisReading = richPointValue.getPointValue().getValue();
                        double previousReading = latestPreviousReading.getValue();
                        double delta = thisReading - previousReading;

                        // if delta is equal to or exceeds our allowed number of
                        // outages
                        if (delta >= outageMonitor.getNumberOfOutages()) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private List<PointValueHolder> getLatestPreviousReading(RichPointData richPointValue) {
                Date startDate = outageMonitorService.getLatestPreviousReadingDate(outageMonitor);

                // use a threshold of time that is up to x times the time
                // period.
                // TODO - possibly look at using a roleProperty or config for
                // setting the 4 part.
                int maxNumberOfDays = outageMonitor.getTimePeriodDays() * 4;
                Duration maxDuration = Duration.standardDays(maxNumberOfDays);
                Date stopDate = new Instant(startDate).minus(maxDuration).toDate();
                return rawPointHistoryDao.getPointData(richPointValue.getPointValue().getId(),
                                                       startDate,
                                                       stopDate,
                                                       1);
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
    public void setSystemDateFormattingService(
            SystemDateFormattingService systemDateFormattingService) {
        this.systemDateFormattingService = systemDateFormattingService;
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
