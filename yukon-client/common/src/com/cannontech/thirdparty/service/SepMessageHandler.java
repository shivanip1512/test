package com.cannontech.thirdparty.service;

import com.cannontech.common.model.YukonCancelTextMessage;
import com.cannontech.common.model.YukonTextMessage;
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
    public void handleControlMessage(SepControlMessage message);
    
    /**
     * Process the Restore Message
     * 
     * @param paoIdentifier
     * @param message
     */
    public void handleRestoreMessage(SepRestoreMessage message);
    
    /**
     * Handle the association of the LMControlHistory table and the Integration's Event Table
     * 
     * @param eventId
     * @param controlHistoryId
     */
    public void handleAssociationMessage(int eventId, int controlHistoryId);
    
    /**
     * Handles sending the SEP text message to the integrated system.
     * 
     * @param zigbeeTextMessage
     */
    public void handleSendTextMessage(YukonTextMessage zigbeeTextMessage);

    /**
     * Handles canceling of the SEP text message to the integrated system.
     * 
     * @param cancelZigbeeText
     */
    public void handleCancelTextMessage(YukonCancelTextMessage cancelZigbeeText);

}
