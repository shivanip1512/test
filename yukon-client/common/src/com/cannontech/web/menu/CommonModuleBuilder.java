package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.core.io.Resource;

import com.cannontech.user.checker.AggregateUserChecker;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.RolePropertyUserCheckerFactory;
import com.cannontech.user.checker.UserChecker;

/**
 * The purpose of this class is to parse the module_config.xml file (although
 * the exact file is passed to it in the constructor) and store the settings
 * for each module that is encountered. This is likely the only file that will
 * ever need to understand the module_config.xml file.
 */
public class CommonModuleBuilder implements ModuleBuilder {
    private RolePropertyUserCheckerFactory userCheckerFactory;
    private Map<String, ModuleBase> moduleMap = new TreeMap<String, ModuleBase>();
    private List<SimpleMenuOption> quickLinkList = new ArrayList<SimpleMenuOption>(5);
    private final Resource moduleConfigFile;
    private final String menuKeyPrefix = "yukon.web.menu.config.";
    
    public CommonModuleBuilder(Resource moduleConfigFile) throws CommonMenuException {
        this.moduleConfigFile = moduleConfigFile;
    }
    
    public void processConfigFile() throws CommonMenuException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(moduleConfigFile.getInputStream());
            processQuickLinks(configDoc);
            processModules(configDoc);
            
        } catch (Exception e) {
            throw new CommonMenuException("Can't build menu from configuration", e);
        }
    }

    private void processQuickLinks(Document configDoc) {
        Element rootElem = configDoc.getRootElement();
        Element quickLinks = rootElem.getChild("quicklinks");
        List linkList = quickLinks.getChildren("option");
        for (Iterator iter = linkList.iterator(); iter.hasNext();) {
            Element optionElem = (Element) iter.next();
            SimpleMenuOption menuOption = createSimpleMenuOption(optionElem, menuKeyPrefix + "quicklinks");
            UserChecker checker = getCheckerForElement(optionElem);
            
            menuOption.setPropertyChecker(checker);
            quickLinkList.add(menuOption);
        }
    }

    private void processModules(Document configDoc) throws Exception {
        Element rootElem = configDoc.getRootElement();
        List moduleList = rootElem.getChildren("module");
        for (Iterator iter = moduleList.iterator(); iter.hasNext();) {
            Element moduleElement = (Element) iter.next();
            buildModule(moduleElement);
        }
    }

    private void buildModule(Element moduleElement) throws CommonMenuException {
        String moduleName = moduleElement.getAttributeValue("name");
        ModuleBase moduleBase = new ModuleBase(moduleName);
        // quickLinkList should have been built by now
        moduleBase.setQuickLinks(quickLinkList);
        MenuBase menuBase = new MenuBase();
        moduleBase.setMenuBase(menuBase);
        Element topLinks = moduleElement.getChild("toplinks");
        if (topLinks != null) {
            List topOptions = topLinks.getChildren("option");
            for (Iterator iterator = topOptions.iterator(); iterator.hasNext();) {
                Element topOptionElement = (Element) iterator.next();
                BaseMenuOption topLevelOption = processTopOption(topOptionElement, menuKeyPrefix + moduleName);
                UserChecker checker = getCheckerForElement(topOptionElement);
                
                topLevelOption.setPropertyChecker(checker);
                menuBase.addTopLevelOption(topLevelOption);
            }
        }
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
        List cssElements = moduleElement.getChildren("css");
        for (Iterator iter = cssElements.iterator(); iter.hasNext();) {
            Element cssElement = (Element) iter.next();
            moduleBase.addCssFiles(cssElement.getAttributeValue("file"));
        }
        List scriptElements = moduleElement.getChildren("script");
        for (Iterator iter = scriptElements.iterator(); iter.hasNext();) {
            Element scriptElement = (Element) iter.next();
            moduleBase.addScriptFiles(scriptElement.getAttributeValue("file"));
        }
        UserChecker checkerForElement = getCheckerForElement(moduleElement);
        moduleBase.setModuleChecker(checkerForElement);
        
        moduleMap.put(moduleBase.getModuleName(), moduleBase);
    }

    private BaseMenuOption processTopOption(Element topOptionElement, String prefix) throws CommonMenuException {
        String topOptionId = topOptionElement.getAttributeValue("id");
        String key = prefix + "." + topOptionId;
        BaseMenuOption topLevelOption = null;
        Element topAction = topOptionElement.getChild("script");
        Element topLink = topOptionElement.getChild("link");
        Element topSub = topOptionElement.getChild("sublinks");
        if (topAction != null) {
            SimpleMenuOptionAction topLevelOptionTemp = new SimpleMenuOptionAction(key);
            topLevelOptionTemp.setScript(topAction.getTextTrim());
            topLevelOption = topLevelOptionTemp;
        } else if (topLink != null) {
            SimpleMenuOptionLink topLevelOptionTemp = new SimpleMenuOptionLink(key);
            topLevelOptionTemp.setLinkUrl(topLink.getTextTrim());
            topLevelOption = topLevelOptionTemp;
        } else if (topSub != null) {
            TopLevelOption topLevelOptionTemp = new TopLevelOption(key);
            List subOptionElements = topSub.getChildren("option");
            for (Iterator iter = subOptionElements.iterator(); iter.hasNext();) {
                Element subElement = (Element) iter.next();
                SimpleMenuOption subLevelOption = createSimpleMenuOption(subElement, topLevelOptionTemp);
                if (subLevelOption == null) {
                    throw new CommonMenuException("Illegal value found under: " + key);
                }
                UserChecker checker = getCheckerForElement(subElement);
                
                subLevelOption.setPropertyChecker(checker);
                topLevelOptionTemp.addSubLevelOption(subLevelOption);
            }
            topLevelOption = topLevelOptionTemp;
            
        }
        if (topLevelOption == null) {
            throw new CommonMenuException("No script, link, or sublinks found under: " + key);
        }
        
        topLevelOption.setId(topOptionId);
        
        return topLevelOption;
    }

    private SimpleMenuOption createSimpleMenuOption(Element subElement, BaseMenuOption parent) {
        return createSimpleMenuOption(subElement, parent.getLinkKey());
    }
    
    private SimpleMenuOption createSimpleMenuOption(Element subElement, String prefix) {
        SimpleMenuOption subLevelOption = null;
        String subOptionId = subElement.getAttributeValue("id");
        String key;
        if (StringUtils.isBlank(prefix)) {
            key = subOptionId;
        } else {
            key = prefix + "." + subOptionId;
        }
        Element subAction = subElement.getChild("script");
        Element subLink = subElement.getChild("link");
        if (subAction != null) {
            SimpleMenuOptionAction subLevelOptionTemp = new SimpleMenuOptionAction(key);
            subLevelOptionTemp.setScript(subAction.getTextTrim());
            subLevelOption = subLevelOptionTemp;
        } else if (subLink != null) {
            SimpleMenuOptionLink subLevelOptionTemp = new SimpleMenuOptionLink(key);
            subLevelOptionTemp.setLinkUrl(subLink.getTextTrim());
            subLevelOption = subLevelOptionTemp;
        }

        subLevelOption.setId(subOptionId);
        
        return subLevelOption;
    }

    private UserChecker getCheckerForElement(Element topOptionElement) {
        List<UserChecker> checkers = new ArrayList<UserChecker>(1);
        List children = topOptionElement.getChildren();
        for (Iterator iter = children.iterator(); iter.hasNext();) {
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
    
}
