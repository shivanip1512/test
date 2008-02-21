package com.cannontech.web.menu;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ecs.html.A;
import org.apache.ecs.html.Div;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.Option;
import org.apache.ecs.html.Script;
import org.apache.ecs.html.Select;
import org.apache.ecs.html.Span;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.application.WebClientRole;
import com.cannontech.util.ServletUtil;

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
    private List<TopLevelOption> subMenuParents = new ArrayList<TopLevelOption>(4);
    private String SEPERATOR = "  ";
    private String breadCrumbs;
    private final HttpServletRequest httpServletRequest;
    private LiteYukonUser yukonUser;
    private MenuFeatureSet features = new MenuFeatureSet();
    private String[] selections = new String[2];
    private final MessageSourceAccessor messageSource;
    
    /**
     * Create a new menu renderer for a given ServletRequest and ModuleMenuBase.
     * The ServletRequest is required so that absolute URLs can be adjusted`
     * for the context path.
     * @param request the current request object
     * @param moduleBase the menu base of the current module
     * @param messageSource 
     */
    public StandardMenuRenderer(HttpServletRequest request, ModuleBase moduleBase, MessageSourceAccessor messageSource) {
        httpServletRequest = request;
        this.moduleBase = moduleBase;
        this.messageSource = messageSource;
        yukonUser = ServletUtil.getYukonUser(request);
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
        Iterator topLevelOptionIterator = menuBase.getValidTopLevelOptions(yukonUser);
        boolean first = true;
        while (topLevelOptionIterator.hasNext()) {
            if (!first) {
                leftDiv.addElement(SEPERATOR);
            }
            first = false;
            BaseMenuOption option = (BaseMenuOption) topLevelOptionIterator.next();
            A link = createLink(option, "yukon.web.menu.topMenuDefaultTitle");

            if (option instanceof SimpleMenuOption) {
                SimpleMenuOption simpleOption = (SimpleMenuOption) option;
                link.setHref(buildUrl(simpleOption.getUrl()));
            } else if (option instanceof TopLevelOption) {
                TopLevelOption optionParent = (TopLevelOption) option;
                subMenuParents.add(optionParent);
                // <a href="#" class="menuLink" onclick="menuShow('subMenu', 2); return false;">
                link.setHref("#");
                link.setOnClick(e("ctiMenu.show(this, '" + generateIdForString(option.getLinkKey()) + "'); return false;"));
            }
            link.setClass("stdhdr_menuLink" + ((isOptionSelected(option, 0))? " selected":""));
            leftDiv.addElement(link);
        }
        return leftDiv;
    }

    private A createLink(BaseMenuOption option, String defaultKey) {
        String linkKey = option.getLinkKey();
        A link = new A();
        String menuLabel = messageSource.getMessage(linkKey);
        link.addElement(e(menuLabel));
        
        // get default title
        String title = messageSource.getMessage(defaultKey, menuLabel);
        // check for custom title
        title = messageSource.getMessageWithDefault(linkKey + ".title", title);
        
        link.setTitle(e(title));
        return link;
    }

    private A createLink(String linkKey, String defaultTitle) {
        A link = new A();
        String menuLabel = messageSource.getMessage(linkKey);
        link.addElement(e(menuLabel));
        
        // check for custom title
        String title = messageSource.getMessageWithDefault(linkKey + ".title", defaultTitle);
        
        link.setTitle(e(title));
        return link;
    }
    
    private Div buildSubMenus() {
        Div subDiv = new Div();
        subDiv.setPrettyPrint(true);
        for (Iterator<TopLevelOption> iter = subMenuParents.iterator(); iter.hasNext();) {
            TopLevelOption optionParent = iter.next();
            Div thisMenu = new Div();
            thisMenu.setID(generateIdForString(optionParent.getLinkKey()));
            if(!isOptionSelected(optionParent, 0)){
                thisMenu.setStyle("display: none;");
            }
            Iterator subLevelOptionIterator = optionParent.getValidSubLevelOptions(yukonUser);
            boolean first = true;
            while (subLevelOptionIterator.hasNext()) {
                if (!first) {
                    thisMenu.addElement(SEPERATOR);
                }
                first = false;
                SimpleMenuOption subOption = (SimpleMenuOption) subLevelOptionIterator.next();
                A link = createLink(subOption, "yukon.web.menu.subMenuDefaultTitle");
                if(isOptionSelected(subOption, 1)){
                    link.setClass("selected");
                }
                link.setHref(buildUrl(subOption.getUrl()));
                thisMenu.addElement(link);
            }
            subDiv.addElement(thisMenu);
        }
        return subDiv;
    }
    
    private Div buildTopRightSide() {
        
        String homeurl = DaoFactory.getAuthDao().getRolePropertyValue(yukonUser, WebClientRole.HOME_URL);
        Div right = new Div();
        right.setPrettyPrint(true);
        right.setClass("stdhdr_rightSide");
        if (features.showModuleSelection) {
            String moduleMsg = messageSource.getMessage("yukon.web.menu.module");
            right.addElement(new Span(moduleMsg).setClass("stdhdr_menu"));
            Iterator quickLinkIterator = moduleBase.getValidQuickLinks(yukonUser);
            Select select = new Select();
            select.setOnChange(e("javascript:window.location=(this[this.selectedIndex].value);"));
            String locationSelectMsg = messageSource.getMessage("yukon.web.menu.locationSelect");
            select.addElement(new Option("").addElement(e(locationSelectMsg)).setSelected(true));
            while (quickLinkIterator.hasNext()) {
                SimpleMenuOption element = (SimpleMenuOption) quickLinkIterator.next();
                Option quickOption = new Option(buildUrl(element.getUrl()));
                String menuName = messageSource.getMessage(element.getLinkKey());
                quickOption.addElement(e(menuName));
              
                select.addElement(quickOption);
            }
            right.addElement(select);
        }

        right.addElement("&nbsp;&nbsp;");
        A homeLink = createLink("yukon.web.menu.home", "");
        homeLink.setHref(homeurl);
        homeLink.setClass("stdhdr_menuLink");
        right.addElement(homeLink);
        right.addElement(" ");
        A logoutLink = createLink("yukon.web.menu.logout", "");
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
        
        if (moduleBase.getSearchPath() != null && features.showSearch) {
            Div right = new Div();
            right.setClass("stdhdr_rightSide");
            Form searchForm = new Form(moduleBase.getSearchPath(), moduleBase.getSearchMethod());
            searchForm.setAcceptCharset("ISO-8859-1");
            Div searchDiv = new Div();
            searchDiv.setID("findForm");
            searchDiv.addElement("Find: ");
            searchDiv.addElement(new Input(Input.text, moduleBase.getSearchFieldName()));
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
    
    private String generateIdForString(String title) {
        // strip out bad characters
        return "stdmenu_" + title.replaceAll("[^a-zA-Z0-9]", "_");
    }
    
    private boolean isOptionSelected(BaseMenuOption option, int menuLevel){
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

    public void setMenuSelection(String menuSelection) {
        if(menuSelection != null){
            selections = menuSelection.split("\\|");
        }
    }
}
