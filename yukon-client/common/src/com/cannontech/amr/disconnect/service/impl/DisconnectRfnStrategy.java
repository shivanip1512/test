package com.cannontech.amr.disconnect.service.impl;

import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.disconnect.model.DisconnectCommand;
import com.cannontech.amr.disconnect.model.DisconnectResult;
import com.cannontech.amr.disconnect.model.FilteredDevices;
import com.cannontech.amr.disconnect.service.DisconnectCallback;
import com.cannontech.amr.disconnect.service.DisconnectRfnService;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.config.RfnMeterDisconnectArming;
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
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DisconnectRfnStrategy implements DisconnectStrategy {
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private DisconnectRfnService disconnectRfnService;
    
    private final RfnMeterDisconnectArming mode;
    private Map<PaoType, PaoDefinition> disconnectTypes;
   
    @Autowired
    public DisconnectRfnStrategy(ConfigurationSource configurationSource) {
        String arm = configurationSource.getString(MasterConfigString.RFN_METER_DISCONNECT_ARMING, "FALSE");
        mode = RfnMeterDisconnectArming.getForCparm(arm);
    }

    @Override
    public FilteredDevices filter(Iterable<SimpleDevice> meters) {
        FilteredDevices filteredDevices = new FilteredDevices();

        Iterable<SimpleDevice> metersWithIntegratedDisconnect =
            Iterables.filter(meters, new Predicate<SimpleDevice>() {
                @Override
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
    public  CommandCompletionCallback<CommandRequestDevice> execute(DisconnectCommand command, Set<SimpleDevice> meters, DisconnectCallback callback,
                        CommandRequestExecution execution, YukonUserContext userContext) {
        disconnectRfnService.execute(command, meters, callback, execution, userContext);
        //This strategy doesn't need callback to cancel the execution.
        return null;
    }
    
    @PostConstruct
    public void init() {
        Set<PaoDefinition> paoDefinitions =
            paoDefinitionDao.getPaosThatSupportTag(PaoTag.DISCONNECT_RFN);
        disconnectTypes =
            Maps.uniqueIndex(paoDefinitions, new Function<PaoDefinition, PaoType>() {
                @Override
                public PaoType apply(PaoDefinition daoDefinition) {
                    return daoDefinition.getType();
                }
            });
    }

    @Override
    public boolean supportsArm(Iterable<SimpleDevice> meters) {
        if (mode == RfnMeterDisconnectArming.ARM || mode == RfnMeterDisconnectArming.BOTH) {
            Iterable<SimpleDevice> rfnDevices =
                Iterables.filter(meters, new Predicate<SimpleDevice>() {
                    @Override
                    public boolean apply(SimpleDevice d) {
                        return d.getDeviceType().isRfn();
                    }
                });
            if (rfnDevices.iterator().hasNext()) {
                return true;
            }
        }
        return false;
    }
}
