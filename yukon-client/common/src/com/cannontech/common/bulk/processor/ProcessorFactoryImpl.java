package com.cannontech.common.bulk.processor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.collection.device.model.CollectionActionDetail;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao.LastActionStatus;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DeviceConfigState;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    @Autowired private DeviceConfigurationService deviceConfigurationService = null;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;

    @Override
    public Processor<SimpleDevice> createAssignConfigurationToYukonDeviceProcessor(final DeviceConfiguration configuration,
            Map<Integer, DeviceConfigState> deviceToState, YukonUserContext userContext) {
        return new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                DeviceConfigState state = deviceToState.get(device.getDeviceId());
                if (state != null && state.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
                    MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                    throw new ProcessingException(accessor.getMessage("yukon.web.widgets.configWidget.actionInProgress"),
                            CollectionActionDetail.INVALID_STATE);
                }
                try {
                    String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
                    deviceConfigurationService.assignConfigToDevice(configuration, device, userContext.getYukonUser(),
                            deviceName);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), CollectionActionDetail.UNSUPPORTED);
                }
            }
        };
    }

    @Override
    public Processor<SimpleDevice> createUnassignConfigurationToYukonDeviceProcessor(
            Map<Integer, DeviceConfigState> deviceToState, LiteYukonUser user) {
        return new SingleProcessor<SimpleDevice>() {
            @Override
            public void process(SimpleDevice device) throws ProcessingException {
                DeviceConfigState state = deviceToState.get(device.getDeviceId());
                if (state != null && state.getLastActionStatus() == LastActionStatus.IN_PROGRESS) {
                    throw new ProcessingException("Cannot unassign while config action is in progress.",
                            CollectionActionDetail.INVALID_STATE);
                }
                try {
                    String deviceName = dbCache.getAllPaosMap().get(device.getPaoIdentifier().getPaoId()).getPaoName();
                    deviceConfigurationService.unassignConfig(device, user, deviceName);
                } catch (InvalidDeviceTypeException e) {
                    throw new ProcessingException(e.getMessage(), CollectionActionDetail.UNSUPPORTED);
                }
            }
        };
    }
}
