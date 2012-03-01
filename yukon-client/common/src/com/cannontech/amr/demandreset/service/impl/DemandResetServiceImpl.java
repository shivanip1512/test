package com.cannontech.amr.demandreset.service.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.demandreset.service.DemandResetCallback;
import com.cannontech.amr.demandreset.service.DemandResetCallback.Results;
import com.cannontech.amr.demandreset.service.DemandResetService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class DemandResetServiceImpl implements DemandResetService {
    private static final Logger log = YukonLogManager.getLogger(DemandResetServiceImpl.class);

    private final static Function<DemandResetStrategy, Predicate<YukonPao>> isValidFunctionFromStrategy =
        new Function<DemandResetStrategy, Predicate<YukonPao>>() {
            @Override
            public Predicate<YukonPao> apply(DemandResetStrategy input) {
                return input.getValidDeviceFunction();
            }
        };

    private final static class Callback implements DemandResetCallback {
        AtomicBoolean finished = new AtomicBoolean(false);
        Results results;

        @Override
        public void completed(Results results) {
            this.results = results;
            finished.set(true);
        }
    }

    @Autowired private List<DemandResetStrategy> strategies;

    private static Predicate<YukonPao> isValidDevice;

    @PostConstruct
    public void init() {
        isValidDevice = Predicates.or(Iterables.transform(strategies, isValidFunctionFromStrategy));
    }

    @Override
    public <T extends YukonPao> Iterable<T> validDevices(Iterable<T> paos) {
        return Iterables.filter(paos, isValidDevice);
    }

    @Override
    public void sendDemandReset(Iterable<? extends YukonPao> devices, DemandResetCallback callback,
                                LiteYukonUser user) {
        List<Callback> callbacks = Lists.newArrayList();

        for (DemandResetStrategy strategy : strategies) {
            Callback strategyCallback = new Callback();
            callbacks.add(strategyCallback);
            strategy.sendDemandReset(Iterables.filter(devices, strategy.getValidDeviceFunction()),
                                     strategyCallback, user);
        }

        int millisWaited = 0;
        boolean finished = false;
        // TODO:  Read this timeout from master.cfg or a role property or whatever is correct.
        while (!finished && millisWaited < 30 * 1000) {
            try {
                Thread.sleep(250);
                millisWaited += 250;
                finished = true;
                for (Callback strategyCallback : callbacks) {
                    if (!strategyCallback.finished.get()) {
                        finished = false;
                    }
                }
            } catch (InterruptedException e) {
                log.warn("caught exception in sendDemandReset", e);
            }
        }

        Results finalResults = new Results();
        for (Callback strategyCallback : callbacks) {
            if (strategyCallback.finished.get()) {
                finalResults.append(strategyCallback.results);
            } else {
                // TODO:  What do we do if one times out?
                throw new RuntimeException();
            }
        }

        callback.completed(finalResults);
    }
}
