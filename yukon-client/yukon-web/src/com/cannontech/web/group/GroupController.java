package com.cannontech.web.group;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.BulkProcessingResultHolder;
import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.CollectingBulkProcessorCallback;
import com.cannontech.common.bulk.iterator.InputStreamIterator;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.commands.GroupCommandExecutor;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupHierarchy;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.util.YukonDeviceToIdMapper;
import com.cannontech.common.util.MapQueue;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.util.ServletUtil;

@SuppressWarnings("unchecked")
public class GroupController extends MultiActionController {

    private Logger log = YukonLogManager.getLogger(GroupController.class);

    private DeviceGroupService deviceGroupService = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupEditorDao deviceGroupEditorDao = null;

    private CommandDao commandDao = null;
    private GroupCommandExecutor groupCommandExecutor = null;

    private BulkProcessor bulkProcessor = null;
    private ObjectMapperFactory objectMapperFactory = null;
    private ProcessorFactory processorFactory = null;

    private PaoDao paoDao = null;
    private MeterDao meterDao = null;

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }

    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }

    public void setCommandDao(CommandDao commandDao) {
        this.commandDao = commandDao;
    }

    public void setGroupCommandExecutor(GroupCommandExecutor groupCommandExecutor) {
        this.groupCommandExecutor = groupCommandExecutor;
    }

    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }

    public void setObjectMapperFactory(ObjectMapperFactory objectMapperFactory) {
        this.objectMapperFactory = objectMapperFactory;
    }

    public void setProcessorFactory(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Required
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("home.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = null;
        DeviceGroup rootGroup = deviceGroupService.getRootGroup();
        mav.addObject("rootGroup", rootGroup);

        if (!StringUtils.isEmpty(groupName)) {
            group = deviceGroupService.resolveGroupName(groupName);
        } else {
            group = rootGroup;
        }
        mav.addObject("group", group);

        // Create a list of groups the current group could move to excluding the
        // current group itself, any descendant groups of the current group and
        // any groups that are not modifiable
        List<DeviceGroup> groups = deviceGroupDao.getAllGroups();
        List<DeviceGroup> moveGroups = new ArrayList<DeviceGroup>();
        MapQueue<DeviceGroup, DeviceGroup> childList = new MapQueue<DeviceGroup, DeviceGroup>();
        for (DeviceGroup deviceGroup : groups) {
            if (deviceGroup.isModifiable() 
                && !deviceGroup.equals(group) 
                && !deviceGroup.isDescendantOf(group)
                && !deviceGroup.equals(group.getParent())) {
                moveGroups.add(deviceGroup);
            }
            if (deviceGroup.getParent() != null) {
                childList.add(deviceGroup.getParent(), deviceGroup);
            }
        }
        mav.addObject("moveGroups", moveGroups);

        DeviceGroupHierarchy groupHierarchy = createHierarchy(rootGroup, childList);
        mav.addObject("groupHierarchy", groupHierarchy);
        
        List<DeviceGroup> childGroups = childList.get(group);
        mav.addObject("subGroups", childGroups);

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices", false);
        mav.addObject("showDevices", showDevices);
        
        List<YukonDevice> deviceList = deviceGroupDao.getChildDevices(group);
        List<Integer> deviceIdList = new MappingList<YukonDevice, Integer>(deviceList, new YukonDeviceToIdMapper());
        mav.addObject("deviceIdsInGroup", StringUtils.join(deviceIdList, ","));

        mav.addObject("deviceCount", deviceIdList.size());
        
        return mav;

    }
    
    private DeviceGroupHierarchy createHierarchy(DeviceGroup root, MapQueue<DeviceGroup, DeviceGroup> childList) {
        DeviceGroupHierarchy result = new DeviceGroupHierarchy();
        result.setGroup(root);
        List<DeviceGroup> list = childList.get(root);
        for (DeviceGroup childGroup : list) {
            DeviceGroupHierarchy childHierarchy = createHierarchy(childGroup, childList);
            result.getChildGroupList().add(childHierarchy);
        }
        return result;
    }

    public ModelAndView getDevicesForGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("deviceMembers.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        List<Meter> deviceList = meterDao.getChildMetersByGroup(group);
        
        Collections.sort(deviceList, meterDao.getMeterComparator());
        
        mav.addObject("deviceList", deviceList);

        mav.addObject("group", group);

        return mav;

    }

    public ModelAndView updateGroupName(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        String newGroupName = ServletRequestUtils.getStringParameter(request, "newGroupName");

        // Make sure a new name was entered and doesn't contain slashes
        newGroupName = newGroupName.trim();
        if (StringUtils.isEmpty(newGroupName) || (newGroupName.contains("\\")) || (newGroupName.contains("/"))) {
            mav.addObject("errorMessage",
                          "You must enter a New Group Name.  Group names may not contain slashes.");
            return mav;
        }

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        group.setName(newGroupName.trim());
        
        try {
            deviceGroupEditorDao.updateGroup((StoredDeviceGroup) group);
        } catch (DuplicateException e){
            mav.addObject("errorMessage", e.getMessage());
            return mav;
        }

        mav.addObject("groupName", group.getFullName());

        return mav;

    }

    public ModelAndView addChild(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {

            String childGroupName = ServletRequestUtils.getStringParameter(request,
                                                                           "childGroupName");

            // Make sure a new name was entered and doesn't contain slashes
            childGroupName = childGroupName.trim();
            if (StringUtils.isEmpty(childGroupName) || (childGroupName.contains("\\")) || (childGroupName.contains("/"))) {
                mav.addObject("errorMessage",
                              "You must enter a Sub Group Name.  Group names may not contain slashes.");
                return mav;
            }

            try {
                deviceGroupEditorDao.addGroup((StoredDeviceGroup) group,
                                              DeviceGroupType.STATIC,
                                              childGroupName);
            } catch (DuplicateException e) {
                mav.addObject("errorMessage", e.getMessage());
                return mav;
            }

        } else {
            mav.addObject("errorMessage", "Cannot add sub group to " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView addDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        if (group.isModifiable()) {

            String deviceId = ServletRequestUtils.getStringParameter(request, "deviceId");

            String[] ids = StringUtils.split(deviceId, ",");

            List<YukonDevice> deviceList = new ArrayList<YukonDevice>();
            for (String id : ids) {
                id = id.trim();
                
                if (!StringUtils.isEmpty(id)) {
                    Integer idInt = Integer.parseInt(id);
                    YukonDevice device = new YukonDevice();
                    device.setDeviceId(idInt);
                    deviceList.add(device);
                }
            }

            ((DeviceGroupMemberEditorDao) deviceGroupEditorDao).addDevices((StoredDeviceGroup) group,
                                                                           deviceList);
        } else {
            mav.addObject("errorMessage", "Cannot add devices to " + group.getFullName());
            return mav;
        }

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices");
        mav.addObject("showDevices", showDevices);

        return mav;
    }

    public ModelAndView showAddDevicesByFile(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("addDevicesByFile.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", group);

        return mav;
    }

    public ModelAndView showAddDevicesByAddress(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("addDevicesByAddress.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", group);

        return mav;
    }

    public ModelAndView addDevicesByFile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/addDevicesResult");
        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        if (group.isModifiable()) {

            boolean isMultipart = ServletFileUpload.isMultipartContent(request);

            if (isMultipart) {
                MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

                MultipartFile dataFile = mRequest.getFile("dataFile");
                if (dataFile == null || dataFile.getSize() == 0) {
                    mav.setViewName("redirect:/spring/group/showAddDevicesByFile");
                    mav.addObject("errorMessage",
                                  "You must choose a file that has data to add devices.");
                    return mav;
                }

                mav.addObject("resultInfo",
                              "Results for upload from file: " + dataFile.getOriginalFilename());

                ObjectMapper<String, YukonDevice> yukonDeviceMapper = null;

                try {

                    // Create a collecting callback and stick it into the
                    // session for later use (progress updating, etc...)
                    CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
                    request.getSession().setAttribute("bulkAddDeviceToGroup", callback);

                    // Create an iterator to iterate through the file line by
                    // line
                    Iterator<String> iterator = new InputStreamIterator(dataFile.getInputStream());

                    // Create the mapper based on the type of file upload
                    String uploadType = ServletRequestUtils.getStringParameter(request,
                                                                               "uploadType");
                    if ("PAONAME".equalsIgnoreCase(uploadType)) {
                        yukonDeviceMapper = objectMapperFactory.createPaoNameToYukonDeviceMapper();
                    } else if ("METERNUMBER".equalsIgnoreCase(uploadType)) {
                        yukonDeviceMapper = objectMapperFactory.createMeterNumberToYukonDeviceMapper();
                    } else if ("ADDRESS".equalsIgnoreCase(uploadType)) {
                        yukonDeviceMapper = objectMapperFactory.createAddressToYukonDeviceMapper();
                    } else if ("BULK".equalsIgnoreCase(uploadType)) {
                        yukonDeviceMapper = objectMapperFactory.createBulkImporterToYukonDeviceMapper();
                        // Skip the first line in the file for bulk import
                        // format - header line
                        iterator.next();
                    }

                    // Get the processor that adds devices to groups
                    Processor<YukonDevice> addToGroupProcessor = processorFactory.createAddYukonDeviceToGroupProcessor((StoredDeviceGroup) group);

                    bulkProcessor.backgroundBulkProcess(iterator,
                                                        yukonDeviceMapper,
                                                        addToGroupProcessor,
                                                        callback);

                } catch (IOException e) {
                    mav.addObject("errorMessage",
                                  "There was a problem processing the file: " + e.getMessage());
                }

            }

        } else {
            mav.setViewName("redirect:/spring/group/home");
            mav.addObject("errorMessage", "Cannot add devices to " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView addDevicesByAddressRange(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/addDevicesResult");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        if (group.isModifiable()) {

            Integer startRange = ServletRequestUtils.getIntParameter(request, "startRange");
            Integer endRange = ServletRequestUtils.getIntParameter(request, "endRange");

            if (startRange == null) {
                mav.setViewName("redirect:/spring/group/showAddDevicesByAddressRange");
                mav.addObject("errorMessage", "Please enter a valid integer Start of Range value.");
                return mav;
            }
            if (endRange == null) {
                mav.setViewName("redirect:/spring/group/showAddDevicesByAddressRange");
                mav.addObject("errorMessage", "Please enter a valid integer End of Range value.");
                return mav;
            }
            if (endRange <= startRange) {
                mav.setViewName("redirect:/spring/group/showAddDevicesByAddressRange");
                mav.addObject("errorMessage",
                              "Please enter an End of Range value that is greater than the Start of Range value.");
                return mav;
            }

            mav.addObject("resultInfo",
                          "Results for device add for address range: " + startRange + " - " + endRange);

            List<LiteYukonPAObject> litePaos = paoDao.getLiteYukonPaobjectsByAddressRange(startRange,
                                                                                          endRange);

            ObjectMapper<LiteYukonPAObject, YukonDevice> yukonDeviceMapper = objectMapperFactory.createLiteYukonPAObjectToYukonDeviceMapper();
            Processor<YukonDevice> addToGroupProcessor = processorFactory.createAddYukonDeviceToGroupProcessor((StoredDeviceGroup) group);

            // Create a collecting callback and stick it into the session
            // for later use (progress updating, etc...)
            CollectingBulkProcessorCallback callback = new CollectingBulkProcessorCallback();
            request.getSession().setAttribute("bulkAddDeviceToGroup", callback);

            bulkProcessor.backgroundBulkProcess(litePaos.iterator(),
                                                yukonDeviceMapper,
                                                addToGroupProcessor,
                                                callback);

        } else {
            mav.setViewName("redirect:/spring/group/home");
            mav.addObject("errorMessage", "Cannot add devices to " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView removeDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        Boolean showDevices = ServletRequestUtils.getBooleanParameter(request, "showDevices");
        mav.addObject("showDevices", showDevices);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);

        if (group.isModifiable()) {

            Integer deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");

            YukonDevice device = new YukonDevice();
            device.setDeviceId(deviceId);
            ((DeviceGroupMemberEditorDao) deviceGroupEditorDao).removeDevices((StoredDeviceGroup) group,
                                                                              Collections.singletonList(device));

        } else {
            mav.addObject("errorMessage", "Cannot remove devices from " + group.getFullName());
            return mav;
        }

        return mav;
    }

    public ModelAndView moveGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        String parentGroupName = ServletRequestUtils.getStringParameter(request, "parentGroupName");

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        DeviceGroup parentGroup = deviceGroupService.resolveGroupName(parentGroupName);

        // Make sure we can move the group
        if (group.isEditable()) {
            deviceGroupEditorDao.moveGroup((StoredDeviceGroup) group,
                                           (StoredDeviceGroup) parentGroup);

            // Reload the group to get the new name
            StoredDeviceGroup newGroup = deviceGroupEditorDao.getGroupById(((StoredDeviceGroup) group).getId());
            mav.addObject("groupName", newGroup.getFullName());

        } else {
            mav.addObject("errorMessage", "Cannot move Group: " + group.getFullName());
            mav.addObject("groupName", groupName);
        }

        return mav;
    }

    public ModelAndView removeGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/group/home");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");
        mav.addObject("groupName", groupName);

        String removeGroupName = ServletRequestUtils.getStringParameter(request, "removeGroupName");
        DeviceGroup removeGroup = deviceGroupService.resolveGroupName(removeGroupName);

        // Make sure we can remove the group
        if (removeGroup.isEditable()) {
            deviceGroupEditorDao.removeGroup((StoredDeviceGroup) removeGroup);
        } else {
            mav.addObject("errorMessage", "Cannot remove Group: " + removeGroup.getFullName());
        }

        return mav;
    }

    public ModelAndView addDevicesResult(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("addDevicesResult.jsp");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        DeviceGroup group = null;
        if (!StringUtils.isEmpty(groupName)) {
            group = deviceGroupService.resolveGroupName(groupName);
        } else {
            group = deviceGroupService.getRootGroup();
        }
        mav.addObject("group", group);

        BulkProcessingResultHolder results = (BulkProcessingResultHolder) request.getSession()
                                                                                 .getAttribute("bulkAddDeviceToGroup");
        mav.addObject("results", results);

        String uploadFile = ServletRequestUtils.getStringParameter(request, "uploadFile");
        mav.addObject("uploadFile", uploadFile);

        return mav;

    }

    public ModelAndView updateBulkDeviceAdd(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("addDevicesResultUpdate.jsp");

        BulkProcessingResultHolder results = (BulkProcessingResultHolder) request.getSession()
                                                                                 .getAttribute("bulkAddDeviceToGroup");
        mav.addObject("results", results);

        return mav;

    }

    public ModelAndView groupProcessing(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("groupProcessing.jsp");

        Vector commands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
        mav.addObject("commands", commands);

        List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
        mav.addObject("groups", groups);

        String defaultEmailSubject = "Group Processing completed";
        if (!groups.isEmpty()) {
            defaultEmailSubject = "Group Processing for " + groups.get(0).getFullName() + " completed. (" + ((LiteCommand) commands.get(0)).getCommand() + ")";
        }
        String emailSubject = ServletRequestUtils.getStringParameter(request,
                                                                     "emailSubject",
                                                                     defaultEmailSubject);
        mav.addObject("emailSubject", emailSubject);

        return mav;
    }

    public ModelAndView executeCommand(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView();

        String groupName = ServletRequestUtils.getStringParameter(request, "groupSelect");
        LiteYukonUser user = ServletUtil.getYukonUser(request);

        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", groupName);
        Set<Integer> deviceIds = deviceGroupService.getDeviceIds(Collections.singletonList(group));

        String command = ServletRequestUtils.getStringParameter(request, "commandString");
        mav.addObject("command", command);

        String emailAddresses = ServletRequestUtils.getStringParameter(request, "emailAddresses");
        mav.addObject("emailAddresses", emailAddresses);

        String emailSubject = ServletRequestUtils.getStringParameter(request,
                                                                     "emailSubject",
                                                                     "Group Processing for " + group.getFullName() + " completed. (" + command + ")");
        mav.addObject("emailSubject", emailSubject);

        boolean error = false;
        if (StringUtils.isBlank(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg", "You must enter at least one valid email address.");
        } else if (!checkEmailAddresses(emailAddresses)) {
            error = true;
            mav.addObject("errorMsg",
                          "One or more of the email addresses is not formatted correctly. " + "\nPlease enter a comma separated list of valid email addresses.");
        } else if (StringUtils.isBlank(command)) {
            error = true;
            mav.addObject("errorMsg", "You must enter a valid command");
        }

        if (error) {
            mav.setViewName("groupProcessing.jsp");

            Vector commands = commandDao.getAllCommandsByCategory(DeviceTypes.STRING_MCT_410IL[0]);
            mav.addObject("commands", commands);

            List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
            mav.addObject("groups", groups);
        } else {
            mav.setViewName("redirect:/spring/group/commandExecuted");

            try {
                groupCommandExecutor.execute(deviceIds, command, emailAddresses, emailSubject, user);
            } catch (MessagingException e) {
                log.warn("Unable to execute, putting error in model", e);
                mav.addObject("error", "Unable to send email");
            } catch (PaoAuthorizationException e) {
                log.warn("Unable to execute, putting error in model", e);
                mav.addObject("error", "Unable to execute specified commands");
            }
        }

        return mav;
    }

    public ModelAndView commandExecuted(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("commandExecuting.jsp");

        String command = ServletRequestUtils.getStringParameter(request, "command");
        mav.addObject("command", command);

        String group = ServletRequestUtils.getStringParameter(request, "group");
        mav.addObject("group", group);

        String emailAddresses = ServletRequestUtils.getStringParameter(request, "emailAddresses");
        mav.addObject("emailAddresses", emailAddresses);

        return mav;
    }

    /**
     * Helper method to check for email address format
     * @param emailAddresses - comma separated list of email addresses
     * @return True if all email addresses are formatted correctly
     */
    private boolean checkEmailAddresses(String emailAddresses) {

        String[] addresses = emailAddresses.split(",");

        for (String address : addresses) {

            if (!address.matches(".+@.+\\.[a-z]+")) {
                return false;
            }

        }

        return true;
    }

}
