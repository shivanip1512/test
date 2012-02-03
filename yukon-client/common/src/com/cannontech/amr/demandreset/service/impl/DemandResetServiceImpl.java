package com.cannontech.amr.demandreset.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandCallback;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestExecutionParameterDto;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DemandResetServiceImpl implements DemandResetService {
    // This isn't the complete command but the version of groupCommandExecutor.execute we're calling
    // right now doesn't use it anyway.
    // In 5.4.2, we'll switch to the version of groupCommandExecutor.execute that does.  When we do,
    // this will be the correct command.  (We need an update in porter to support this.)
    private final static String command = "putvalue ied reset";

    private final static ImmutableMap<PaoType, CommandCallback> commandsByPaoType;
    static {
        Builder<PaoType, CommandCallback> builder = ImmutableMap.builder();

        builder.put(PaoType.MCT430A, new CommandCallback() {
            @Override
            public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
                return "putvalue ied reset alpha";
            }
        });
        builder.put(PaoType.MCT430A3, new CommandCallback() {
            @Override
            public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
                return "putvalue ied reset a3";
            }
        });
        builder.put(PaoType.MCT430S4, new CommandCallback() {
            @Override
            public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
                return "putvalue ied reset s4";
            }
        });
        builder.put(PaoType.MCT430SL, new CommandCallback() {
            @Override
            public String generateCommand(CommandRequestExecutionParameterDto parameterDto) {
                return "putvalue ied reset sentinal";
            }
        });

        commandsByPaoType = builder.build();
    }

    private final static Predicate<YukonPao> isValidDevice = new Predicate<YukonPao>() {
        @Override
        public boolean apply(YukonPao pao) {
            return commandsByPaoType.containsKey(pao.getPaoIdentifier().getPaoType());
        }
    };
    private final static Predicate<YukonPao> isInvalidDevice = Predicates.not(isValidDevice);

    @Autowired
    private GroupCommandExecutor groupCommandExecutor;

    @Override
    public <T extends YukonPao> Iterable<T> validDevices(Iterable<T> paos) {
        return Iterables.filter(paos, isValidDevice);
    }

    @Override
    public <T extends YukonPao> Iterable<T> invalidDevices(Iterable<T> paos) {
        return Iterables.filter(paos, isInvalidDevice);
    }
    
    @Override
    public void sendDemandReset(Iterable<? extends YukonPao> paos, LiteYukonUser user) {
        // In 5.4.1 we won't support RFN so we'll just do a simple porter commands for
        // now. This will change in 5.4.2 and this comment will be removed.

        final List<SimpleDevice> devices = Lists.newArrayList(
            Iterables.transform(paos, new Function<YukonPao, SimpleDevice>() {
                @Override
                public SimpleDevice apply(YukonPao pao) {
                    return new SimpleDevice(pao);
                }
            }));

        // TODO:  We should split DeviceCollection into two interfaces--one for the basic collection
        // of devices needed by the service calls that execute the commands (and this method)
        // and another which contains the UI stuff (getDescription and getCollectionParameters).
        DeviceCollection deviceCollection = new DeviceCollection() {
            @Override
            public Iterator<SimpleDevice> iterator() {
                return devices.iterator();
            }

            @Override
            public List<SimpleDevice> getDevices(int start, int size) {
                return devices.subList(start, start + size);
            }

            @Override
            public List<SimpleDevice> getDeviceList() {
                return devices;
            }

            @Override
            public long getDeviceCount() {
                return devices.size();
            }

            @Override
            public MessageSourceResolvable getDescription() {
                return null;
            }

            @Override
            public Map<String, String> getCollectionParameters() {
                return null;
            }
        };
        Function<SimpleDevice, CommandRequestDevice> commandForDevice =
            new Function<SimpleDevice, CommandRequestDevice>() {
                @Override
                public CommandRequestDevice apply(SimpleDevice device) {
                    CommandRequestDevice retVal = new CommandRequestDevice();
                    retVal.setDevice(device);
                    retVal.setCommandCallback(commandsByPaoType.get(device.getDeviceType()));
                    return retVal;
                }
            };
        List<CommandRequestDevice> requests = Lists.transform(devices, commandForDevice);
        SimpleCallback<GroupCommandResult> ceCallback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult item) throws Exception {
            }
        };
        groupCommandExecutor.execute(deviceCollection, command, requests,
                                     DeviceRequestType.GROUP_COMMAND, ceCallback, user);
    }
}
