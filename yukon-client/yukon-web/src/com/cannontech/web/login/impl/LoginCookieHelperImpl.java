package com.cannontech.web.login.impl;

import java.security.GeneralSecurityException;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import com.cannontech.core.authentication.service.CryptoService;
import com.cannontech.web.login.LoginCookieHelper;
import com.cannontech.web.login.UserPasswordHolder;

public class LoginCookieHelperImpl implements LoginCookieHelper {
    private static final String delimiter = ":";
    private CryptoService cryptoService;

    @Override
    public String createCookieValue(final String username, final String password) throws GeneralSecurityException {
        final String randomString = RandomStringUtils.randomNumeric(10);
        final StringBuilder sb = new StringBuilder();
        sb.append(randomString);
        sb.append(delimiter);
        sb.append(username);
        sb.append(delimiter);
        sb.append(password);

        String cryptValue = cryptoService.encrypt(sb.toString());
        return cryptValue;
    }

    @Override
    public UserPasswordHolder decodeCookieValue(String cryptValue) throws GeneralSecurityException {
        String value = cryptoService.decrypt(cryptValue);

        String[] split = value.split(delimiter);
        if (split.length < 3) {
            throw new GeneralSecurityException("Illegal cookie value");
        }

        String username = split[1];
        String password = getPassword(2, split);

        UserPasswordHolder holder = new UserPasswordHolder(username, password);
        return holder;
    }
    
    private String getPassword(int startIndex, String[] split) {
        int length = split.length - startIndex;
        String[] tempSplit = new String[length];
        
        System.arraycopy(split, startIndex, tempSplit, 0, length);

        String result = StringUtils.join(tempSplit, delimiter);
        return result;
    }

    public void setCryptoService(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }
}
