package com.cannontech.common.device.commands;

import java.util.Set;

import javax.mail.MessagingException;

public interface GroupCommandExecutor {

    public void execute(Set<Integer> deviceIds, String command, String emailAddresses)
            throws MessagingException;

}
