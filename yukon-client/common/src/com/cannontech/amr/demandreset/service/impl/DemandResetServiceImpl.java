package com.cannontech.amr.demandreset.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
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
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DemandResetServiceImpl implements DemandResetService {
    private final static String COMMAND = "putvalue ied reset";
    private final static Set<PaoType> validTypes =
        ImmutableSet.of(PaoType.MCT430A,
                        PaoType.MCT430A3,
                        PaoType.MCT430S4,
                        PaoType.MCT430SL,
                        PaoType.MCT470);

    private final static Predicate<YukonPao> isValidDevice = new Predicate<YukonPao>() {
        @Override
        public boolean apply(YukonPao pao) {
            return validTypes.contains(pao.getPaoIdentifier().getPaoType());
        }
    };
    private final static Predicate<YukonPao> isInvalidDevice = Predicates.not(isValidDevice);

    @Autowired private GroupCommandExecutor groupCommandExecutor;

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

        SimpleCallback<GroupCommandResult> gceCallback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult item) throws Exception {
            }
        };

        groupCommandExecutor.execute(deviceCollection, COMMAND, DeviceRequestType.GROUP_COMMAND,
                                     gceCallback, user);
    }
}
