package com.cannontech.web.contextualMenu;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cannontech.common.bulk.collection.device.DeviceCollectionFactory;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.contextualMenu.model.menu.DeviceMenu;
import com.cannontech.web.contextualMenu.model.menu.Menu;
import com.cannontech.web.contextualMenu.model.menuEntry.BaseMenuEntry;
import com.cannontech.web.contextualMenu.model.menuEntry.DeviceCollectionMenuEntry;
import com.cannontech.web.contextualMenu.model.menuEntry.DeviceMenuEntry;
import com.cannontech.web.contextualMenu.model.menuEntry.MenuEntry;
import com.cannontech.web.contextualMenu.model.menuEntry.MenuSeparator;
import com.cannontech.web.contextualMenu.model.menuEntry.SingleDeviceMenuEntry;
import com.google.common.collect.Maps;

@Controller
@RequestMapping("/*")
public class ContextualMenuController {
    
    @Autowired private PaoDao paoDao;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DeviceCollectionFactory deviceCollectionFactory;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Autowired private List<Menu> menus;
    private Map<String, Menu> menuBeanMap;
    
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody JSONArray list(YukonUserContext userContext, HttpServletRequest request)
            throws ServletRequestBindingException {
        String menuBeanId = request.getParameter("menuId"); // not including the word "Bean" as a security measure, i.e. menuBeanId
        Menu menu = menuBeanMap.get(menuBeanId);
        Map<String, String> inputParams = ServletUtil.getParameterMap(request);
        JSONArray array = null;

        if (menu != null && menu instanceof DeviceMenu) {
            array = getDeviceMenu((DeviceMenu) menu, inputParams, request, userContext);
        } else {
            throw new RuntimeException("Couldn't find menu with beanId " + menuBeanId);
        }
        
        return array;
    }
    
    private JSONArray getDeviceMenu(DeviceMenu deviceMenu, Map<String, String> inputParams,
                                    HttpServletRequest request, YukonUserContext userContext) {
        LiteYukonUser yukonUser = userContext.getYukonUser();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        List<MenuEntry> menuEntries = deviceMenu.getMenuEntries();
        JSONArray array = new JSONArray();
        
        for (MenuEntry menuEntry : menuEntries) {
            String url = null;
            if (menuEntry instanceof MenuSeparator) {
                addMenuSeperator(array);
                continue;
            } else if (menuEntry instanceof SingleDeviceMenuEntry) {
                SingleDeviceMenuEntry singleDeviceMenuEntry = (SingleDeviceMenuEntry) menuEntry;
                url = singleDeviceMenuEntry.getUrl(deviceMenu.getCollectionCategory(), inputParams);
            } else if (menuEntry instanceof DeviceCollectionMenuEntry) {
                DeviceCollectionMenuEntry deviceCollectionMenuEntry = (DeviceCollectionMenuEntry) menuEntry;
                url = deviceCollectionMenuEntry.getUrl(deviceMenu.getCollectionCategory(), inputParams);
            } else {
                throw new RuntimeException("DeviceMenu can only contain MenuEntry elements of type MenuSeperator, SingleDeviceMenuEntry, and DeviceCollectionMenuEntry");
            }
            
            DeviceMenuEntry deviceMenuEntry = (DeviceMenuEntry) menuEntry;
            checkMenuEntrySupports(deviceMenu, deviceMenuEntry, inputParams);
            if (!canViewMenuEntry(deviceMenuEntry, yukonUser)) continue;
            
            String text = accessor.getMessage(deviceMenuEntry.getFormatKey());
            url = ServletUtil.createSafeUrl(request, url);
            url = StringEscapeUtils.escapeHtml(url);
            
            addMenuEntry(array, url, text);
        }
        return array;
    }
    
    private <T extends BaseMenuEntry> boolean canViewMenuEntry(T menuEntry, LiteYukonUser yukonUser) {
        if (menuEntry.getRequiredRole() != null
                && !rolePropertyDao.checkRole(menuEntry.getRequiredRole(), yukonUser))
            return false;
        if (menuEntry.getRequiredRoleProperties() != null
                && !rolePropertyDao.checkAllProperties(yukonUser, menuEntry.getRequiredRoleProperties()))
            return false;
        return true;
    }
    
    private void checkMenuEntrySupports(Menu menu, DeviceMenuEntry menuEntry, Map<String, String> inputParams) {
        Integer deviceId = menuEntry.getDeviceId(menu.getCollectionCategory(), inputParams);
        if (deviceId != null) {
            YukonPao yukonPao = paoDao.getYukonPao(deviceId);
            if (!menuEntry.supports(yukonPao.getPaoIdentifier())) {
                throw new RuntimeException("Device " + yukonPao + " does not support being in a SingleDeviceMenuEntry");
            }
        }
    }
    
    private void addMenuEntry(JSONArray jsonArray, String url, String text, boolean divider) {
        JSONObject obj = new JSONObject();
        obj.put("url", url);
        obj.put("text", text);
        obj.put("divider", divider);
        jsonArray.add(obj);
    }
    
    private void addMenuEntry(JSONArray jsonArray, String url, String text) {
        addMenuEntry(jsonArray, url, text, false);
    }
    
    private void addMenuSeperator(JSONArray jsonArray) {
        addMenuEntry(jsonArray, StringUtils.EMPTY, StringUtils.EMPTY, true);
    }
    
    @PostConstruct
    private void init() {
        menuBeanMap = Maps.newHashMapWithExpectedSize(menus.size());
        for (Menu menu : menus) {
            menuBeanMap.put(menu.getBeanName(), menu);
        }
    }
}