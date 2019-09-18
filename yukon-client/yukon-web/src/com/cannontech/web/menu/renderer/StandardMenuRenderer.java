package com.cannontech.web.menu.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.core.io.Resource;

import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigLicenseKey;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.roleproperties.HierarchyPermissionLevel;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.ServletUtil;
import com.google.common.collect.Iterables;

/**
 * This class consumes the menu_config.xml file and will write out menus in the form:
 * <p><pre>
 * {@code
 * <li class="dropdown">
 *     <a href="/some/url/">Some Menu</a>
 *     <ul>
 *         <li class="first"><a href="/some/url/">Option 1</a></li>
 *         <li><a href="/some/url/">Option 2</a></li>
 *         <li><a href="/some/url/">Option 3</a></li>
 *         <li class="common-section"><a href="/some/url/">Option 4</a></li>
 *         <li class="last"><a href="/some/url/">Option 5</a></li>
 *      </ul>
 * </li>}
 * </pre></p>
 * 
 * This class uses the JDOM library to parse the menu_config.xml file 
 * and JSoup to generate the corresponding html.  Some class names clash
 * between the two libraries ("Element") therefore some fully qualified 
 * class names are needed.
 */
public class StandardMenuRenderer {
    
    @Autowired private RolePropertyDao rpDao;
    @Autowired private EnergyCompanyDao ecDao;
    @Autowired private GlobalSettingDao gsDao;
    @Autowired private YukonUserContextMessageSourceResolver resolver;
    @Autowired private ConfigurationSource configurationSource;

    private Resource menuConfigFile;
    private final static String key = "yukon.web.menu.";
    private final static Namespace NS = Namespace.getNamespace("http://yukon.cannontech.com/menus");
    
    public StandardMenuRenderer(Resource menuConfigFile) {
        this.menuConfigFile = menuConfigFile;
    }
    
    public void renderMenu(HttpServletRequest req, Writer writer) throws IOException, JDOMException {
        
        YukonUserContext context = YukonUserContextUtils.getYukonUserContext(req);
        
        SAXBuilder saxBuilder = new SAXBuilder();
        Document doc = saxBuilder.build(menuConfigFile.getInputStream());
        Elements elems = new Elements();
        
        Element rootElement = doc.getRootElement();
        Iterable<Element> menus = getElementChildren(rootElement, "menu");
        
        for (Element menu : menus) {
            buildMenu(req, context, menu, elems);
        }
        String outerHtml = elems.outerHtml();
        writer.write(outerHtml);
    }

    /**
     * Adds a menu to the list of menus if the user passes the permissions for that menu and 
     * the user passes the permissions of at least one menu option.
     */
    private void buildMenu(HttpServletRequest req, YukonUserContext context, Element menu, Elements menus) {
        
        LiteYukonUser user = context.getYukonUser();
        
        /* Bail out if we don't have permission to view this menu. */
        if (!checkPermissions(menu, user)) {
            return;
        }
        
        Iterable<Element> options = getElementChildren(menu.getChild("options", NS), "option");
        
        MessageSourceAccessor accessor = resolver.getMessageSourceAccessor(context);
        boolean firstOption = true;
        Elements menuOptions = new Elements();
        
        Iterator<Element> iter = options.iterator();
        while(iter.hasNext()) {
            
            Element option = iter.next();
            if (checkPermissions(option, user)) {
                
                org.jsoup.nodes.Element optionLi = new org.jsoup.nodes.Element(Tag.valueOf("li"), "");
                optionLi.addClass("menu-option");
                
                if (firstOption) {
                    optionLi.addClass("first");
                    firstOption = false;
                }
                if (!iter.hasNext()) {
                    optionLi.addClass("last");
                }
                if (option.getAttributeValue("section") != null) {
                    optionLi.addClass("common-section");
                }
                
                String link = buildUrl(req, option.getChild("link", NS).getText());
                String text = null;
                try {
                    text = accessor.getMessage(key + menu.getAttributeValue("name") 
                            + "." + option.getAttributeValue("name"));
                } catch (NoSuchMessageException e) {
                    text = accessor.getMessage(key + option.getAttributeValue("name"));
                }
                optionLi.append("<a class=\"menu-option-link\" href=\"" + link + "\">" + text + "</a>");
                
                menuOptions.add(optionLi);
            }
        }
        
        if (!menuOptions.isEmpty()) {
            org.jsoup.nodes.Element menuLi = new org.jsoup.nodes.Element(Tag.valueOf("li"), "");
            menuLi.addClass("menu dropdown");
            
            String text = accessor.getMessage(key + menu.getAttributeValue("name"));
            
            Element link = menu.getChild("link", NS);
            if (link != null) {
                String href = buildUrl(req, link.getText());
                // Don't use 'href' so we can disable the linking for touch screens.
                menuLi.append("<a class=\"menu-title\" data-url=\"" + href + "\">" + text + "</a>");
            } else {
                menuLi.append("<a class=\"menu-title\">" + text + "</a>");
            }
            
            org.jsoup.nodes.Element menuUl = new org.jsoup.nodes.Element(Tag.valueOf("ul"), "");
            menuUl.addClass("menu-options");
            
            for (org.jsoup.nodes.Element menuOption : menuOptions) {
                menuUl.appendChild(menuOption);
            }
            
            menuLi.appendChild(menuUl);
            menus.add(menuLi);
        }
    }
    
