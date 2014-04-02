package com.cannontech.web.contextualMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
import com.cannontech.web.contextualMenu.model.menu.ContextualMenu;
import com.cannontech.web.contextualMenu.model.menu.Menu;
import com.cannontech.web.contextualMenu.model.menuEntry.DeviceCollectionMenuAction;
import com.cannontech.web.contextualMenu.model.menuEntry.DeviceMenuAction;
import com.cannontech.web.contextualMenu.model.menuEntry.MenuEntry;
import com.cannontech.web.contextualMenu.model.menuEntry.MenuSeparator;
import com.cannontech.web.contextualMenu.model.menuEntry.SingleDeviceMenuAction;
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
    
    @RequestMapping(value="list", method = RequestMethod.GET)
    public @ResponseBody List<Object> list(YukonUserContext context, HttpServletRequest req) {
        
        String menuBeanId = req.getParameter("menuId"); // not including the word "Bean" as a security measure, i.e. menuBeanId
        Menu menu = menuBeanMap.get(menuBeanId);
        
        List<Object> array = null;

        if (menu instanceof ContextualMenu) {
            array = getMenuJsonArray((ContextualMenu) menu, req, context);
        } else {
            throw new RuntimeException("Couldn't find menu with beanId " + menuBeanId);
        }
        
        return array;
    }
    
    private List<Object> getMenuJsonArray(ContextualMenu menu, HttpServletRequest req, YukonUserContext context) {
        
        LiteYukonUser user = context.getYukonUser();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(context);
        List<MenuEntry> menuEntries = menu.getMenuEntries();
        List<Object> array = new ArrayList<>();

        for (MenuEntry menuEntry : menuEntries) {
            String url = null;
            if (menuEntry instanceof MenuSeparator) {
                array.add(getMenuEntryJson(StringUtils.EMPTY, StringUtils.EMPTY, true));
                continue;
            } else if (menuEntry instanceof SingleDeviceMenuAction) {
                SingleDeviceMenuAction singleDeviceMenuEntry = (SingleDeviceMenuAction) menuEntry;
                url = singleDeviceMenuEntry.getUrl(menu.getCollectionCategory(), req);
            } else if (menuEntry instanceof DeviceCollectionMenuAction) {
                DeviceCollectionMenuAction deviceCollectionMenuEntry = (DeviceCollectionMenuAction) menuEntry;
                url = deviceCollectionMenuEntry.getUrl(menu.getCollectionCategory(), req);
            } else {
                throw new RuntimeException("DeviceMenu can only contain MenuEntry elements of type MenuSeperator, SingleDeviceMenuAction, and DeviceCollectionMenuAction");
            }
            
            DeviceMenuAction deviceMenuEntry = (DeviceMenuAction) menuEntry;
            checkMenuEntrySupports(menu, deviceMenuEntry, ServletUtil.getParameterMap(req));
            if (canViewMenuEntry(deviceMenuEntry, user)) {
                String text = accessor.getMessage(deviceMenuEntry.getFormatKey());
                array.add(getMenuEntryJson(url, text, false));
            }
        }
        return array;
    }
    
    private <T extends DeviceMenuAction> boolean canViewMenuEntry(T menuEntry, LiteYukonUser yukonUser) {
        if (menuEntry.getRequiredRole() != null
                && !rolePropertyDao.checkRole(menuEntry.getRequiredRole(), yukonUser))
            return false;
        if (menuEntry.getRequiredRoleProperties() != null
                && !rolePropertyDao.checkAllProperties(yukonUser, menuEntry.getRequiredRoleProperties()))
            return false;
        return true;
    }
    
    private void checkMenuEntrySupports(Menu menu, DeviceMenuAction menuEntry, Map<String, String> inputParams) {
        Integer deviceId = menuEntry.getDeviceId(menu.getCollectionCategory(), inputParams);
        if (deviceId != null) {
            YukonPao yukonPao = paoDao.getYukonPao(deviceId);
            if (!menuEntry.supports(yukonPao.getPaoIdentifier())) {
                throw new RuntimeException("Device " + yukonPao + " does not support being in a SingleDeviceMenuEntry");
            }
        }
    }

    private Map<String, Object> getMenuEntryJson(String url, String text, boolean divider) {
        Map<String, Object> obj = Maps.newHashMapWithExpectedSize(3);
        obj.put("url", url);
        obj.put("text", text);
        obj.put("divider", divider);
        return obj;
    }

    @PostConstruct
    private void init() {
        menuBeanMap = Maps.newHashMapWithExpectedSize(menus.size());
        for (Menu menu : menus) {
            menuBeanMap.put(menu.getBeanName(), menu);
        }
    }
}