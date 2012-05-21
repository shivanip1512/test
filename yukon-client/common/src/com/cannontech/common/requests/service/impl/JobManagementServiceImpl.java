package com.cannontech.common.requests.service.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.requests.service.JobManagementService;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenStatus;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.token.service.TokenService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class JobManagementServiceImpl implements JobManagementService {

    @Autowired private TokenService tokenService;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    
    private final Cache<Token, YukonJob> jobs = CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.DAYS).build();
    
    @Override
    public Token createJob(TokenType tokenType, YukonJob runnable) {
        Token token = null;
        
        // Create token for the job, and put the runnable in the jobs map.
        synchronized (jobs) {
            token = tokenService.makeToken(tokenType, jobs.asMap().keySet());
            jobs.put(token, runnable);
        }
        
        // Put the runnable in the thread pool.
        executorService.execute(runnable);
        
        // Return the job's token.
        return token;
    }
    
    @Override
    public YukonJob findJob(Token token) {
        return jobs.getIfPresent(token);
    }

    @Override
    public TokenType getHandledType() {
        return TokenType.YUKON_JOB;
    }

    @Override
    public TokenStatus getStatus(Token token) {
        return jobs.getIfPresent(token);
    }
}
