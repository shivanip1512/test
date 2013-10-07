package com.cannontech.web.updater;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.TemplateProcessorFactory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

/**
 * Abstract Base Class for UpdateBacking services.  Wraps up some of the common processing
 * for DatedObjects and return values
 *
 * @param <T> - Type of DatedObject this service handles
 */
public abstract class UpdateBackingServiceBase<T> implements UpdateBackingService {
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private TemplateProcessorFactory templateProcessorFactory;

    public abstract DatedObject<T> getDatedObject(int objectId);

    private final List<String> estimatedLoadFields = Lists.newArrayList("CONNECTED_LOAD", "DIVERSIFIED_LOAD", 
            "KW_SAVINGS","ESTIMATED_LOAD_ERROR");
    
    /**
     * Method to get the current value from the datedObject
     * @param object - New version of the dated object
     * @param idBits - idBits from the update request
     * @param userContext - current userContext
     * @return Value from datedObject
     */
    public abstract Object getValue(DatedObject<T> object, String[] idBits, YukonUserContext userContext);

    @Override
    public String getLatestValue(String identifier, long afterDateLong, YukonUserContext userContext) {
        String[] idBits = identifier.split("\\/");
        int objectId = Integer.parseInt(idBits[0]);
        String fieldName = String.valueOf(idBits[1]);

        DatedObject<T> datedObject = getDatedObject(objectId);

        Date afterDate = new Date(afterDateLong);

        if (datedObject == null || 
                !datedObject.getDate().before(afterDate) || 
                estimatedLoadFields.contains(fieldName)) {
            Object value = getValue(datedObject, idBits, userContext);

            if (value != null) {
                if (value instanceof String) {
                    return (String) value;
                } else if (value instanceof MessageSourceResolvable) {
                    MessageSourceAccessor messageSourceAccessor =
                        messageSourceResolver.getMessageSourceAccessor(userContext);
                    return messageSourceAccessor.getMessage((MessageSourceResolvable) value);
                } else if (value instanceof ResolvableTemplate) {
                    return templateProcessorFactory.processResolvableTemplate((ResolvableTemplate) value, userContext);
                }
            }
        }

        return null;
    }

    @Override
    public boolean isValueAvailableImmediately(String fullIdentifier, long afterDate, YukonUserContext userContext) {
        // Default implementation assumes data is available now
        return true;
    }
}
