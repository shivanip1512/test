package com.cannontech.notif.handler;

import com.cannontech.message.util.Message;

/**
 * @author rneuharth
 *
 * Common interface to handle notifications
 *
 */
public interface INotifHandler
{
    void start();

    boolean isValid( Message msg );
}
