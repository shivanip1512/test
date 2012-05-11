package com.cannontech.common.requests.service;

import com.cannontech.common.requests.runnable.YukonJobRunnable;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;

public interface JobManagementService {
    public Token createJob(TokenType tokenType, YukonJobRunnable runnable);
    public YukonJobRunnable findJob(Token token);
}
