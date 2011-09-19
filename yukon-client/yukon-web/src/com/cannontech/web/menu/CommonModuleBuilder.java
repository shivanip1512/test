package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.core.authorization.service.RoleAndPropertyDescriptionService;
import com.cannontech.stars.service.EnergyCompanyService;
import com.cannontech.user.checker.AggregateAndUserChecker;
import com.cannontech.user.checker.NullUserChecker;
import com.cannontech.user.checker.UserChecker;
import com.cannontech.web.menu.option.MenuOption;
import com.cannontech.web.menu.option.SimpleMenuOptionAction;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.DynamicMenuOptionProducer;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;
import com.cannontech.web.menu.option.producer.MenuOptionProducerFactory;
import com.cannontech.web.menu.option.producer.SearchProducer;
import com.cannontech.web.menu.option.producer.SearchProducerFactory;
import com.cannontech.web.menu.option.producer.StaticMenuOptionProducer;
import com.google.common.collect.Lists;

/**
 * The purpose of this class is to parse the module_config.xml file (although
 * the exact file is passed to it in the constructor) and store the settings
 * for each module that is encountered. This is likely the only file that will
 * ever need to understand the module_config.xml file.
 */
public class CommonModuleBuilder implements ModuleBuilder {
    private RoleAndPropertyDescriptionService roleAndPropertyDescriptionService;
    private Map<String, ModuleBase> moduleMap = new TreeMap<String, ModuleBase>();
    private MenuBase portalLinksBase = null;
    private MenuOptionProducerFactory menuOptionProducerFactory;
    private SearchProducerFactory searchProducerFactory;
    private final Resource moduleConfigFile;
    private final String menuKeyPrefix = "yukon.web.menu.portal";
    private final String menuKeyModPrefix = "yukon.web.menu.config.";
    private ConfigurationSource configurationSource;
    private EnergyCompanyService energyCompanyService;
    
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
        Element topLinks = portalLinks.getChild("options");
        List<MenuOptionProducer> portalLinkList = processOptionsElement(topLinks, menuKeyPrefix);
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
        
        Element topMenu = moduleElement.getChild("menu");
        List<MenuOptionProducer> topLevelOptions;
        if (topMenu != null) {
            Element topOptions = topMenu.getChild("options");
            topLevelOptions = processOptionsElement(topOptions, menuKeyModPrefix + moduleName);
        } else {
            topLevelOptions = Collections.emptyList();
        }
        MenuBase menuBase = new MenuBase(topLevelOptions);
        moduleBase.setMenuBase(menuBase);
        
        Element searchElement = moduleElement.getChild("search");
        if (searchElement != null) {
        	String beanName = searchElement.getAttributeValue("bean");
        	SearchProducer searchProducer = searchProducerFactory.getSearchProducer(beanName);
        	moduleBase.setSearchProducer(searchProducer);
        }
        Element skinElement = moduleElement.getChild("skin");
        if (skinElement != null) {
            String skinName = skinElement.getAttributeValue("name");
            LayoutSkinEnum skin = LayoutSkinEnum.valueOf(skinName);
            moduleBase.setSkin(skin);
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
        
        Element crumbs = moduleElement.getChild("pages");
        Iterable<PageInfo> pageInfos = processPages(crumbs, moduleName, null);
        for (PageInfo pageInfo : pageInfos) {
            moduleBase.addPageInfo(pageInfo);
        }

        moduleMap.put(moduleBase.getModuleName(), moduleBase);
    }

    private List<PageInfo> processPages(Element parentOfPages, String moduleName, PageInfo parent) {
        if (parentOfPages == null) return Collections.emptyList();
        List<?> children = parentOfPages.getChildren("page");
        List<PageInfo> result = Lists.newArrayList();
        List<PageInfo> directChildren = Lists.newArrayList();
        for (Iterator<?> iterator = children.iterator(); iterator.hasNext();) {
            Element pageElement = (Element) iterator.next();
            PageInfo pageInfo = new PageInfo();
            pageInfo.setModuleName(moduleName);
            pageInfo.setParent(parent);
            
            String idValue = pageElement.getAttributeValue("name");
            pageInfo.setName(idValue);
            
            String navigationMenuRoot = pageElement.getAttributeValue("navigationMenuRoot");
            pageInfo.setNavigationMenuRoot(BooleanUtils.toBoolean(navigationMenuRoot));
            
            String contributeToMenu = pageElement.getAttributeValue("contributeToMenu");
            pageInfo.setContributeToMenu(BooleanUtils.toBoolean(contributeToMenu));
            
            String hideSearch = pageElement.getAttributeValue("hideSearch");
            pageInfo.setHideSearch(BooleanUtils.toBoolean(hideSearch));
            
            String typeString = pageElement.getAttributeValue("type");
            PageTypeEnum pageType;
            if (typeString == null) {
                pageType = PageTypeEnum.BASIC;
            } else {
                pageType = PageTypeEnum.valueOf(typeString);
            }
            pageInfo.setPageType(pageType);
            
            String menuSelection = pageElement.getChildTextTrim("menu");
            pageInfo.setRenderMenu(menuSelection != null);
            pageInfo.setMenuSelection(menuSelection);
            
            String linkText = pageElement.getChildTextTrim("link");
            pageInfo.setLinkExpression(linkText);
            
            List<?> argumentElements = pageElement.getChildren("labelArgument");
            for (Iterator<?> argumentIterator = argumentElements.iterator(); argumentIterator.hasNext();) {
                Element argumentElement = (Element) argumentIterator.next();
                String argumentText = argumentElement.getTextTrim();
                pageInfo.addLabelArgumentExpression(argumentText);
            }
            result.add(pageInfo);
            directChildren.add(pageInfo);
            
            String infoInclude = pageElement.getChildTextTrim("infoInclude");
            pageInfo.setDetailInfoIncludePath(infoInclude);
            
            UserChecker userChecker = getCheckerForElement(pageElement);
            if(parent != null){
                pageInfo.setUserChecker(new AggregateAndUserChecker(userChecker, parent.getUserChecker()));
            } else {
                pageInfo.setUserChecker(userChecker);
            }
            
            // now add the children of the element
            Element subPagesElement = pageElement.getChild("pages");
            List<PageInfo> childPages = processPages(subPagesElement, moduleName, pageInfo);
            result.addAll(childPages);
            
        }
        if (parent != null) {
        	parent.setChildPages(directChildren);
        }
        return result;
    }

