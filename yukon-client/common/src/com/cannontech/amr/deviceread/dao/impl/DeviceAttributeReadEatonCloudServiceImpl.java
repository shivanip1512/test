package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.model.CollectionActionCancellationCallback;
import com.cannontech.common.bulk.collection.device.model.CollectionActionResult;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.device.commands.dao.CommandRequestExecutionResultDao;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.stream.StreamUtils;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.eatonCloud.model.v1.EatonCloudCommunicationExceptionV1;
import com.cannontech.dr.eatonCloud.service.v1.EatonCloudDataReadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.PointData;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadEatonCloudServiceImpl implements DeviceAttributeReadStrategy {
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadEatonCloudServiceImpl.class);

    @Autowired private EatonCloudDataReadService readService;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getStrategy() {
        return StrategyType.EATON_CLOUD;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isCloudLcr();
    }

    @Override
    public boolean isReadable(Iterable<PaoMultiPointIdentifier> devices) {
        return !Iterables.isEmpty(devices);
    }

    @Override
    public void initiateRead(Iterable<PaoMultiPointIdentifier> devices, DeviceAttributeReadCallback callback,
            CommandRequestExecution execution, LiteYukonUser user) {
        try {
            if (callback.getResult() != null) {
                callback.getResult().addCancellationCallback(
                    new CollectionActionCancellationCallback(getStrategy(), callback));
            }

            Set<Integer> deviceIds =
                StreamUtils.stream(devices).map(device -> device.getPao().getPaoId()).collect(
                    Collectors.toSet());
            
            commandRequestExecutionResultDao.saveExecutionRequest(execution.getId(), deviceIds);

            DateTime start = new DateTime();
            DateTime end = start.minusDays(7);
            Range<Instant> range =  new Range<Instant>(end.toInstant(), false, start.toInstant(), true);

            // All devices succeeded.
            Multimap<PaoIdentifier, PointData> devicesToPointValues = readService.collectDataForRead(deviceIds, range, "COLLECTION ACTION");
          
            for (PaoIdentifier pao : devicesToPointValues.keySet()) {
                Map<Integer, Set<PointData>> data = devicesToPointValues.get(pao).stream()
                        .collect(Collectors.groupingBy(d -> d.getId(), Collectors.toSet()));

                data.forEach((pointId, values) -> {
                    // we will always have time stamp,  we do not create point data if we can't parse date
                    PointData latestData = values.stream().max(Comparator.comparing(PointData::getPointDataTimeStamp))
                            .orElse(null);
                    // success
                    callback.receivedValue(pao, latestData);
                });
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, pao.getPaoId(), 0);
                callback.receivedLastValue(pao, "");
            }

            DeviceError deviceError = DeviceError.UNKNOWN;
            DeviceErrorDescription error = deviceErrorTranslatorDao.translateErrorCode(deviceError);
            SpecificDeviceErrorDescription deviceErrorDescription =
                new SpecificDeviceErrorDescription(error, deviceError.getDescriptionResolvable());
            
            //failure
            StreamUtils.stream(devices)
                .filter( device -> !devicesToPointValues.keys().contains(device.getPao()))
                .map(PaoMultiPointIdentifier::getPao).forEach(device -> {
                        commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, device.getPaoId(),
                            deviceError.getCode());
                        callback.receivedError(device, deviceErrorDescription);
                    });
            
            callback.complete(getStrategy());
        } catch (EatonCloudCommunicationExceptionV1  error) {
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
            MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                    "yukon.common.device.attributeRead.general.readError", error.getDisplayMessage());
            SpecificDeviceErrorDescription deviceError = new SpecificDeviceErrorDescription(errorDescription, detail);
            log.error("Error", error);
            callback.receivedException(deviceError);
        }
    }
    
    @Override
    public int getRequestCount(Collection<PaoMultiPointIdentifier> devicesForThisStrategy) {
        return devicesForThisStrategy.size();
    }

    @Override
    public void cancel(CollectionActionResult result, LiteYukonUser user) {
        // doesn't support cancellation
        result.getCancellationCallbacks(getStrategy()).forEach(CollectionActionCancellationCallback::cancel);
    }
}