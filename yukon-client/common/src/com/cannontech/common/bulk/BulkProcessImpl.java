package com.cannontech.common.bulk;

import java.util.Date;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;

import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;

/**
 * Default implementation class for BulkProcess
 */
public class BulkProcessImpl implements BulkProcess {

    private int id = 0;
    private YukonUserContext userContext = null;
    private BulkProcessingResultHolder resultHolder = null;
    private Date startDate = null;
    private DeviceCollection deviceCollection = null;
    private MessageSourceResolvable actionMessage = null;

    public void setId(int id) {
        this.id = id;
    }

    public void setUserContext(YukonUserContext user) {
        this.userContext = user;
    }

    public void setResultHolder(BulkProcessingResultHolder resultHolder) {
        this.resultHolder = resultHolder;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public void setActionMessage(String key, String... args) {
        this.actionMessage = new YukonMessageSourceResolvable(key, args);
    }

    public int getId() {
        return id;
    }

    public YukonUserContext getUserContext() {
        return userContext;
    }

    public BulkProcessingResultHolder getResultHolder() {
        return resultHolder;
    }

    public Date getStartDate() {
        return startDate;
    }

    public MessageSourceResolvable getActionMessage() {
        return actionMessage;
    }

    public MessageSourceResolvable getDeviceCollectionMessage() {

        String key = this.deviceCollection.getDescriptionKey();
        List<String> parameterList = this.deviceCollection.getParameterList();
        YukonMessageSourceResolvable resolvable = new YukonMessageSourceResolvable(key,
                                                                                   parameterList);

        return resolvable;
    }

    public DeviceCollection getDeviceCollection() {
        return this.deviceCollection;
    }

}
