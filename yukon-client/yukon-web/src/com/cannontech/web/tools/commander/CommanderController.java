package com.cannontech.web.tools.commander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.geojson.FeatureCollection;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
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

import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.dao.PaoLocationDao;
import com.cannontech.common.pao.model.DistanceUnit;
import com.cannontech.common.pao.model.PaoLocation;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.TransactionType;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.mbean.ServerDatabaseCache;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.tools.commander.model.CommandParams;
import com.cannontech.web.tools.commander.model.CommandRequest;
import com.cannontech.web.tools.commander.model.CommandRequestException;
import com.cannontech.web.tools.commander.model.CommandRequestExceptionType;
import com.cannontech.web.tools.commander.service.CommanderService;
import com.cannontech.web.tools.mapping.service.PaoLocationService;
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
    @Autowired private DBPersistentDao dbPersistentDao;
    @Autowired private CommandDao commandDao;
    @Autowired private CommanderService commanderService;
    @Autowired private DeviceSearchService deviceSearchService;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    private static final String keyBase = "yukon.web.modules.tools.commander";
    private static final String json = MediaType.APPLICATION_JSON_VALUE;
    private static final Comparator<CommandRequest> requestSorter = new Comparator<CommandRequest>() {
        @Override
        public int compare(CommandRequest o1, CommandRequest o2) {
            if (o1.getTimestamp() == o2.getTimestamp()) {
                return 0;
            } else if (o1.getTimestamp() < o2.getTimestamp()) {
                return -1;
            } else {
                return 1;
            }
        }
    };
    
    @RequestMapping({"/commander", "/commander/"})
    public String commander(ModelMap model, LiteYukonUser user) {
        
        LiteYukonPAObject[] routes = paoDao.getRoutesByType(PaoType.ROUTE_CCU, PaoType.ROUTE_MACRO);
        model.addAttribute("routes", routes);
        List<CommandRequest> requests = new ArrayList<>(commanderService.getRequests(user).values());
        Collections.sort(requests, requestSorter);
        model.addAttribute("requests", requests);
        
        return "commander/commander.jsp";
    }
    
    /** Get the route for the device */
    @RequestMapping("/commander/route/{paoId}")
    public @ResponseBody LiteYukonPAObject route(LiteYukonUser user, HttpServletResponse resp, @PathVariable int paoId) {
        
        LiteYukonPAObject pao = paoDao.getLiteYukonPAO(paoId);
        PaoType type = pao.getPaoType();
        if (type.isRoutable()) {
            LiteYukonPAObject route = cache.getAllRoutesMap().get(pao.getRouteID());
            return route;
        } else {
            resp.setStatus(HttpStatus.BAD_REQUEST.value());
            return null;
        }
    }
    
    /** Change route popup */
    @RequestMapping("/commander/route/{routeId}/change")
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
            CarrierBase db = (CarrierBase) dbPersistentDao.retrieveDBPersistent(pao);
            db.getDeviceRoutes().setRouteID(routeId);
            dbPersistentDao.performDBChange(db, TransactionType.UPDATE);
            Log.debug("User: " + user.getUsername() + " change route on " + pao.getPaoName() + " from " 
                + oldRoute.getPaoName() + " to " + newRoute.getPaoName());
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
        try {
            commands = commanderService.sendCommand(userContext, params);
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
        
        List<Map<String, CommandRequest>> requests = new ArrayList<>();
        for (CommandRequest command : commands) {
            ImmutableMap<String, CommandRequest> request = ImmutableMap.of("request", command);
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
    
    /** Get paos nearby */
    @RequestMapping(value="/commander/{paoId}/nearby", method=RequestMethod.POST)
    public @ResponseBody FeatureCollection nearby(HttpServletResponse resp, @PathVariable int paoId) {
        
        PaoLocation location = paoLocationDao.getLocation(paoId);
        if (location == null) {
            resp.setStatus(HttpStatus.NO_CONTENT.value());
            return null;
        }
        List<PaoLocation> locations = paoLocationService.getNearbyLocations(location, 5, DistanceUnit.MILES);
        
        return paoLocationService.getFeatureCollection(locations);
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