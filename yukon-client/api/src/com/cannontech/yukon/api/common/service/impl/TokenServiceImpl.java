package com.cannontech.yukon.api.common.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.yukon.api.common.service.Token;
import com.cannontech.yukon.api.common.service.TokenService;
import com.cannontech.yukon.api.common.service.TokenStatus;
import com.cannontech.yukon.api.common.service.TokenType;
import com.google.common.collect.Maps;

public class TokenServiceImpl implements TokenService {
    private final Map<TokenType, TokenHandler> handlers = Maps.newHashMap();

    @Override
    public TokenStatus getStatus(Token token) {
        return handlers.get(token.getType()).getStatus(token);
    }

    @Override
    public Token makeToken(TokenType type, Set<Token> tokensInUse) {
        // Make a token using the key format "s-n" where "s" is the number of seconds since the
        // epoch and n is an increasing number used to avoid duplication within a given second.
        Instant now = new Instant();
        String secondsBit = Long.toString(now.getMillis() / 1000) + "-";
        int instanceInSecond = 0;
        Token retVal;
        do {
            retVal = new Token(type, secondsBit + instanceInSecond);
            instanceInSecond++;
        } while (tokensInUse.contains(retVal));

        return retVal;
    }

    @Autowired
    public void setHandlers(List<TokenHandler> handlers) {
        for (TokenHandler handler : handlers) {
            this.handlers.put(handler.getHandledType(), handler);
        }
    }
}
