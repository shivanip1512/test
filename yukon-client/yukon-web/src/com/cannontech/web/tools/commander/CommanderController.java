package com.cannontech.web.tools.commander;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.collection.DeviceIdListCollectionProducer;
import com.cannontech.common.device.commands.CommandPriority;
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
import com.cannontech.common.pao.service.LocationService;
import com.cannontech.common.util.JsonUtils;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.core.users.model.UserPreference;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.db.command.CommandCategoryUtil;
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
import com.cannontech.web.user.service.UserPreferenceService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Controller
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class CommanderController {
    
    @Autowired private ServerDatabaseCache cache;
    @Autowired private PaoDao paoDao;
    @Autowired private PaoLocationDao paoLocationDao;
    @Autowired private LocationService locationService;
    @Autowired private CommandDao commandDao;
    @Autowired private CommanderService commanderService;
    @Autowired private CommanderEventLogService eventLogger;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private UserPreferenceService userPreferenceService;
    @Autowired @Qualifier("idList") private DeviceIdListCollectionProducer dcProducer;
    
    private static final Logger log = YukonLogManager.getLogger(CommanderController.class);
    
    private static final TypeReference<List<RecentTarget>> recentTargetsType = new TypeReference<List<RecentTarget>>() {};
    private static final String keyBase = "yukon.web.modules.tools.commander";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    private static final Random unAuthorizedcommandIds = new Random(System.currentTimeMillis());
    private static final boolean queueCommand = false;
    private static final Comparator<CommandRequest> onTimestamp = new Comparator<CommandRequest>() {
        @Override
        public int compare(CommandRequest o1, CommandRequest o2) {
            return Long.compare(o1.getTimestamp(), o2.getTimestamp());
        }
    };
    private static final int RECENT_TARGET_MAXIMUM_SIZE_LIMIT = 10;
    
    /** The commander page. */
    @RequestMapping({"/commander", "/commander/"})
    public String commander(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        addRoutesandRecentsTargetToModel(req, model, user);
        return "commander/commander.jsp";
    }

    /** Render commander page when redirected from device home detail page . */
    @RequestMapping("/redirectToCommander")
    public String redirectFromMeterDetailPage(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        addRoutesandRecentsTargetToModel(req, model, user);
        addPaoDetailsToModel(req, model, user);
        return "commander/commander.jsp";
    }

    /** Add routes and recents target data to model map object . */
    private void addRoutesandRecentsTargetToModel(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
        model.addAttribute("routes", routes);
        List<CommandRequest> requests = new ArrayList<>(commanderService.getRequests(user).values());
        Collections.sort(requests, onTimestamp);
        model.addAttribute("requests", requests);
        String lastTarget = userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_LAST_TARGET);
        if (lastTarget != null) {
            CommandTarget target = CommandTarget.valueOf(lastTarget);
            model.addAttribute("target", target);
            if (!target.isPao()) {
                model.addAttribute("serialNumber", Integer.valueOf(
                    userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_LAST_SERIAL_NUMBER)));
                model.addAttribute("routeId", Integer.valueOf(
                    userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_LAST_ROUTE_ID)));
            }
        } else {
            // Default to device target
            model.addAttribute("target", CommandTarget.DEVICE);
        }
        if (userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_RECENT_TARGETS) != null) {
            String recentPrefStringValue =
                userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_RECENT_TARGETS);
            List<RecentTarget> recentTargets;
            try {
                recentTargets = JsonUtils.fromJson(recentPrefStringValue, recentTargetsType);
                model.addAttribute("recentTargets", buildViewableTargets(recentTargets));
            } catch (IOException e) {
                log.error("Commander failed to load the recent target, because recent Target JSON format is incorrect."
                    + " To see the correct recent targets, please make correction in user preference for recent targets");
            }
        }
        model.addAttribute("executeManualCommand",
            rolePropertyDao.checkProperty(YukonRoleProperty.EXECUTE_MANUAL_COMMAND, user));
    }

    /** Add PAO data to model map object . */
    private void addPaoDetailsToModel(HttpServletRequest req, ModelMap model, LiteYukonUser user) {
        String lastTarget = userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_LAST_TARGET);
        if (lastTarget != null) {
            CommandTarget target = CommandTarget.valueOf(lastTarget);
            if (target.isPao()) {
                // Device or load group
                Integer paoId = Integer.valueOf(
                    userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_LAST_PAO_ID));
                if (paoId != null) {
                    try {
                        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
                        model.addAttribute("paoId", paoId);
                        // Add route info if available
                        PaoType type = pao.getPaoType();
                        model.addAttribute("routable", type.isRoutable());
                        model.addAttribute("meter", type.isMeter());
                        if (type.isRoutable()) {
                            LiteYukonPAObject route = cache.getAllRoutesMap().get(pao.getRouteID());
                            model.addAttribute("route", route);
                        }
                        if (rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION,
                            HierarchyPermissionLevel.INTERACT, user)) {
                            model.addAttribute("changeRoute", true);
                        }
                    } catch (NotFoundException nfe) {
                        // the lastPaoId may have been deleted since it was last used
                        paoId = null;
                    }
                }
            }
        }
    }

    /** A device was chosen, get the details to setup the actions button. */
    @RequestMapping("/commander/{paoId}/data")
    public @ResponseBody Map<String, Object> data(LiteYukonUser user, HttpServletResponse resp, @PathVariable int paoId) {

        Map<String, Object> data = new HashMap<>();
        try {
            LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
            data.put("pao", pao);
            
            PaoType type = pao.getPaoType();
            data.put("isMeter", type.isMeter());
            data.put("isRoutable", type.isRoutable());
            if (type.isRoutable()) {
                LiteYukonPAObject route = cache.getAllRoutesMap().get(pao.getRouteID());
                data.put("route", route);
            }
            if(rolePropertyDao.checkLevel(YukonRoleProperty.ENDPOINT_PERMISSION, HierarchyPermissionLevel.INTERACT, user)){
                data.put("changeRoute", true);
            }
        } catch (NotFoundException nfe) {
            // paoId may have been deleted
        }
        return data;
    }
    
    /** Change route popup */
    @RequestMapping("/commander/route/{routeId}/change")
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
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
    @CheckPermissionLevel(property = YukonRoleProperty.ENDPOINT_PERMISSION, level = HierarchyPermissionLevel.INTERACT)
    public @ResponseBody LiteYukonPAObject changeRoute(HttpServletResponse resp, ModelMap model, LiteYukonUser user,
            @PathVariable int paoId, @PathVariable int routeId) {
                
        LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
        Map<Integer, LiteYukonPAObject> routes = cache.getAllRoutesMap();
        LiteYukonPAObject oldRoute = routes.get(pao.getRouteID());
        LiteYukonPAObject newRoute = routes.get(routeId);
        
        log.debug("User: " + user.getUsername() + " attemting to change route on " + pao.getPaoName() + " from " 
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
            
            log.debug("User: " + user.getUsername() + " changed route on " + pao.getPaoName() + " from " 
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
    @RequestMapping(value = "/commander/requests", method = RequestMethod.POST, produces = json, consumes = json)
    public @ResponseBody List<CommandRequest> requests(LiteYukonUser user,
            @RequestBody RequestIdsContainer requestIdsContainer) {
        
        Map<Integer, CommandRequest> requests = commanderService.getRequests(user);
        List<CommandRequest> desired = new ArrayList<>();
        for (String id : requestIdsContainer.requestIds) {
            CommandRequest request = requests.get(Integer.parseInt(id));
            if (request != null) {
                desired.add(request);
            }
        }
        
        return desired;
    }

    @RequestMapping("editSettingsPopup")
    public String editSettingsPopup(ModelMap model, HttpServletRequest req, LiteYukonUser user) {
        Integer priority = userPreferenceService.getCommanderPriority(user);
        model.addAttribute("priority", priority);
        Boolean queueCmd =
            Boolean.valueOf(userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_QUEUE_COMMAND));
        if (queueCmd == null) {
            queueCmd = queueCommand;
        }
        model.addAttribute("queueCommand", queueCmd);
        CommandParams commandParams = new CommandParams();
        commandParams.setQueueCommand(queueCmd);
        model.addAttribute("commandParams", commandParams);
        model.addAttribute("minCmdPriority",CommandPriority.minPriority);
        model.addAttribute("maxCmdPriority", CommandPriority.maxPriority);

        return "commander/commanderSettings.jsp";
    }

    /** Refresh the console. */
    @RequestMapping(value = "/commander/refresh", method = RequestMethod.POST, produces = json, consumes = json)
    public @ResponseBody List<CommandRequest> refresh(LiteYukonUser user) {
        List<CommandRequest> requests = new ArrayList<>(commanderService.getRequests(user).values());
        Collections.sort(requests, onTimestamp);
        return requests;
    }

    @RequestMapping("/commander/execute")
    public @ResponseBody Map<String, Object> execute(HttpServletResponse resp, YukonUserContext userContext,
            @ModelAttribute CommandParams params, LiteYukonUser user) {

        Map<String, Object> result = new HashMap<>();

        Integer priority = userPreferenceService.getCommanderPriority(user);
        params.setPriority(priority);

        List<CommandRequest> commands = null;
        List<String> authorizedCommand = new ArrayList<>();
        Map<Integer, String> unAuthorizedCommand = new HashMap<>();
        Map<String, Boolean> commandWithAuthorization = new HashMap<>();
        Map<String, Integer> commandCounts = Maps.newConcurrentMap();
        MessageSourceAccessor accessor = null;

        if (params.getPaoId() != null) {
            LiteYukonPAObject pao = cache.getAllPaosMap().get(params.getPaoId());
            commandWithAuthorization = commanderService.authorizeCommand(params, userContext, pao);
        } else {
            commandWithAuthorization = commanderService.authorizeCommand(params, userContext, "lmdevice");
        }

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
                commandCounts = commanderService.parseCommand(params, userContext);
                commands = commanderService.sendCommand(userContext, params, commandCounts);

                if (params.getTarget() == CommandTarget.DEVICE || params.getTarget() == CommandTarget.LOAD_GROUP) {
                    LiteYukonPAObject pao = cache.getAllPaosMap().get(params.getPaoId());
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
                    accessor = messageResolver.getMessageSourceAccessor(userContext);
                    String reason = accessor.getMessage(keyBase + ".error.porter.invalid",
                                                        commanderService.getPorterHost());
                    result.put("reason", reason);
                } else {
                    result.put("reason", e.getMessage());
                }

                resp.setStatus(HttpStatus.BAD_REQUEST.value());
            }

            // if loop count for loopback command is more than 10 , show message on console .
            if (!commandCounts.isEmpty()) {
                for (Map.Entry<String, Integer> entry : commandCounts.entrySet()) {
                    if (entry.getKey().trim().startsWith("loop") && entry.getValue() > 10) {
                        result.put("showLoopCountMessage", true);
                        accessor = messageResolver.getMessageSourceAccessor(userContext);
                        String msg = accessor.getMessage(keyBase + ".maxLoopCountExceeded");
                        result.put("maxLoopCountExceededMsg", msg);
                        break;
                    } else {
                        result.put("showLoopCountMessage", false);
                    }
                }
            }
            List<Map<String, CommandRequest>> requests = new ArrayList<>();
            for (CommandRequest command : commands) {
                ImmutableMap<String, CommandRequest> request = ImmutableMap.of("request", command);
                requests.add(request);
            }
            result.put("requests", requests);
        }

        if (!unAuthorizedCommand.isEmpty()) {
            accessor = messageResolver.getMessageSourceAccessor(userContext);
            String unAuthorizedMessage = accessor.getMessage(keyBase + ".error.unAuthorizedMessage",
                                                             commanderService.getPorterHost());
            result.put("unAuthorizedCommand", unAuthorizedCommand);
            result.put("unAuthorizedErrorMsg", unAuthorizedMessage);
        }

        // Save user preferences for commander
        try {
            List<UserPreference> userPreferences = updateCommanderUserPreferences(user, params);
            Optional<UserPreference> results =
                userPreferences
                    .stream()
                    .filter(preference -> preference.getName() == UserPreferenceName.COMMANDER_RECENT_TARGETS)
                    .findFirst();

            if (results.isPresent()) {
                String recentPrefStringValue = results.get().getValue();
                List<RecentTarget> recentTargets = JsonUtils.fromJson(recentPrefStringValue, recentTargetsType);

                if (!recentTargets.isEmpty()) {
                    result.put("recentTargets", buildViewableTargets(recentTargets));
                }
            }
        } catch (IOException e) {
            log.error("Commander failed to set the recent target, because recent Target JSON format is incorrect."
                + " To see the correct recent targets, please make correction in user preference for recent targets");
        }
        return result;
    }
    
    
    @GetMapping("/commander/customCommands")
    public String customCommandsPopup(ModelMap model, @RequestParam(value="paoId", required=false) Integer paoId, 
                                      @RequestParam(value="category", required=false) String category) {
        //get command categories and paotypes
        Set<CommandCategory> categories = CommandCategoryUtil.getAllCategories();
        List<CommandCategory> categoryList = new ArrayList<CommandCategory>();
        categoryList.addAll(categories);
        categoryList.add(CommandCategory.EXPRESSCOM_SERIAL);
        categoryList.add(CommandCategory.VERSACOM_SERIAL);
        model.addAttribute("categories", categoryList);
        List<PaoType> existingPaoTypes = paoDao.getExistingPaoTypes();
        model.addAttribute("paoTypes", existingPaoTypes);
        
        List<LiteDeviceTypeCommand> typeCommands = new ArrayList<>();

        if (paoId != null) {
            YukonPao pao = cache.getAllPaosMap().get(paoId);
            String type = pao.getPaoIdentifier().getPaoType().name();
            typeCommands = getCommandsByCategory(type);
            model.addAttribute("selectedPaoType", pao.getPaoIdentifier().getPaoType());
        } else if (category != null) {
            CommandCategory cmdCategory = CommandCategory.valueOf(category);
            typeCommands = commandDao.getAllDevTypeCommands(cmdCategory.getDbString());
            model.addAttribute("selectedCategory", cmdCategory.getDbString());
        }

        model.addAttribute("typeCommands", typeCommands);
        Map<Integer, LiteCommand> commands = cache.getAllCommands();
        model.addAttribute("commands", commands);
        return "commander/customCommands.jsp";
    }
    
    private List<LiteDeviceTypeCommand> getCommandsByCategory(String category) {
        List<LiteDeviceTypeCommand> typeCommands = new ArrayList<>();
        
        //check if Command Category
        boolean commandCategory = CommandCategoryUtil.isCommandCategory(category);
        if (commandCategory || CommandCategoryUtil.isExpressComOrVersaCom(category)) {
            List<LiteCommand> cmds = commandDao.getAllCommandsByCategory(category);
            for (LiteCommand cmd : cmds) {
                LiteDeviceTypeCommand typeCmd = new LiteDeviceTypeCommand(0, cmd.getCommandId(), category, 0, 'Y');
                typeCommands.add(typeCmd);
            }
        } else {
            PaoType paoType = PaoType.valueOf(category);
            typeCommands = commandDao.getAllDevTypeCommands(paoType.getDbString()); 
        }
        
        return typeCommands;
    }
    
    @GetMapping("/commander/customCommandsByCategory")
    public String customCommandsByCategory(ModelMap model, String category) {
        List<LiteDeviceTypeCommand> typeCommands = getCommandsByCategory(category);
        Map<Integer, LiteCommand> commands = cache.getAllCommands();
        model.addAttribute("typeCommands", typeCommands);
        model.addAttribute("commands", commands);
        return "commander/customCommandsTable.jsp";
    }
    
    /** Get commands for a particular pao. */
    @RequestMapping("/commander/commands")
    public @ResponseBody List<LiteCommand> commands(LiteYukonUser user, int paoId) {
        
        List<LiteCommand> commands = new ArrayList<>(); 
        try {
            YukonPao pao = cache.getAllPaosMap().get(paoId);
            String type = pao.getPaoIdentifier().getPaoType().getDbString();
            commands = commands(type, user);
        } catch (NotFoundException nfe){
            // paoId may have already been deleted
        }
        return commands;
    }
    
    /** Get commands for a non PaoType types. */
    @RequestMapping(value = "/commander/type-commands", method = RequestMethod.GET)
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
        
        List<PaoDistance> nearby = locationService.getNearbyLocations(location, 5, DistanceUnit.MILES, 
              Lists.newArrayList(PaoTag.COMMANDER_REQUESTS));
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
            if (command.isVisible()) {
                visible.add(commandDao.getCommand(command.getCommandId()));
            }
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
                if (pao == null){
                    continue; // May have had an old id in the preference
                }
                
                viewable.setLabel(pao.getPaoName());
            } else {
                
                LiteYukonPAObject route = cache.getAllPaosMap().get(recent.getRouteId());
                if (route == null){
                    continue; // May have had an old id in the preference
                }
                
                viewable.setLabel(recent.getSerialNumber() + " - " + route.getPaoName());
            }
            viewables.add(viewable);
        }
        
        return viewables;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RequestIdsContainer {
        public List<String> requestIds;
    }

    /**
     * Updates all the Commander page related User Preferences to cache, if found in the params
     * 
     * @param user, selected user details
     * @param params, command parameters for retrieving the target information
     * @throws IOException, throws exception if JSON parsing fails for recent targets
     */
    private List<UserPreference> updateCommanderUserPreferences(LiteYukonUser user, CommandParams params) throws IOException {

        List<UserPreference> preferences = new ArrayList<>();

        // Get recent targets set in the preferences, parse the JSON string to a list of RecentTarget objects
        String recentPrefStringValue =
            userPreferenceService.getPreference(user, UserPreferenceName.COMMANDER_RECENT_TARGETS);
        
        List<RecentTarget> recentTargets =
            recentPrefStringValue == null ? new ArrayList<>() : JsonUtils.fromJson(recentPrefStringValue,
                recentTargetsType);

        // Initialize the current target from the params    
        if (params.getTarget() != null) {
            RecentTarget currentTarget =
                new RecentTarget(params.getTarget().name(), params.getPaoId(), params.getRouteId(),
                    params.getSerialNumber());

            // Find if the current target already exists in the recent targets preferences
            List<RecentTarget> existingTargets =
                recentTargets.stream()
                    .filter(preference -> preference.equals(currentTarget))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(existingTargets)) {
                // Target selected is a new target, add target to the recent targets preferences
                if (recentTargets.size() == RECENT_TARGET_MAXIMUM_SIZE_LIMIT) {
                    // Maintain the recent targets list size count, remove the oldest element - LRU at the end of List
                    recentTargets.remove(RECENT_TARGET_MAXIMUM_SIZE_LIMIT - 1);
                }
                recentTargets.add(0, currentTarget); // Recent target at the top of list
            }
            preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_RECENT_TARGETS,
                JsonUtils.toJson(recentTargets), true));
            preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_LAST_TARGET,
                params.getTarget().name(), true));
            
            if (params.getTarget() == (CommandTarget.DEVICE) || params.getTarget() == (CommandTarget.LOAD_GROUP)) {
                preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_LAST_PAO_ID,
                    Integer.toString(params.getPaoId()), true));
            } else {
                preferences.add(new UserPreference(user.getUserID(), 
                    UserPreferenceName.COMMANDER_LAST_SERIAL_NUMBER, params.getSerialNumber(), true));
                preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_LAST_ROUTE_ID,
                    Integer.toString(params.getRouteId()), true));
            }
        }
        
        if (params.getPriority() > 0) {
            preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_PRIORITY,
                Integer.toString(params.getPriority()), true));
        }

        preferences.add(new UserPreference(user.getUserID(), UserPreferenceName.COMMANDER_QUEUE_COMMAND,
                Boolean.toString(params.isQueueCommand()), true));
        
        // Update preferences all at once
        userPreferenceService.updateUserPreferences(user.getUserID(), preferences);
        return preferences;
    }

    /** This method is exposed for UI operations, which selectively want to update the commander preferences */
    @RequestMapping(value = "/commander/updateCommanderPreferences", method = RequestMethod.POST)
    public void updateDeviceTargetPreferences(HttpServletResponse resp, YukonUserContext userContext,
            @ModelAttribute CommandParams params, LiteYukonUser user) throws IOException {
        updateCommanderUserPreferences(user, params);
    }
}