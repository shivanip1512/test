package com.cannontech.web.tools.commander;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;
import com.cannontech.web.tools.commander.model.CommandRequestExceptionType;
import com.cannontech.web.tools.commander.model.CommandType;
import com.cannontech.web.tools.commander.service.CommanderService;
import com.google.common.collect.ImmutableMap;

@Controller
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class CommanderController {
    
    @Autowired private PaoDao paoDao;
    @Autowired private CommandDao commandDao;
    @Autowired private CommanderService commanderService;
    @Autowired private DeviceSearchService deviceSearchService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final String keyBase = "yukon.web.modules.tools.commander";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    
    @RequestMapping({"/commander", "/commander/"})
    public String commander(ModelMap model) {
        
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
        model.addAttribute("routes", routes);
        
        return "commander/commander.jsp";
    }
    
    /** Clear all commands from our cache for the user. */
    @RequestMapping("/commander/clear")
    public void clear(LiteYukonUser user, HttpServletResponse resp) {
        
        commanderService.clearUser(user);
        resp.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    /** Retrieve the list of requests asked for. */
    @RequestMapping(value="/commander/requests", method=RequestMethod.POST, produces=json, consumes=json)
    public @ResponseBody List<CommandRequest> requests(LiteYukonUser user, @RequestBody List<String> requestIds) {
        
        Map<Integer, CommandRequest> requests = commanderService.getRequests(user);
        List<CommandRequest> desired = new ArrayList<>();
        for (String id : requestIds) {
            CommandRequest request = requests.get(Integer.parseInt(id));
            if (request != null) desired.add(request);
        }
        
        return desired;
    }
    
    /** Execute a command */
    @RequestMapping("/commander/execute")
    public @ResponseBody Map<String, Object> execute(HttpServletResponse resp, YukonUserContext userContext, 
            @ModelAttribute CommandParams params) {
        
        LiteYukonUser user = userContext.getYukonUser();
        Map<String, Object> result = new HashMap<>();
        
        List<CommandRequest> commands = null;
        try {
            commands = commanderService.sendCommand(user, params);
        } catch (CommandRequestException e) {
            commands = e.getRequests();
            if (e.getType() == CommandRequestExceptionType.PORTER_CONNECTION_INVALID) {
                MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                String reason = accessor.getMessage(keyBase + ".error.porter.invalid", commanderService.getPorterHost());
                result.put("reason", reason);
            } else {
                result.put("reason", e.getMessage());
            }
            
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        
        List<Map<String, Object>> requests = new ArrayList<>();
        for (CommandRequest command : commands) {
            String reqPrintout = buildRequestPrintout(userContext, command.getParams());
            ImmutableMap<String, Object> request = ImmutableMap.of("request", command, "requestText", reqPrintout);
            requests.add(request);
        }
        result.put("requests", requests);
        
        return result;
    }
    
    /** Get commands for a particular pao. */
    @RequestMapping("/commander/commands")
    public @ResponseBody List<LiteCommand> commands(LiteYukonUser user, int paoId) {
        
        YukonPao pao = paoDao.getYukonPao(paoId);
        String type = pao.getPaoIdentifier().getPaoType().getDbString();
        List<LiteCommand> commands = commands(type, user);
        
        return commands;
    }
    
    /** Get commands for a non PaoType types. */
    @RequestMapping("/commander/type-commands")
    public @ResponseBody List<LiteCommand> commands(LiteYukonUser user, CommandCategory type) {
        
        List<LiteCommand> commands = commands(type.getDbString(), user);
        
        return commands;
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
    
    /** Get all visible commands, sorted by display order, that this user has permission to use. */
    private List<LiteCommand> commands(String type, LiteYukonUser user) {
        
        List<LiteDeviceTypeCommand> all = commandDao.getAllDevTypeCommands(type); 
        List<LiteCommand> visible = new ArrayList<>();
        for (LiteDeviceTypeCommand command : all) {
            if (command.isVisible()) visible.add(commandDao.getCommand(command.getCommandId()));
        }
        
        return commandDao.filterCommandsForUser(visible, user);
    }
    
}