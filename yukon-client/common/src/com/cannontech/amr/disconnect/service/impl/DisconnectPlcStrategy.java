package com.cannontech.amr.disconnect.service.impl;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectPlcService;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.common.device.commands.CommandCompletionCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.dao.model.CommandRequestExecution;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class DisconnectPlcStrategy implements DisconnectStrategy{
    
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectPlcService disconnectPlcService;
    
    private Map<PaoType, PaoDefinition> integratedTypes;
    private Map<PaoType, PaoDefinition> collarTypes;

    @Override
    public FilteredDevices filter(Iterable<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();

        Iterable<SimpleDevice> metersWithIntegratedDisconnect =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                @Override
                public boolean apply(SimpleDevice d) {
                    return integratedTypes.get(d.getDeviceType()) != null;
                }
            });

        // add valid meters
        filteredDevices.addValid(metersWithIntegratedDisconnect);


        Iterable<SimpleDevice> metersWithDiconnectCollar =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                @Override
                public boolean apply(SimpleDevice d) {
                    return collarTypes.get(d.getDeviceType()) != null;
                }
            });
        Iterable<Integer> deviceIds = Iterables.transform(metersWithDiconnectCollar, SimpleDevice.TO_PAO_ID);

        Function<Integer, Integer> toSelf = Functions.identity();
        final Map<Integer, Integer> metersIdWithValidAddress =
            Maps.uniqueIndex(meterDao.getMetersWithDisconnectCollarAddress(deviceIds), toSelf);

        Iterable<SimpleDevice> metersWithValidAddress =
            Iterables.filter(metersWithDiconnectCollar, new Predicate<SimpleDevice>() {
                @Override
                public boolean apply(SimpleDevice d) {
                    return metersIdWithValidAddress.get(d.getDeviceId()) != null;
                }
            });

        // add valid meters
        filteredDevices.addValid(metersWithValidAddress);

        Iterable<SimpleDevice> metersNotConfigured =
            Iterables.filter(metersWithDiconnectCollar, new Predicate<SimpleDevice>() {
                @Override
                public boolean apply(SimpleDevice d) {
                    return metersIdWithValidAddress.get(d.getDeviceId()) == null;
                }
            });

        // add not configured
        filteredDevices.addNotConfigured(metersNotConfigured);

        return filteredDevices;
    }

    @Override
    public void cancel(DisconnectResult result, YukonUserContext userContext) {
        disconnectPlcService.cancel(result, userContext);
    }
    
    @Override
    public CommandCompletionCallback<CommandRequestDevice> execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, YukonUserContext userContext) {
        return disconnectPlcService.execute(command, meters, callback, execution, userContext.getYukonUser());
    }
    
    @PostConstruct
    public void init() {
        Set<PaoDefinition> collar =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_COLLAR_COMPATIBLE);

        Set<PaoDefinition> supportDisconnect =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_410,
                                                   PaoTag.DISCONNECT_213,
                                                   PaoTag.DISCONNECT_310);

        Set<PaoDefinition> integrated = Sets.difference(supportDisconnect, collar);

        // devices with integrated disconnect
        integratedTypes =
            Maps.uniqueIndex(integrated, new Function<PaoDefinition, PaoType>() {
                @Override
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });

        // devices with collar
        collarTypes =
            Maps.uniqueIndex(collar, new Function<PaoDefinition, PaoType>() {
                @Override
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });
    }

    @Override
    public boolean supportsArm(Iterable<SimpleDevice> meters) {
        return false;
    }
}
