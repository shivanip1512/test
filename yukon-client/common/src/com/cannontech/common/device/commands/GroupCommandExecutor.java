package com.cannontech.common.device.commands;

import java.util.Set;

import javax.mail.MessagingException;

import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface GroupCommandExecutor {

    public void execute(Set<Integer> deviceIds, String command, String emailAddresses, String emailSubject, LiteYukonUser user)
            throws MessagingException, PaoAuthorizationException;

}
