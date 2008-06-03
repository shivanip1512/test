package com.cannontech.web.amr.device.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.cannontech.common.bulk.BulkProcessor;
import com.cannontech.common.bulk.iterator.CloseableIterator;
import com.cannontech.common.bulk.iterator.InputStreamIterator;
import com.cannontech.common.bulk.mapper.AddressToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.BulkImporterToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.ChainingMapper;
import com.cannontech.common.bulk.mapper.LiteYukonPAObjectToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.MeterNumberToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.PaoIdToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.PaoNameToYukonDeviceMapper;
import com.cannontech.common.bulk.mapper.StringToIntegerMapper;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.bulk.processor.ProcessorFactory;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

/**
 * Spring controller class for Device Configuration
 */
public class DeviceConfigurationController extends MultiActionController {

    public DeviceConfigurationDao deviceConfigurationDao = null;

    private BulkProcessor bulkProcessor = null;
    private ProcessorFactory processorFactory = null;
    private PaoIdToYukonDeviceMapper paoIdToYukonDeviceMapper = null;

    private PaoDao paoDao = null;
    private DeviceGroupProviderDao deviceGroupDao = null;
    private DeviceGroupService deviceGroupService = null;
    
    private LiteYukonPAObjectToYukonDeviceMapper liteYukonPAObjectToYukonDeviceMapper = null;
    private PaoNameToYukonDeviceMapper paoNameToYukonDeviceMapper = null;
    private MeterNumberToYukonDeviceMapper meterNumberToYukonDeviceMapper = null;
    private AddressToYukonDeviceMapper addressToYukonDeviceMapper = null;
    private BulkImporterToYukonDeviceMapper bulkImporterToYukonDeviceMapper = null;
    
