package com.cannontech.openadr.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.openadr.exception.OpenAdrConfigurationException;
import com.cannontech.openadr.service.OpenAdrConfigurationService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;

// TODO: Get rid of this class and improve the global settings dao.
public class OpenAdrConfigurationServiceImpl implements OpenAdrConfigurationService {
	private static final Logger log = YukonLogManager.getLogger(OpenAdrConfigurationServiceImpl.class);
	
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private YukonUserDao yukonUserDao;
    
    private final int DEFAULT_REQUEST_INTERVAL_MILLIS = 60000;
    private final long DEFAULT_REPLY_LIMIT = 0;
    
    @Override
    public long getReplyLimit() {
        return globalSettingDao.getOptionalInteger(GlobalSettingType.OADR_REPLY_LIMIT)
                .map(Integer::longValue)
                .orElse(DEFAULT_REPLY_LIMIT);
    }

    @Override
    public int getRequestInterval() {
        return globalSettingDao.getOptionalInteger(GlobalSettingType.OADR_REQUEST_INTERVAL)
                .orElse(DEFAULT_REQUEST_INTERVAL_MILLIS);
    }
    
    @Override
    public Period getOpenEndedDuration() {
    	String gsVal = globalSettingDao.getString(GlobalSettingType.OADR_OPEN_ENDED_CONTROL_DURATION);
    	
    	try {
    		long millis = convertInputToMillis(gsVal);
    		return new Period(millis, PeriodType.millis());
    	} catch (IllegalArgumentException iae) {
    		// One of the preconditions failed, probably the formatting.
    		log.error("Open-ended duration string " + gsVal + " doesn't match the expected format. Please change the value" +
    				  "to a decimal value followed by a 'w', 'd', 'h', or 'm'.", iae);
    	} catch (RuntimeException re) {
    		// Something else happened - number format error perhaps?
    		log.error("Unable to parse the provided open-ended duration: " + gsVal, re);
    	}
    	
    	/* 
    	 * We weren't able to convert the string into a period. Returning null allows the caller to determine what
    	 * needs to be done if no user-defined duration was acquired.
    	 */
    	return null;
    }

    @Override
    public String getVenId() {
        String gsVal = globalSettingDao.getString(GlobalSettingType.OADR_VEN_ID);
        if (StringUtils.isBlank(gsVal)) {
            throw new OpenAdrConfigurationException("No VEN identifier defined in Global Settings.");
        }
        return gsVal;
    }
    
    @Override
    public String getVtnId() {
        String gsVal = globalSettingDao.getString(GlobalSettingType.OADR_VTN_ID);
        if (StringUtils.isBlank(gsVal)) {
            throw new OpenAdrConfigurationException("No VTN identifier defined in Global Settings.");
        }
        return gsVal;
    }

    @Override
    public String getVtnUrl() {
        String url = globalSettingDao.getString(GlobalSettingType.OADR_VTN_URL);
        if (StringUtils.isBlank(url)) {
            throw new OpenAdrConfigurationException("No VTN URL defined in Global Settings.");
        }
        return url;
    }

    @Override
    public String getKeystorePath() {
        String path = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PATH);
        if (StringUtils.isBlank(path)) {
            throw new OpenAdrConfigurationException("No keystore path defined in Global Settings.");
        }
        return path;
    }

    @Override
    public String getKeystorePassword() {
        String pass = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PASSWORD);
        if (StringUtils.isBlank(pass)) {
            throw new OpenAdrConfigurationException("No keystore password defined in Global Settings.");
        }
        return pass;
    }

    @Override
    public String getTruststorePath() {
        String tPath = globalSettingDao.getString(GlobalSettingType.OADR_TRUSTSTORE_PATH);
        if (StringUtils.isBlank(tPath)) {
            throw new OpenAdrConfigurationException("No truststore path defined in Global Settings.");
        }
        return tPath;
    }

    @Override
    public String getTruststorePassword() {
        String tPass = globalSettingDao.getString(GlobalSettingType.OADR_KEYSTORE_PASSWORD);
        if (StringUtils.isBlank(tPass)) {
            throw new OpenAdrConfigurationException("No truststore password defined in Global Settings.");
        }
        return tPass;
    }

    @Override
    public String getVtnThumbprint() {
        String thumb = globalSettingDao.getString(GlobalSettingType.OADR_VTN_THUMBPRINT);
        if (StringUtils.isBlank(thumb)) {
            throw new OpenAdrConfigurationException("No VTN thumbprint defined in Global Settings.");
        }
        return thumb;
    }

    @Override
    public LiteYukonUser getOadrUser() {
        Integer oadrUserId = globalSettingDao.getOptionalInteger(GlobalSettingType.OADR_YUKON_USER)
                .orElseThrow(() -> 
                    new OpenAdrConfigurationException("No OpenADR Yukon User defined in Global Settings."));
        
        LiteYukonUser oadrUser = yukonUserDao.getLiteYukonUser(oadrUserId);
        if (oadrUser == null) {
            // Weird, right?
            throw new OpenAdrConfigurationException("The OpenADR Yukon User specified in Global Settings doesn't exist.");
        }
        
        return oadrUser;
    }
    
    /**
     * Convert the user defined string from its current format into milliseconds
     * 
     * PRECONDITIONS: The userVal argument must be non-null and must match a decimal value followed by
     * 		a letter denoting the denomination: w for week, d for day, h for hour, or m for minute.
     * 
     * Examples:
     * 		1.3w
     * 		0.02w
     * 		2.5d
     * 		5.45h
     * 		100m
     * 
     * @param userVal the global settings string the user has defined for the open ended control duration
     * @return the number of milliseconds corresponding to the string value specified by the user.
     * @throws IllegalArgumentException if either of the two preconditions aren't met.
     */
    private long convertInputToMillis(String userVal) throws IllegalArgumentException {
    	checkNotNull(userVal);
    	checkArgument(userVal.matches("^\\d+\\.?\\d*[wdhm]"));
    	
		// Get the decimal portion of the value.
		Double decVal = Double.valueOf(userVal.substring(0, userVal.length()-1));
		
		// Convert whatever the user provided to milliseconds.
		char denom = userVal.charAt(userVal.length()-1);

		switch (denom) {
		case 'W':
		case 'w':
			// Week - convert to days
			decVal *= 7.0;
		case 'D':
		case 'd':
			// Day - convert to hours
			decVal *= 24.0;
		case 'H':
		case 'h':
			// Hour - convert to minutes
			decVal *= 60.0;
		case 'M':
		case 'm':
			// Minute - convert to millis
			decVal *= (60.0 * 1000.0);
			break;
		default:
			// Error
			break;
		}
		
		return Math.round(decVal);
    }
}