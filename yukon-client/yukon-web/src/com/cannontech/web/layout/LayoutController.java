package com.cannontech.web.layout;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.tagext.BodyContent;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.version.VersionTools;
import com.cannontech.core.dao.EnergyCompanyNotFoundException;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.CssLibrary;
import com.cannontech.web.JsLibrary;
import com.cannontech.web.Writable;
import com.cannontech.web.menu.CommonModuleBuilder;
import com.cannontech.web.menu.LayoutSkinEnum;
import com.cannontech.web.menu.Module;
import com.cannontech.web.menu.PageInfo;
import com.cannontech.web.menu.renderer.LeftSideMenuRenderer;
import com.cannontech.web.menu.renderer.StandardMenuRenderer;
import com.cannontech.web.taglib.StandardPageInfo;
import com.cannontech.web.taglib.StandardPageTag;
import com.cannontech.web.user.service.UserPreferenceService;
import com.cannontech.web.util.WebUtilityService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

@Controller
public class LayoutController {
    
    @Autowired private CommonModuleBuilder moduleBuilder;
    @Autowired private ConfigurationSource configSource;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private HttpExpressionLanguageResolver expressionLanguageResolver;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private PageDetailProducer pageDetailProducer;
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StandardMenuRenderer stdMenuRender;
    @Autowired private UserPreferenceService prefService;
    @Autowired private YukonUserContextMessageSourceResolver messageSourceResolver;
    @Autowired private YukonUserDao userDao;
    @Autowired private WebUtilityService webUtilService;
    
    private List<String> libraryScriptFiles;
    private List<String> yukonScriptFiles;
    private List<String> standardCssFiles;
    
    @PostConstruct
    public void initialize() {
        
        boolean dev = configSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE);
        
        /** JS ORDER MATTERS! **/
        Builder<String> b = ImmutableList.builder();
        
        b.add(JsLibrary.DEBUGGER.getPath(dev));
        b.add(JsLibrary.MODERNIZR.getPath(dev));
        b.add(JsLibrary.MOMENT.getPath(dev));
        b.add(JsLibrary.MOMENT_TZ.getPath(dev));
        
        b.add(JsLibrary.JQUERY.getPath(dev) );
        b.add(JsLibrary.JQUERY_UI.getPath(dev));
        b.add(JsLibrary.JQUERY_CHECK_ALL.getPath(dev));
        b.add(JsLibrary.JQUERY_FORM.getPath(dev));
        b.add(JsLibrary.JQUERY_PLACEHOLDER.getPath(dev));
        b.add(JsLibrary.JQUERY_TIPSY.getPath(dev));
        b.add(JsLibrary.JQUERY_SPECTRUM.getPath(dev));
        b.add(JsLibrary.JQUERY_CHOSEN.getPath(dev));
        b.add(JsLibrary.JQUERY_ACTUAL.getPath(dev));
        b.add(JsLibrary.JQUERY_SCROLLTO.getPath(dev));
        b.add(JsLibrary.JQUERY_COOKIE.getPath(dev));
        b.add(JsLibrary.JQUERY_FLOTCHARTS.getPath(dev));
        b.add(JsLibrary.JQUERY_FLOTCHARTS_SELECTION.getPath(dev));
        b.add(JsLibrary.JQUERY_FLOTCHARTS_AXIS_LABEL.getPath(dev));
        b.add(JsLibrary.JQUERY_FLOTCHARTS_RESIZE.getPath(dev));
        b.add(JsLibrary.JQUERY_FLOTCHARTS_TIME.getPath(dev));
        b.add(JsLibrary.JQUERY_SCROLL_TABLE_BODY.getPath(dev));
        libraryScriptFiles = b.build();
        
