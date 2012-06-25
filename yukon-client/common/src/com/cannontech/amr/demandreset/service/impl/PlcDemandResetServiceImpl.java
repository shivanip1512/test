package com.cannontech.amr.demandreset.service.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.joda.time.DateMidnight;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.PlcDemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PlcDemandResetServiceImpl implements PlcDemandResetService {
    private final static Logger log = YukonLogManager.getLogger(PlcDemandResetServiceImpl.class);

    private final static String DEMAND_RESET_COMMAND = "putvalue ied reset";
    private final static String LAST_RESET_TIME_COMMAND = "getconfig ied time";

    @Autowired private GroupCommandExecutor groupCommandExecutor;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private PointDao pointDao;
    @Autowired private AttributeService attributeService;

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        return paoDefinitionDao.filterPaosForTag(devices, PaoTag.PLC_DEMAND_RESET);
    }

    @Override
    public void sendDemandReset(Set<? extends YukonPao> paos,
                                final DemandResetCallback callback, final LiteYukonUser user) {
        final List<SimpleDevice> devices = Lists.newArrayList(
            Iterables.transform(paos, new Function<YukonPao, SimpleDevice>() {
                @Override
                public SimpleDevice apply(YukonPao pao) {
                    return new SimpleDevice(pao);
                }
            }));

        final DeviceCollection deviceCollection = new DeviceCollection() {
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
        // Use midnight in the local (server) time.  This assumes the meters use the same timezone
        // as the server.  (The reset time for a 470 is always at midnight of the previous night
        // (morning) of the reset.)
        final DateMidnight whenRequested = new DateMidnight();

        SimpleCallback<GroupCommandResult> gceCallback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult item) throws Exception {
                Results results = new Results(item.getResultHolder().getErrors());
                callback.initiated(results);
                retreiveLastResetTime(deviceCollection, callback, whenRequested.toInstant(), user);
            }
        };
        groupCommandExecutor.execute(deviceCollection, DEMAND_RESET_COMMAND,
                                     DeviceRequestType.GROUP_COMMAND, gceCallback, user);
    }

    private void retreiveLastResetTime(DeviceCollection deviceCollection,
                                       final DemandResetCallback demandResetCallback,
                                       final Instant whenRequested, LiteYukonUser user) {
        // TODO: needs to go out at the same time but with a lower priority than the reset request
        final Set<SimpleDevice> devicesLeft = Sets.newHashSet(deviceCollection.getDeviceList());
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult item) throws Exception {
                Map<SimpleDevice, List<PointValueHolder>> values = item.getResultHolder().getValues();
                for (Map.Entry<SimpleDevice, List<PointValueHolder>> entry : values.entrySet()) {
                    SimpleDevice device = entry.getKey();
                    List<PointValueHolder> pointValues = entry.getValue();
                    log.debug("retreiveLastResetTime.callback.handle; device = " + device);
                    Date lastResetTime = null;
                    PaoPointIdentifier drcPaoPoint =
                            attributeService.getPaoPointIdentifierForAttribute(device,
                                                                               BuiltInAttribute.IED_DEMAND_RESET_COUNT);
                    if (drcPaoPoint == null) {
                        log.error("could not find 'IED Demand Reset Count' point for " + device
                                  + ", cannot verify");
                        demandResetCallback.cannotVerify(device,
                                "device does not have \"IED Demand Reset Count\" point");
                        break;
                    }
                    for (PointValueHolder pointValue : pointValues) {
                        PaoPointIdentifier paoPointIdentifier =
                                pointDao.getPaoPointIdentifier(pointValue.getId());
                        if (paoPointIdentifier.getPointIdentifier().equals(drcPaoPoint.getPointIdentifier())) {
                            lastResetTime = pointValue.getPointDataTimeStamp();
                            break;
                        }
                    }
                    if (lastResetTime == null) {
                        log.error("could not find 'IED Demand Reset Count' point for " + device
                                  + ", cannot verify");
                        demandResetCallback.cannotVerify(device,
                                                         "\"IED Demand Reset Count\" point value missing");
                    } else {
                        Instant lastResetInstant = new Instant(lastResetTime);
                        if (lastResetInstant.isBefore(whenRequested)) {
                            demandResetCallback.failed(device);
                        } else {
                            demandResetCallback.verified(device);
                        }
                    }
                    devicesLeft.remove(device);
                }
            }
        };
        groupCommandExecutor.execute(deviceCollection, LAST_RESET_TIME_COMMAND,
                                     DeviceRequestType.GROUP_COMMAND, callback, user);
    }
}
