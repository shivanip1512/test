package com.cannontech.web.menu.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.A;
import org.apache.ecs.html.Div;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Script;
import org.apache.ecs.html.Select;
import org.apache.ecs.html.Span;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.constants.LoginController;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.CommonMenuException;
import com.cannontech.web.menu.MenuBase;
import com.cannontech.web.menu.MenuFeatureSet;
import com.cannontech.web.menu.ModuleBase;
import com.cannontech.web.menu.PageInfo;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOption;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.SearchFormData;
import com.cannontech.web.menu.option.producer.SearchProducer;
import com.cannontech.web.menu.option.producer.SearchType;

/**
 * Outputs the HTML to display the menu for a given request and module.
 * The configuration of the menu currently comes from the menu_structure.xml file
 * (which is processed by the CommonMenuBuilder class).
 * 
 * For better or worse, this class makes extensive use of the Elemental
 * Construction Set from Jakarta. This library isn't the best designed
 * library, but it gets the job done. One problem area is escaping. It seems
 * that the ECS library claims to escape content, this doesn't appear
 * to be the case. Therefore, I've used the e() method to HTML escape
 * all of the text. The other problem is that the output comes out on 
 * one big line. Other than calling setPrettyPrint on each element, there
 * doesn't seem to be a fix.
 */
public class StandardMenuRenderer implements MenuRenderer {
    private final ModuleBase moduleBase;
    private List<SubMenuOption> subMenuParents = new ArrayList<SubMenuOption>(4);
    private String SEPERATOR = "  ";
    private String breadCrumbs;
    private String homeUrl;
    private final HttpServletRequest httpServletRequest;
    private YukonUserContext userContext;
    private MenuFeatureSet features = new MenuFeatureSet();
    private String[] selections = new String[2];
    private final MessageSourceAccessor messageSource;
    private PageInfo currentPage;
    
    /**
     * Create a new menu renderer for a given ServletRequest and ModuleMenuBase.
     * The ServletRequest is required so that absolute URLs can be adjusted
     * for the context path.
     * @param request the current request object
     * @param moduleBase the menu base of the current module
     * @param messageSource 
     */
    public StandardMenuRenderer(HttpServletRequest request, ModuleBase moduleBase, PageInfo currentPage, YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.httpServletRequest = request;
        this.moduleBase = moduleBase;
        this.userContext = YukonUserContextUtils.getYukonUserContext(request);
        this.messageSource = messageSourceResolver.getMessageSourceAccessor(userContext);
        this.currentPage = currentPage; // may null for non "new" <pages> setup in module_config
    }
    
    public String renderMenu() {
        Div menuDiv = buildMenu();
        return menuDiv.toString();
    }

    private Div buildMenu() {
        
        Div menuDiv = new Div();
        menuDiv.setPrettyPrint(true);
        menuDiv.setID("Menu");
        String menuBody = "var ctiMenu = new CtiMenu('subMenu');";
        menuDiv.addElement(new Script().setType("text/javascript")
                           .setLanguage("").addElement(menuBody));
        
        Div topDiv = new Div();
        topDiv.setPrettyPrint(true);
        topDiv.setID("topMenu");
        Div insideDiv = new Div();
        topDiv.addElement(insideDiv);
        menuDiv.addElement(topDiv);
        if (features.showMainNavigation) {
            insideDiv.addElement(buildTopLeftSide());
        }
        insideDiv.addElement(buildTopRightSide());
        insideDiv.addElement(new Div().setStyle("clear: both"));
        
        if (subMenuParents.size() > 0 && features.showMainNavigation) {
            Div middleDiv = new Div();
            middleDiv.setPrettyPrint(true);
            middleDiv.setID("subMenu");
            middleDiv.addElement(buildSubMenus());
            menuDiv.addElement(middleDiv);
        }
        
        Div bottomDiv = new Div();
        bottomDiv.setPrettyPrint(true);
        bottomDiv.setID("bottomBar");
        bottomDiv.addElement(buildBottomBar());
        menuDiv.addElement(bottomDiv);
        
        return menuDiv;
    }

