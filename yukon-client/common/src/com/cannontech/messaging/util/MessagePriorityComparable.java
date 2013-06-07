package com.cannontech.messaging.util;

import java.util.Comparator;

import com.cannontech.messaging.message.BaseMessage;

public class MessagePriorityComparable implements Comparator<BaseMessage> {

    @Override
    public int compare(BaseMessage o1, BaseMessage o2) {
        return o1.getPriority() - o2.getPriority();
    }

}
