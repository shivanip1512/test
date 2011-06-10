package com.cannontech.web.layout;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBooleanKeysEnum;
import com.cannontech.web.taglib.JsLibrary;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.CommonModuleBuilder;
import com.cannontech.web.menu.LayoutSkinEnum;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.PageInfo;
import com.cannontech.web.menu.renderer.LeftSideMenuRenderer;
import com.cannontech.web.menu.renderer.MenuRenderer;
import com.cannontech.web.menu.renderer.StandardMenuRenderer;
import com.cannontech.web.taglib.StandardPageInfo;
import com.cannontech.web.taglib.StandardPageTag;
import com.cannontech.web.taglib.Writable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@Controller
public class LayoutController {
    private RolePropertyDao rolePropertyDao;
    private CommonModuleBuilder moduleBuilder;
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private PageDetailProducer pageDetailProducer;
    private ConfigurationSource configurationSource;
    
    private List<String> layoutScriptFiles;
    
    @PostConstruct
    public void initialize() {
        Builder<String> builder = ImmutableList.builder();
    	builder.add(JsLibrary.PROTOTYPE.getPath());
    	builder.add(JsLibrary.YUKON_UI.getPath());
        builder.add("/JavaScript/yukonGeneral.js");
        if (configurationSource.getBoolean(MasterConfigBooleanKeysEnum.DEVELOPMENT_MODE)) {
            builder.add("/JavaScript/basicLogger.js");
        } else {
            builder.add("/JavaScript/basicLoggerStub.js");
        }
        builder.add("/JavaScript/CtiMenu.js");
        builder.add("/JavaScript/dataUpdater.js");
        builder.add(JsLibrary.SCRIPTACULOUS.getPath());
        builder.add("/JavaScript/simpleCookies.js");
        builder.add("/JavaScript/alert.js");
        builder.add("/JavaScript/javaWebStartLauncher.js");
        layoutScriptFiles = builder.build();
    }
    
    @RequestMapping("/")
    public String display(HttpServletRequest request, HttpServletResponse response, ModelMap map, Locale locale) throws JspException {

        // get data passed over in attributes
        final BodyContent bodyContent = StandardPageTag.getBodyContent(request);
        map.addAttribute("bodyContent", new Writable() {
            public void write(Writer out) throws IOException {
                bodyContent.writeOut(out);
            }
        });
        
        StandardPageInfo tagInfo = StandardPageTag.getStandardPageInfo(request);
        map.addAttribute("info", tagInfo);
        
        final YukonUserContext userContext = YukonUserContextUtils.getYukonUserContext(request);
        final MessageSourceAccessor messageSourceAccessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        ModuleBase moduleBase = getModuleBase(tagInfo.getModuleName());
        map.addAttribute("module", moduleBase);
        
        PageInfo pageInfo = moduleBase.getPageInfo(tagInfo.getPageName());

        PageDetail pageDetailTemp;
        if (pageInfo != null) {
            pageDetailTemp = pageDetailProducer.render(pageInfo, request, messageSourceAccessor);
        } else {
            // create dummy page detail for pre-2010 pages
            pageDetailTemp = new PageDetail();
            pageDetailTemp.setBreadCrumbText("");
            if (StringUtils.isNotBlank(tagInfo.getTitle())) {
                pageDetailTemp.setPageTitle(tagInfo.getTitle());
            } else if (StringUtils.isNotBlank(tagInfo.getPageName())){
                try {
                    String pageTitleKey = "yukon.web.modules." + tagInfo.getModuleName() + "." + tagInfo.getPageName() + ".pageTitle";
                    pageDetailTemp.setPageTitle(messageSourceAccessor.getMessage(pageTitleKey));
                } catch (NoSuchMessageException e) {
                    pageDetailTemp.setPageTitle(messageSourceAccessor.getMessageWithDefault("yukon.web.defaults.pageTitle", ""));
                }
                
            }
        }
        
        final PageDetail pageDetail = pageDetailTemp;
        
        map.addAttribute("pageDetail", pageDetail);
        
        List<String> moduleConfigCssList = new ArrayList<String>(moduleBase.getCssFiles());
        removeDuplicates(moduleConfigCssList);
        map.addAttribute("moduleConfigCss", moduleConfigCssList);
        
        List<String> innerContentCssList = new ArrayList<String>(tagInfo.getCssFiles());
        removeDuplicates(innerContentCssList);
        map.addAttribute("innerContentCss", innerContentCssList);
        
        LiteYukonUser user = ServletUtil.getYukonUser(request);
        String cssLocations = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.STD_PAGE_STYLE_SHEET, user);
        cssLocations = StringUtils.defaultString(cssLocations,"");
        String[] cssLocationArray = cssLocations.split("\\s*,\\s*");
        List<String> loginGroupCssList = new ArrayList<String>(Arrays.asList(cssLocationArray));
        removeDuplicates(loginGroupCssList); 
        map.addAttribute("loginGroupCss", loginGroupCssList);
        
