package com.cannontech.web.commander;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
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
import com.cannontech.amr.device.search.model.DeviceSearchResultEntry;
import com.cannontech.amr.device.search.model.FieldValueStartsWith;
import com.cannontech.amr.device.search.model.FilterBy;
import com.cannontech.amr.device.search.model.OrderByField;
import com.cannontech.amr.device.search.model.SearchField;
import com.cannontech.amr.device.search.service.DeviceSearchService;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.model.DefaultItemsPerPage;
import com.cannontech.common.model.DefaultSort;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.SortingParameters;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.common.sort.SortableColumn;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.yc.bean.YCBean;

@Controller
@RequestMapping(value = "/*")
@CheckRoleProperty(YukonRoleProperty.ENABLE_WEB_COMMANDER)
public class CommanderController {
    
    @Autowired private DeviceSearchService deviceSearchService;
    @Autowired private CommandDao commandDao;
    @Autowired private YukonUserContextMessageSourceResolver messageResolver;
    
    @RequestMapping("select")
    public String select(
            @RequestParam(value = "category", defaultValue = "MCT") DeviceSearchCategory category,
            @DefaultItemsPerPage(25) PagingParameters paging,
            @DefaultSort(dir=Direction.desc, sort="NAME") SortingParameters sorting,
            ModelMap model, HttpServletRequest request, YukonUserContext userContext) {
        List<SearchField> fields = deviceSearchService.getFieldsForCategory(category);
        
        DeviceSearchField deviceSearchField = DeviceSearchField.valueOf(sorting.getSort());
        OrderByField orderBy = new OrderByField(deviceSearchField, sorting.getDirection() == Direction.desc);
        
        FilterBy categoryfilter = deviceSearchService.getFiltersForCategory(category);
        List<FilterBy> editableFilters = getQueryFilters(request, fields);
        
        List<FilterBy> searchFilters = new ArrayList<FilterBy>();
        searchFilters.add(categoryfilter);
        searchFilters.addAll(editableFilters);
        SearchResults<DeviceSearchResultEntry> deviceSearchResults = deviceSearchService.search(fields, searchFilters, 
                orderBy, paging.getStartIndex(), paging.getItemsPerPage());
        
        model.addAttribute("filters", editableFilters);
        model.addAttribute("fields", fields);
        model.addAttribute("category", category);
        model.addAttribute("deviceSearchResults", deviceSearchResults);
        model.addAttribute("menuSelection", getMenuSelection(category));
        
        // add columns
        MessageSourceAccessor accessor = messageResolver.getMessageSourceAccessor(userContext);
        Direction dir = sorting.getDirection();
        List<SortableColumn> columns = new ArrayList<>();
        for (SearchField searchField : fields) {
            if (searchField.isVisible()) {
                DeviceSearchField field = (DeviceSearchField) searchField;
                boolean active = field == deviceSearchField;
                String text = accessor.getMessage(field);
                SortableColumn col = SortableColumn.of(dir, active, text, field.name());
                columns.add(col);
            }
        }
        model.addAttribute("columns", columns);
        
        return "select.jsp";
    }

    @RequestMapping(value = "/command", method = RequestMethod.GET)
    public String command(@RequestParam(required = true) Integer deviceId, ModelMap model, YukonUserContext userContext,
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
        
        model.addAttribute("lPao", lPao);
        model.addAttribute("deviceId", deviceId);
        model.addAttribute("deviceName", getDeviceName(ycBean, null));
        model.addAttribute("meterDetailDisplayable", deviceSearchService.isMeterDetailsSupported(lPao));
        model.addAttribute("availableCommands", getAvailableCommands(ycBean, lPao, userContext));
        model.addAttribute("menuSelection", getMenuSelection(deviceSearchService.getDeviceCategory(deviceId)));
        model.addAttribute("redirectURI", String.format("%s?deviceId=%d", request.getRequestURI(), deviceId));
        model.addAttribute("category", DeviceSearchCategory.fromLiteYukonPAObject(lPao));

        mapCommonAttributes(ycBean, model);

        return "command.jsp";
    }

