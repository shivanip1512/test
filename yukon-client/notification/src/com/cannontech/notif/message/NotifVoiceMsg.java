package com.cannontech.notif.message;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.message.util.Message;

/**
 * Use the base classes message string to 
 * give a description of the error.
 */
public class NotifVoiceMsg extends Message
{
	private int notifProgramID = CtiUtilities.NONE_ZERO_ID;
	
	/**
	 * NotifVoiceMsg constructor comment.
	 */
	public NotifVoiceMsg() {
		super();
	}

    /**
	 * @return
	 */
	public int getNotifProgramID()
	{
		return notifProgramID;
	}

	/**
	 * @param i
	 */
	public void setNotifProgramID(int i)
	{
        notifProgramID = i;
	}

}
