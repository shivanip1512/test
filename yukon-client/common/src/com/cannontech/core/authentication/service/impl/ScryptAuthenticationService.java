package com.cannontech.core.authentication.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.general.KDF;
import org.bouncycastle.crypto.general.KDF.SCryptFactory;
import org.bouncycastle.crypto.general.KDF.ScryptParameters;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.events.loggers.UsersEventLogService;
import com.cannontech.core.authentication.dao.YukonUserPasswordDao;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.service.AuthenticationProvider;
import com.cannontech.core.authentication.service.PasswordEncrypter;
import com.cannontech.core.authentication.service.PasswordSetProvider;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ScryptAuthenticationService implements AuthenticationProvider, PasswordSetProvider, PasswordEncrypter {
    @Autowired private YukonUserPasswordDao userPasswordDao;
    @Autowired private UsersEventLogService usersEventLogService; 

    private static SCryptFactory scryptFactory;
    private final static int derivedKeyLength = 32;

    /**
     * Default encryption values. Changing these parameters will change the password hashing, but they are
     * stored with the password so they can be changed to increase password security as computers get faster.
     */
    private final static int cpuCostParam = 16384;
    private final static int memoryCostParam = 8;
    private final static int parallelParam = 1;
    
    public ScryptAuthenticationService() {
        scryptFactory = new SCryptFactory();
    }

    @Override
    public void setPassword(LiteYukonUser user, String newPassword) {
        String digest = encryptPassword(newPassword, cpuCostParam, memoryCostParam, parallelParam);
        userPasswordDao.setPassword(user, AuthType.SCRYPT, digest);
        usersEventLogService.passwordUpdated(user); 
    }

    @Override
    public boolean comparePassword(LiteYukonUser yukonUser, String newPassword, String previousDigest) {
        boolean isSetPasswordRequired = false;
        return checkPassword(previousDigest, newPassword, yukonUser, isSetPasswordRequired);
    }

    @Override
    public boolean login(LiteYukonUser user, String password) {
        boolean isSetPasswordRequired = true;
        String digest = userPasswordDao.getDigest(user);
        return checkPassword(digest, password, user, isSetPasswordRequired);
    }

    /**
     * Encrypt the given password. This is exposed as an extra public method (which is not part of
     * {@link AuthenticationProvider}) because it is only needed when we are encrypting old plain
     * text passwords from the database. We can't use {@link #setPassword(LiteYukonUser, String)} because this
     * encrypting should not change the password changed date.
     */
    @Override
    public String encryptPassword(String password) {
        return encryptPassword(password, cpuCostParam, memoryCostParam, parallelParam);

    }

    /**
     * This method encrypt the password based upon the scrypt parameters
     * The Format of hashing password is $cpuCostParam$memoryCostParam$parallelParam$SALT$KEY
     * SALT and KEY are base64-encoded.
     * Returns the String depends upon the password.
     * 
     * @throws IllegalStateException
     */
    private static String encryptPassword(String password, int cpuCostParam, int memoryCostParam, int parallelParam) {
        try {
            byte[] salt = new byte[16];
            SecureRandom.getInstance("SHA1PRNG").nextBytes(salt);
            byte[] fullHashedPasswordString = getHashedPassword(salt, cpuCostParam,
                memoryCostParam, parallelParam, password);
            StringBuilder sb = new StringBuilder((salt.length + fullHashedPasswordString.length) * 2);
            sb.append("$").append(cpuCostParam).append("$").append(memoryCostParam).append("$").append(parallelParam);
            sb.append("$").append(new String(Base64.encode(salt))).append('$');
            sb.append(new String(Base64.encode(fullHashedPasswordString)));
            return sb.toString();
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("The Character Encoding is not supported");
        }
    }

    /**
     * This method check if the user is logged into the application with correct password and auto
     * update the database if there is any change in scrypt parameters.
     * Returns the boolean check depends upon the stored password.
     * 
     * @throws IllegalStateException
     */
    private boolean checkPassword(String digest, String password, LiteYukonUser user, boolean isSetPasswordRequired) {
        boolean isPasswordMatched = false;
        String[] digestParts = digest.split("\\$");
        int storedCpuCostParam = Integer.parseInt(digestParts[1]);
        int storedMemoryCostParam = Integer.parseInt(digestParts[2]);
        int storedParallelParam = Integer.parseInt(digestParts[3]);
        byte[] salt = Base64.decode(digestParts[4]);
        byte[] fullStoredHashedPasswordString = Base64.decode(digestParts[5]);
        try {
            if (cpuCostParam == storedCpuCostParam && memoryCostParam == storedMemoryCostParam
                && parallelParam == storedParallelParam) {
                byte[] fullHashedPasswordString = getHashedPassword(salt, cpuCostParam,
                    memoryCostParam, parallelParam, password);
                isPasswordMatched = Arrays.equals(fullHashedPasswordString, fullStoredHashedPasswordString);
            } else {
                byte[] fullHashedPasswordString = getHashedPassword(salt,
                    storedCpuCostParam, storedMemoryCostParam, storedParallelParam, password);
                isPasswordMatched = Arrays.equals(fullHashedPasswordString, fullStoredHashedPasswordString);
                if (isPasswordMatched && isSetPasswordRequired) {
                    setPassword(user, password);
                }
            }
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("JVM Doest not support UTF-8 ?");
        }

        return isPasswordMatched;
    }
    
    private static byte[] getHashedPassword(byte[] salt, int cpuCost, int memoryCost, int parallelizationParam,
            String password) throws UnsupportedEncodingException {
        byte[] fullHashedPasswordString = new byte[derivedKeyLength];
        ScryptParameters scryptParameters =
            KDF.SCRYPT.using(salt, cpuCost, memoryCost, parallelizationParam, password.getBytes("UTF-8"));
        scryptFactory.createKDFCalculator(scryptParameters).generateBytes(fullHashedPasswordString);
        return fullHashedPasswordString;
    }
}
