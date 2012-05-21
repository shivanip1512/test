package com.cannontech.common.requests.service;

import com.cannontech.common.requests.runnable.YukonJob;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.token.service.TokenService.TokenHandler;

public interface JobManagementService extends TokenHandler {
    public Token createJob(TokenType tokenType, YukonJob runnable);
    public YukonJob findJob(Token token);
}
