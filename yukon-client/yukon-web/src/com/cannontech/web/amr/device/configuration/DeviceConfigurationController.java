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
import com.cannontech.common.device.config.dao.ConfigurationType;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.config.model.ConfigurationTemplate;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.web.security.annotation.CheckRoleProperty;

/**
 * Spring controller class for Device Configuration
 */
@CheckRoleProperty(YukonRoleProperty.ADMIN_VIEW_CONFIG)
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

}
