package com.cannontech.web.commander;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cannontech.amr.device.search.model.DeviceSearchCategory;
import com.cannontech.amr.device.search.model.DeviceSearchField;
import com.cannontech.amr.device.search.model.DeviceSearchFilterBy;
import com.cannontech.amr.device.search.model.DeviceSearchOrderBy;
import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.search.SearchResult;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yc.bean.YCBean;

@Controller
@RequestMapping(value = "/*")
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class CommanderController {
    @Autowired private DeviceSearchService deviceSearchService;
    @Autowired private CommandDao commandDao;
    
    @RequestMapping
    public String select(
            @RequestParam(value = "category", defaultValue = "MCT") DeviceSearchCategory category,
            @RequestParam(value = "orderBy", defaultValue = "NAME") DeviceSearchOrderBy orderBy,
            @RequestParam(value = "descending", defaultValue = "false") Boolean orderByDescending,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer itemsPerPage,
            ModelMap modelMap,
            YukonUserContext userContext,
            HttpServletRequest request) {
        
        YCBean ycBean = setupYCBean(request.getSession(), userContext);
        
        int pageComputed = (page == null) ? 1 : page;
        int itemsPerPageComputed = (itemsPerPage == null) ? 25 : itemsPerPage;
        
        List<DeviceSearchField> fields = deviceSearchService.getFieldsForCategory(category);
        
        List<DeviceSearchFilterBy> filterByList = deviceSearchService.getFiltersForFields(fields);
        modelMap.addAttribute("filterByList", filterByList);
        
        List<DeviceSearchFilterBy> queryFilters = getQueryFilter(request, filterByList);
        SearchResult<LiteYukonPAObject> deviceSearchResults = deviceSearchService.search(category, queryFilters, orderBy, orderByDescending, pageComputed, itemsPerPageComputed);
        
        modelMap.addAttribute("fields", fields);
        modelMap.addAttribute("category", category);
        modelMap.addAttribute("availableRoutes", ycBean.getValidRoutes());
        modelMap.addAttribute("orderBy", orderBy);
        modelMap.addAttribute("deviceSearchResults", deviceSearchResults);
        modelMap.addAttribute("menuSelection", getMenuSelection(category));
        
        return "select.jsp";
    }

    @RequestMapping(value = "/command", method = RequestMethod.GET)
    public String command(
            @RequestParam(required = true) Integer deviceId,
            ModelMap modelMap,
            YukonUserContext userContext,
            HttpServletRequest request) {
        
        YCBean ycBean = setupYCBean(request.getSession(), userContext);
        
        if(deviceId == null) {
            if(ycBean.getLiteYukonPao() != null) {
                deviceId = ycBean.getLiteYukonPao().getLiteID();
            } else {
                deviceId = -1;
            }
        }
        
        if(ycBean.getLiteYukonPao() == null || ycBean.getLiteYukonPao().getLiteID() != deviceId) {
            LiteYukonPAObject lPao = deviceSearchService.getDevice(deviceId);
            ycBean.setLiteYukonPao(lPao);
            ycBean.setDeviceType(lPao);
        }
        
        LiteYukonPAObject lPao = ycBean.getLiteYukonPao();
        
        modelMap.addAttribute("lPao", lPao);
        modelMap.addAttribute("deviceId", deviceId);
        modelMap.addAttribute("deviceName", getDeviceName(ycBean, null));
        modelMap.addAttribute("meterDetailDisplayable", deviceSearchService.isMeterDetailsSupported(lPao));
        modelMap.addAttribute("availableCommands", getAvailableCommands(ycBean, lPao, userContext));
        modelMap.addAttribute("menuSelection", getMenuSelection(deviceSearchService.getDeviceCategory(deviceId)));
        modelMap.addAttribute("redirectURI", String.format("%s?deviceId=%d", request.getRequestURI(), deviceId));
        
        mapCommonAttributes(ycBean, modelMap, userContext);

        return "command.jsp";
    }

    @RequestMapping(value = "/command/{serialType}", method = RequestMethod.GET)
    public String commandSerialType(
            @PathVariable String serialType,
            @RequestParam(required = false) String serialNumber,
            ModelMap modelMap,
            YukonUserContext userContext,
            HttpServletRequest request) {
        
        YCBean ycBean = setupYCBean(request.getSession(), userContext);

        ycBean.setLiteYukonPao(null);
        if("xcom".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL);
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("vcom".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("sa205".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.STRING_CMD_SA205_SERIAL);
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("sa305".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.STRING_CMD_SA305_SERIAL);
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        }
        
        modelMap.addAttribute("deviceName", getDeviceName(ycBean, serialType));
        modelMap.addAttribute("serialType", serialType);
        if(serialNumber != null && !serialNumber.isEmpty()) {
            modelMap.addAttribute("serialNumber", serialNumber);
        }
        modelMap.addAttribute("availableRoutes", ycBean.getValidRoutes());
        modelMap.addAttribute("availableCommands", getAvailableCommands(ycBean, serialType, userContext));
        modelMap.addAttribute("menuSelection", getMenuSelection(serialType));
        
        mapCommonAttributes(ycBean, modelMap, userContext);
        
        String redirectURI;
        if(serialNumber != null && !serialNumber.isEmpty()) {
            redirectURI = String.format("%s?serialNumber=%s", request.getRequestURI(), serialNumber);
        } else {
            redirectURI = request.getRequestURI();
        }
        modelMap.addAttribute("redirectURI", redirectURI);
        
        return "command.jsp";
    }

    private YCBean setupYCBean(HttpSession session, YukonUserContext userContext) {
        YCBean ycBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
        if(ycBean == null) {
            session.setAttribute(ServletUtil.ATT_YC_BEAN, new YCBean());
            ycBean = (YCBean)session.getAttribute(ServletUtil.ATT_YC_BEAN);
        }
        
        ycBean.setUserContext(userContext);
        ycBean.setLiteUser(userContext.getYukonUser());
        
        return ycBean;
    }

    private void mapCommonAttributes(YCBean ycBean, ModelMap modelMap, YukonUserContext userContext) {
        modelMap.addAttribute("deviceType", ycBean.getDeviceType());
        modelMap.addAttribute("currentCommand", ycBean.getCommandString().replaceAll("noqueue", "").trim());
        modelMap.addAttribute("resultText", ycBean.getResultText());
        modelMap.addAttribute("deviceHistory", ycBean.getDeviceHistory());
        modelMap.addAttribute("sa205History", getSerialNumberHistory(ycBean, CommandCategory.STRING_CMD_SA205_SERIAL));
        modelMap.addAttribute("sa305History", getSerialNumberHistory(ycBean, CommandCategory.STRING_CMD_SA305_SERIAL));
        modelMap.addAttribute("xcomHistory", getSerialNumberHistory(ycBean, CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL));
        modelMap.addAttribute("vcomHistory", getSerialNumberHistory(ycBean, CommandCategory.STRING_CMD_VERSACOM_SERIAL));
    }

    private String getDeviceName(YCBean ycBean, String serialType) {
        String name = null;
        
        if ("xcom".equals(serialType)) {
            name = "Expresscom";
        } else if ("vcom".equals(serialType)) {
            name = "Versacom";
        } else if ("sa205".equals(serialType)) {
            name = "DCU-205";
        } else if ("sa305".equals(serialType)) {
            name = "DCU-305";
        } else {
            name = ycBean.getLiteYukonPao().getPaoName();
        }
        
        return name;
    }

    private Map<String, String> getAvailableCommands(YCBean ycBean, Object commandObject, YukonUserContext userContext) {
        LinkedHashMap<String, String> commands = new LinkedHashMap<String, String>();
        
        for(Object o : ycBean.getLiteDeviceTypeCommandsVector()) {
            LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)o;
            LiteCommand lc = commandDao.getCommand(ldtc.getCommandID());
            String commandString = StringEscapeUtils.escapeHtml(lc.getCommand());
            
            if(ldtc.isVisible() && ycBean.isAllowCommand(commandString, userContext.getYukonUser(), commandObject)) {
                commands.put(commandString, lc.getLabel());
            }
        }
        
        return commands;
    }

    private Vector<String> getSerialNumberHistory(YCBean ycBean, String serialType) {
        Vector<String> serialNumbers = ycBean.getSerialNumbers(serialType);
        
        if(serialNumbers == null) {
            serialNumbers = new Vector<String>();
        }
        
        return serialNumbers;
    }

    private String getMenuSelection(DeviceSearchCategory category) {
        if(category != null) {
            switch(category) {
                case CAP:
                    return "capcontrol|capcontrolsub";
                case LMGROUP:
                    return "lm|group";
                case IED:
                    return "devices|ied";
                case RTU:
                    return "devices|rtu";
                case TRANSMITTER:
                    return "devices|transmitter";
                case MCT:
                default:
                    return "devices|mct";
            }
        }
        return "";
    }

    private String getMenuSelection(String serialType) {
        if ("xcom".equals(serialType)) {
            return "lm|xcom";
        } else if ("vcom".equals(serialType)) {
            return "lm|vcom";
        } else if ("sa205".equals(serialType)) {
            return "lm|205serial";
        } else if ("sa305".equals(serialType)) {
            return "lm|305serial";
        }
        
        return "";
    }

    private List<DeviceSearchFilterBy> getQueryFilter(HttpServletRequest request, List<DeviceSearchFilterBy> filterByList) {
        List<DeviceSearchFilterBy> queryFilter = new ArrayList<DeviceSearchFilterBy>();
        for (DeviceSearchFilterBy filterBy : filterByList) {
            String filterValue = ServletRequestUtils.getStringParameter(request, filterBy.getName(), "").trim();
            if (!StringUtils.isBlank(filterValue)) {
                filterBy.setValue(filterValue);
                queryFilter.add(filterBy);
            }
        }
        
        return queryFilter;
    }
}