        Set<String> finalScriptList = new LinkedHashSet<String>();
        
        finalScriptList.addAll(layoutScriptFiles);

        // get script files declared in the module
        finalScriptList.addAll(moduleBase.getScriptFiles());
        finalScriptList.addAll(tagInfo.getScriptFiles());
        map.addAttribute("javaScriptFiles", finalScriptList);
        
        LayoutSkinEnum skin = moduleBase.getSkin();
        
        // handle new and old methods for specifying menu (but not both)
        boolean showNewMenu = false;
        if (pageInfo != null) {
            showNewMenu = pageInfo.isRenderMenu();
        }
        boolean showOldMenu = tagInfo.isShowMenu();
        boolean showMenu = false;
        if (showOldMenu && showNewMenu) {
            throw new IllegalStateException("Menu cannot be specified on JSP and in module_config.xml");
        }
        
        String menuSelection = null;
        if (showOldMenu) {
            menuSelection = tagInfo.getMenuSelection();
            showMenu = true;
        } else if (showNewMenu) {
            menuSelection = pageInfo.getMenuSelection();
            showMenu = true;
        }
        
        if (showMenu) {
            // setup menu
            final MenuRenderer menuRenderer;
            if (skin.isLeftSideMenu()) {
                menuRenderer = new LeftSideMenuRenderer(request, moduleBase, messageSourceResolver);
            } else {
                menuRenderer = new StandardMenuRenderer(request, moduleBase, pageInfo, messageSourceResolver);
            }
            menuRenderer.setMenuSelection(menuSelection);
            // if bread crumbs were specified within the JSP, use them (old style)
            String breadCrumbs = tagInfo.getBreadCrumbs();
            if (breadCrumbs == null) {
                // otherwise get the from the PageDetail object (new style)
                breadCrumbs = pageDetail.getBreadCrumbText();
            }
            menuRenderer.setBreadCrumb(breadCrumbs);
            menuRenderer.setHomeUrl("/home");
            map.addAttribute("menuRenderer", new Writable() {
                public void write(Writer out) throws IOException {
                    menuRenderer.renderMenu(out);
                }
            });
        }
        
        boolean showContextualNavigation = pageInfo != null && pageInfo.isShowContextualNavigation();
        map.addAttribute("showContextualNavigation", showContextualNavigation);
        if (showContextualNavigation) {
            map.addAttribute("contextualNavigationMenu", new Writable() {
                @Override
                public void write(Writer out) throws IOException {
                    out.append(pageDetail.getRenderContextualNavigation());
                }
            });
        }
        
        map.addAttribute("currentTime", new Date());
        
        // prevent Firefox "back-forward cache" http://developer.mozilla.org/en/docs/Using_Firefox_1.5_caching
        response.addHeader("Cache-Control", "no-store");   
        
        return skin.getViewName();
    }
    
    @ModelAttribute("yukonVersion")
    public String getYukonVersion() {
        return VersionTools.getYUKON_VERSION();
    }
    
    @ModelAttribute("buildInfo")
    public String getyukonBuild() {
        Map<String, String> buildInfo = VersionTools.getBuildInfo();
        if (buildInfo.containsKey("JOB_NAME") && buildInfo.containsKey("BUILD_NUMBER")) {
            return "<a href=\"http://hudson.cooperpowereas.net/job/" + buildInfo.get("JOB_NAME") + "/" 
            + buildInfo.get("BUILD_NUMBER") + "\">" + buildInfo.get("BUILD_NUMBER") + "</a>";
        } else {
            return "undefined";
        }
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
    public void setMessageSourceResolver(YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setPageDetailProducer(PageDetailProducer pageDetailProducer) {
        this.pageDetailProducer = pageDetailProducer;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
}
