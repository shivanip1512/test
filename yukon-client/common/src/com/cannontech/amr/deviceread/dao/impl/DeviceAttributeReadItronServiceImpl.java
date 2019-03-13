package com.cannontech.amr.deviceread.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.Logger;
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
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.itron.service.ItronCommunicationException;
import com.cannontech.dr.itron.service.ItronDataReadService;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

public class DeviceAttributeReadItronServiceImpl implements DeviceAttributeReadStrategy {
    private static final Logger log = YukonLogManager.getLogger(DeviceAttributeReadItronServiceImpl.class);

    @Autowired private ItronDataReadService itronDataReadService;
    @Autowired private CommandRequestExecutionResultDao commandRequestExecutionResultDao;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public StrategyType getStrategy() {
        return StrategyType.ITRON;
    }

    @Override
    public boolean canRead(PaoType paoType) {
        return paoType.isItron();
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

            List<Integer> deviceIds =
                StreamSupport.stream(devices.spliterator(), false).map(device -> device.getPao().getPaoId()).collect(
                    Collectors.toList());
            // All devices succeeded.
            Multimap<PaoIdentifier, PointValueHolder> devicesToPointValues =
                itronDataReadService.collectDataForRead(deviceIds);
            for (PaoIdentifier pao : devicesToPointValues.keySet()) {
                for (PointValueHolder pointValue : devicesToPointValues.values()) {
                    callback.receivedValue(pao, pointValue);
                }
                commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, pao.getPaoId(), 0);
                callback.receivedLastValue(pao, "");
            }

            DeviceError deviceError = DeviceError.UNKNOWN;
            DeviceErrorDescription error = deviceErrorTranslatorDao.translateErrorCode(deviceError);
            SpecificDeviceErrorDescription deviceErrorDescription =
                new SpecificDeviceErrorDescription(error, deviceError.getDescriptionResolvable());
            
            StreamSupport.stream(devices.spliterator(), false)
                .filter( device -> !devicesToPointValues.keys().contains(device.getPao()))
                .map(PaoMultiPointIdentifier::getPao).forEach(device -> {
                        commandRequestExecutionResultDao.saveCommandRequestExecutionResult(execution, device.getPaoId(),
                            deviceError.getCode());
                        callback.receivedError(device, deviceErrorDescription);
                    });

        } catch (ItronCommunicationException error) {
            DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(DeviceError.TIMEOUT);
            MessageSourceResolvable detail = YukonMessageSourceResolvable.createSingleCodeWithArguments(
                "yukon.common.device.attributeRead.general.readError", error.getMessage());
            SpecificDeviceErrorDescription deviceError = new SpecificDeviceErrorDescription(errorDescription, detail);
            log.error(error);
            callback.receivedException(deviceError);
        }
        callback.complete(getStrategy());
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