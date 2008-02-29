package com.cannontech.web.layout;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.CommonModuleBuilder;
import com.cannontech.web.menu.MenuRenderer;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.StandardMenuRenderer;
import com.cannontech.web.taglib.StandardPageInfo;
import com.cannontech.web.taglib.StandardPageTag;
import com.cannontech.web.taglib.Writable;

@Controller
public class LayoutController {
    private AuthDao authDao;
    private CommonModuleBuilder moduleBuilder;
    private YukonUserContextMessageSourceResolver messageSourceResolver;

    private List<String> layoutScriptFiles = new ArrayList<String>(3);
    
    {
        layoutScriptFiles.add("/JavaScript/prototype.js");
        layoutScriptFiles.add("/JavaScript/yukonGeneral.js");
        layoutScriptFiles.add("/JavaScript/CtiMenu.js");
        layoutScriptFiles.add("/JavaScript/dataUpdater.js");
        layoutScriptFiles.add("/JavaScript/scriptaculous/effects.js");
        layoutScriptFiles.add("/JavaScript/simpleCookies.js");
        layoutScriptFiles.add("/JavaScript/alert.js");
        layoutScriptFiles.add("/JavaScript/javaWebStartLauncher.js");
    }

    @RequestMapping("/")
    public String display(HttpServletRequest request, HttpServletResponse response, ModelMap map, Locale locale) throws JspException {
        YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        // get data passed over in attributes
        final BodyContent bodyContent = StandardPageTag.getBodyContent(request);
        map.addAttribute("bodyContent", new Writable() {
            public void write(Writer out) throws IOException {
                bodyContent.writeOut(out);
            }
        });
        
        StandardPageInfo info = StandardPageTag.getStandardPageInfo(request);
        map.addAttribute("info", info);
        
        ModuleBase moduleBase = getModuleBase(info.getModuleName());
        map.addAttribute("module", moduleBase);
        
        List<String> moduleConfigCssList = moduleBase.getCssFiles();
        removeDuplicates(moduleConfigCssList);
        map.addAttribute("moduleConfigCss", moduleConfigCssList);
        
        List<String> innerContentCssList = info.getCssFiles();
        removeDuplicates(innerContentCssList);
        map.addAttribute("innerContentCss", innerContentCssList);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        String cssLocations = authDao.getRolePropertyValue(user, WebClientRole.STD_PAGE_STYLE_SHEET);
        cssLocations = StringUtils.defaultString(cssLocations,"");
        String[] cssLocationArray = cssLocations.split("\\s*,\\s*");
        List<String> loginGroupCssList = new ArrayList<String>(Arrays.asList(cssLocationArray));
        removeDuplicates(loginGroupCssList); 
        map.addAttribute("loginGroupCss", loginGroupCssList);
        
        Set<String> finalScriptList = new LinkedHashSet<String>();
        
        finalScriptList.addAll(layoutScriptFiles);

        // get script files declared in the module
        finalScriptList.addAll(moduleBase.getScriptFiles());
        finalScriptList.addAll(info.getScriptFiles());
        map.addAttribute("javaScriptFiles", finalScriptList);
        
        if (info.isShowMenu()) {
            // setup menu
            MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
            final MenuRenderer menuRenderer = new StandardMenuRenderer(request, moduleBase, messageSourceAccessor);

            menuRenderer.setMenuSelection(info.getMenuSelection());
            menuRenderer.setBreadCrumb(info.getBreadCrumbs());
            map.addAttribute("menuRenderer", new Writable() {
                public void write(Writer out) throws IOException {
                    menuRenderer.renderMenu(out);
                }
            });
        }   
        
        // prevent Firefox "back-forward cache" http://developer.mozilla.org/en/docs/Using_Firefox_1.5_caching
        response.addHeader("Cache-Control", "no-store");                                                                                                

        return moduleBase.getSkin();
    }
    
    @ModelAttribute("yukonVersion")
    public String getYukonVersion() {
        return VersionTools.getYUKON_VERSION();
    }
    
    private ModuleBase getModuleBase(String moduleName) throws JspException {
        ModuleBase moduleBase = moduleBuilder.getModuleBase(moduleName);

        return moduleBase;
    }
    
    private void removeDuplicates(List<String> list) {
        Set<String> set = new LinkedHashSet<String>();
        
        for (String file : list) {
            // we want the order to reflect the position of the last reference to the file
            file = StringUtils.strip(file);
            if (StringUtils.isNotBlank(file)) {
                set.remove(file);
                set.add(file);
            }
        }
        
        list.clear();
        list.addAll(set);
    }
    
    @Autowired
    public void setModuleBuilder(CommonModuleBuilder moduleBuilder) {
        this.moduleBuilder = moduleBuilder;
    }
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
        this.authDao = authDao;
    }
    
    @Autowired
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

}
