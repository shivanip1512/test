package com.cannontech.web.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
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
import com.cannontech.web.menu.option.producer.StaticMenuOptionProducer;
import com.google.common.collect.Lists;

/**
 * The purpose of this class is to parse the module_config.xml file (although
 * the exact file is passed to it in the constructor) and store the settings
 * for each module that is encountered. This is likely the only file that will
 * ever need to understand the module_config.xml file.
 */
public class CommonModuleBuilder implements ModuleBuilder {
	
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private EnergyCompanyService energyCompanyService;
    @Autowired private RoleAndPropertyDescriptionService roleAndPropertyDescriptionService;

    private final Map<String, Module> moduleMap = new TreeMap<String, Module>();
    private MenuOptionProducerFactory menuOptionProducerFactory;
    private final Resource moduleConfigFile;
    private final String menuKeyModPrefix = "yukon.web.menu.config.";
    private final static Namespace ns = Namespace.getNamespace("http://yukon.cannontech.com/modules");
    
    public CommonModuleBuilder(Resource moduleConfigFile) throws ModuleConfigException {
        this.moduleConfigFile = moduleConfigFile;
    }
    
    @PostConstruct
    public void processConfigFile() throws ModuleConfigException {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document configDoc = builder.build(moduleConfigFile.getInputStream());
            processModules(configDoc);
        } catch (Exception e) {
            throw new ModuleConfigException("Can't build module from configuration", e);
        }
    }

    private void processModules(Document configDoc) throws Exception {
        Element rootElem = configDoc.getRootElement();
        List<?> moduleList = rootElem.getChildren("module", ns);
        for (Iterator<?> iter = moduleList.iterator(); iter.hasNext();) {
            Element moduleElement = (Element) iter.next();
            buildModule(moduleElement);
        }
    }

    private void buildModule(Element moduleElement) throws ModuleConfigException {
        String moduleName = moduleElement.getAttributeValue("name");
        Module module = new Module(moduleName);
        
        // TODO: REMOVE when able: Only consumer pages uses "menu" now and hopefully not for long
        Element topMenu = moduleElement.getChild("menu", ns);
        List<MenuOptionProducer> topLevelOptions;
        if (topMenu != null) {
            Element topOptions = topMenu.getChild("options", ns);
            topLevelOptions = processOptionsElement(topOptions, menuKeyModPrefix + moduleName);
        } else {
            topLevelOptions = Collections.emptyList();
        }
        MenuBase menuBase = new MenuBase(topLevelOptions);
        module.setMenuBase(menuBase);
        
        Element skinElement = moduleElement.getChild("skin", ns);
        if (skinElement != null) {
            String skinName = skinElement.getAttributeValue("name");
            LayoutSkinEnum skin = LayoutSkinEnum.valueOf(skinName);
            module.setSkin(skin);
        }
        
        List<?> cssElements = moduleElement.getChildren("css", ns);
        for (Iterator<?> iter = cssElements.iterator(); iter.hasNext();) {
            Element cssElement = (Element) iter.next();
            module.addCssFiles(cssElement.getAttributeValue("file"));
        }
        
        List<?> scriptElements = moduleElement.getChildren("script", ns);
        for (Iterator<?> iter = scriptElements.iterator(); iter.hasNext();) {
            Element scriptElement = (Element) iter.next();
            module.addScriptFiles(scriptElement.getAttributeValue("file"));
        }
        
        Element crumbs = moduleElement.getChild("pages", ns);
        Iterable<PageInfo> pageInfos = processPages(crumbs, moduleName, null);
        for (PageInfo pageInfo : pageInfos) {
            module.addPageInfo(pageInfo);
        }

        moduleMap.put(module.getModuleName(), module);
    }

    private List<PageInfo> processPages(Element parentOfPages, String moduleName, PageInfo parent) {
        if (parentOfPages == null) return Collections.emptyList();
        List<?> children = parentOfPages.getChildren("page", ns);
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
            
            String hideFavorite = pageElement.getAttributeValue("hideFavorite");
            pageInfo.setHideFavorite(BooleanUtils.toBoolean(hideFavorite));
            
            String typeString = pageElement.getAttributeValue("type");
            PageTypeEnum pageType;
            if (typeString == null) {
                pageType = PageTypeEnum.BASIC;
            } else {
                pageType = PageTypeEnum.valueOf(typeString);
            }
            pageInfo.setPageType(pageType);
            
            // TODO: REMOVE when able: Only consumer pages uses "menu" now and hopefully not for long
            String menuSelection = pageElement.getChildTextTrim("menu", ns);
            pageInfo.setRenderMenu(menuSelection != null);
            pageInfo.setMenuSelection(menuSelection);
            
            String linkText = pageElement.getChildTextTrim("link", ns);
            pageInfo.setLinkExpression(linkText);
            
            List<?> argumentElements = pageElement.getChildren("labelArgument", ns);
            for (Iterator<?> argumentIterator = argumentElements.iterator(); argumentIterator.hasNext();) {
                Element argumentElement = (Element) argumentIterator.next();
                String argumentText = argumentElement.getTextTrim();
                pageInfo.addLabelArgumentExpression(argumentText);
            }
            result.add(pageInfo);
            directChildren.add(pageInfo);
            
            String infoInclude = pageElement.getChildTextTrim("infoInclude", ns);
            pageInfo.setDetailInfoIncludePath(infoInclude);
            
            UserChecker userChecker = getCheckerForElement(pageElement);
            if (parent != null) {
                pageInfo.setUserChecker(new AggregateAndUserChecker(userChecker, parent.getUserChecker()));
            } else {
                pageInfo.setUserChecker(userChecker);
            }
            
            // now add the children of the element
            Element subPagesElement = pageElement.getChild("pages", ns);
            List<PageInfo> childPages = processPages(subPagesElement, moduleName, pageInfo);
            result.addAll(childPages);
            
        }
        if (parent != null) {
        	parent.setChildPages(directChildren);
        }
        return result;
    }

    private List<MenuOptionProducer> processOptionsElement(Element linksElement, String prefix)
        throws ModuleConfigException {
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

        menuOptionProducer.setUserChecker(checker);

        return menuOptionProducer;
    }

    private MenuOptionProducer processOptionElement(Element optionElement, String prefix) throws ModuleConfigException {
        MenuOption menuOption = null;
        
        UserChecker checker = getCheckerForElement(optionElement);
        
        String optionId = optionElement.getAttributeValue("name");
        String key = prefix + "." + optionId;
        
        Element topAction = optionElement.getChild("script", ns);
        Element topLink = optionElement.getChild("link", ns);
        Element subOptions = optionElement.getChild("options", ns);
        
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
            } else if (name.equals("requireEcOperatorOrSuperUser")) {
                checker = energyCompanyService.createEcOperatorOrSuperUserChecker();
            } else if (name.equals("requireCanEditEnergyCompany")) {
                checker = energyCompanyService.createCanEditEnergyCompany();
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
    
    @Override
    public Module getModule(String moduleName) {
        
        refreshModules();
        
        Module module = moduleMap.get(moduleName);
        Validate.notNull(module, "Unknown module name \"" + moduleName + "\" (check module_config.xml).");
        return module;
    }

    // Refresh the modules for development mode
    private void refreshModules() {
        boolean devMode = configurationSource.getBoolean(MasterConfigBoolean.DEVELOPMENT_MODE, false);
        if(devMode) {
            // refresh the cached module configuration on each call if we're in development mode
            this.processConfigFile();
        }
    }
    
    @Override
    public List<Module> getAllModules() {
        refreshModules();
        
        return Lists.newArrayList(moduleMap.values());
    }

    public void setMenuOptionProducerFactory(MenuOptionProducerFactory menuOptionProducerFactory) {
        this.menuOptionProducerFactory = menuOptionProducerFactory;
    }
}