package com.cannontech.amr.disconnect.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectRfnService;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DisconnectRfnStrategy implements DisconnectStrategy {
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectRfnService disconnectRfnService;

    @Override
    public FilteredDevices filter(Iterable<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();

        Set<PaoDefinition> paoDefinitions =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_RFN);
        final Map<PaoType, PaoDefinition> disconnectTypes =
            Maps.uniqueIndex(paoDefinitions, new Function<PaoDefinition, PaoType>() {
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });

        Iterable<SimpleDevice> metersWithIntegratedDisconnect =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                public boolean apply(SimpleDevice d) {
                    return disconnectTypes.get(d.getDeviceType()) != null;

                }
            });
        filteredDevices.addValid(metersWithIntegratedDisconnect);
        return filteredDevices;
    }


    @Override
    public void cancel(DisconnectResult result, YukonUserContext userContext) {
        disconnectRfnService.cancel(result, userContext);
    }


    @Override
    public void execute(DisconnectCommand command, Iterable<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, YukonUserContext userContext) {
        disconnectRfnService.execute(command, meters, callback, execution, userContext);
    }
}
