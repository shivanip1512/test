package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.core.io.Resource;

import com.cannontech.user.checker.AggregateUserChecker;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionAction;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.DynamicMenuOptionProducer;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;
import com.cannontech.web.menu.option.producer.MenuOptionProducerFactory;
import com.cannontech.web.menu.option.producer.StaticMenuOptionProducer;

/**
 * The purpose of this class is to parse the module_config.xml file (although
 * the exact file is passed to it in the constructor) and store the settings
 * for each module that is encountered. This is likely the only file that will
 * ever need to understand the module_config.xml file.
 */
public class CommonModuleBuilder implements ModuleBuilder {
    private RolePropertyUserCheckerFactory userCheckerFactory;
    private Map<String, ModuleBase> moduleMap = new TreeMap<String, ModuleBase>();
    private MenuBase portalLinksBase = null;
    private List<String> portalJavaScriptIncludes = new ArrayList<String>(3);
    private MenuOptionProducerFactory menuOptionProducerFactory;
    private final Resource moduleConfigFile;
    private final String menuKeyPrefix = "yukon.web.menu.portal";
    private final String menuKeyModPrefix = "yukon.web.menu.config.";
    
    public CommonModuleBuilder(Resource moduleConfigFile) throws CommonMenuException {
        this.moduleConfigFile = moduleConfigFile;
    }
    
