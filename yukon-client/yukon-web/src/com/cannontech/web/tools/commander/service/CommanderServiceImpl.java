package com.cannontech.web.tools.commander.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.device.Device;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;
import com.cannontech.web.tools.commander.model.CommandRequestExceptionType;
import com.cannontech.web.tools.commander.model.CommandResponse;
import com.cannontech.web.tools.commander.model.CommandType;
import com.cannontech.yukon.BasicServerConnection;

/**
 * Service to enable sending of commands to porter from the web commander page and handle
 * the responses.
 * 
 * This service stores all requests and responses initiated from the web commander page
 * for each user in a concurrent cache and provides the ability to clear that cache per user.
 * 
 * NOTE: The request and response id's are integers instead of longs since javascript must 
 * consume the data and javascript cannot support the full range of java longs.
 */
public class CommanderServiceImpl implements CommanderService, MessageListener {
    
    private static final Logger log = YukonLogManager.getLogger(CommanderServiceImpl.class);
    private static final Random requestIds = new Random(System.currentTimeMillis());
    private static final Random responseIds = new Random(System.currentTimeMillis());
    private static final int priority = 14;
    private static final char separator = '&';
    
    // User id to message id to request
    private Map<Integer, Map<Integer, CommandRequest>> commands = new ConcurrentHashMap<>();
    
    @Autowired private @Qualifier("porter") BasicServerConnection porter;
    @PostConstruct private void init() { porter.addMessageListener(this); }
    @Autowired private CommandDao commandDao;
    @Autowired private PaoDao paoDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @Override
    public List<CommandRequest> sendCommand(YukonUserContext userContext, CommandParams params) throws CommandRequestException {
        
        LiteYukonUser user = userContext.getYukonUser();
        
        log.debug("User: " + user + " attempting command: " + params);
        // TODO log the attempt with event log service
        
        List<CommandRequest> commands = buildCommands(params, userContext);
        
        if (!porter.isValid()) {
            for (CommandRequest command : commands) {
                command.setComplete(true);
            }
            throw new CommandRequestException("Porter Connection is invalid.", commands, 
                    CommandRequestExceptionType.PORTER_CONNECTION_INVALID);
        }
        
        List<Request> reqs = buildPorterRequest(user, commands);
        for (Request req : reqs) {
            porter.write(req);
            log.debug("User: " + user + " attempting command: " + params + " produced request to porter: " + req);
            // TODO log the resulting requests sent to porter with event log service
        }
        
        return commands;
    }
    
    @Override
    public Map<Integer, CommandRequest> getRequests(LiteYukonUser user) {
        
        Map<Integer, CommandRequest> requests = commands.get(user.getUserID());
        if (requests == null) {
            requests = new HashMap<>();
            commands.put(user.getUserID(), requests);
        }
        
        return requests;
    }
    
    @Override
    public void clearUser(LiteYukonUser user) {
        commands.remove(user.getUserID());
        log.debug("User: " + user + " clear their command history from cache.");
        // TODO log the resulting requests sent to porter with event log service
    }
    
    @Override
    public void messageReceived(MessageEvent e) {
        
        if (e.getMessage() instanceof Return) {
            Return rtn = (Return) e.getMessage();
            Set<Integer> messageIds = new HashSet<>();
            for (Map<Integer, CommandRequest> requests : commands.values()) {
                messageIds.addAll(requests.keySet());
            }
            if (messageIds.contains((int)rtn.getUserMessageID())) {
                storeResponse(rtn);
            }
        }
        
    }
    
    @Override
    public String getPorterHost() {
        String porterHost = ((ClientConnection)porter).getConnectionUri().getRawAuthority();
        return porterHost;
    }
    
    /**
     * Creates a {@link CommandResponse} for the porter return message and stores
     * it in the internal concurrent hash map's corresponding requests map by user id.
     * Returns the {@link CommandResponse} that was created.
     */
    private CommandResponse storeResponse(Return rtn) {
        
        boolean found = false;
        int id = responseIds.nextInt();
        CommandResponse resp = CommandResponse.of(id, rtn);
        
        for (Map<Integer, CommandRequest> reqsByUser : commands.values()) {
            
            int requestId = (int) rtn.getUserMessageID();
            if (reqsByUser.containsKey(requestId)) {
                
                found = true;
                log.debug("Storing response from porter: " + rtn);
                
                CommandRequest req = reqsByUser.get(requestId);
                req.getResponses().add(resp);
                if (resp.getExpectMore() == 0) {
                    req.setComplete(true);
                }
                break;
            }
        }
        
        if (!found) log.warn("Could not store message: " + rtn + ". UserMessageId not found.");
        
        return resp;
    }
    
