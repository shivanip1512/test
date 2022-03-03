package com.cannontech.common.util.jms.api;

import java.util.Arrays;

import org.springframework.util.Assert;

public class JmsApiDirectoryHelper {

    /**
     * This method enforce the specified JmsApis to have a same queueName.
     */
    public static JmsApi<?, ?, ?> requireMatchingQueueNames(JmsApi<?, ?, ?>... apis) {
        var distinctQueueNames = Arrays.stream(apis)
                                       .map(JmsApi::getQueueName)
                                       .distinct()
                                       .count();
        Assert.state(distinctQueueNames == 1, "Queue names must match: " + apis);
        return apis[0];
    }

}