    private List<MenuOptionProducer> processOptionsElement(Element linksElement, String prefix)
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

    // Get a menu option for a particular bean with its i18n name
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
        
        String optionId = optionElement.getAttributeValue("name");
        String key = prefix + "." + optionId;
        
        Element topAction = optionElement.getChild("script");
        Element topLink = optionElement.getChild("link");
        Element subOptions = optionElement.getChild("options");
        
        if (topAction != null) {
            SimpleMenuOptionAction actionMenuOption = new SimpleMenuOptionAction(optionId, key);
            actionMenuOption.setScript(topAction.getTextTrim());
            menuOption = actionMenuOption;
        } else if (topLink != null && subOptions == null) {
            SimpleMenuOptionLink linkMenuOption = new SimpleMenuOptionLink(optionId, key);
            linkMenuOption.setLinkUrl(topLink.getTextTrim());
            menuOption = linkMenuOption;
        } else if (subOptions != null) {
            List<MenuOptionProducer> subLinkOptions = processOptionsElement(subOptions, key);

            // Collapse parent elements by default if there is no children
            boolean collapseIfEmpty = true;
            String collapseIfEmptyString = optionElement.getAttributeValue("collapseIfEmpty");
            if(!StringUtils.isBlank(collapseIfEmptyString)) {
            	collapseIfEmpty = Boolean.valueOf(collapseIfEmptyString);
            }
            SubMenuOption subMenuOption = new SubMenuOption(optionId, key, collapseIfEmpty);
            subMenuOption.setSubOptions(subLinkOptions);
            if (topLink != null) {
                subMenuOption.setLinkUrl(topLink.getTextTrim());
            }
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
            String name = child.getName();
            if (name.equals("requireRoleProperty")) {
                checker = roleAndPropertyDescriptionService.compile(prop);
            } else if (name.equals("requireEcOperator")) {
                checker = energyCompanyService.createEcOperatorChecker();
            }
            
            if (checker != null) {
                checkers.add(checker);
            }
        }
        if (checkers.isEmpty()) {
            return NullUserChecker.getInstance();
        } else if (checkers.size() == 1){
            // not needed, but saves a little memory and processing time
            return checkers.get(0);
        } else {
            return new AggregateAndUserChecker(checkers);
        }
    }
    
    public ModuleBase getModuleBase(String moduleName) {
        
        refreshModules();
        
        ModuleBase moduleBase = moduleMap.get(moduleName);
        Validate.notNull(moduleBase, "Unknown module name \"" + moduleName + "\" (check module_config.xml).");
        return moduleBase;
    }

    // Refresh the modules for development mode
    private void refreshModules() {
        boolean devMode = configurationSource.getBoolean("DEVELOPMENT_MODE", false);
        if(devMode) {
            // refresh the cached module configuration on each call if we're in development mode
            this.processConfigFile();
        }
    }
    
    public List<ModuleBase> getAllModules() {
        refreshModules();
        
        return Lists.newArrayList(moduleMap.values());
    }

    public void setMenuOptionProducerFactory(
            MenuOptionProducerFactory menuOptionProducerFactory) {
        this.menuOptionProducerFactory = menuOptionProducerFactory;
    }
    
    public void setSearchProducerFactory(SearchProducerFactory searchProducerFactory) {
		this.searchProducerFactory = searchProducerFactory;
	}
    
    @Autowired
    public void setRoleAndPropertyDescriptionService(
            RoleAndPropertyDescriptionService roleAndPropertyDescriptionService) {
        this.roleAndPropertyDescriptionService = roleAndPropertyDescriptionService;
    }
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }
    
    @Autowired
    public void setEnergyCompanyService(EnergyCompanyService energyCompanyService) {
        this.energyCompanyService = energyCompanyService;
    }
    
}