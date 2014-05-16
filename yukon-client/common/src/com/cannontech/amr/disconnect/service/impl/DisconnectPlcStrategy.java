package com.cannontech.amr.disconnect.service.impl;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectPlcService;
import com.cannontech.amr.meter.dao.MeterDao;
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
import com.google.common.collect.Sets;

public class DisconnectPlcStrategy implements DisconnectStrategy{
    
    @Autowired private MeterDao meterDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectPlcService disconnectPlcService;

    @Override
    public FilteredDevices filter(Iterable<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();

        Set<PaoDefinition> collar =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_COLLAR_COMPATIBLE);

        Set<PaoDefinition> supportDisconnect =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_410,
                                                   PaoTag.DISCONNECT_213,
                                                   PaoTag.DISCONNECT_310);

        Set<PaoDefinition> integrated = Sets.difference(supportDisconnect, collar);

        // devices with integrated disconnect
        final Map<PaoType, PaoDefinition> integratedTypes =
            Maps.uniqueIndex(integrated, new Function<PaoDefinition, PaoType>() {
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });
        Iterable<SimpleDevice> metersWithIntegratedDisconnect =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                public boolean apply(SimpleDevice d) {
                    return integratedTypes.get(d.getDeviceType()) != null;
                }
            });

        // add valid meters
        filteredDevices.addValid(metersWithIntegratedDisconnect);

        // devices with collar
        final Map<PaoType, PaoDefinition> collarTypes =
            Maps.uniqueIndex(collar, new Function<PaoDefinition, PaoType>() {
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });
        Iterable<SimpleDevice> metersWithDiconnectCollar =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                public boolean apply(SimpleDevice d) {
                    return collarTypes.get(d.getDeviceType()) != null;
                }
            });
        Iterable<Integer> deviceIds =
            Iterables.transform(metersWithDiconnectCollar, new Function<SimpleDevice, Integer>() {
                @Override
                public Integer apply(SimpleDevice d) {
                    return d.getDeviceId();
                }
            });

        final Map<Integer, Integer> metersIdWithValidAddress =
            Maps.uniqueIndex(meterDao.getMetersWithDisconnectCollarAddress(deviceIds),
                             new Function<Integer, Integer>() {
                                 public Integer apply(Integer deviceId) {
                                     return deviceId;
                                 }
                             });

        Iterable<SimpleDevice> metersWithValidAddress =
            Iterables.filter(metersWithDiconnectCollar, new Predicate<SimpleDevice>() {
                public boolean apply(SimpleDevice d) {
                    return metersIdWithValidAddress.get(d.getDeviceId()) != null;
                }
            });

        // add valid meters
        filteredDevices.addValid(metersWithValidAddress);

        Iterable<SimpleDevice> metersNotConfigured =
            Iterables.filter(metersWithDiconnectCollar, new Predicate<SimpleDevice>() {
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
    public void execute(DisconnectCommand command, Iterable<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, YukonUserContext userContext) {
        disconnectPlcService.execute(command, meters, callback, execution, userContext);
    }
}