    public void processConfigFile() throws CommonMenuException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(moduleConfigFile.getInputStream());
            processPortalLinks(configDoc);
            processModules(configDoc);
            
        } catch (Exception e) {
            throw new CommonMenuException("Can't build menu from configuration", e);
        }
    }

    private void processPortalLinks(Document configDoc) throws CommonMenuException {
        Element rootElem = configDoc.getRootElement();
        Element portalLinks = rootElem.getChild("portals");
        Element topLinks = portalLinks.getChild("links");
        List<MenuOptionProducer> portalLinkList = processLinksElement(topLinks, menuKeyPrefix);
        portalLinksBase = new MenuBase(portalLinkList);
    }

    private void processModules(Document configDoc) throws Exception {
        Element rootElem = configDoc.getRootElement();
        List<?> moduleList = rootElem.getChildren("module");
        for (Iterator<?> iter = moduleList.iterator(); iter.hasNext();) {
            Element moduleElement = (Element) iter.next();
            buildModule(moduleElement);
        }
    }

    private void buildModule(Element moduleElement) throws CommonMenuException {
        String moduleName = moduleElement.getAttributeValue("name");
        ModuleBase moduleBase = new ModuleBase(moduleName);
        // quickPortalList should have been built by now
        moduleBase.setPortalLinks(portalLinksBase);
        for (String jsFile : portalJavaScriptIncludes) {
            moduleBase.addScriptFiles(jsFile);
        }
        
        Element topLinks = moduleElement.getChild("links");
        List<MenuOptionProducer> topLevelOptions = processLinksElement(topLinks, menuKeyModPrefix + moduleName);
        MenuBase menuBase = new MenuBase(topLevelOptions);
        moduleBase.setMenuBase(menuBase);
        
        Element searchElement = moduleElement.getChild("search");
        if (searchElement != null) {
            moduleBase.setSearchPath(searchElement.getAttributeValue("action"));
            moduleBase.setSearchFieldName(searchElement.getAttributeValue("fieldname"));
            moduleBase.setSearchMethod(searchElement.getAttributeValue("method"));
        }
        Element skinElement = moduleElement.getChild("skin");
        if (skinElement != null) {
            moduleBase.setSkin(skinElement.getAttributeValue("name"));
        }
        List<?> cssElements = moduleElement.getChildren("css");
        for (Iterator<?> iter = cssElements.iterator(); iter.hasNext();) {
            Element cssElement = (Element) iter.next();
            moduleBase.addCssFiles(cssElement.getAttributeValue("file"));
        }
        List<?> scriptElements = moduleElement.getChildren("script");
        for (Iterator<?> iter = scriptElements.iterator(); iter.hasNext();) {
            Element scriptElement = (Element) iter.next();
            moduleBase.addScriptFiles(scriptElement.getAttributeValue("file"));
        }
        UserChecker checkerForElement = getCheckerForElement(moduleElement);
        moduleBase.setModuleChecker(checkerForElement);
        
        moduleMap.put(moduleBase.getModuleName(), moduleBase);
    }

    private List<MenuOptionProducer> processLinksElement(Element linksElement, String prefix)
        throws CommonMenuException {
        List<MenuOptionProducer> options = new ArrayList<MenuOptionProducer>();
        if (linksElement != null) {
            List<?> elementList = linksElement.getChildren();
            for (Iterator<?> iterator = elementList.iterator(); iterator.hasNext();) {
                Element element = (Element) iterator.next();
                
                MenuOptionProducer menuOptionProducer = null;
                if ("option".equals(element.getName())) {
                    menuOptionProducer = processOptionElement(element, prefix);
                } else if ("dynamicOptions".equals(element.getName())) {
                    menuOptionProducer = processDynamicOptionsElement(element, prefix);
                } else {
                    throw new RuntimeException("Unknown element found in the links element: " + element.getName());
                }
                
                if (menuOptionProducer != null) {
                    options.add(menuOptionProducer);
                }
            }
        }
        return options;
    }

    private MenuOptionProducer processDynamicOptionsElement(Element element, String prefix) {
        String beanName = element.getAttributeValue("bean");
        DynamicMenuOptionProducer menuOptionProducer = menuOptionProducerFactory.createMenuOptions(beanName);
        UserChecker checker = getCheckerForElement(element);
        
        menuOptionProducer.addUserChecker(checker);
        
        return menuOptionProducer;
    }

    private MenuOptionProducer processOptionElement(Element optionElement, String prefix) throws CommonMenuException {
        MenuOption menuOption = null;
        
        UserChecker checker = getCheckerForElement(optionElement);
        
        String optionId = optionElement.getAttributeValue("id");
        String key = prefix + "." + optionId;
        
        Element topAction = optionElement.getChild("script");
        Element topLink = optionElement.getChild("link");
        Element subLinks = optionElement.getChild("links");
        
        if (topAction != null) {
            SimpleMenuOptionAction actionMenuOption = new SimpleMenuOptionAction(optionId, key);
            actionMenuOption.setScript(topAction.getTextTrim());
            menuOption = actionMenuOption;
        } else if (topLink != null) {
            SimpleMenuOptionLink linkMenuOption = new SimpleMenuOptionLink(optionId, key);
            linkMenuOption.setLinkUrl(topLink.getTextTrim());
            menuOption = linkMenuOption;
        } else if (subLinks != null) {
            List<MenuOptionProducer> subLinkOptions = processLinksElement(subLinks, key);
            SubMenuOption subMenuOption = new SubMenuOption(optionId, key);
            subMenuOption.setSubOptions(subLinkOptions);
            menuOption = subMenuOption;
        }
        
        StaticMenuOptionProducer menuOptionProducer = new StaticMenuOptionProducer(menuOption, checker);

        return menuOptionProducer;
    }

    private UserChecker getCheckerForElement(Element topOptionElement) {
        List<UserChecker> checkers = new ArrayList<UserChecker>(1);
        List<?> children = topOptionElement.getChildren();
        for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
            Element child = (Element) iter.next();
            UserChecker checker  = null;
            String prop = child.getAttributeValue("value");
            if (child.getName().equals("requireProperty")) {
                checker = userCheckerFactory.createPropertyChecker(prop);
            } else if (child.getName().equals("requireFalseProperty")) {
                checker = userCheckerFactory.createFalsePropertyChecker(prop);
            } else if (child.getName().equals("requireRole")) {
                checker = userCheckerFactory.createRoleChecker(prop);
            }
            
            if (checker != null) {
                checkers.add(checker);
            }
        }
        if (checkers.isEmpty()) {
            return new NullUserChecker();
        } else if (checkers.size() == 1){
            // not needed, but saves a little memory and processing time
            return checkers.get(0);
        } else {
            return new AggregateUserChecker(checkers);
        }
    }
    
    public ModuleBase getModuleBase(String moduleName) {
        ModuleBase moduleBase = moduleMap.get(moduleName);
        Validate.notNull(moduleBase, "Unknown module name \"" + moduleName + "\" (check module_config.xml).");
        return moduleBase;
    }

    public void setUserCheckerFactory(
            RolePropertyUserCheckerFactory userCheckerFactory) {
        this.userCheckerFactory = userCheckerFactory;
    }
    
    public void setMenuOptionProducerFactory(
            MenuOptionProducerFactory menuOptionProducerFactory) {
        this.menuOptionProducerFactory = menuOptionProducerFactory;
    }
    
}
