package com.cannontech.web.tools.commander;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.events.loggers.CommanderEventLogService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoDistance;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckPermissionLevel;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;
import com.cannontech.web.tools.commander.model.CommandRequestExceptionType;
import com.cannontech.web.tools.commander.model.CommandTarget;
import com.cannontech.web.tools.commander.model.RecentTarget;
import com.cannontech.web.tools.commander.model.ViewableTarget;
import com.cannontech.web.tools.commander.service.CommanderService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
import com.cannontech.web.util.WebUtilityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@Controller
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class CommanderController {
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private PaoLocationService paoLocationService;
    @Autowired private CommandDao commandDao;
    @Autowired private CommanderService commanderService;
    @Autowired private CommanderEventLogService eventLogger;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private WebUtilityService webUtil;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    
    private static final TypeReference<List<RecentTarget>> recentTargetsType = new TypeReference<List<RecentTarget>>() {};
    private static final String keyBase = "yukon.web.modules.tools.commander";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    private static final Random unAuthorizedcommandIds = new Random(System.currentTimeMillis());
    private static final Comparator<CommandRequest> onTimestamp = new Comparator<CommandRequest>() {
        @Override
        public int compare(CommandRequest o1, CommandRequest o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };
    
    /** The commander page. */
    @RequestMapping({"/commander", "/commander/"})
    public String commander(HttpServletRequest req, ModelMap model, LiteYukonUser user) throws IOException {
        
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
        model.addAttribute("routes", routes);
        List<CommandRequest> requests = new ArrayList<>(commanderService.getRequests(user).values());
        Collections.sort(requests, onTimestamp);
        model.addAttribute("requests", requests);
        
        // Check cookie for last target of command execution
        String lastTarget = webUtil.getYukonCookieValue(req, "commander", "lastTarget", null, JsonUtils.STRING_TYPE);
        if (lastTarget != null) {
            
            CommandTarget target = CommandTarget.valueOf(lastTarget);
            model.addAttribute("target", target);
            
            if (target.isPao()) {
                // Device or load group
                Integer paoId = webUtil.getYukonCookieValue(req, "commander", "lastPaoId", null, JsonUtils.INT_TYPE);
                if (paoId != null) {
                    model.addAttribute("paoId", paoId);
                    // Add route info if available
                    LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
                    PaoType type = pao.getPaoType();
                    model.addAttribute("routable", type.isRoutable());
                    model.addAttribute("meter", type.isMeter());
                    if (type.isRoutable()) {
                        LiteYukonPAObject route = cache.getAllRoutesMap().get(pao.getRouteID());
                        model.addAttribute("route", route);
                    }
                    if(rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.LIMITED, user)){
                        model.addAttribute("changeRoute", true);
                    }
                }
            } else {
                model.addAttribute("serialNumber", webUtil.getYukonCookieValue(req, "commander", "lastSerialNumber", null, 
                        JsonUtils.STRING_TYPE));
                model.addAttribute("routeId", webUtil.getYukonCookieValue(req, "commander", "lastRouteId", null, 
                        JsonUtils.INT_TYPE));
            }
        } else {
            // Default to device target
            model.addAttribute("target", CommandTarget.DEVICE);
        }
        
        List<RecentTarget> recentTargets = webUtil.getYukonCookieValue(req, "commander", "recentTargets", null, 
                recentTargetsType);
        if (recentTargets != null) {
            model.addAttribute("recentTargets", buildViewableTargets(recentTargets));
        }
        model.addAttribute("executeManualCommand", !rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, user));
        
        return "commander/commander.jsp";
    }
    
    /** A device was chosen, get the details to setup the actions button. */
    @RequestMapping("/commander/{paoId}/data")
    public @ResponseBody Map<String, Object> data(LiteYukonUser user, HttpServletResponse resp, @PathVariable int paoId) {
        
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
        Map<String, Object> data = new HashMap<>();
        data.put("pao", pao);
        
        PaoType type = pao.getPaoType();
        data.put("isMeter", type.isMeter());
        data.put("isRoutable", type.isRoutable());
        if (type.isRoutable()) {
            LiteYukonPAObject route = cache.getAllRoutesMap().get(pao.getRouteID());
            data.put("route", route);
        }
        if(rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.LIMITED, user)){
            data.put("changeRoute", true);
        }
        return data;
    }
    
    /** Change route popup */
    @RequestMapping("/commander/route/{routeId}/change")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.LIMITED)
    public String changeRoute(ModelMap model, @PathVariable int routeId) {
        
        Map<Integer, LiteYukonPAObject> all = cache.getAllRoutesMap();
        LiteYukonPAObject route = all.get(routeId);
        List<LiteYukonPAObject> potentials = new ArrayList<>(all.values());
        potentials.remove(route);
        Collections.sort(potentials, PaoUtils.NAME_COMPARE);
        Iterable<LiteYukonPAObject> filtered = Iterables.filter(potentials, new Predicate<LiteYukonPAObject>() {
            @Override
            public boolean apply(LiteYukonPAObject input) {
                return input.getPaoType() == PaoType.ROUTE_MACRO || input.getPaoType() == PaoType.ROUTE_CCU;
            }
        });
        model.addAttribute("route", route);
        model.addAttribute("routes", filtered.iterator());
        
        return "commander/changeRoute.jsp";
    }
    
    /** Change route */
    @RequestMapping(value="/commander/{paoId}/route/{routeId}", method=RequestMethod.POST)
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.LIMITED)
    public @ResponseBody LiteYukonPAObject changeRoute(HttpServletResponse resp, ModelMap model, LiteYukonUser user,
            @PathVariable int paoId, @PathVariable int routeId) {
                
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
        Map<Integer, LiteYukonPAObject> routes = cache.getAllRoutesMap();
        LiteYukonPAObject oldRoute = routes.get(pao.getRouteID());
        LiteYukonPAObject newRoute = routes.get(routeId);
        
        Log.debug("User: " + user.getUsername() + " attemting to change route on " + pao.getPaoName() + " from " 
                + oldRoute.getPaoName() + " to " + newRoute.getPaoName());
        
        try {
            if (!pao.getPaoType().isRoutable()) {
                throw new IllegalArgumentException("Device does not support routes: " + pao);
            }
            if (newRoute.getPaoType() != PaoType.ROUTE_CCU && newRoute.getPaoType() != PaoType.ROUTE_MACRO) {
                throw new IllegalArgumentException("Route type: " + newRoute.getPaoType() + " not supported on "
                        + pao);
            }
            
            deviceUpdateService.changeRoute(SimpleDevice.of(pao.getPaoIdentifier()), routeId);
            
            Log.debug("User: " + user.getUsername() + " changed route on " + pao.getPaoName() + " from " 
                + oldRoute.getPaoName() + " to " + newRoute.getPaoName());
            
            eventLogger.changeRoute(user, pao.getPaoName(), oldRoute.getPaoName(), newRoute.getPaoName(), 
                    pao.getLiteID(), oldRoute.getLiteID(), newRoute.getLiteID());
        } catch (RuntimeException e) {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        
        return newRoute;
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
        
        Map<String, Object> result = new HashMap<>();
        
        List<CommandRequest> commands = null;
        List<String> authorizedCommand = new ArrayList<String>();
        Map<Integer, String> unAuthorizedCommand = new HashMap<Integer, String>();
        LiteYukonPAObject pao = cache.getAllPaosMap().get(params.getPaoId());
        
        Map<String, Boolean> commandWithAuthorization = commanderService.authorizeCommand(params, userContext, pao);;
        
        for (String key : commandWithAuthorization.keySet()) {
            if (commandWithAuthorization.get(key) == true) {
                authorizedCommand.add(key);
            } else {
                unAuthorizedCommand.put(unAuthorizedcommandIds.nextInt(), key);
            }
        }
        if (authorizedCommand.size() != 0) {
            params.setCommand(Joiner.on('&').join(authorizedCommand));
            try {
                commands = commanderService.sendCommand(userContext, params);

                if (params.getTarget() == CommandTarget.DEVICE || params.getTarget() == CommandTarget.LOAD_GROUP) {
                    eventLogger.executeOnPao(userContext.getYukonUser(),
                                             params.getCommand(),
                                             pao.getPaoName(),
                                             pao.getLiteID());
                } else {
                    LiteYukonPAObject route = cache.getAllPaosMap().get(params.getRouteId());
                    eventLogger.executeOnSerial(userContext.getYukonUser(),
                                                params.getCommand(),
                                                params.getSerialNumber(),
                                                route.getPaoName(),
                                                route.getLiteID());
                }

            } catch (CommandRequestException e) {
                commands = e.getRequests();
                if (e.getType() == CommandRequestExceptionType.PORTER_CONNECTION_INVALID) {
                    MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
                    String reason = accessor.getMessage(keyBase + ".error.porter.invalid",
                                                        commanderService.getPorterHost());
                    result.put("reason", reason);
                } else {
                    result.put("reason", e.getMessage());
                }

                resp.setStatus(HttpStatus.BAD_REQUEST.value());
            }

            List<Map<String, CommandRequest>> requests = new ArrayList<>();
            for (CommandRequest command : commands) {
                ImmutableMap<String, CommandRequest> request = ImmutableMap.of("request", command);
                requests.add(request);
            }
            result.put("requests", requests);
        } 
        
        if (!unAuthorizedCommand.isEmpty()) {
            MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
            String unAuthorizedMessage = accessor.getMessage(keyBase + ".error.unAuthorizedMessage",
                                                             commanderService.getPorterHost());
            result.put("unAuthorizedCommand", unAuthorizedCommand);
            result.put("unAuthorizedErrorMsg", unAuthorizedMessage);
        }
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
    
    /** Get paos nearby */
    @RequestMapping(value="/commander/{paoId}/nearby")
    public String nearby(HttpServletResponse resp, ModelMap model, @PathVariable int paoId) {
        
        PaoLocation location = paoLocationDao.getLocation(paoId);
        if (location == null) {
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        
        List<PaoDistance> nearby = paoLocationService.getNearbyLocations(location, 5, DistanceUnit.MILES, 
                PaoTag.COMMANDER_REQUESTS);
        if (nearby.size() > 10) { 
            nearby = nearby.subList(0, 10);
        }
        if (nearby.isEmpty()) {
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        } else {
            model.addAttribute("nearby", nearby);
            List<Integer> nearbyIds = new ArrayList<>(PaoUtils.asPaoIdList(nearby));
            nearbyIds.add(paoId); // Include this device
            model.addAttribute("nearbyCollection", dcProducer.createDeviceCollection(nearbyIds, null));
            return "commander/nearby.jsp";
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
    
    /** Builds a list of objects consumed by a recent targets dropdown menu. */
    private List<ViewableTarget> buildViewableTargets(List<RecentTarget> recents) {
        
        List<ViewableTarget> viewables = new ArrayList<>();
        for (RecentTarget recent : recents) {
            
            ViewableTarget viewable = new ViewableTarget();
            viewable.setTarget(recent);
            CommandTarget type = CommandTarget.valueOf(recent.getTarget());
            
            if (type == CommandTarget.DEVICE || type == CommandTarget.LOAD_GROUP) {
                
                LiteYukonPAObject pao = cache.getAllPaosMap().get(recent.getPaoId());
                if (pao == null) continue; // May have had an old id in the cookie
                
                viewable.setLabel(pao.getPaoName());
            } else {
                
                LiteYukonPAObject route = cache.getAllPaosMap().get(recent.getRouteId());
                if (route == null) continue; // May have had an old id in the cookie
                
                viewable.setLabel(recent.getSerialNumber() + " - " + route.getPaoName());
            }
            viewables.add(viewable);
        }
        
        return viewables;
    }
    
}