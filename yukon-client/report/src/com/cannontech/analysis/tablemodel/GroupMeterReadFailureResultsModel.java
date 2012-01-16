package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.PlcDeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class GroupMeterReadFailureResultsModel extends GroupFailureResultsModelBase {
    
    private PlcDeviceAttributeReadService plcDeviceAttributeReadService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String title;
    private Set<? extends Attribute> attributes;
    
    public void doLoadData() {
        GroupMeterReadResult result = plcDeviceAttributeReadService.getResult(resultKey);
        this.attributes = result.getAttributes();
        doLoadData(result);
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        Function<Attribute, String> attrDesc = new Function<Attribute, String>() {
            @Override
            public String apply(Attribute from) {
                return from.getDescription();
            }
        };
        
        if (attributes == null && StringUtils.isNotBlank(resultKey)) {
            GroupMeterReadResult result = plcDeviceAttributeReadService.getResult(resultKey);
            // attributes
            this.attributes = result.getAttributes();
            String prettyText = StringUtils.join(Iterables.transform(attributes, attrDesc).iterator(), ", ");
            info.put(accessor.getMessage(baseKey + "attributes"), prettyText);
        }
        
        title = accessor.getMessage(baseKey + "groupMeterReadFailure.title");
        return info;
    }

    public String getTitle() {
        return title;
    }
    
    @Autowired
    public void setPlcDeviceAttributeReadService(PlcDeviceAttributeReadService plcDeviceAttributeReadService) {
        this.plcDeviceAttributeReadService = plcDeviceAttributeReadService;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}