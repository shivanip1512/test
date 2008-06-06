package com.cannontech.web.group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.alert.service.AlertService;
import com.cannontech.common.bulk.collection.DeviceCollection;
import com.cannontech.common.bulk.collection.DeviceGroupCollectionHelper;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.commands.GroupCommandResult;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.util.ResolvableTemplate;
import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.web.util.ExtTreeNode;

@Controller
@RequestMapping("/commander/*")
public class GroupCommanderController implements InitializingBean {

    private Logger log = YukonLogManager.getLogger(GroupCommanderController.class);

    private CommandDao commandDao;
    private GroupCommandExecutor groupCommandExecutor;
    private AlertService alertService;
    private DeviceGroupService deviceGroupService;
    private DeviceGroupCollectionHelper deviceGroupCollectionHelper;

    private PaoCommandAuthorizationService commandAuthorizationService;
    
    // available meter commands
    private List<LiteCommand> meterCommands;

    public void afterPropertiesSet() {
        this.meterCommands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
    }
    
    @Autowired
    public void setCommandDao(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    @Autowired
    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    @Autowired
	public void setCommandAuthorizationService(
			PaoCommandAuthorizationService commandAuthorizationService) {
		this.commandAuthorizationService = commandAuthorizationService;
	}
    
    @Autowired
    public void setAlertService(AlertService alertService) {
        this.alertService = alertService;
    }
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Autowired
    public void setDeviceGroupCollectionHelper(DeviceGroupCollectionHelper deviceGroupCollectionHelper) {
        this.deviceGroupCollectionHelper = deviceGroupCollectionHelper;
    }

    @RequestMapping
    public void collectionProcessing(DeviceCollection deviceCollection, LiteYukonUser user, ModelMap model)
    throws ServletException {
        
        List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
        model.addAttribute("commands", commands);
        
        model.addAttribute("deviceCollection", deviceCollection);
        
    }
    
    @RequestMapping
    public void groupProcessing(LiteYukonUser user, ModelMap model)
            throws ServletException {

        List<LiteCommand> commands = commandDao.getAuthorizedCommands(meterCommands, user);
        model.addAttribute("commands", commands);
        
        // make a device group hierarchy starting at root, only modifiable groups
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        DeviceGroupHierarchy groupHierarchy = deviceGroupService.getDeviceGroupHierarchy(rootGroup, new NonHiddenDeviceGroupPredicate());
        
        ExtTreeNode root = DeviceGroupTreeUtils.makeDeviceGroupExtTree(groupHierarchy, "Groups", null);
        
        JSONObject jsonObj = new JSONObject(root.toMap());
        String dataJson = jsonObj.toString();
        model.addAttribute("dataJson", dataJson);
    }
    
    @RequestMapping
    public String executeGroupCommand(String groupName, String commandString, LiteYukonUser user, ModelMap map) throws ServletException {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceCollection deviceCollection = deviceGroupCollectionHelper.buildDeviceCollection(group);
        return executeCollectionCommand(deviceCollection, commandString, user, map);
    }

    @RequestMapping
    public String executeCollectionCommand(DeviceCollection deviceCollection, String commandString, LiteYukonUser user, ModelMap map)
            throws ServletException {

        boolean error = false;
        if (StringUtils.isBlank(commandString)) {
            error = true;
            map.addAttribute("errorMsg", "You must enter a valid command");
        } else if (!commandAuthorizationService.isAuthorized(user, commandString)) {
        	error = true;
            map.addAttribute("errorMsg", "User is not authorized to execute this command.");
        }
        
        if (error) {
            return "redirect:/spring/group/commander/groupProcessing";
        }
        
        SimpleCallback<GroupCommandResult> callback = new SimpleCallback<GroupCommandResult>() {
            @Override
            public void handle(GroupCommandResult result) {
                ResolvableTemplate resolvableTemplate = new ResolvableTemplate("yukon.common.alerts.commandCompletion");
                int successCount = result.getResultHolder().getResultStrings().size();
                resolvableTemplate.addData("successCount", successCount);
                int failureCount = result.getResultHolder().getErrors().size();
                resolvableTemplate.addData("failureCount", failureCount);
                int total = failureCount + successCount;
                resolvableTemplate.addData("percentSuccess", (float)successCount *100 / total);
                resolvableTemplate.addData("command", result.getCommand());
                resolvableTemplate.addData("resultKey", result.getKey());
                
                CommandCompletionAlert commandCompletionAlert = new CommandCompletionAlert(new Date(), resolvableTemplate);
                
                alertService.add(commandCompletionAlert);

            }
        };

        try {
            String key = 
                groupCommandExecutor.execute(deviceCollection, commandString, callback, user);
            map.addAttribute("resultKey", key);
            return "redirect:/spring/group/commander/resultDetail";
        } catch (PaoAuthorizationException e) {
            log.warn("Unable to execute, putting error in model", e);
            map.addAttribute("error", "Unable to execute specified commands");
        }

        // must have been an error, try again
        map.addAttribute("command", commandString);
        return "redirect:/spring/group/commander/groupProcessing";
    }

    @RequestMapping
    public void resultList(ModelMap map) {
        
        List<GroupCommandResult> completed = groupCommandExecutor.getCompleted();
        List<GroupCommandResult> pending = groupCommandExecutor.getPending();
        
        ArrayList<GroupCommandResult> allResults = new ArrayList<GroupCommandResult>(completed.size() + pending.size());
        allResults.addAll(completed);
        allResults.addAll(pending);
        
        map.addAttribute("resultList", allResults);
    }
    
    @RequestMapping
    public void resultDetail(String resultKey, ModelMap map) {
        
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        map.addAttribute("result", result);
    }
    
    @RequestMapping({"errorsList", "successList"})
    public void results(String resultKey, ModelMap map) {
        GroupCommandResult result = groupCommandExecutor.getResult(resultKey);
        
        map.addAttribute("result", result);
    }
}
