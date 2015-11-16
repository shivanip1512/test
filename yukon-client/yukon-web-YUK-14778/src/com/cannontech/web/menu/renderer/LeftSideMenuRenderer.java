package com.cannontech.web.menu.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.ecs.Element;
import org.apache.ecs.html.A;
import org.apache.ecs.html.Div;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.Span;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.MenuBase;
import com.cannontech.web.menu.MenuFeatureSet;
import com.cannontech.web.menu.Module;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOption;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;

/**
 * MenuRenderer which renders the menu along the left side of the page
 */
public class LeftSideMenuRenderer implements MenuRenderer {

    private final HttpServletRequest httpServletRequest;
    private final Module module;
    private final MessageSourceAccessor messageSource;
    private YukonUserContext userContext;
    private MenuFeatureSet features = new MenuFeatureSet();
    private String[] selections = new String[2];

    // This renderer only supports 3 levels of menu depth
    private static final int maxParentDepth = 2;

    /**
     * Create a new menu renderer for a given ServletRequest and ModuleMenuBase.
     * The ServletRequest is required so that absolute URLs can be adjusted` for
     * the context path.
     * @param request the current request object
     * @param module the menu base of the current module
     */
    public LeftSideMenuRenderer(HttpServletRequest request, Module module,
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.httpServletRequest = request;
        this.module = module;
        this.userContext = YukonUserContextUtils.getYukonUserContext(request);
        this.messageSource = messageSourceResolver.getMessageSourceAccessor(userContext);
    }

    @Override
    public MenuFeatureSet getFeatures() {
        return features;
    }

    @Override
    public void renderMenu(Writer out) throws IOException {

        MenuBase menuBase = this.module.getMenuBase();
        Div menuDiv = this.buildMenu(menuBase);
        menuDiv.output(out);

    }

    /**
     * Helper method to build up the menu html elements
     * @param menuBase - MenuBase to get the menu options from
     * @return Div containing menu
     */
    private Div buildMenu(MenuBase menuBase) {

        Div menuDiv = new Div();

        List<Element> menuOptions = this.generateMenuOptions(menuBase, 1);

        for (Element element : menuOptions) {
            menuDiv.addElement(element);
        }

        return menuDiv;

    }