    @RequestMapping(value = "/command/{serialType}", method = RequestMethod.GET)
    public String commandSerialType( @PathVariable String serialType,
            @RequestParam(required = false) String serialNumber, ModelMap model, YukonUserContext userContext,
            HttpServletRequest request) {
        YCBean ycBean = setupYCBean(request.getSession(), userContext);

        ycBean.setLiteYukonPao(null);
        if("xcom".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.EXPRESSCOM_SERIAL.getDbString());
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("vcom".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.VERSACOM_SERIAL.getDbString());
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("sa205".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.SA205_SERIAL.getDbString());
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        } else if("sa305".equals(serialType)) {
            ycBean.setDeviceType(CommandCategory.SA305_SERIAL.getDbString());
            ycBean.setSerialNumber(ycBean.getDeviceType(), serialNumber);
        }
        
        model.addAttribute("deviceName", getDeviceName(ycBean, serialType));
        model.addAttribute("serialType", serialType);
        if(serialNumber != null && !serialNumber.isEmpty()) {
            model.addAttribute("serialNumber", serialNumber);
        }
        model.addAttribute("availableRoutes", ycBean.getValidRoutes());
        model.addAttribute("availableCommands", getAvailableCommands(ycBean, serialType, userContext));
        model.addAttribute("menuSelection", getMenuSelection(serialType));
        
        mapCommonAttributes(ycBean, model);
        
        String redirectURI;
        if(serialNumber != null && !serialNumber.isEmpty()) {
            redirectURI = ServletUtil.addParameters(request.getRequestURI(), "serialNumber", serialNumber);
        } else {
            redirectURI = request.getRequestURI();
        }
        model.addAttribute("redirectURI", redirectURI);
        
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

    private void mapCommonAttributes(YCBean ycBean, ModelMap model) {
        model.addAttribute("deviceType", ycBean.getDeviceType());
        model.addAttribute("currentCommand", ycBean.getCommandString().replaceAll("noqueue", "").trim());
        model.addAttribute("resultText", ycBean.getResultText());
        model.addAttribute("deviceHistory", ycBean.getDeviceHistory());
        model.addAttribute("sa205History", getSerialNumberHistory(ycBean, CommandCategory.SA205_SERIAL.getDbString()));
        model.addAttribute("sa305History", getSerialNumberHistory(ycBean, CommandCategory.SA305_SERIAL.getDbString()));
        model.addAttribute("xcomHistory", getSerialNumberHistory(ycBean, CommandCategory.EXPRESSCOM_SERIAL.getDbString()));
        model.addAttribute("vcomHistory", getSerialNumberHistory(ycBean, CommandCategory.VERSACOM_SERIAL.getDbString()));
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
        
        for(Object o : ycBean.getLiteDeviceTypeCommands()) {
            LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)o;
            LiteCommand lc = commandDao.getCommand(ldtc.getCommandId());
            String commandString = StringEscapeUtils.escapeHtml4(lc.getCommand());
            
            if(ldtc.isVisible() && ycBean.isAllowCommand(commandString, userContext.getYukonUser(), commandObject)) {
                commands.put(commandString, lc.getLabel());
            }
        }
        
        return commands;
    }

    private List<String> getSerialNumberHistory(YCBean ycBean, String serialType) {
        List<String> serialNumbers = ycBean.getSerialNumbers(serialType);
        
        if(serialNumbers == null) {
            serialNumbers = Collections.emptyList();
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
        return StringUtils.EMPTY;
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
        
        return StringUtils.EMPTY;
    }

    private List<FilterBy> getQueryFilters(HttpServletRequest request, List<SearchField> fields) {
        List<FilterBy> queryFilter = new ArrayList<FilterBy>();
        for (SearchField field : fields) {
            if(field.isVisible()) {
                FieldValueStartsWith filter = new FieldValueStartsWith(field);
                String filterValue = ServletRequestUtils.getStringParameter(request, field.getFieldName(), "").trim();
                if (!StringUtils.isBlank(filterValue)) {
                    filter.setFilterValue(filterValue);
                }
                queryFilter.add(filter);
            }
        }
        
        return queryFilter;
    }
}