    private Div buildTopLeftSide() {
        Div leftDiv = new Div();
        leftDiv.setPrettyPrint(true);
        leftDiv.setClass("stdhdr_leftSide");
        MenuBase menuBase = moduleBase.getMenuBase();
        List<MenuOption> topLevelOptions = menuBase.getMenuOptions(userContext);
        Iterator<MenuOption> topLevelOptionIterator = topLevelOptions.iterator();
        boolean first = true;
        while (topLevelOptionIterator.hasNext()) {
            if (!first) {
                leftDiv.addElement(SEPERATOR);
            }
            first = false;
            MenuOption option = topLevelOptionIterator.next();
            boolean notLast = topLevelOptionIterator.hasNext();
            
            A link;
            if (option instanceof SimpleMenuOption) {
                link = createLink(option, "yukon.web.menu.simpleMenuDefaultTitle");
                SimpleMenuOption simpleOption = (SimpleMenuOption) option;
                link.setHref(buildUrl(simpleOption.getUrl()));
                
                if(simpleOption.isNewWindow()) {
                    link.setTarget("blank");
                }
                
            } else if (option instanceof SubMenuOption) {
                SubMenuOption subOption = (SubMenuOption)option;
                List<MenuOption> menuOptions = subOption.getMenuOptions(userContext);

                link = createLink(option, "yukon.web.menu.subMenuDefaultTitle");
                
                boolean collapse = menuOptions.isEmpty() && subOption.isCollapseIfEmpty();
                if (collapse) continue;
                
                // add sub menu if this isn't a link and isn't collapsed
                if (!subOption.hasLink() || isOptionSelected(option, 0)) {
                    subMenuParents.add(subOption);
                }
                
                if (subOption.hasLink()) {
                    link.setHref(buildUrl(subOption.getUrl()));
                } else {
                    // determine index in list of this option
                    int optionIndex = subMenuParents.size() - 1;
                    String jsId = generateIdForString(optionIndex);
                    
                    link.setHref("#");
                    link.setOnClick(e("ctiMenu.show(this, '" + jsId + "'); return false;"));
                }
            } else {
                throw new CommonMenuException("Unknown MenuOption type encountered: " + option.getClass());
            }
            link.setClass("stdhdr_menuLink" + ((isOptionSelected(option, 0))? " selected":"") + (notLast ? " border_menuLink" : ""));
            leftDiv.addElement(link);
        }
        return leftDiv;
    }

    private A createLink(MenuOption option, String defaultKey) {
        A link = new A();
        MessageSourceResolvable message = option.getMenuText();
        String menuLabel = messageSource.getMessage(message);
        link.addElement(e(menuLabel));
        
        String title;
        try {
            // check for custom title
            MessageSourceResolvable tooltip = option.getMenuTooltip();
            title = messageSource.getMessage(tooltip);
        } catch (NoSuchMessageException e1) {
            // get default title
            title = messageSource.getMessage(defaultKey, menuLabel);
        }
        
        link.setTitle(e(title));
        return link;
    }

    private A createLink(String linkKey, String defaultTitle) {
        return createLink(linkKey, defaultTitle, null);
    }
    
    private A createLink(String linkKey, String defaultTitle, Object argument) {
        A link = new A();
        String menuLabel;
        if (argument == null) {
            menuLabel = messageSource.getMessage(linkKey);
        } else {
            menuLabel = messageSource.getMessage(linkKey, argument);
        }
        link.addElement(e(menuLabel));
        
        // check for custom title
        String title = messageSource.getMessageWithDefault(linkKey + ".title", defaultTitle);
        
        link.setTitle(e(title));
        return link;
    }
    