    public void setDeviceConfigurationDao(DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public void setBulkProcessor(BulkProcessor bulkProcessor) {
        this.bulkProcessor = bulkProcessor;
    }

    public void setProcessorFactory(ProcessorFactory processorFactory) {
        this.processorFactory = processorFactory;
    }

    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }

    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }

    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
        this.deviceGroupService = deviceGroupService;
    }
    
    @Required
    public void setPaoIdToYukonDeviceMapper(PaoIdToYukonDeviceMapper paoIdToYukonDeviceMapper) {
        this.paoIdToYukonDeviceMapper = paoIdToYukonDeviceMapper;
    }

    public ModelAndView home(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("home");

        List<String> configurationTemplateList = new ArrayList<String>();
        List<ConfigurationTemplate> templates = deviceConfigurationDao.getAllConfigurationTemplates();
        for (ConfigurationTemplate template : templates) {
            configurationTemplateList.add(template.getName());
        }

        mav.addObject("configurationTemplateList", configurationTemplateList);

        List<ConfigurationBase> existingConfigs = deviceConfigurationDao.getAllConfigurations();
        mav.addObject("existingConfigs", existingConfigs);

        return mav;
    }

    public ModelAndView createConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        String configTemplate = ServletRequestUtils.getStringParameter(request,
                                                                       "configurationTemplate");

        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(configTemplate);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        return mav;
    }

    public ModelAndView editConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        ConfigurationType type = configuration.getType();
        String templateName = type.getConfigurationTemplateName();
        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(templateName);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("configurationId", configId.toString());

        return mav;
    }

    public ModelAndView removeConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        deviceConfigurationDao.delete(configId);

        String viewPath = new String("redirect:/spring/deviceConfiguration?home");

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("message", "Successfully removed device configuration");

        return mav;
    }

    public ModelAndView cloneConfig(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        // Save config with new name and id (name can only be 60 characters long)
        String configName = configuration.getName();
        if(configName.length() > 53) {
            configName = configName.substring(0, 52);
        }
        configuration.setName(configName + " (copy)");
        configuration.setId(null);
        deviceConfigurationDao.save(configuration);

        ConfigurationType type = configuration.getType();
        String templateName = type.getConfigurationTemplateName();
        ConfigurationTemplate template = deviceConfigurationDao.getConfigurationTemplate(templateName);

        String viewPath = new String("redirect:/spring/deviceConfiguration/" + template.getView());

        ModelAndView mav = new ModelAndView(viewPath);

        mav.addObject("configurationId", configuration.getId().toString());

        return mav;
    }

    public ModelAndView assignDevices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("assignDevices");

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");

        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);
        mav.addObject("configuration", configuration);

        List<YukonDevice> deviceList = deviceConfigurationDao.getAssignedDevices(configuration);
        mav.addObject("deviceList", deviceList);

        List<Integer> deviceIdList = new ArrayList<Integer>();
        for (YukonDevice device : deviceList) {
            deviceIdList.add(device.getDeviceId());
        }

        mav.addObject("selectedDeviceIds", StringUtils.join(deviceIdList, ","));

        // Get all of the device groups and add them to the model
        List<? extends DeviceGroup> groups = deviceGroupDao.getAllGroups();
        mav.addObject("groups", groups);

        return mav;
    }

    public ModelAndView assignConfigToDevices(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/deviceConfiguration?assignDevices");

        String deviceIds = ServletRequestUtils.getStringParameter(request, "deviceIds");

        String[] ids = StringUtils.split(deviceIds, ",");
        List<String> deviceIdList = Arrays.asList(ids);

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");
        mav.addObject("configuration", configId);
        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        Processor<YukonDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration);
        ObjectMapper<String, YukonDevice> mapper = new ChainingMapper<String, YukonDevice>(new StringToIntegerMapper(), paoIdToYukonDeviceMapper);

        bulkProcessor.bulkProcess(deviceIdList.iterator(), mapper, processor);

        return mav;
    }

    public ModelAndView assignConfigByAddress(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/deviceConfiguration?assignDevices");

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");
        mav.addObject("configuration", configId);
        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        Integer startRange = ServletRequestUtils.getIntParameter(request, "startRange");
        Integer endRange = ServletRequestUtils.getIntParameter(request, "endRange");

        if (startRange == null) {
            mav.addObject("errorMessage", "Please enter a valid integer Start of Range value.");
            return mav;
        }
        if (endRange == null) {
            mav.addObject("errorMessage", "Please enter a valid integer End of Range value.");
            return mav;
        }
        if (endRange <= startRange) {
            mav.addObject("errorMessage",
                          "Please enter an End of Range value that is greater than the Start of Range value.");
            return mav;
        }

        List<LiteYukonPAObject> litePaos = paoDao.getLiteYukonPaobjectsByAddressRange(startRange,
                                                                                      endRange);

        ObjectMapper<LiteYukonPAObject, YukonDevice> mapper = liteYukonPAObjectToYukonDeviceMapper;
        Processor<YukonDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration);

        bulkProcessor.bulkProcess(litePaos.iterator(), mapper, processor);

        return mav;
    }

    public ModelAndView assignConfigByGroup(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/deviceConfiguration?assignDevices");

        String groupName = ServletRequestUtils.getStringParameter(request, "groupName");

        // Get the group and then all the devices in the group
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        mav.addObject("group", groupName);
        Set<YukonDevice> devices = deviceGroupService.getDevices(Collections.singletonList(group));

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");
        mav.addObject("configuration", configId);
        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        Processor<YukonDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration);

        bulkProcessor.bulkProcess(devices.iterator(), processor);

        return mav;
    }

    public ModelAndView assignConfigByFileUpload(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/deviceConfiguration?assignDevices");

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");
        mav.addObject("configuration", configId);
        ConfigurationBase configuration = deviceConfigurationDao.getConfiguration(configId);

        boolean isMultipart = ServletFileUpload.isMultipartContent(request);

        if (isMultipart) {
            MultipartHttpServletRequest mRequest = (MultipartHttpServletRequest) request;

            MultipartFile dataFile = mRequest.getFile("dataFile");
            if (dataFile == null || dataFile.getSize() == 0) {
                mav.addObject("errorMessage",
                              "You must choose a file that has data to add devices.");
                return mav;
            }

            ObjectMapper<String, YukonDevice> mapper = null;

            CloseableIterator<String> iterator = null;
            try {

                // Create an iterator to iterate through the file line by
                // line
                iterator = new InputStreamIterator(dataFile.getInputStream());

                // Create the mapper based on the type of file upload
                String uploadType = ServletRequestUtils.getStringParameter(request, "uploadType");
                if ("PAONAME".equalsIgnoreCase(uploadType)) {
                    mapper = paoNameToYukonDeviceMapper;
                } else if ("METERNUMBER".equalsIgnoreCase(uploadType)) {
                    mapper = meterNumberToYukonDeviceMapper;
                } else if ("ADDRESS".equalsIgnoreCase(uploadType)) {
                    mapper = addressToYukonDeviceMapper;
                } else if ("BULK".equalsIgnoreCase(uploadType)) {
                    mapper = bulkImporterToYukonDeviceMapper;
                    // Skip the first line in the file for bulk import
                    // format - header line
                    iterator.next();
                }

                // Get the processor that adds devices to groups
                Processor<YukonDevice> processor = processorFactory.createAssignConfigurationToYukonDeviceProcessor(configuration);

                bulkProcessor.bulkProcess(iterator, mapper, processor);

            } catch (IOException e) {
                mav.addObject("errorMessage",
                              "There was a problem processing the file: " + e.getMessage());
            } finally {
                CtiUtilities.close(iterator);
            }

        }

        return mav;

    }

    public ModelAndView unassignConfigForDevice(HttpServletRequest request,
            HttpServletResponse response) throws ServletException {

        ModelAndView mav = new ModelAndView("redirect:/spring/deviceConfiguration?assignDevices");

        Integer configId = ServletRequestUtils.getIntParameter(request, "configuration");
        mav.addObject("configuration", configId);

        Integer deviceId = ServletRequestUtils.getIntParameter(request, "deviceId");
        deviceConfigurationDao.unassignConfig(deviceId);

        return mav;
    }

}
