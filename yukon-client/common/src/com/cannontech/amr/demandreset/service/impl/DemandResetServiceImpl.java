package com.cannontech.amr.demandreset.service.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.MutableDuration;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DemandResetServiceImpl implements DemandResetService {
    private static final Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

    private final static class Callback implements DemandResetCallback {
        DemandResetCallback callerCallback;
        AtomicBoolean finished = new AtomicBoolean(false);
        Results results;

        @Override
        public void initiated(Results results) {
            // These get aggregated (from all strategies) so we only make a single call back to
            // the the caller of sendDemandReset.
            this.results = results;
            finished.set(true);
        }

        // These go out one at a time to the caller so we can just proxy.
        @Override
        public void verified(SimpleDevice device) {
            callerCallback.verified(device);
        }

        @Override
        public void failed(SimpleDevice device) {
            callerCallback.failed(device);
        }

        @Override
        public void cannotVerify(SimpleDevice device, String reason) {
            callerCallback.cannotVerify(device, reason);
        }
    }

    @Autowired private List<DemandResetStrategy> strategies;
    @Autowired private ConfigurationSource configurationSource;

    private static Duration replyTimeout;

    @PostConstruct
    public void init() {
        replyTimeout =
            configurationSource.getDuration("DEMAND_RESET_REPLY_TIMEOUT", Duration.standardMinutes(1));

    }

    @Override
    public <T extends YukonPao> Set<T> filterDevices(Set<T> devices) {
        Set<T> allValidDevices = Sets.newHashSet();
        for (DemandResetStrategy strategy : strategies) {
            allValidDevices.addAll(strategy.filterDevices(devices));
        }
        return allValidDevices;
    }

    @Override
    public void sendDemandReset(Set<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        List<Callback> callbacks = Lists.newArrayList();

        for (DemandResetStrategy strategy : strategies) {
            Set<? extends YukonPao> strategyDevices = strategy.filterDevices(devices);
            if (strategyDevices.iterator().hasNext()) {
                Callback strategyCallback = new Callback();
                strategyCallback.callerCallback = callback;
                callbacks.add(strategyCallback);
                strategy.sendDemandReset(strategyDevices, strategyCallback, user);
            }
        }

        MutableDuration millisWaited = new MutableDuration(0);
        Duration waitPeriod = new Duration(250);
        boolean finished = false;
        while (!finished && millisWaited.isShorterThan(replyTimeout)) {
            try {
                Thread.sleep(waitPeriod.getMillis());
                millisWaited.plus(waitPeriod);
            } catch (InterruptedException e) {
                log.warn("caught exception in sendDemandReset", e);
            }
            finished = true;
            for (Callback strategyCallback : callbacks) {
                if (!strategyCallback.finished.get()) {
                    finished = false;
                }
            }
        }

        Results finalResults = new Results();
        for (Callback strategyCallback : callbacks) {
            if (strategyCallback.finished.get()) {
                finalResults.append(strategyCallback.results);
            } else {
                log.error("timed out waiting for demand reset");
                throw new RuntimeException("timed out waiting for demand reset");
            }
        }

        callback.initiated(finalResults);
    }
}
