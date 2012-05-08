package com.cannontech.amr.profileCollection.service;

import java.util.Set;

import org.joda.time.ReadableInstant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.service.TokenService.TokenHandler;
import com.cannontech.user.YukonUserContext;

public interface ProfileCollectionService extends TokenHandler {
    /**
     * Create a "past profile collection" job.  Status of the job can later be queried using
     * the returned Token and a call to either {@link #getStatus(Token)} or
     * {@link TokenService#getStatus(Token)}
     */
    Token createJob(Set<PaoIdentifier> devices, int channelNum, ReadableInstant start,
                    ReadableInstant stop, YukonUserContext userContext);

    /**
     * Cancel the job specified by the given token if it exists and is still running.
     */
    void cancelJob(Token token, YukonUserContext userContext);
}