    private Div buildSubMenus() {
        Div subDiv = new Div();
        subDiv.setPrettyPrint(true);
        // use old school loop to track index
        for (int optionIndex = 0; optionIndex < subMenuParents.size(); ++optionIndex) {
            SubMenuOption optionParent = subMenuParents.get(optionIndex);

            Div thisMenu = new Div();
            String jsId = generateIdForString(optionIndex);
            thisMenu.setID(jsId);
            
            if (!isOptionSelected(optionParent, 0)) {
                thisMenu.setStyle("display: none;");
            }
            Iterator<MenuOption> subLevelOptionIterator = optionParent.getMenuOptions(userContext)
                                                                      .iterator();
            boolean first = true;
            while (subLevelOptionIterator.hasNext()) {
                if (!first) {
                    thisMenu.addElement(SEPERATOR);
                }
                first = false;

                MenuOption option = subLevelOptionIterator.next();
                boolean notLast = subLevelOptionIterator.hasNext();
                
                if (option instanceof SimpleMenuOption) {
                    SimpleMenuOption simpleSubOption = (SimpleMenuOption) option;
                    A link = createLink(simpleSubOption, "yukon.web.menu.simpleMenuDefaultTitle");
                    
                    String anchorClass = "";
                    
                    if (isOptionSelected(simpleSubOption, 1)) {
                        anchorClass += "selected";
                    }
                    if (notLast) {
                        anchorClass += " border_submenuLink";
                    }
                    if(StringUtils.isNotBlank(anchorClass)) {
                        link.setClass(anchorClass);
                    }
                    link.setHref(buildUrl(simpleSubOption.getUrl()));
                    thisMenu.addElement(link);
                } else {
                    // multiple menu levels aren't supported here
                    throw new CommonMenuException("StandardMenuRenderer only supports two level menus");
                }
            }
            subDiv.addElement(thisMenu);
        }
        return subDiv;
    }

    private Div buildTopRightSide() {
        
        Div right = new Div();
        right.setPrettyPrint(true);
        right.setClass("stdhdr_rightSide");
        
        Span alertSpan = new Span();
        alertSpan.setID("alertSpan");
        
        A alertSpanAnchor = new A("javascript:alert_handleOnClick();");
        alertSpanAnchor.addElement(messageSource.getMessage("yukon.web.menu.alertBeforeNum"));

        Span countSpan = new Span();
        countSpan.setID("alertCountSpan");

        alertSpanAnchor.addElement(countSpan);
        alertSpanAnchor.addElement(messageSource.getMessage("yukon.web.menu.alertAfterNum"));
        alertSpan.addElement(alertSpanAnchor);
        right.addElement(alertSpan);
        
        if (features.showModuleSelection) {
            right.addElement("&nbsp;&nbsp;");
            String moduleMsg = messageSource.getMessage("yukon.web.menu.module");
            right.addElement(new Span(moduleMsg).setClass("stdhdr_menu"));
            MenuBase portalLinks = moduleBase.getPortalLinks();
            
            // generate portal drop down
            SelectMenuConfiguration selectMenuConfiguration = new SelectMenuConfiguration();
    		selectMenuConfiguration.setMenuBase(portalLinks);
    		selectMenuConfiguration.setHeaderKey("yukon.web.menu.locationSelect");
    		SelectMenuOptionRenderer selectMenuOptionRenderer = new SelectMenuOptionRenderer();
    		Select select = selectMenuOptionRenderer.generateSelect(selectMenuConfiguration, messageSource, userContext);
            
            right.addElement(select);
        }

        right.addElement("&nbsp;&nbsp;");
        A homeLink = createLink("yukon.web.menu.home", "");
        homeLink.setHref(homeUrl);
        homeLink.setClass("stdhdr_menuLink border_menuLink");
        right.addElement(homeLink);
        right.addElement(" ");
        
        A logoutLink;
        HttpSession session = httpServletRequest.getSession(false);
        boolean savedUser = session.getAttribute(LoginController.SAVED_YUKON_USERS) != null;
        if (savedUser) {
            StarsDatabaseCache cache = StarsDatabaseCache.getInstance();
            int energyCompanyId = ServletUtils.getStarsYukonUser(session).getEnergyCompanyID();
            String companyName = cache.getEnergyCompany(energyCompanyId).getParent().getName();
            logoutLink = createLink("yukon.web.menu.backToEc", "", companyName);
        } else {
            logoutLink = createLink("yukon.web.menu.logout", "");
        }
        
        logoutLink.setHref(buildUrl("/servlet/LoginController?ACTION=LOGOUT"));
        logoutLink.setClass("stdhdr_menuLink");
        right.addElement(logoutLink);
        right.addElement(" ");
        return right;
    }

