package com.cannontech.thirdparty.service;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.thirdparty.messaging.SepControlMessage;
import com.cannontech.thirdparty.messaging.SepRestoreMessage;

public interface SepMessageHandler {
    
    /**
     * Returns true if this handler supports the PaoType
     * 
     * @param paoIdentifier
     * @return
     */
    public boolean handlePao(PaoIdentifier paoIdentifier);
    
    /**
     * Process the Control Message.
     * 
     * @param paoIdentifier
     * @param message
     */
    public void handleControlMessage(PaoIdentifier paoIdentifier, SepControlMessage message);
    
    /**
     * Process the Restore Message
     * 
     * @param paoIdentifier
     * @param message
     */
    public void handleRestoreMessage(PaoIdentifier paoIdentifier, SepRestoreMessage message);
    
    /**
     * Handle the association of the LMControlHistory table and the Integration's Event Table
     * 
     * @param eventId
     * @param controlHistoryId
     */
    public void handleAssociationMessage(int eventId, int controlHistoryId);
}
