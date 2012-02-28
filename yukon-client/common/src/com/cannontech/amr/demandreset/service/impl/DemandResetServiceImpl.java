package com.cannontech.amr.demandreset.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.commands.MultipleDeviceResultHolder;
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
    private static final Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

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
    private static class Callback implements SimpleCallback<GroupCommandResult> {
        AtomicBoolean finished = new AtomicBoolean(false);
        MultipleDeviceResultHolder results;

        @Override
        public void handle(GroupCommandResult item) throws Exception {
            results = item.getResultHolder();
            for (Map.Entry<SimpleDevice, String> entry : results.getResultStrings().entrySet()) {
                SimpleDevice device = entry.getKey();
                String result = entry.getValue();
                System.out.println(device + "; result = " + result);
            }
            finished.set(true);
        }
    }

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
    public void sendDemandReset(Iterable<? extends YukonPao> paos, DemandResetCallback callback,
                                LiteYukonUser user) {
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

        Callback gceCallback = new Callback();
        groupCommandExecutor.execute(deviceCollection, COMMAND, DeviceRequestType.GROUP_COMMAND,
                                     gceCallback, user);

        int millisWaited = 0;
        while (!gceCallback.finished.get() && millisWaited < 30 * 1000) {
            try {
                Thread.sleep(250);
                millisWaited += 250;
            } catch (InterruptedException e) {
                log.warn("caught exception in sendDemandReset", e);
            }
        }
        callback.completed(gceCallback.results);
    }
}
