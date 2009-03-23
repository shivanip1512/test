package com.cannontech.notif.voice;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SimpleTemplateProcessor;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.notif.voice.callstates.Connecting;
import com.cannontech.notif.voice.callstates.NoConnection;
import com.google.common.collect.Maps;


public class UrlDialerFactory implements DialerFactory {
    private RolePropertyDao rolePropertyDao;
    private YukonUserDao yukonUserDao;
    
    private Logger log = YukonLogManager.getLogger(UrlDialerFactory.class);

    @Override
    public Dialer createDialer(LiteEnergyCompany energyCompany) {
        LiteYukonUser ecUser = yukonUserDao.getLiteYukonUser(energyCompany.getUserID());
        final String urlTemplate = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.IVR_URL_DIALER_TEMPLATE, ecUser);
        log.debug("template: " + urlTemplate);
        final String successMatcherStr = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.IVR_URL_DIALER_SUCCESS_MATCHER, ecUser);
        final String dialPrefix = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.CALL_PREFIX, null);
        Pattern successPatternTemp;
        try {
            successPatternTemp = Pattern.compile(successMatcherStr);
        } catch (Exception e1) {
            successPatternTemp = Pattern.compile(".*");
        }
        final Pattern successPattern = successPatternTemp;
        log.debug("success pattern: " + successPattern);

        return new Dialer() {

            @Override
            protected void dialCall(Call call) {
                call.changeState(new Connecting());
                Map<String,String> callParameters = Maps.newHashMap();
                callParameters.putAll(call.getCallParameters());
                String phoneNumber = dialPrefix + call.getNumber().getPhoneNumber();
                callParameters.put("PHONE_NUMBER", phoneNumber);
                
                SimpleTemplateProcessor templateProcessor = new SimpleTemplateProcessor();
                String urlStr = templateProcessor.process(urlTemplate, callParameters);
                try {
                    URL url = new URL(urlStr);
                    log.debug("Initiating call: " + url);
                    URLConnection connection = url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    byte[] copyToByteArray = FileCopyUtils.copyToByteArray(inputStream);
                    String string = new String(copyToByteArray).trim();
                    log.debug("URL response: " + StringUtils.left(string, 200));
                    log.trace(connection.getHeaderFields());
                    log.trace(string);

                    Matcher matcher = successPattern.matcher(string);
                    if (!matcher.matches()) {
                        log.debug("Result didn't match, setting NoConnection");
                        call.changeState(new NoConnection(StringUtils.left(string, 50)));
                    }
                    
                } catch (MalformedURLException e) {
                    log.error("Unable to initiate call to: " + urlStr);
                } catch (IOException e) {
                    log.error("Unable to initiate call", e);
                }
            }

            @Override
            public String toString() {
                return "UrlDialer";
            }
            
        };
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonUserDao(YukonUserDao yukonUserDao) {
        this.yukonUserDao = yukonUserDao;
    }
}
