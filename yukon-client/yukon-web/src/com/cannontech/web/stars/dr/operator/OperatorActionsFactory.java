package com.cannontech.web.stars.dr.operator;

import java.util.List;
import java.util.Map;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.SingleUserChecker;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.MenuBase;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.SubMenuOption;
import com.cannontech.web.menu.option.producer.LeftSideContextualMenuOptionsProducer;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;
import com.cannontech.web.menu.option.producer.StaticMenuOptionProducer;
import com.cannontech.web.menu.renderer.SelectMenuConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OperatorActionsFactory {
	
	public static LeftSideContextualMenuOptionsProducer getLeftSideContxtualMenuLinks(int accountId, int energyCompanyId, String selectedMenuId,YukonUserContext userContext) {
		
		List<SimpleMenuOptionLink> menuOptions = Lists.newArrayList();
		
		// url parameters
		Map<String, String> parameterMap = Maps.newLinkedHashMap();
		parameterMap.put("accountId", Integer.toString(accountId));
		parameterMap.put("energyCompanyId", Integer.toString(energyCompanyId));
		String queryString = ServletUtil.buildQueryStringFromMap(parameterMap, false);
		
		// general
		SimpleMenuOptionLink generalActionLink = new SimpleMenuOptionLink("general", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.general"));
		generalActionLink.setLinkUrl("/spring/stars/operator/general/account/accountEdit?" + queryString);
		menuOptions.add(generalActionLink);

		// contacts
		SimpleMenuOptionLink contactsActionLink = new SimpleMenuOptionLink("contacts", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.contacts"));
		contactsActionLink.setLinkUrl("/spring/stars/operator/general/contacts/contactList?" + queryString);
		menuOptions.add(contactsActionLink);
		
		// residence
		SimpleMenuOptionLink residenceActionLink = new SimpleMenuOptionLink("residence", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.residence"));
		residenceActionLink.setLinkUrl("/spring/stars/operator/general/residence/residenceEdit?" + queryString);
		menuOptions.add(residenceActionLink);
		
		// call tracking
		SimpleMenuOptionLink callTrackingActionLink = new SimpleMenuOptionLink("callTracking", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.callTracking"));
		callTrackingActionLink.setLinkUrl("/spring/stars/operator/general/callTracking?" + queryString);
		menuOptions.add(callTrackingActionLink);
		
		// create call
		SimpleMenuOptionLink createCallActionLink = new SimpleMenuOptionLink("createCall", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.createCall"));
		createCallActionLink.setLinkUrl("/spring/stars/operator/general/createCall?" + queryString);
		menuOptions.add(createCallActionLink);
		
		// control history
		SimpleMenuOptionLink controlHistoryActionLink = new SimpleMenuOptionLink("controlHistory", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.controlHistory"));
		controlHistoryActionLink.setLinkUrl("/spring/stars/operator/general/contacts/controlHistory?" + queryString);
		menuOptions.add(controlHistoryActionLink);
		
		// enrollment
		SimpleMenuOptionLink enrollmentActionLink = new SimpleMenuOptionLink("enrollment", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.enrollment"));
		enrollmentActionLink.setLinkUrl("/spring/stars/operator/general/contacts/enrollment?" + queryString);
		menuOptions.add(enrollmentActionLink);
		
		// appliances
		SimpleMenuOptionLink appliancesActionLink = new SimpleMenuOptionLink("appliances", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.appliances"));
		appliancesActionLink.setLinkUrl("/spring/stars/operator/general/appliances?" + queryString);
		menuOptions.add(appliancesActionLink);
		
		// hardware
		SimpleMenuOptionLink hardwareActionLink = new SimpleMenuOptionLink("hardware", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.hardware"));
		hardwareActionLink.setLinkUrl("/spring/stars/operator/general/hardware?" + queryString);
		menuOptions.add(hardwareActionLink);
		
		// serviceRequest
		SimpleMenuOptionLink serviceRequestActionLink = new SimpleMenuOptionLink("serviceRequest", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.serviceRequest"));
		serviceRequestActionLink.setLinkUrl("/spring/stars/operator/general/serviceRequest?" + queryString);
		menuOptions.add(serviceRequestActionLink);
		
		// serviceHistory
		SimpleMenuOptionLink serviceHistoryActionLink = new SimpleMenuOptionLink("serviceHistory", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.serviceHistory"));
		serviceHistoryActionLink.setLinkUrl("/spring/stars/operator/general/serviceHistory?" + queryString);
		menuOptions.add(serviceHistoryActionLink);
		
		// changeLogin
		SimpleMenuOptionLink changeLoginActionLink = new SimpleMenuOptionLink("changeLogin", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.changeLogin"));
		changeLoginActionLink.setLinkUrl("/spring/stars/operator/general/changeLogin?" + queryString);
		menuOptions.add(changeLoginActionLink);
		
		// accountLog
		SimpleMenuOptionLink accountLogActionLink = new SimpleMenuOptionLink("accountLog", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.accountLog"));
		accountLogActionLink.setLinkUrl("/spring/stars/operator/general/account/accountLog?" + queryString);
		menuOptions.add(accountLogActionLink);

        // FAQ
        SimpleMenuOptionLink faqActionLink = new SimpleMenuOptionLink("FAQ", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.faq"));
        faqActionLink.setLinkUrl("/spring/stars/operator/faq?" + queryString);
        menuOptions.add(faqActionLink);

		return new LeftSideContextualMenuOptionsProducer(menuOptions, selectedMenuId);
	}

	public static SelectMenuConfiguration getAccountActionsSelectMenuConfiguration(int accountId, int energyCompanyId, YukonUserContext userContext) {
		
		SingleUserChecker singleUserChecker = new SingleUserChecker(userContext.getYukonUser());
		
		// url parameters
		Map<String, String> parameterMap = Maps.newLinkedHashMap();
		parameterMap.put("accountId", Integer.toString(accountId));
		parameterMap.put("energyCompanyId", Integer.toString(energyCompanyId));
		String queryString = ServletUtil.buildQueryStringFromMap(parameterMap, false);
		
		// menu base
		List<MenuOptionProducer> menuOptionsProducers = Lists.newArrayList();
		MenuBase menuBase = new MenuBase(menuOptionsProducers);
		
		// GENERAL
		//--------------------------------------------------------
		SubMenuOption generalSubMenu = new SubMenuOption("general", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.general"), false);
		StaticMenuOptionProducer generalSubMenuOptionProducer = new StaticMenuOptionProducer(generalSubMenu, singleUserChecker);
		menuOptionsProducers.add(generalSubMenuOptionProducer);
		List<MenuOptionProducer> generalMenuOptionsProducers = Lists.newArrayList();
		generalSubMenu.setSubOptions(generalMenuOptionsProducers);
		
		// contacts
		SimpleMenuOptionLink contactsActionLink = new SimpleMenuOptionLink("contacts", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.contacts"));
		contactsActionLink.setLinkUrl("/spring/stars/operator/general/contacts/contacts?" + queryString);
		MenuOptionProducer contactsAction = new StaticMenuOptionProducer(contactsActionLink, singleUserChecker);
		generalMenuOptionsProducers.add(contactsAction);
		
		// residence
		SimpleMenuOptionLink residenceActionLink = new SimpleMenuOptionLink("residence", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.residence"));
		residenceActionLink.setLinkUrl("/spring/stars/operator/general/residence/residenceEdit?" + queryString);
		MenuOptionProducer residenceAction = new StaticMenuOptionProducer(residenceActionLink, singleUserChecker);
		generalMenuOptionsProducers.add(residenceAction);
		
		// call tracking
		SimpleMenuOptionLink callTrackingActionLink = new SimpleMenuOptionLink("callTracking", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.callTracking"));
		callTrackingActionLink.setLinkUrl("/spring/stars/operator/general/callTracking?" + queryString);
		MenuOptionProducer callTrackingAction = new StaticMenuOptionProducer(callTrackingActionLink, singleUserChecker);
		generalMenuOptionsProducers.add(callTrackingAction);
		
		// create call
		SimpleMenuOptionLink createCallActionLink = new SimpleMenuOptionLink("createCall", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.createCall"));
		createCallActionLink.setLinkUrl("/spring/stars/operator/general/createCall?" + queryString);
		MenuOptionProducer createCallAction = new StaticMenuOptionProducer(createCallActionLink, singleUserChecker);
		generalMenuOptionsProducers.add(createCallAction);
		
		// PROGRAMS
		//--------------------------------------------------------
		SubMenuOption programsSubMenu = new SubMenuOption("programs", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.programs"), false);
		StaticMenuOptionProducer programsSubMenuOptionProducer = new StaticMenuOptionProducer(programsSubMenu, singleUserChecker);
		menuOptionsProducers.add(programsSubMenuOptionProducer);
		List<MenuOptionProducer> programMenuOptionsProducers = Lists.newArrayList();
		programsSubMenu.setSubOptions(programMenuOptionsProducers);
		
		// control history
		SimpleMenuOptionLink controlHistoryActionLink = new SimpleMenuOptionLink("controlHistory", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.controlHistory"));
		controlHistoryActionLink.setLinkUrl("/spring/stars/operator/general/contacts/controlHistory?" + queryString);
		MenuOptionProducer controlHistoryAction = new StaticMenuOptionProducer(controlHistoryActionLink, singleUserChecker);
		programMenuOptionsProducers.add(controlHistoryAction);
		
		// enrollment
		SimpleMenuOptionLink enrollmentActionLink = new SimpleMenuOptionLink("enrollment", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.enrollment"));
		enrollmentActionLink.setLinkUrl("/spring/stars/operator/general/contacts/enrollment?" + queryString);
		MenuOptionProducer enrollmentAction = new StaticMenuOptionProducer(enrollmentActionLink, singleUserChecker);
		programMenuOptionsProducers.add(enrollmentAction);
		
		SelectMenuConfiguration selectMenuConfiguration = new SelectMenuConfiguration();
		selectMenuConfiguration.setMenuBase(menuBase);
		selectMenuConfiguration.setHeaderKey("yukon.web.modules.operator.accountAction.header");
		
		return selectMenuConfiguration;
	}
}
