package com.cannontech.web.tools.commander.service;

import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;

public interface CommanderService {
    
    /**
     * Sends a command or commands to porter using parameters specified. The request(s) and response(s)
     * are stored in a concurrent hash map by user id. The request(s) are return or added to the 
     * {@link CommandRequestException} if thrown. 
     * @throws {@link CommandRequestException} when the connection to porter is invalid, contains the {@link CommandRequest}s.
     */
    List<CommandRequest> sendCommand(YukonUserContext userContext, CommandParams params) throws CommandRequestException;
    
    /**
     * Removes the requests stored for this user from the internal concurrent hash map.
     * This method does not cancel pending requests to porter and does not ask porter
     * to cancel any pending requests it is working on.
     */
    void clearUser(LiteYukonUser user);
    
    /**
     * Get all stored requests (and responses) for the user as a map of request id to request.
     */
    Map<Integer, CommandRequest> getRequests(LiteYukonUser user);
    
    /** Returns the Porter authority which is the hostname and port number for the connection to Porter. */
    String getPorterHost();
    
    /**
     * Check if the user is authorized to execute this command on the pao.
     */

    Map<String, Boolean> authorizeCommand(CommandParams params, YukonUserContext userContext,
            LiteYukonPAObject pao);
    
}