    /**
     * Helper method to generate an element for each menu option in the producer
     * @param producer - Producer to generate menu options for
     * @param level - Current level of the menu being generated
     * @return List with each menu option element
     */
    private List<Element> generateMenuOptions(MenuOptionProducer producer,
            int level) {

        List<Element> menuOptionList = new ArrayList<Element>();
        
        String expandImgSrc = messageSource.getMessage("yukon.web.menu.config.consumer.leftMenu.expandIcon");
        expandImgSrc = ServletUtil.createSafeUrl(httpServletRequest, expandImgSrc);
        String collapseImgSrc = messageSource.getMessage("yukon.web.menu.config.consumer.leftMenu.collapseIcon");
        collapseImgSrc = ServletUtil.createSafeUrl(httpServletRequest, collapseImgSrc);
        
        // Loop over options for this producer - mostly for dynamic option
        // producers which may produce multiple options
        for (MenuOption option : producer.getMenuOptions(userContext)) {

            // Add a .css class when the current option is selected
            boolean optionSelected = this.isOptionSelected(option, level);
            String selectedClass = "";
            if (optionSelected) {
                selectedClass += " leftSelected" + level;
            }

            Div optionDiv = new Div();
            optionDiv.setClass("menuOption" + level + selectedClass);

            // Get the menu text from the message source (user specific)
            String menuText = StringEscapeUtils.escapeHtml4(messageSource.getMessage(option.getMenuText()));
            
            if (option instanceof SimpleMenuOption) {

                // Menu option has no children - javascript or link menu option

                if (!optionSelected) {
                    // Non-selected options are links
                    A link = new A();
                    link.addElement(menuText);

                    SimpleMenuOption optionLink = (SimpleMenuOption) option;
                    String url = StringEscapeUtils.escapeHtml4(ServletUtil.createSafeUrl(httpServletRequest,
                                                                                        optionLink.getUrl()));
                    link.setHref(url);
                    
                    if(optionLink.isNewWindow()) {
                        link.setTarget("blank");
                    }

                    optionDiv.addElement(link);
                } else {
                    // Selected items are divs (cannot be clicked)
                    optionDiv.addElement(menuText);
                }
                menuOptionList.add(optionDiv);
            } else if (option instanceof SubMenuOption) {
                SubMenuOption subMenuOption = (SubMenuOption) option;
                // Menu option has children - Create parent menu item
                A link = new A();
                link.addElement(menuText);

                // Create a Div which will contain all the sub options of the
                // current option
                Div childrenDiv = new Div();
                String hideRevealSetupJs = null;
                
                if (level == maxParentDepth) { // why not a > here?
                    
                    // Set child div id for hide/show
                    String expandableOptionSpanId = option.getId() + "_expanderSpan";
                    String childDivId = option.getId() + "_children";
                    String expandImgId  = childDivId + "_expandImg";
                    String collapseImgId  = childDivId + "_collapseImg";
                    
                    // stick everything in this expandableOption, which wil itself be put into the optionDiv
                    Span expandableOption = new Span();
                    expandableOption.setID(expandableOptionSpanId);
                    expandableOption.setClass("expandCollapseSpan");

                    hideRevealSetupJs = "<script type=\"text/javascript\">" +
                                               "hideRevealSectionSetup('"+expandImgId+"', '"+collapseImgId+"', '"+expandableOptionSpanId+"', '"+childDivId+"', false, '"+expandableOptionSpanId+"');" +
                                               "</script>";
                    
                    // configure div that holds child divs
                    childrenDiv.setID(childDivId);
                    childrenDiv.setClass("popUpMenuItem");
                    
                    // configure expand/collapse icon
                    IMG expandImg = new IMG(expandImgSrc);
                    expandImg.setID(expandImgId);
                    expandImg.setClass("expandCollapseImg");

                    IMG collapseImg = new IMG(collapseImgSrc);
                    collapseImg.setID(collapseImgId);
                    collapseImg.setClass("expandCollapseImg");
                    
                    // prefix menu option with expand/collapse img
                    expandableOption.addElement(expandImg);
                    expandableOption.addElement(collapseImg);
                    expandableOption.addElement(link);

                    optionDiv.addElement(expandableOption);
                    
                }  else {
                    
                    optionDiv.addElement(link);
                }
                

                // Generate child menu items
                int childLevel = level + 1;
                List<Element> childOptions = this.generateMenuOptions(subMenuOption,
                                                                      childLevel);
                for (Element child : childOptions) {
                    childrenDiv.addElement(child);
                }
                
                optionDiv.addElement(childrenDiv);
                if (hideRevealSetupJs != null) {
                    optionDiv.addElement(hideRevealSetupJs);
                }

                if(!childOptions.isEmpty() || !subMenuOption.isCollapseIfEmpty()) {
                    menuOptionList.add(optionDiv);
                }
            }
        }

        return menuOptionList;
    }

    /**
     * Helper method to determine if menu option is selected
     * @param option - Option in question
     * @param menuLevel - Level of option in question
     * @return True if option is selected
     */
    private boolean isOptionSelected(MenuOption option, int menuLevel) {
        if ((menuLevel - 1) < selections.length && option.getId() != null) {
            return option.getId().equals(selections[menuLevel - 1]);
        }
        return false;
    }

    @Override
    public void setBreadCrumb(String breadCrumbs) {
        // not used by this renderer
    }

    @Override
    public void setFeatures(MenuFeatureSet features) {
        this.features = features;
    }

    @Override
    public void setMenuSelection(String menuSelection) {
        if (menuSelection != null) {
            this.selections = menuSelection.split("\\|");
        }
    }
    
    @Override
    public void setHomeUrl(String homeUrl) {
        // not used by this renderer
    }

}