    /** Build a list of Porter {@link Request} objects from the parameters provided. */
    private List<Request> buildPorterRequest(LiteYukonUser user, List<CommandRequest> commands) {
        
        List<Request> requests = new ArrayList<>();
        
        for (CommandRequest command : commands) {
            CommandParams params = command.getParams();
            CommandType type = params.getType();
            int deviceId = type.hasPao() ? params.getPaoId() : Device.SYSTEM_DEVICE_ID;
            
            Request request = new Request(deviceId, params.getCommand(), command.getId());
            
            request.setPriority(priority);
            if (type.hasRoute()) {
                request.setRouteID(params.getRouteId());
            }
            request.setUserName(user.getUsername());
            
            requests.add(request);
        }
        
        return requests;
    }
    
    /**
     * Build a {@link CommandRequest} for all commands in the parameters.
     * (Mulitiple commands can be specified in the same text by using an '&')
     */
    private List<CommandRequest> buildCommands(CommandParams params, YukonUserContext userContext) {
        
        List<CommandRequest> reqs = new ArrayList<>();
        List<String> commands = splitCommands(params);
        
        for (String command : commands) {
            
            int messageId = requestIds.nextInt();
            CommandParams copy = params.copy();
            copy.setCommand(command);
            
            // Add 'update'
            if (!copy.getCommand().contains("update")) {
                copy.setCommand(copy.getCommand() + " update");
            }
            
            // Add 'noqueue'
            if (!copy.getCommand().contains("noqueue")) {
                copy.setCommand(copy.getCommand() + " noqueue");
            }
            
            // Add Serial Number
            if (params.getType().hasSerialNumber()) {
                copy.setCommand(setSerialNumber(copy.getCommand(), copy.getSerialNumber()));
            }
            
            CommandRequest cr = new CommandRequest();
            cr.setId(messageId);
            cr.setParams(copy);
            cr.setTimestamp(Instant.now().getMillis());
            cr.setRequestText(buildRequestPrintout(userContext, copy));
            
            getRequests(userContext.getYukonUser()).put(messageId, cr);
            reqs.add(cr);
            
        }
        
        return reqs;
    }
    
    /** Returns the text representing the request to put in the console window. */
    private String buildRequestPrintout(YukonUserContext userContext, CommandParams params) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        CommandType type = params.getType();
        String key = type.getRequestTextKey();
        if (type.hasRoute()) {
            String route = paoDao.getYukonPAOName(params.getRouteId());
            return accessor.getMessage(key, params.getSerialNumber(), route, params.getCommand());
        } else {
            String pao = paoDao.getYukonPAOName(params.getPaoId());
            return accessor.getMessage(key, pao, params.getCommand());
        }
    }
    
    /** Add the serial number to the command if there is no serial number specified. */
    private String setSerialNumber(String commandText, String serialNumber) {
        
        String command = commandText;
        
        if (commandText.indexOf("serial") == -1) {
            command += " serial " + serialNumber;
        }
        
        return command;
    }
    
    /** Parse command string for multiple commands separated by the '&' character. */
    private List<String> splitCommands(CommandParams params) {
        
        String commandText = params.getCommand();
        List<String> commands = new ArrayList<>();
        
        String command = commandText;
        
        int startAt = 0;
        int splitAt = command.indexOf(separator);
        
        // Leave anything inside quotes alone, they are name tokens.
        int firstQuote = command.indexOf("'");
        int secondQuote = command.indexOf("'", firstQuote + 1);
        if (splitAt > firstQuote && splitAt < secondQuote) {
            splitAt = command.indexOf(separator, secondQuote);
        }
        
        while (splitAt > -1) {
            String front = command.substring(0, splitAt).trim();
            startAt = splitAt + 1;
            commands.add(parseCommand(front, params));
            
            command = command.substring(startAt).trim();
            splitAt = command.indexOf(separator);
        }
        // Add the final (or only) command.
        commands.add(parseCommand(command, params));
        
        return commands;
    }
    
    /**
     * Attempt to replace a "user-friendly" command label with the porter accepted command string.
     * Ex.  User typed "Read My Meter" instead of "getvalue kwh".
     */
    public String parseCommand(String command, CommandParams params) {
        
        CommandType type = params.getType();
        List<LiteDeviceTypeCommand> commands = new ArrayList<>();
        if (type == CommandType.DEVICE || type == CommandType.DEVICE) {
            YukonPao pao = paoDao.getYukonPao(params.getPaoId());
            String deviceType = pao.getPaoIdentifier().getPaoType().getDbString();
            commands = commandDao.getAllDevTypeCommands(deviceType);
        } else {
            if (type == CommandType.EXPRESSCOM) {
                commands = commandDao.getAllDevTypeCommands(CommandCategory.EXPRESSCOM_SERIAL.getDbString());
            } else {
                commands = commandDao.getAllDevTypeCommands(CommandCategory.VERSACOM_SERIAL.getDbString());
            }
        }
        
        for (LiteDeviceTypeCommand ldtc : commands) {
            if (ldtc.isVisible()) {
                LiteCommand lc = commandDao.getCommand(ldtc.getCommandId());
                if (lc.getLabel().trim().equalsIgnoreCase(command)) {
                    return lc.getCommand();
                }
            }
        }
        
        return command; 
    }
    
}