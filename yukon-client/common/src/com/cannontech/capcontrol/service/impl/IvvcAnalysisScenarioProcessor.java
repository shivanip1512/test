package com.cannontech.capcontrol.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.message.IvvcAnalysisMessage;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.IvvcAnalysisFormatType;
import com.cannontech.capcontrol.service.IvvcAnalysisScenarioMsgFormatter;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class IvvcAnalysisScenarioProcessor {
    
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private PaoDao paoDao;
    private PaoLoadingService paoLoadingService;
    private ZoneDao zoneDao;

    private ImmutableMap<IvvcAnalysisFormatType, IvvcAnalysisScenarioMsgFormatter> formattersMap;
    
    @PostConstruct
    public void init() {
        IvvcAnalysisScenarioMsgFormatter subBusTwoFloatFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.SUBBUSID_FLOAT_FLOAT;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);

                int subBusId = message.getSubBusId();
                String subBusLink = getCapControlFacesEditorLinkHtml(subBusId, userContext);
                
                float actualPercentage = message.getFloatData().get(0);
                float minPercentage = message.getFloatData().get(1);
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext, 
                                                             dateTimeString, subBusLink, actualPercentage, minPercentage);
                return msg;
            }
        };
        IvvcAnalysisScenarioMsgFormatter subBusCcObjectZoneFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.SUBBUS_ID_REG_ID;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);
                
                int subBusId = message.getSubBusId();
                String subBusLink = getCapControlFacesEditorLinkHtml(subBusId, userContext);
                
                int regId = message.getIntData().get(0);
                String regLink = getCapControlFacesEditorLinkHtml(regId, userContext);
                
                Zone zone;
                try {
                    zone = zoneDao.getZoneByRegulatorId(regId);
                } catch (OrphanedRegulatorException e) {
                    zone = new Zone();
                    zone.setId(0);
                    String msg = getUnknownMessage(userContext);
                    zone.setName(msg);
                }
                String zoneLink = getZoneLinkHtml(zone);
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext,
                                                             dateTimeString, subBusLink, regLink, zoneLink);
                return msg;
            }
        };
        IvvcAnalysisScenarioMsgFormatter subBusIntFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.SUBBUS_ID_INT;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);
                
                int subBusId = message.getSubBusId();
                String subBusLink = getCapControlFacesEditorLinkHtml(subBusId, userContext);
                
                int tapPeriod = Iterables.getOnlyElement(message.getIntData());
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext,
                                                             dateTimeString, subBusLink, tapPeriod);
                return msg;
            }
        };
        IvvcAnalysisScenarioMsgFormatter subBusFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.SUBBUS_ID;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);
                
                int subBusId = message.getSubBusId();
                String subBusLink = getCapControlFacesEditorLinkHtml(subBusId, userContext);
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext,
                                                             dateTimeString, subBusLink);
                return msg;
            }
        };
        IvvcAnalysisScenarioMsgFormatter subBusCcObjectFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.SUBBUS_ID_CC_PAO_ID;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);
                
                int subBusId = message.getSubBusId();
                String subBusLink = getCapControlFacesEditorLinkHtml(subBusId, userContext);
                
                int ccId = Iterables.getOnlyElement(message.getIntData());
                String ccLink = getCapControlFacesEditorLinkHtml(ccId, userContext);
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext, 
                                                             dateTimeString, subBusLink, ccLink);
                return msg;
            }
        };
        IvvcAnalysisScenarioMsgFormatter intCcObjectFormatter = new IvvcAnalysisScenarioMsgFormatter() {
            @Override
            public IvvcAnalysisFormatType getFormatType() {
                return IvvcAnalysisFormatType.INT_CC_ID;
            }
            @Override
            public String format(IvvcAnalysisMessage message, YukonUserContext userContext) {
                String dateTimeString = getTimeStamp(message.getTimeStamp(), userContext);
                
                int num = message.getIntData().get(0);
                
                int ccId = message.getIntData().get(1);
                String ccLink = getCapControlFacesEditorLinkHtml(ccId, userContext);
                
                String msg = getMessageWithScenarioIdAndArgs(message.getScenarioId(), userContext, 
                                                             dateTimeString, num, ccLink);
                return msg;
            }
        };

        Builder<IvvcAnalysisFormatType, IvvcAnalysisScenarioMsgFormatter> builder = ImmutableMap.builder();
        builder.put(subBusTwoFloatFormatter.getFormatType(), subBusTwoFloatFormatter);
        builder.put(subBusCcObjectZoneFormatter.getFormatType(), subBusCcObjectZoneFormatter);
        builder.put(subBusIntFormatter.getFormatType(), subBusIntFormatter);
        builder.put(subBusFormatter.getFormatType(), subBusFormatter);
        builder.put(subBusCcObjectFormatter.getFormatType(), subBusCcObjectFormatter);
        builder.put(intCcObjectFormatter.getFormatType(), intCcObjectFormatter);
        formattersMap = builder.build();
    }
    
    public String getMessage(IvvcAnalysisMessage message, YukonUserContext userContext) {
        String msg = formattersMap.get(message.getType().getFormatType()).format(message, userContext);
        return msg;
    }
    
    private String getMessageWithScenarioIdAndArgs(int scenarioId, YukonUserContext userContext, Object... args) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String messageKey = getMessageKey(scenarioId);
        String msg = accessor.getMessage(messageKey, args);
        return msg;
    }
    
    private String getTimeStamp(long timeStampLong, YukonUserContext userContext) {
        DateTime timeStamp = new DateTime(timeStampLong, userContext.getJodaTimeZone());
        String dateTimeString = timeStamp.toString(DateTimeFormat.mediumDateTime());
        return dateTimeString;
    }
    
    private String getMessageKey(int scenarioId) {
        return "yukon.web.modules.capcontrol.ivvc.busView.analysisScenario" + scenarioId;
    }
    
    private String getPaoNameWithId(int paoId, YukonUserContext userContext) {
        try {
            YukonPao yukonPao = paoDao.getYukonPao(paoId);
            DisplayablePao displayablePao = paoLoadingService.getDisplayablePao(yukonPao);
            return displayablePao.getName();
        } catch (NotFoundException e) {
            return getUnknownMessage(userContext);
        }
    }
    
    private String getUnknownMessage(YukonUserContext userContext) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        String msg = accessor.getMessage("yukon.web.modules.capcontrol.ivvc.busView.analysisScenario.unknown");
        return msg;
    }
    
    private String getZoneLinkHtml(Zone zone) {
        Map<String, String> argMap = Maps.newHashMap();
        argMap.put("zoneId", String.valueOf(zone.getId()));
        argMap.put("isSpecialArea", "false");
        
        String name = zone.getName();
        String html = getLinkHtml("/spring/capcontrol/ivvc/zone/detail", name, argMap);
        return html;
    }
    
    private String getCapControlFacesEditorLinkHtml(int ccId, YukonUserContext userContext) {
        String name = getPaoNameWithId(ccId, userContext);
        String html = getLinkHtml("/editor/cbcBase.jsf?type=2&amp;itemid=" + ccId, name, new HashMap<String, String>());
        return html;
    }
    
    private String getLinkHtml(String url, String value, Map<String, String> argMap) {
        String html = "<a href=\"" + url;
        for (Entry<String, String> argEntry : argMap.entrySet()) {
            html += "?" + argEntry.getKey() + "=" + argEntry.getValue();
        }
        html += "\">" + value + "</a>";
        return html;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setPaoLoadingService(PaoLoadingService paoLoadingService) {
        this.paoLoadingService = paoLoadingService;
    }
    
    @Autowired
    public void setZoneDao(ZoneDao zoneDao) {
        this.zoneDao = zoneDao;
    }
}
