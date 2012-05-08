package com.cannontech.common.token.service.impl;

import java.util.Set;

import org.joda.time.Instant;

import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.token.service.TokenService;

public class TokenServiceImpl implements TokenService {
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
}
