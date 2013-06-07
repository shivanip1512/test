package com.cannontech.multispeak.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.messaging.message.loadcontrol.data.GroupBase;
import com.cannontech.messaging.message.loadcontrol.data.GroupEmetcon;
import com.cannontech.messaging.message.loadcontrol.data.GroupExpresscom;
import com.cannontech.messaging.message.loadcontrol.data.GroupVersacom;
import com.cannontech.messaging.message.loadcontrol.data.Program;
import com.cannontech.multispeak.db.MspLMGroupCommunications;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus;
import com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMProgramMode;

public interface MspLMGroupDao {

    /**
     * Returns a list of all LMGroupEmetcon devices and their communications setup  
     * @return
     */
    public List<MspLMGroupCommunications> getLMGroupEmetconCommunications(GroupEmetcon lmGroupEmetcon);
    
    /**
     * Returns a list of all LMGroupExpresscom devices and their communications setup  
     * @return
     */
    public List<MspLMGroupCommunications> getLMGroupExpresscomCommunications(GroupExpresscom lmGroupExpresscom);
    
    /**
     * Returns a list of all LMGroupVersacom devices and their communications setup  
     * @return
     */
    public List<MspLMGroupCommunications> getLMGroupVerscomCommunications(GroupVersacom lmGroupVersacom);
    
    /**
     * Returns a list of all lmGroup devices and their communications setup  
     * @param lmGroupBase
     * @return
     * @throws IllegalArgumentException
     */
    public List<MspLMGroupCommunications> getLMGroupCommunications(GroupBase lmGroupBase) throws IllegalArgumentException;
    

    /**
     * Returns the status of the MspLMGroupCommunications
     * If any of the transmitter disable flags are set, returns "Offline"
     * Else, check the status point on the transmitter.
     * 	If the status point on any transmitters is "bad" (1), return "Failed"
     *  Else, return "Online"
     * @param mspLMGroupCommunications
     * @return
     */
    public com.cannontech.multispeak.db.MspLMGroupCommunications.MspLMGroupStatus getStatus(
    		List<MspLMGroupCommunications> mspLMGroupCommunications);
    
    /**
     * Returns the most prominent status of all statuses
     * Order of prominence is Failed, Offline, Partial, Online
     * @param mspLMGroupStatus
     * @return
     */
    public MspLMGroupStatus getMasterStatus(Set<MspLMGroupStatus> mspLMGroupStatus);
    
    /**
     * Returns the mode of the LMProgramBase
     * If the program is active, returns "Coin"
     * Else returns "Non-coin" 
     * @param lmProgramBase
     * @return
     */
    public MspLMProgramMode getMode(Program lmProgramBase);
    
    /**
     * Returns the most prominent mode of all modes 
     * Order of prominence is Coin, Non-coin
     * @param mspLMProgramMode
     * @return
     */
    public MspLMProgramMode getMasterMode(Set<MspLMProgramMode> mspLMProgramMode);
}
