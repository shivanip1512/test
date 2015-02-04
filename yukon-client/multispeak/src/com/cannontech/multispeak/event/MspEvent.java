package com.cannontech.multispeak.event;

import com.cannontech.message.porter.message.Return;

/**
 * @author stacey
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface MspEvent {

    /**
     * Handles a Return message received. Returns true if the event is done being processed and
     * should be removed from any structure storing it.
     * 
     * @param returnMsg
     * @return boolean
     */
    public boolean messageReceived(Return returnMsg);

    public void eventNotification();

    public boolean isPopulated();
}