    private Div buildBottomBar() {
        Div wrapper = new Div();
        wrapper.setPrettyPrint(true);
        
        if (breadCrumbs != null) {
            Div left = new Div();
            left.setClass("stdhdr_leftSide");
            left.addElement(breadCrumbs);
            wrapper.addElement(left);
        }
        
        SearchProducer searchProducer = moduleBase.getSearchProducer();
        SearchFormData formData = null;
        if (searchProducer != null) {
            formData = searchProducer.getSearchProducer(userContext);
        }
        if (formData != null && features.showSearch && (currentPage == null || !currentPage.isHideSearch())) {
            
            
            Div right = new Div();
            right.setClass("stdhdr_rightSide");
            Form searchForm = new Form(formData.getFormAction(), formData.getFormMethod());
            searchForm.setAcceptCharset("ISO-8859-1");
            
            Div searchDiv = new Div();
            searchDiv.setID("findForm");
            
            // search types?
            if (formData.getTypeOptions() != null) {
                
                Select select = new Select();
                select.setName(formData.getTypeName());
                select.setOnChange("$('textInput').value = ''");
                
                for (SearchType searchType : formData.getTypeOptions()) {
                    
                    Option option = new Option();
                    option.setValue(searchType.getSearchTypeValue());
                    option.addElement(messageSource.getMessage(searchType.getDisplayKey()));
                    
                    select.addElement(option);
                }
                
                searchDiv.addElement(select);
            } else {
                searchDiv.addElement("Find: ");
            }
            
            
            // search box
            Input textInput = new Input(Input.text, formData.getFieldName(), "");
            textInput.setID("textInput");
            
            searchDiv.addElement(textInput);
            searchDiv.addElement(new Input(Input.image, "Go")
                                  .setSrc(buildUrl("/WebConfig/yukon/Buttons/GoButtonGray.gif"))
                                  .setAlt("Go")
                                  .setStyle("vertical-align: middle"));
            searchForm.addElement(searchDiv);
            right.addElement(searchForm);
            wrapper.addElement(right);
        }
        
        wrapper.addElement(new Div().setStyle("clear: both"));
        return wrapper;
    }

    
    public void renderMenu(Writer out) throws IOException {
        Div menuDiv = buildMenu();
        menuDiv.output(out);
    }
    
    public void setBreadCrumb(String breadCrumbs) {
        this.breadCrumbs = breadCrumbs;
    }
    
    private String buildUrl(String url) {
        return StringEscapeUtils.escapeHtml(ServletUtil.createSafeUrl(httpServletRequest, url));
    }
    
    private String generateIdForString(int index) {
        return "stdmenu_" + index;
    }
    
    private boolean isOptionSelected(MenuOption option, int menuLevel){
        if(menuLevel < selections.length && option.getId() != null){
            return option.getId().equals(selections[menuLevel]);
        }
        return false;
    }
    
    /**
     * Escape a string for use in HTML.
     * This method has a short name to reduce typing.
     * @param input unsafe string
     * @return string with HTML characters escaped
     */
    private String e(String input) {
        return StringEscapeUtils.escapeHtml(input);
    }

    public void setFeatures(MenuFeatureSet features) {
        this.features = features;
    }
    
    public MenuFeatureSet getFeatures() {
        return features;
    }
    
    public void setHomeUrl(String homeUrl) {
        this.homeUrl = homeUrl;
    }

    public void setMenuSelection(String menuSelection) {
        if (menuSelection != null){
            selections = menuSelection.split("\\|");
        }
    }
    
}
