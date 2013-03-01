package com.cannontech.openadr.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.openadr.exception.OpenAdrConfigurationException;
import com.cannontech.openadr.service.OpenAdrConfigurationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

public class OpenAdrConfigurationServiceImpl implements OpenAdrConfigurationService {

    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserDao yukonUserDao;
    
    private final int DEFAULT_REQUEST_INTERVAL_MILLIS = 60000;
    private final long DEFAULT_REPLY_LIMIT = 0;
    
    @Override
    public long getReplyLimit() {
        Integer gsVal = globalSettingDao.getInteger(GlobalSettingType.OADR_REPLY_LIMIT);
        return (gsVal != null) ? gsVal : DEFAULT_REPLY_LIMIT;
    }

    @Override
    public int getRequestInterval() {
        Integer gsVal = globalSettingDao.getInteger(GlobalSettingType.OADR_REQUEST_INTERVAL);
        return (gsVal != null) ? gsVal : DEFAULT_REQUEST_INTERVAL_MILLIS;
    }

    @Override
    public String getVenId() {
        String gsVal = globalSettingDao.getString(GlobalSettingType.OADR_VEN_ID);
        if (StringUtils.isBlank(gsVal)) {
            throw new OpenAdrConfigurationException("No VEN identifier defined in Global Settings. OpenADR Service shutting down.");
        }
        return gsVal;
    }
    
    @Override
    public String getVtnId() {
        String gsVal = globalSettingDao.getString(GlobalSettingType.OADR_VTN_ID);
        if (StringUtils.isBlank(gsVal)) {
            throw new OpenAdrConfigurationException("No VTN identifier defined in Global Settings. OpenADR Service shutting down.");
        }
        return gsVal;
    }

    @Override
    public String getVtnUrl() {
        String url = globalSettingDao.getString(GlobalSettingType.OADR_VTN_URL);
        if (StringUtils.isBlank(url)) {
            throw new OpenAdrConfigurationException("No VTN URL defined in Global Settings. OpenADR Service shutting down.");
        }
        return url;
    }

    @Override
    public String getKeystorePath() {
        String path = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PATH);
        if (StringUtils.isBlank(path)) {
            throw new OpenAdrConfigurationException("No keystore path defined in Global Settings. OpenADR Service shutting down.");
        }
        return path;
    }

    @Override
    public String getKeystorePassword() {
        String pass = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PASSWORD);
        if (StringUtils.isBlank(pass)) {
            throw new OpenAdrConfigurationException("No keystore password defined in Global Settings. OpenADR Service shutting down.");
        }
        return pass;
    }

    @Override
    public String getTruststorePath() {
        String tPath = globalSettingDao.getString(GlobalSettingType.OADR_TRUSTSTORE_PATH);
        if (StringUtils.isBlank(tPath)) {
            throw new OpenAdrConfigurationException("No truststore path defined in Global Settings. OpenADR Service shutting down.");
        }
        return tPath;
    }

    @Override
    public String getTruststorePassword() {
        String tPass = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PASSWORD);
        if (StringUtils.isBlank(tPass)) {
            throw new OpenAdrConfigurationException("No truststore password defined in Global Settings. OpenADR Service shutting down.");
        }
        return tPass;
    }

    @Override
    public String getVtnThumbprint() {
        String thumb = globalSettingDao.getString(GlobalSettingType.OADR_VTN_THUMBPRINT);
        if (StringUtils.isBlank(thumb)) {
            throw new OpenAdrConfigurationException("No VTN thumbprint defined in Global Settings. OpenADR Service shutting down.");
        }
        return thumb;
    }

    @Override
    public LiteYukonUser getOadrUser() {
        Integer oadrUserId = globalSettingDao.getInteger(GlobalSettingType.OADR_YUKON_USER);
        if (oadrUserId == null) {
            throw new OpenAdrConfigurationException("No OpenADR Yukon User defined in Global Settings. OpenADR Service shutting down.");
        } 
        
        LiteYukonUser oadrUser = yukonUserDao.getLiteYukonUser(oadrUserId);
        if (oadrUser == null) {
            // Weird, right?
            throw new OpenAdrConfigurationException("The OpenADR Yukon User specified in Global Settings doesn't exist. OpenADR Service shutting down.");
        }
        
        return oadrUser;
    }
}