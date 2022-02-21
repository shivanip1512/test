package com.cannontech.web.login.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.LoginController;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.encryption.CryptoException;
import com.cannontech.encryption.CryptoUtils;
import com.cannontech.encryption.impl.AESPasswordBasedCrypto;
import com.cannontech.web.login.LoginCookieHelper;
import com.cannontech.web.login.UserPasswordHolder;

public class LoginCookieHelperImpl implements LoginCookieHelper {
    private static Logger log = YukonLogManager.getLogger(LoginCookieHelperImpl.class);

    // any printable ascii char not 0-9, a-f, or A-F
    private static final String cookieValueSeparater = "_";

    private static AESPasswordBasedCrypto encrypter;

    static {
        try {
            encrypter = new AESPasswordBasedCrypto(getPassKey());
        } catch (CryptoException | IOException | JDOMException e) {
            throw new IllegalStateException("Corrupt or invalid crypto file found. "
                        + "If this file has been tampered with it will need to be"
                        + " removed and a new one will be generated.", e);
        }
    }

    @Override
    public void setRememberMeCookie(HttpServletRequest request, HttpServletResponse response,
                                      String username, String password) {
        String usernameEncrypted = encrypter.encryptToHexStr(username);
        String passwordEncrypted = encrypter.encryptToHexStr(password);
        String cookieValue = usernameEncrypted + cookieValueSeparater + passwordEncrypted;
        Cookie cookie = new Cookie(LoginController.REMEMBER_ME_COOKIE, cookieValue);
        cookie.setMaxAge(Integer.MAX_VALUE);
        String contextPath = request.getContextPath();
        if ("".equals(contextPath)) {
            contextPath = "/";
        }
        cookie.setPath(contextPath);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    @Override
    public UserPasswordHolder readRememberMeCookie(HttpServletRequest request) {
        String [] encryptedCredentials = getRememberMeCookieValue(request).split(cookieValueSeparater);
        if (encryptedCredentials.length != 2) {
            return null;
        }

        try {
            String username = encrypter.decryptHexStr(encryptedCredentials[0]);
            String password = encrypter.decryptHexStr(encryptedCredentials[1]);
            return new UserPasswordHolder(username, password);
        } catch (CryptoException | DecoderException e) {
            log.error("Invalid cookie value. Unable to login via remember me cookie", e);
        }
        return null;
    }

    /**
     * Returns the remembermeLoginCookie crypto key
     */
    private static char[] getPassKey() throws IOException, CryptoException, JDOMException {
        Path rememberMeLoginCookieKey = BootstrapUtils.getKeysFolder().resolve("loginCookieKeyfile.dat");

        if (!Files.exists(rememberMeLoginCookieKey)) {
            log.info(rememberMeLoginCookieKey.getFileName() + " doesn't exist."
                     + " Creating a new CryptoFile for remember me cookie");
            CryptoUtils.createNewCryptoFile(rememberMeLoginCookieKey.toFile());
        }
        return CryptoUtils.getPasskeyFromCryptoFile(rememberMeLoginCookieKey.toFile());
    }

    /**
     * @return the encrypted username/password stored in the remember me cookie or 
     *         empty string if valid cookie isn't found
     */
    private String getRememberMeCookieValue(HttpServletRequest request) {
        final Cookie[] cookieArray = request.getCookies();
        if (cookieArray == null) {
            //TODO: Remove this logging later. Added for YUK-18491.
            log.trace("No cookies found in the request.");
            return "";
        }

        for (Cookie cookie : cookieArray) {
            if (cookie.getName().equals(LoginController.REMEMBER_ME_COOKIE)) {
                String cookieValue = cookie.getValue();
                return cookieValue != null ? cookieValue : "";
            }
        }
        //TODO: Remove this logging later. Added for YUK-18491.
        log.trace("No REMEMBER_ME_COOKIE cookie found in the request.");
        return "";
    }
}