    /**
     * Returns true if the user has permission to view this element or there are is no 'permissions'
     * child element.
     */
    private boolean checkPermissions(Element element, LiteYukonUser user) {
        Element permissions = element.getChild("permissions", NS);
        
        if (permissions != null) {
            
            Element or = permissions.getChild("or", NS);
            Element and = permissions.getChild("and", NS);
            
            if (or != null) {
                /* Skip this menu if NONE of the elements in 'or' are met */
                if (!checkAny(or.getChildren(), user)) {
                    return false;
                }
            } else if (and != null) {
                /* Skip this menu if ALL of the elements in 'and' are NOT met */
                if (!checkAll(and.getChildren(), user)) {
                    return false;
                }
            } else {
                /* Default to an 'or' type check on whatever is in here */
                if (!checkAny(permissions.getChildren(), user)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Returns true if the element condition is met, element must be one of {@link Permission}.
     */
    private boolean checkPermission(Element permission, LiteYukonUser user) {
        Permission type = Permission.valueOf(permission.getName());
        if (type == Permission.role) {
            return rpDao.checkRole(YukonRole.valueOf(permission.getAttributeValue("name")), user);
        } else if (type == Permission.roleProperty) {
            String level = permission.getAttributeValue("level");
            YukonRoleProperty property = YukonRoleProperty.valueOf(permission.getAttributeValue("name"));
            if(level == null) {
                return rpDao.checkProperty(property, user);
            } else {
                return rpDao.checkLevel(property, HierarchyPermissionLevel.valueOf(level), user);
            }
        } else if (type == Permission.ecOperator) {
             return ecDao.isEnergyCompanyOperator(user);
        } else if (type == Permission.license) {
            String value = permission.getAttributeValue("name");
            return configurationSource.isLicenseEnabled(MasterConfigLicenseKey.valueOf(value));
        } else if (type == Permission.masterConfig) {
            String value = permission.getAttributeValue("name");
            return configurationSource.getBoolean(value, false);
        } else {
            /* Not used yet and only supporting booleans, add 'value' to globalSetting element in schema if needed */
            return gsDao.getBoolean(GlobalSettingType.valueOf(permission.getAttributeValue("name")));
        }
    }
    
    /**
     * Returns true if any of the permissions are met. 
     */
    private boolean checkAny(List<?> permissions, LiteYukonUser user) {
        for (Object obj : permissions) {
            Element permission = (Element) obj;
            if (checkPermission(permission, user)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if all of the permissions are met. 
     */
    private boolean checkAll(List<?> permissions, LiteYukonUser user) {
        for (Object obj : permissions) {
            Element permission = (Element) obj;
            if (!checkPermission(permission, user)) {
                return false;
            }
        }
        return true;
    }

    public static Iterable<Element> getElementChildren(Element element, String name) {
        return Iterables.filter(element.getChildren(name, NS), Element.class);
    }
    
    private String buildUrl(HttpServletRequest req, String url) {
        return StringEscapeUtils.escapeHtml4(ServletUtil.createSafeUrl(req, url));
    }
    
    
    private enum Permission {
        role,
        roleProperty,
        ecOperator,
        license,
        masterConfig,
        globalSetting
    }
}