        b = ImmutableList.builder();
        // Add Yukon common libraries
        b.add(JsLibrary.YUKON.getPath(dev));
        b.add(JsLibrary.YUKON_UI.getPath(dev));
        b.add(JsLibrary.YUKON_HUB.getPath(dev));
        b.add(JsLibrary.YUKON_COOKIE.getPath(dev));
        b.add(JsLibrary.YUKON_ALERTS.getPath(dev));
        b.add(JsLibrary.YUKON_CONFIRM.getPath(dev));
        b.add(JsLibrary.YUKON_UI_UTIL.getPath(dev));
        b.add(JsLibrary.YUKON_UI_MOVABLES.getPath(dev));
        b.add(JsLibrary.YUKON_UPDATER.getPath(dev));
        b.add(JsLibrary.YUKON_DROPDOWN.getPath(dev));
        b.add(JsLibrary.YUKON_TOOLTIP.getPath(dev));
        b.add(JsLibrary.YUKON_ANALYTICS.getPath(dev));
        b.add(JsLibrary.YUKON_FAVORITES.getPath(dev));
        b.add(JsLibrary.YUKON_FLOTCHARTS.getPath(dev));
        b.add(JsLibrary.YUKON_SIMPLE_POPUPS.getPath(dev));
        b.add(JsLibrary.YUKON_PICKER.getPath(dev));
        b.add(JsLibrary.YUKON_DEVICE_GROUP_PICKER.getPath(dev));
        b.add(JsLibrary.YUKON_HISTORICAL_READINGS.getPath(dev));
        yukonScriptFiles = b.build();
        
