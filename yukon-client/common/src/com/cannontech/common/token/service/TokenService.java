package com.cannontech.common.token.service;

import java.util.Set;

import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenStatus;
import com.cannontech.common.token.TokenType;

public interface TokenService {
    /**
     * For each {@link TokenType}, there needs to be one service implementing TokenHandler to
     * service request for status via the generic tokenStatusRequest web service.
     */
    interface TokenHandler {
        TokenType getHandledType();
        TokenStatus getStatus(Token token);
    }

    /**
     * A convenience method for making a token.  This will generate a token using the current time
     * and will use tokensInUse to ensure there are no duplicates.
     */
    Token makeToken(TokenType type, Set<Token> tokensInUse);
}
