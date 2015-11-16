package com.cannontech.web.contextualMenu.model.menuEntry;

/**
 * This class serves as a placeholder for representing a separator between two MenuEntry items
 * 
 * It is designed to be initialized and used within an applicationContext.xml file. For example:
 * 
 * <bean id="separatorMenuItem" class="com.cannontech.web.contextualMenu.model.menuEntry.MenuSeparator"/>
 * 
 * <bean id="someSpecificMenu" class="com.cannontech.web.contextualMenu.model.menu.SomeSpecificMenu">
 *      <property name="menuEntries">
 *          <list>
 *               <ref bean="menuItemSomeMenuBeanId1"/>
 *               <ref bean="separatorMenuItem"/>
 *               <ref bean="menuItemSomeMenuBeanId2"/>
 *               <ref bean="menuItemSomeMenuBeanId3"/>
 *               <ref bean="separatorMenuItem"/>
 *               <ref bean="menuItemSomeMenuBeanId4"/>
 *          </list>
 *       </property>
 *   </bean>
 */
public class MenuSeparator implements MenuEntry {
}