        /** CSS ORDER MATTERS! **/
        b = ImmutableList.builder();
        b.add(CssLibrary.NORMALIZE.getPath());
        b.add(CssLibrary.BOOTSTRAP.getPath());
        b.add(CssLibrary.ANIMATE.getPath());
        b.add(CssLibrary.LAYOUT.getPath());
        b.add(CssLibrary.YUKON.getPath());
        b.add(CssLibrary.BUTTONS.getPath());
        b.add(CssLibrary.ICONS.getPath());
        b.add(dev ? CssLibrary.JQUERY_UI.getPath() : CssLibrary.JQUERY_UI_MIN.getPath());
        b.add(CssLibrary.TIPSY.getPath());
        b.add(CssLibrary.SPECTRUM.getPath());
        b.add(CssLibrary.CHOSEN.getPath());
        b.add(CssLibrary.FLOTCHARTS.getPath());
        standardCssFiles = b.build();
    }
    
    /** StandardPageTag forwards to here! */
    @RequestMapping("/")
    public String display(final HttpServletRequest request, HttpServletResponse response, 
            ModelMap model, YukonUserContext userContext) {
        
        LiteYukonUser user = userContext.getYukonUser();
        MessageSourceAccessor accessor = messageSourceResolver.getMessageSourceAccessor(userContext);
        
        model.addAttribute("homeUrl", webUtilService.getHomeUrl(request, user));
        
        // get data passed over - in attributes
        final BodyContent bodyContent = StandardPageTag.getBodyContent(request);
        
        // create a callback for writing out the body (as opposed to just putting a string of the body in the model)
        model.addAttribute("bodyContent", new Writable() {
            @Override
            public void write(Writer out) throws IOException {
                bodyContent.writeOut(out);
            }
        });
        
        final StandardPageInfo tagInfo = StandardPageTag.getStandardPageInfo(request);
        model.addAttribute("info", tagInfo);
        
        // used for determining page title etc...
        final Module moduleBase = getModuleBase(tagInfo.getModuleName());
        model.addAttribute("module", moduleBase);
        
        // parse the module_config.xml and figure out our hierarchy for menus etc...
        PageInfo pageInfo = moduleBase.getPageInfo(tagInfo.getPageName());
        
        PageDetail pageDetailTemp;
        if (pageInfo != null) {
            model.addAttribute("canFavorite", !pageInfo.isHideFavorite());
            List<String> resolvedLabelArgs =
                expressionLanguageResolver.resolveElExpressions(pageInfo.getLabelArgumentExpressions(), request);
            String labelArgs = com.cannontech.common.util.StringUtils.listAsJsSafeString(resolvedLabelArgs);
            model.addAttribute("labelArgs", labelArgs);
            pageDetailTemp = pageDetailProducer.render(pageInfo, request, accessor);
        } else {
            // create dummy page detail for pre-2010 pages
            // TODO  consider delegating this functionality to the PageInfo object itself: new PageDetail(tagInfo)
            pageDetailTemp = new PageDetail();
            pageDetailTemp.setBreadCrumbText("");
            if (StringUtils.isNotBlank(tagInfo.getTitle())) {
                pageDetailTemp.setPageTitle(tagInfo.getTitle());
            } else if (StringUtils.isNotBlank(tagInfo.getPageName())) {
                try {
                    String pageTitleKey =
                        "yukon.web.modules." + tagInfo.getModuleName() + "." + tagInfo.getPageName() + ".pageTitle";
                    pageDetailTemp.setPageTitle(accessor.getMessage(pageTitleKey));
                } catch (NoSuchMessageException e) {
                    pageDetailTemp.setPageTitle(accessor.getMessageWithDefault(
                        "yukon.common.pageTitle", ""));
                }
                
            }
        }
        
        final PageDetail pageDetail = pageDetailTemp;
        
        model.addAttribute("pageDetail", pageDetail);
        
        model.addAttribute("servletPath", tagInfo.getServletPath());
        
        model.addAttribute("standardCssFiles", standardCssFiles);
        
        List<String> moduleConfigCssList = new ArrayList<String>(moduleBase.getCssFiles());
        removeDuplicates(moduleConfigCssList);
        model.addAttribute("moduleConfigCss", moduleConfigCssList);
        
        List<String> innerContentCssList = new ArrayList<String>(tagInfo.getCssFiles());
        removeDuplicates(innerContentCssList);
        model.addAttribute("innerContentCss", innerContentCssList);
        
        String cssLocations = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.STD_PAGE_STYLE_SHEET, user);
        cssLocations = StringUtils.defaultString(cssLocations, "");
        String[] cssLocationArray = cssLocations.split("\\s*,\\s*");
        List<String> loginGroupCssList = new ArrayList<String>(Arrays.asList(cssLocationArray));
        removeDuplicates(loginGroupCssList);
        model.addAttribute("loginGroupCss", loginGroupCssList);
        
        model.addAttribute("libraryScriptFiles", libraryScriptFiles);
        
        Set<String> finalScriptList = new LinkedHashSet<String>();
        
        finalScriptList.addAll(yukonScriptFiles);
        
        // get script files declared in the module
        finalScriptList.addAll(moduleBase.getScriptFiles());
        finalScriptList.addAll(tagInfo.getScriptFiles());
        model.addAttribute("yukonScriptFiles", finalScriptList);
        
        LayoutSkinEnum skin = moduleBase.getSkin();
        
        // setup menu
        if (skin.isLeftSideMenu()) {
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
                final LeftSideMenuRenderer menuRenderer =
                    new LeftSideMenuRenderer(request, moduleBase, messageSourceResolver);
                menuRenderer.setMenuSelection(menuSelection);
                // if bread crumbs were specified within the JSP, use them (old style)
                String breadCrumbs = tagInfo.getBreadCrumbs();
                if (breadCrumbs == null) {
                    // otherwise get the from the PageDetail object (new style)
                    breadCrumbs = pageDetail.getBreadCrumbText();
                }
                menuRenderer.setBreadCrumb(breadCrumbs);
                menuRenderer.setHomeUrl(webUtilService.getHomeUrl(request, user));
                model.addAttribute("menuRenderer", new Writable() {
                    @Override
                    public void write(Writer out) throws IOException {
                        menuRenderer.renderMenu(out);
                    }
                });
            }
        } else {
            model.addAttribute("menuRenderer", new Writable() {
                @Override
                public void write(Writer out) throws IOException {
                    try {
                        stdMenuRender.renderMenu(request, out);
                    } catch (JDOMException e) {
                        throw new IOException("Problem parsing menu_config.xml for menus.", e);
                    }
                }
            });

            model.addAttribute("bcRenderer", new Writable() {
                @Override
                public void write(Writer out) throws IOException {
                    String breadCrumbs = tagInfo.getBreadCrumbs();
                    if (breadCrumbs == null) {
                        /* Get the crumbs from the PageDetail object (new style) */
                        breadCrumbs = pageDetail.getBreadCrumbText();
                    }
                    out.write(breadCrumbs);
                }
            });
        }
        
        LiteYukonUser yukonUser = userContext.getYukonUser();
        String username = yukonUser.getUsername();
        String energyCompanyName = null;
        
        try {
            YukonEnergyCompany energyCompany = ecDao.getEnergyCompanyByOperator(yukonUser);
            energyCompanyName = energyCompany.getName();
        } catch (EnergyCompanyNotFoundException e) {
            // The user does not need an Energy Company just to log in.
        }
        
        model.addAttribute("energyCompanyName", energyCompanyName);
        model.addAttribute("username", username);
        
        model.addAttribute("displayName", buildDisplayName(userDao.getLiteContact(yukonUser.getLiteID()), yukonUser));
        
        boolean showContextualNavigation = pageInfo != null && pageInfo.isShowContextualNavigation();
        model.addAttribute("showContextualNavigation", showContextualNavigation);
        if (showContextualNavigation) {
            model.addAttribute("contextualNavigationMenu", new Writable() {
                @Override
                public void write(Writer out) throws IOException {
                    out.append(pageDetail.getRenderContextualNavigation());
                }
            });
        }
        
        model.addAttribute("currentTime", new Date());
        
        model.addAttribute("alertSounds", prefService.getDefaultNotificationAlertSound(yukonUser));
        model.addAttribute("alertFlash", prefService.getDefaultNotificationAlertFlash(yukonUser));
        
        // prevent Firefox "back-forward cache" http://developer.mozilla.org/en/docs/Using_Firefox_1.5_caching
        response.addHeader("Cache-Control", "no-cache, no-store");
        
        return skin.getViewName();
    }
    
    private String buildDisplayName(LiteContact contact, LiteYukonUser user) {
        if (contact == null) {
            return user.getUsername();
        } else if (StringUtils.isBlank(contact.getContFirstName()) && StringUtils.isBlank(contact.getContFirstName())) {
            return user.getUsername();
        } else if (contact.getContFirstName().equalsIgnoreCase(CtiUtilities.STRING_NONE)
            && contact.getContLastName().equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
            return user.getUsername();
        }
        if (contact.getContFirstName().equalsIgnoreCase(CtiUtilities.STRING_NONE)
            || StringUtils.isBlank(contact.getContFirstName())) {
            return user.getUsername();
        }
        String displayName = contact.getContFirstName();
        if (!contact.getContLastName().equalsIgnoreCase(CtiUtilities.STRING_NONE)
            && !StringUtils.isBlank(contact.getContLastName())) {
            displayName += " " + contact.getContLastName();
        }
        return displayName;
    }
    
    @ModelAttribute("yukonVersion")
    public String getYukonVersion() {
        return VersionTools.getYUKON_VERSION();
    }
    
    @ModelAttribute("showNM")
    public boolean showNetworkManagerLink(ModelMap model, LiteYukonUser user) {
        boolean showNM = rolePropertyDao.checkProperty(YukonRoleProperty.ADMIN_NM_ACCESS, user);
        if (showNM) {
            String url = globalSettingDao.getString(GlobalSettingType.NETWORK_MANAGER_ADDRESS);
            model.addAttribute("nmUrl", url);
        }
        return showNM;
    }
    
    @ModelAttribute("buildInfo")
    public String getYukonBuild() {
        Map<String, String> buildInfo = VersionTools.getBuildInfo();
        if (buildInfo.containsKey("JOB_NAME") && buildInfo.containsKey("BUILD_NUMBER")) {
            return "<a href=\"http://swbuild.cooperpowereas.net/job/" + buildInfo.get("JOB_NAME") + "/"
                + buildInfo.get("BUILD_NUMBER") + "\">" + buildInfo.get("BUILD_NUMBER") + "</a>";
        }
        return "undefined";
    }
    
    private Module getModuleBase(String moduleName) {
        Module moduleBase = moduleBuilder.getModule(moduleName);
        
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
    
}