package com.cannontech.yukon.api.amr.service;

import java.util.Set;

import org.joda.time.ReadableInstant;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.api.common.service.Token;
import com.cannontech.yukon.api.common.service.TokenService;
import com.cannontech.yukon.api.common.service.TokenService.TokenHandler;

public interface ProfileCollectionService extends TokenHandler {
    /**
     * Create a "past profile collection" job.  Status of the job can later be queried using
     * the returned Token and a call to either {@link #getStatus(Token)} or
     * {@link TokenService#getStatus(Token)}
     */
    Token createJob(Set<PaoIdentifier> devices, int channelNum, ReadableInstant start,
                    ReadableInstant stop, YukonUserContext userContext);
}
