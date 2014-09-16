package com.cannontech.analysis.tablemodel;

import java.util.LinkedHashMap;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.service.GroupMeterReadResult;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class GroupMeterReadFailureResultsModel extends GroupFailureResultsModelBase {
    
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private static String title;
    private Set<? extends Attribute> attributes;
    @Autowired private ObjectFormattingService objectFormattingService;

    @Override
    public void doLoadData() {
        GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
        this.attributes = result.getAttributes();
        doLoadData(result);
    }
    
    @Override
    public LinkedHashMap<String, String> getMetaInfo(final YukonUserContext context) {
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        LinkedHashMap<String, String> info = new LinkedHashMap<String, String>();
        Function<Attribute, String> attrDesc = new Function<Attribute, String>() {
            @Override
            public String apply(Attribute from) {
                return objectFormattingService.formatObjectAsString(from.getMessage(), context);
            }
        };
        
        if (attributes == null && StringUtils.isNotBlank(resultKey)) {
            GroupMeterReadResult result = deviceAttributeReadService.getResult(resultKey);
            // attributes
            this.attributes = result.getAttributes();
            String prettyText = StringUtils.join(Iterables.transform(attributes, attrDesc).iterator(), ", ");
            info.put(accessor.getMessage(baseKey + "attributes"), prettyText);
        }
        
        title = accessor.getMessage(baseKey + "groupMeterReadFailure.title");
        return info;
    }

    @Override
    public String getTitle() {
        return title;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}