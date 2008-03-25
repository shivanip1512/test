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
    private List<MenuOptionProducer> portalLinkList = new ArrayList<MenuOptionProducer>(5);
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
        portalLinkList = processLinksElement(topLinks, menuKeyPrefix);
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
        moduleBase.setPortalLinks(portalLinkList);
        for (String jsFile : portalJavaScriptIncludes) {
            moduleBase.addScriptFiles(jsFile);
        }
        
        MenuBase menuBase = new MenuBase();
        moduleBase.setMenuBase(menuBase);
        Element topLinks = moduleElement.getChild("links");
        List<MenuOptionProducer> topLevelOptions = processLinksElement(topLinks, menuKeyModPrefix + moduleName);
        
        menuBase.setTopLevelOptions(topLevelOptions);
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
            List<?> optionElementList = linksElement.getChildren("option");
            for (Iterator<?> iterator = optionElementList.iterator(); iterator.hasNext();) {
                Element optionElement = (Element) iterator.next();
                MenuOptionProducer option = processOptionElement(optionElement, prefix);
                options.add(option);
            }
        }
        return options;
    }

    private MenuOptionProducer processOptionElement(Element optionElement, String prefix) throws CommonMenuException {
        String topOptionId = optionElement.getAttributeValue("id");
        String key = prefix + "." + topOptionId;
        MenuOptionProducer topLevelOption = null;
        Element dynamicLinks = optionElement.getChild("dynamic");
        
        if (dynamicLinks != null) {
            // Dynamic menu option
            String beanName = dynamicLinks.getTextTrim();
            topLevelOption = menuOptionProducerFactory.createMenuOptions(beanName);
        } else {
            // Static menu option
            
            StaticMenuOptionProducer producer = new StaticMenuOptionProducer();
            
            producer.setKey(key);
            
            Element topAction = optionElement.getChild("script");
            if(topAction != null) {
                producer.setScript(topAction.getTextTrim());
            }
            
            Element topLink = optionElement.getChild("link");
            if(topLink != null) {
                producer.setLinkUrl(topLink.getTextTrim());
            }

            Element subLinks = optionElement.getChild("links");
            List<MenuOptionProducer> subLinkOptions = processLinksElement(subLinks, key);
            producer.setChildren(subLinkOptions);

            topLevelOption = producer;
        }
        
        if (topLevelOption == null) {
            throw new CommonMenuException("No script, link, or links found under: " + key);
        }
        
        UserChecker checker = getCheckerForElement(optionElement);
        topLevelOption.setPropertyChecker(checker);
        
        topLevelOption.setId(topOptionId);
        
        return topLevelOption;
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
