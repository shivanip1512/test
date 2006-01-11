package com.cannontech.web.menu;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * The purpose of this class is to parse the module_config.xml file (although
 * the exact file is passed to it in the constructor) and store the settings
 * for each module that is encountered. This is likely the only file that will
 * ever need to understand the menu_structure.xml file.
 */
public class CommonModuleBuilder implements ModuleBuilder {
    private Map moduleMap = new TreeMap();
    private List quickLinkList = new ArrayList(5);
    
    public CommonModuleBuilder(URL moduleConfigFile) throws CommonMenuException {
        processConfigFile(moduleConfigFile);
    }
    
    private void processConfigFile(URL configFile) throws CommonMenuException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(configFile);
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
            SimpleMenuOption menuOption = createSimpleMenuOption(optionElem);
            addCheckersToElement(optionElem, menuOption);
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
                BaseMenuOption topLevelOption = processTopOption(topOptionElement);
                addCheckersToElement(topOptionElement, topLevelOption);
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
        moduleMap.put(moduleBase.getModuleName(), moduleBase);
    }

    private BaseMenuOption processTopOption(Element topOptionElement) throws CommonMenuException {
        String topOptionName = topOptionElement.getAttributeValue("name");
        BaseMenuOption topLevelOption = null;
        Element topAction = topOptionElement.getChild("script");
        Element topLink = topOptionElement.getChild("link");
        Element topSub = topOptionElement.getChild("sublinks");
        if (topAction != null) {
            SimpleMenuOptionAction topLevelOptionTemp = new SimpleMenuOptionAction(topOptionName);
            topLevelOptionTemp.setScript(topAction.getTextTrim());
            topLevelOption = topLevelOptionTemp;
        } else if (topLink != null) {
            SimpleMenuOptionLink topLevelOptionTemp = new SimpleMenuOptionLink(topOptionName);
            topLevelOptionTemp.setLinkUrl(topLink.getTextTrim());
            topLevelOption = topLevelOptionTemp;
        } else if (topSub != null) {
            TopLevelOption topLevelOptionTemp = new TopLevelOption(topOptionName);
            List subOptionElements = topSub.getChildren("option");
            for (Iterator iter = subOptionElements.iterator(); iter.hasNext();) {
                Element subElement = (Element) iter.next();
                SimpleMenuOption subLevelOption = createSimpleMenuOption(subElement);
                if (subLevelOption == null) {
                    throw new CommonMenuException("Illegal value found under: " + topOptionName);
                }
                addCheckersToElement(subElement, subLevelOption);
                topLevelOptionTemp.addSubLevelOption(subLevelOption);
            }
            topLevelOption = topLevelOptionTemp;
            
        }
        if (topLevelOption == null) {
            throw new CommonMenuException("No script, link, or sublinks found under: "
                                          + topOptionName);
        }
        return topLevelOption;
    }

    private SimpleMenuOption createSimpleMenuOption(Element subElement) {
        SimpleMenuOption subLevelOption = null;
        String subOptionName = subElement.getAttributeValue("name");
        Element subAction = subElement.getChild("script");
        Element subLink = subElement.getChild("link");
        if (subAction != null) {
            SimpleMenuOptionAction subLevelOptionTemp = new SimpleMenuOptionAction(subOptionName);
            subLevelOptionTemp.setScript(subAction.getTextTrim());
            subLevelOption = subLevelOptionTemp;
        } else if (subLink != null) {
            SimpleMenuOptionLink subLevelOptionTemp = new SimpleMenuOptionLink(subOptionName);
            subLevelOptionTemp.setLinkUrl(subLink.getTextTrim());
            subLevelOption = subLevelOptionTemp;
        }
        return subLevelOption;
    }

    private void addCheckersToElement(Element topOptionElement, BaseMenuOption topLevelOption) {
        Element requirePropElem = topOptionElement.getChild("requireProperty");
        Element requireFalsePropElem = topOptionElement.getChild("requireFalseProperty");
        Element requireRoleElem = topOptionElement.getChild("requireRole");
        OptionPropertyChecker checker  = null;
        if (requirePropElem != null) {
            String prop = requirePropElem.getAttributeValue("value");
            checker = OptionPropertyChecker.createPropertyChecker(prop);
        } else if (requireRoleElem != null) {
            String role = requireRoleElem.getAttributeValue("value");
            checker = OptionPropertyChecker.createRoleChecker(role);
        } else if (requireRoleElem != null) {
            String prop = requireFalsePropElem.getAttributeValue("value");
            checker = OptionPropertyChecker.createFalsePropertyChecker(prop);
        }
        
        if (checker != null) {
            topLevelOption.setPropertyChecker(checker);
        }
    }
    
    public ModuleBase getModuleBase(String moduleName) {
        ModuleBase moduleBase = (ModuleBase) moduleMap.get(moduleName);
        Validate.notNull(moduleBase, "Unknown module name (check menu_structure.xml).");
        return moduleBase;
    }
    
}
