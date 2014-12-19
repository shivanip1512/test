package com.cannontech.amr.profileCollection.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.profileCollection.service.ProfileCollectionService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.token.DeviceTokenStatus;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenStatus;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.token.service.TokenService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.LoadProfileService;
import com.cannontech.core.service.LoadProfileService.CompletionCallback;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ProfileCollectionServiceImpl implements ProfileCollectionService {
    private final static Logger log = YukonLogManager.getLogger(ProfileCollectionServiceImpl.class);

    @Autowired private LoadProfileService loadProfileService;
    @Autowired private PaoDao paoDao;
    @Autowired private TokenService tokenService;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;

    private class Job {
        final Token token;
        final int channelNum;
        final Instant start;
        final Instant stop;

        // This map contains work we are still waiting on.  Once the callback has completed, it will
        // be removed from this map.
        final Map<PaoIdentifier, Callback> workLeft = Maps.newHashMap();

        List<PaoIdentifier> successes = Lists.newArrayList();
        Map<PaoIdentifier, SpecificDeviceErrorDescription> errors = Maps.newHashMap();
        List<PaoIdentifier> canceledItems = Lists.newArrayList();

        TokenStatus tokenStatus = new DeviceTokenStatus() {
            @Override
            public boolean isFinished() {
                return workLeft.isEmpty();
            }

            @Override
            public List<PaoIdentifier> getSuccesses() {
                if (!isFinished()) {
                    // Until we're done, we'll be modifying the list so it's not safe to return it
                    // without copying it.  So, the user doesn't get anything until they can have
                    // everything.
                    return Collections.emptyList();
                }
                return successes;
            }

            @Override
            public Map<PaoIdentifier, SpecificDeviceErrorDescription> getErrors() {
                if (!isFinished()) {
                    return Collections.emptyMap();
                }
                return errors;
            }

            @Override
            public List<PaoIdentifier> getCanceledItems() {
                if (!isFinished()) {
                    return Collections.emptyList();
                }
                return canceledItems;
            }
        };

        Job(Token token, int channelNum, ReadableInstant start, ReadableInstant stop) {
            log.debug(token + " for channel " + channelNum + ", from " + start + " to " + stop);
            this.token = token;
            this.channelNum = channelNum;
            this.start = start.toInstant();
            this.stop = stop.toInstant();
        }

        TokenStatus getStatus() {
            return tokenStatus;
        }

        void cancel(YukonUserContext userContext) {
            boolean allCanceled = true;
            synchronized (workLeft) {
                for (Map.Entry<PaoIdentifier, Callback> entry : workLeft.entrySet()) {
                    PaoIdentifier paoIdentifier = entry.getKey();
                    Callback callback = entry.getValue();
                    allCanceled &=
                            loadProfileService.removePendingLoadProfileRequest(paoIdentifier,
                                                                               callback.requestId,
                                                                               userContext);
                }
            }
            if (!allCanceled) {
                // Shouldn't happen unless something is broken.
                throw new RuntimeException("could not cancel all requests");
            }
        }

        class Callback implements CompletionCallback {
            long requestId;
            PaoIdentifier paoIdentifier;

            Callback(PaoIdentifier paoIdentifier) {
                this.paoIdentifier = paoIdentifier;
                workLeft.put(paoIdentifier, this);
            }

            @Override
            public void onSuccess(String successInfo) {
                if (log.isDebugEnabled()) {
                    log.debug(token + " onSuccess for " + paoIdentifier);
                }
                successes.add(paoIdentifier);
                workLeft.remove(paoIdentifier);
            }

            @Override
            public void onFailure(int returnStatus, String resultString) {
                log.error(token + " onFailure for " + paoIdentifier + ", returnStatus = " +
                        returnStatus + "; resultString = " + resultString);

                DeviceErrorDescription errorDescription = deviceErrorTranslatorDao.translateErrorCode(returnStatus);
                MessageSourceResolvable detail =
                    YukonMessageSourceResolvable.createSingleCodeWithArguments("yukon.common.device.errorDetail",
                        resultString);
                SpecificDeviceErrorDescription deviceError =
                    new SpecificDeviceErrorDescription(errorDescription, resultString, detail);
                
                errors.put(paoIdentifier, deviceError);
                workLeft.remove(paoIdentifier);
            }

            @Override
            public void onCancel(LiteYukonUser cancelUser) {
                if (log.isDebugEnabled()) {
                    log.debug(token + " onCancel for " + paoIdentifier);
                }
                canceledItems.add(paoIdentifier);
                workLeft.remove(paoIdentifier);
            }
        }

        void initiateLoadProfile(LiteYukonPAObject litePao, YukonUserContext userContext) {
            Callback callback = new Callback(litePao.getPaoIdentifier());
            callback.requestId = loadProfileService.initiateLoadProfile(litePao, channelNum,
                                                                        start.toDate(), stop.toDate(),
                                                                        callback, userContext);
        }
    }

    private final Cache<Token, Job> jobs = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.DAYS).build();

    @Override
    public Token createJob(Set<PaoIdentifier> devices, int channelNum, ReadableInstant start,
                           ReadableInstant stop, YukonUserContext userContext) {
        Token token = null;
        Job pastProfileJob = null;
        synchronized (jobs) {
            token = tokenService.makeToken(TokenType.PROFILE_COLLECTION, jobs.asMap().keySet());
            pastProfileJob = new Job(token, channelNum, start, stop);
            jobs.put(token, pastProfileJob);
        }
        Map<PaoIdentifier, LiteYukonPAObject> litePaos = paoDao.getLiteYukonPaosById(devices);
        for (LiteYukonPAObject litePao : litePaos.values()) {
            pastProfileJob.initiateLoadProfile(litePao, userContext);
        }
        return token;
    }

    @Override
    public void cancelJob(Token token, YukonUserContext userContext) {
        Job pastProfileJob = jobs.getIfPresent(token);
        if (pastProfileJob == null) {
            throw new IllegalArgumentException("no job");
        }
        pastProfileJob.cancel(userContext);
    }

    @Override
    public TokenType getHandledType() {
        return TokenType.PROFILE_COLLECTION;
    }

    @Override
    public TokenStatus getStatus(Token token) {
        Job pastProfileJob = jobs.getIfPresent(token);
        return pastProfileJob == null ? null : pastProfileJob.getStatus();
    }
}
