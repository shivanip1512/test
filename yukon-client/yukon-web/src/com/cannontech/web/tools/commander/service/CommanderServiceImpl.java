package com.cannontech.web.tools.commander.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.errors.dao.DeviceErrorTranslatorDao;
import com.cannontech.amr.errors.model.DeviceErrorDescription;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.authorization.service.LMCommandAuthorizationService;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.command.CommandCategoryUtil;
import com.cannontech.database.db.device.Device;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.message.porter.message.Request;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.util.MessageEvent;
import com.cannontech.message.util.MessageListener;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.tools.commander.DeviceCommandDetail;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;
import com.cannontech.web.tools.commander.model.CommandRequestExceptionType;
import com.cannontech.web.tools.commander.model.CommandResponse;
import com.cannontech.web.tools.commander.model.CommandTarget;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.Maps;

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
    private static final char separator = '&';
    
    // User id to message id to request
    private Map<Integer, Map<Integer, CommandRequest>> commands = new ConcurrentHashMap<>();
    
    @Autowired private @Qualifier("porter") BasicServerConnection porter;
    @PostConstruct private void init() { porter.addMessageListener(this); }
    @Autowired private CommandDao commandDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private PaoCommandAuthorizationService paoCommandAuthService;
    @Autowired private LMCommandAuthorizationService lmCommandAuthService;
    @Autowired private ServerDatabaseCache cache;
    @Autowired private DeviceErrorTranslatorDao deviceErrorTranslatorDao;
    
    @Override
    public List<CommandRequest> sendCommand(YukonUserContext userContext, CommandParams params, Map<String, Integer> commandCounts) throws CommandRequestException {
        
        LiteYukonUser user = userContext.getYukonUser();
        
        log.info("User: " + user + " attempting command: " + params);
        // TODO log the attempt with event log service
        
        List<CommandRequest> commands = buildCommands(params, userContext, commandCounts);
        
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
            log.info("User: " + user + " produced request to porter: " + req);
            // TODO log the resulting requests sent to porter with event log service
        }
        
        return commands;
    }

    @Override
    public Map<String, Integer> parseCommand(CommandParams params, YukonUserContext userContext) {
        Map<String, Integer> commandCounts = Maps.newConcurrentMap();
        List<String> commands = splitCommands(params);
        for (String command : commands) {
            if (command.trim().startsWith("loop")) {
                int loopCount = parseLoopCommand(command);
                commandCounts.put(command, loopCount);
            } else {
                commandCounts.put(command, 1);
            }
        }
        return commandCounts;
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

        if (rtn.isError()) {
            DeviceErrorDescription deviceErrorDesc = deviceErrorTranslatorDao.translateErrorCode(rtn.getStatus());
            resp.getResults().add(deviceErrorDesc.getCategory() + " -- " + deviceErrorDesc.getDescription());
            resp.getResults().add(rtn.getResultString());
        }

        for (Map<Integer, CommandRequest> reqsByUser : commands.values()) {
            
            int requestId = (int) rtn.getUserMessageID();
            if (reqsByUser.containsKey(requestId)) {
                
                found = true;
                log.info("Storing response from porter: " + rtn);
                
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
            CommandTarget type = params.getTarget();
            int deviceId = type.isPao() ? params.getPaoId() : Device.SYSTEM_DEVICE_ID;
            
            Request request = new Request(deviceId, params.getCommand(), command.getId());
            request.setPriority(params.getPriority());
            if (type.isRoute()) {
                request.setRouteID(params.getRouteId());
            }
            request.setUserName(user.getUsername());
            
            requests.add(request);
        }
        
        return requests;
    }
    
    /**
     * Build a {@link CommandRequest} for all commands in the parameters.
     * For loop command we will build command request based on the loop count 
     * and if the loop count is more than 10 than we are building only 10 command request
     * and ignoring the rest.
     * While for command other than loop command we will build a single command request.
     */
    private List<CommandRequest> buildCommands(CommandParams params, YukonUserContext userContext,
            Map<String, Integer> commandCounts) {
        List<CommandRequest> reqs = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : commandCounts.entrySet()) {
            String command = entry.getKey();
            if (command.trim().startsWith("loop")) {
                int loopCount = entry.getValue() <= 10 ? entry.getValue() : 10;
                for (int i = 0; i < loopCount; i++) {
                    command = "loop";
                    buildCommandRequest(params, userContext, reqs, command);
                }
            } else {
                buildCommandRequest(params, userContext, reqs, command);
            }
        }
        return reqs;
    }

    /** Parse the loop command , retrieve the count from the command string.*/
    private int parseLoopCommand(String command) {
        String valueSubstring = null;
        int loopCount = 1;
        for (int j = command.indexOf("loop") + 4; j < command.length(); j++) {
            if (command.charAt(j) != ' ' && command.charAt(j) != '\t') {
                valueSubstring = command.substring(j);
                break;
            }
        }
        if (valueSubstring != null && StringUtils.isNumeric(valueSubstring)) {
            loopCount = Integer.parseInt(valueSubstring);
        }
        return loopCount;
    }
    
    private void buildCommandRequest(CommandParams params, YukonUserContext userContext, List<CommandRequest> reqs,
            String command) {
        int messageId = requestIds.nextInt();
        CommandParams copy = params.copy();
        copy.setCommand(command);

        // Add 'update'
        if (!copy.getCommand().contains("update")) {
            copy.setCommand(copy.getCommand() + " update");
        }

        // If queue command is not opted, add 'noqueue'
        if (!copy.isQueueCommand()) {
            if (!copy.getCommand().contains("noqueue")) {
                copy.setCommand(copy.getCommand() + " noqueue");
            }
        }

        // Add Serial Number
        if (params.getTarget().isSerialNumber()) {
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
    
    @Override
    public Map<String, Boolean> authorizeCommand(CommandParams params,
            YukonUserContext userContext, Object obj) {
        List<String> commands = splitCommands(params);
        Map<String, Boolean> authorizedCommand = new HashMap<String, Boolean>();
        for (String command : commands) {
            if (obj instanceof LiteYukonPAObject) {
                authorizedCommand.put(command,
                                      paoCommandAuthService.isAuthorized(userContext.getYukonUser(),
                                                                         command,
                                                                         (LiteYukonPAObject) obj));
            } else if (obj instanceof String) {
                authorizedCommand.put(command,
                                      lmCommandAuthService.isAuthorized(userContext.getYukonUser(),
                                                                        command,
                                                                        (String) obj));
            }
        }
        return authorizedCommand;
    }
    
    /** Returns the text representing the request to put in the console window. */
    private String buildRequestPrintout(YukonUserContext userContext, CommandParams params) {
        
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        CommandTarget type = params.getTarget();
        String key = type.getRequestTextKey();
        if (type.isRoute()) {
            String route = cache.getAllPaosMap().get(params.getRouteId()).getPaoName();
            return accessor.getMessage(key, params.getSerialNumber(), route, params.getCommand());
        } else {
            String pao = cache.getAllPaosMap().get(params.getPaoId()).getPaoName();
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
        
        CommandTarget type = params.getTarget();
        List<LiteDeviceTypeCommand> commands = new ArrayList<>();
        if (type == CommandTarget.DEVICE || type == CommandTarget.LOAD_GROUP) {
            LiteYukonPAObject pao = cache.getAllPaosMap().get(params.getPaoId());
            if (pao != null) {  //may have been deleted already
                String deviceType = pao.getPaoIdentifier().getPaoType().getDbString();
                commands = commandDao.getAllDevTypeCommands(deviceType);
            }
        } else {
            if (type == CommandTarget.EXPRESSCOM) {
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

    @Transactional
    @Override
    public void save(String deviceTypeOrCategory, List<DeviceCommandDetail> details) {        
        log.debug("Saving - deviceTypeOrCategory {}", deviceTypeOrCategory);
        log.debug("       - details {}", details);

        Map<Integer, LiteCommand> commandsInDb = commandDao.getAllCommands().stream()
                .collect(Collectors.toMap(LiteCommand::getCommandId, Function.identity()));

        deleteRemovedCommands(deviceTypeOrCategory, details);

        Map<String, Long> displayOrderExistingCount = commandDao.getAllDeviceTypeCommands().stream()
                .collect(Collectors.groupingBy(LiteDeviceTypeCommand::getDeviceType, Collectors.counting()));

        details.forEach(detail -> {
            if (detail.getCommandId() == null) {
                createNewCommand(deviceTypeOrCategory, detail, displayOrderExistingCount);
            } else {
                // commands with ids less then 1 can't be modified
                if (detail.getCommandId() > 1) {
                    updateCommand(deviceTypeOrCategory, commandsInDb, detail);
                }
                if (!CommandCategoryUtil.isCommandCategory(deviceTypeOrCategory)) {
                    updateDeviceTypeCommand(detail);
                }
            }
        });
    }

    /**
     * Updates device type command
     */
    private void updateDeviceTypeCommand(DeviceCommandDetail detail) {
        LiteDeviceTypeCommand typeCommand = commandDao.getDeviceTypeCommand(detail.getCommandId());
        log.debug("Updating device type command from [{}]", typeCommand);
        typeCommand.setDisplayOrder(detail.getDisplayOrder());
        typeCommand.setVisibleFlag(detail.isVisibleFlag() ? 'Y' : 'N');
        log.debug("                                  -to [{}]", typeCommand);
        commandDao.updateDeviceTypeCommand(typeCommand);
    }

    /**
     * If new command is different from the old command, updates command
     */
    private void updateCommand(String deviceTypeOrCategory, Map<Integer, LiteCommand> commandsInDb, DeviceCommandDetail detail) {
        LiteCommand existingCommand = commandsInDb.get(detail.getCommandId());
        LiteCommand newCommand = new LiteCommand(detail.getCommandId(), detail.getCommand(), detail.getCommandName(),
                deviceTypeOrCategory);
        if (!existingCommand.equals(newCommand)) {
            log.debug("Updating command from [{}] to [{}]", existingCommand, newCommand);
            commandDao.updateCommand(newCommand);
        }
    }

    /**
     * Deletes commands for paos
     */
    private void deleteRemovedCommands(String deviceTypeOrCategory, List<DeviceCommandDetail> details) {
        List<LiteCommand> commandsInDb = commandDao.getAllCommandsByCategory(deviceTypeOrCategory);

        Map<Integer, DeviceCommandDetail> deviceTypes = details.stream()
                .filter(detail -> detail.getDeviceCommandId() != null)
                .collect(Collectors.toMap(DeviceCommandDetail::getDeviceCommandId, Function.identity()));

        commandsInDb.removeIf(command -> deviceTypes.containsKey(command.getCommandId()) || command.getCommandId() < 1);
        log.debug("Deleting commands {}", commandsInDb);
        // the following commands were removed by user
        commandsInDb.forEach(command -> {
            List<LiteDeviceTypeCommand> typeCommands = commandDao.getAllDevTypeCommands(command.getCommandId());
            log.debug("Deleting devices type commands {}", typeCommands);
            typeCommands.forEach(typeCommand -> commandDao.deleteDeviceTypeCommand(typeCommand));
            log.debug("                     -command {}", command);
            commandDao.deleteCommand(command);
        });
    }

    /**
     * Creates new command
     */
    private void createNewCommand(String commandCategory, DeviceCommandDetail detail,
            Map<String, Long> displayOrderExistingCount) {

        if (CommandCategoryUtil.isCommandCategory(commandCategory)) {
            CommandCategory category = CommandCategory.getForDbString(commandCategory);
            List<PaoType> paoTypes = CommandCategoryUtil.getAllTypesForCategory(category);
            paoTypes.forEach(paoType -> {
                int displayOrder = findNextDisplayOrder(displayOrderExistingCount, paoType);
                log.debug("Creating new command {} label {} paoType {} displayOrder {}", detail.getCommand(),
                        detail.getCommandName(), paoType, displayOrder);
                commandDao.addCommand(detail.getCommand(), detail.getCommandName(), paoType, commandCategory, displayOrder);
            });
        } else {
            PaoType paoType = PaoType.getForDbString(commandCategory);
            int displayOrder = findNextDisplayOrder(displayOrderExistingCount, paoType);
            log.debug("Creating new command {} label {} paoType {} displayOrder {}", detail.getCommand(),
                    detail.getCommandName(), paoType, displayOrder);
            commandDao.addCommand(detail.getCommand(), detail.getCommandName(), paoType, commandCategory, displayOrder);
        }
    }

    private int findNextDisplayOrder(Map<String, Long> displayOrderExistingCount, PaoType paoType) {
        Long lastCommandsDisplayOrder = displayOrderExistingCount.get(paoType.getDbString());
        if (lastCommandsDisplayOrder == null) {
            return 1;
        }
        return lastCommandsDisplayOrder.intValue() + 1;
    }
}