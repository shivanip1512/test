package com.cannontech.amr.deviceread.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.service.MeterReadCommandGeneratorService;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifierWithUnsupported;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

public class CommandHelper {

    @Autowired private MeterReadCommandGeneratorService meterReadCommandGeneratorService;
    @Autowired private AttributeService attributeService;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    /**
     * Attempts to lookup a command to a group of devices. If command is not provided looks up commands by attributes.
     */
    public ParsedCommands parseCommands(Set<SimpleDevice> devices, Set<? extends Attribute> attributes,
           String command) {
        Set<YukonPao> unsupportedDevices = new HashSet<>();
        Set<CommandRequestDevice> commands = new HashSet<>();
        if (!StringUtils.isEmpty(command)) {
            List<SimpleDevice> supportedDevicesByCommand = new ArrayList<>();
            for (SimpleDevice device : devices) {
                if (supportsPorterRequests(device.getDeviceType())) {
                    supportedDevicesByCommand.add(device);
                } else {
                    unsupportedDevices.add(device.getPaoIdentifier());
                }
            }
            commands.addAll(
                Lists.transform(supportedDevicesByCommand, new Function<SimpleDevice, CommandRequestDevice>() {

                    @Override
                    public CommandRequestDevice apply(SimpleDevice device) {
                        CommandRequestDevice cmdReq = new CommandRequestDevice();
                        cmdReq.setCommandCallback(new CommandCallbackBase(command));
                        cmdReq.setDevice(device);
                        return cmdReq;
                    }

                }));
        } else if (attributes != null && !attributes.isEmpty()) {
            PaoMultiPointIdentifierWithUnsupported paoPointIdentifiers =
                attributeService.findPaoMultiPointIdentifiersForAttributesWithUnsupported(devices, attributes);
            if (meterReadCommandGeneratorService.isReadable(paoPointIdentifiers.getSupportedDevicesAndPoints())) {
                List<PaoMultiPointIdentifier> supportedDevicesByAttribute = new ArrayList<>();
                for (YukonPao pao : paoPointIdentifiers.getUnsupportedDevices()) {
                    unsupportedDevices.add(pao.getPaoIdentifier());
                }
                for (PaoMultiPointIdentifier identifier : paoPointIdentifiers.getSupportedDevicesAndPoints()) {
                    if (supportsPorterRequests(identifier.getPao().getPaoType())) {
                        supportedDevicesByAttribute.add(identifier);
                    } else {
                        unsupportedDevices.add(identifier.getPao());
                    }
                }
                commands.addAll(meterReadCommandGeneratorService.getCommandRequests(supportedDevicesByAttribute));
            } else {
                for (SimpleDevice device : devices) {
                    unsupportedDevices.add(device.getPaoIdentifier());
                }
            }
        }
        return new ParsedCommands(unsupportedDevices, commands);
    }

    /**
     * Returns true if the command for this paoType should be send to porter.
     * RF supports porter requests and it supports the COMMANDER_REQUESTS tag, RF should be excluded for Web Schedules.
     */
    private boolean supportsPorterRequests(PaoType type) {
        return !type.isRfn() && paoDefinitionDao.isTagSupported(type, PaoTag.COMMANDER_REQUESTS);
    }

    public class ParsedCommands {
        private final Set<YukonPao> unsupportedDevices;
        private final Set<CommandRequestDevice> commands;

        public ParsedCommands(Set<YukonPao> unsupportedDevices, Set<CommandRequestDevice> commands) {
            this.unsupportedDevices = unsupportedDevices;
            this.commands = commands;
        }

        public Set<YukonPao> getUnsupportedDevices() {
            return unsupportedDevices;
        }

        public Set<CommandRequestDevice> getCommands() {
            return commands;
        }
    }
}
