package com.cannontech.web.stars.dr.operator;

import java.util.List;
import java.util.Map;

import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.user.YukonUserContext;
import com.cannontech.user.checker.SingleUserChecker;
import com.cannontech.util.ServletUtil;
import com.cannontech.web.menu.MenuBase;
import com.cannontech.web.menu.option.SimpleMenuOptionLink;
import com.cannontech.web.menu.option.producer.MenuOptionProducer;
import com.cannontech.web.menu.option.producer.StaticMenuOptionProducer;
import com.cannontech.web.menu.renderer.SelectMenuConfiguration;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class OperatorActionsFactory {
	
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
		
		// account
		SimpleMenuOptionLink accountEditActionLink = new SimpleMenuOptionLink("accountEdit", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.accountEdit"));
		accountEditActionLink.setLinkUrl("/spring/stars/operator/general/account/accountEdit?" + queryString);
		MenuOptionProducer accountEditAction = new StaticMenuOptionProducer(accountEditActionLink, singleUserChecker);
		menuOptionsProducers.add(accountEditAction);

		// contacts
		SimpleMenuOptionLink contactsActionLink = new SimpleMenuOptionLink("contacts", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.contacts"));
		contactsActionLink.setLinkUrl("/spring/stars/operator/general/contacts/contactList?" + queryString);
		MenuOptionProducer contactsAction = new StaticMenuOptionProducer(contactsActionLink, singleUserChecker);
		menuOptionsProducers.add(contactsAction);
		
		// residence
		SimpleMenuOptionLink residenceActionLink = new SimpleMenuOptionLink("residence", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.residence"));
		residenceActionLink.setLinkUrl("/spring/stars/operator/general/residence/residenceEdit?" + queryString);
		MenuOptionProducer residenceAction = new StaticMenuOptionProducer(residenceActionLink, singleUserChecker);
		menuOptionsProducers.add(residenceAction);
		
		// call tracking
		SimpleMenuOptionLink callTrackingActionLink = new SimpleMenuOptionLink("callTracking", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.callTracking"));
		callTrackingActionLink.setLinkUrl("/spring/stars/operator/general/callTracking?" + queryString);
		MenuOptionProducer callTrackingAction = new StaticMenuOptionProducer(callTrackingActionLink, singleUserChecker);
		menuOptionsProducers.add(callTrackingAction);

		// control history
		SimpleMenuOptionLink controlHistoryActionLink = new SimpleMenuOptionLink("controlHistory", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.controlHistory"));
		controlHistoryActionLink.setLinkUrl("/spring/stars/operator/general/contacts/controlHistory?" + queryString);
		MenuOptionProducer controlHistoryAction = new StaticMenuOptionProducer(controlHistoryActionLink, singleUserChecker);
		menuOptionsProducers.add(controlHistoryAction);
		
		// enrollment
		SimpleMenuOptionLink enrollmentActionLink = new SimpleMenuOptionLink("enrollment", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.enrollment"));
		enrollmentActionLink.setLinkUrl("/spring/stars/operator/general/contacts/enrollment?" + queryString);
		MenuOptionProducer enrollmentAction = new StaticMenuOptionProducer(enrollmentActionLink, singleUserChecker);
		menuOptionsProducers.add(enrollmentAction);

		// appliances
		SimpleMenuOptionLink appliancesActionLink = new SimpleMenuOptionLink("enrollment", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.appliances"));
		appliancesActionLink.setLinkUrl("/spring/stars/operator/general/appliances?" + queryString);
		MenuOptionProducer appliancesAction = new StaticMenuOptionProducer(appliancesActionLink, singleUserChecker);
		menuOptionsProducers.add(appliancesAction);

		// hardware
		SimpleMenuOptionLink hardwareActionLink = new SimpleMenuOptionLink("hardware", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.hardware"));
		hardwareActionLink.setLinkUrl("/spring/stars/operator/general/hardware?" + queryString);
		MenuOptionProducer hardwareAction = new StaticMenuOptionProducer(hardwareActionLink, singleUserChecker);
		menuOptionsProducers.add(hardwareAction);

		// serviceRequest
		SimpleMenuOptionLink serviceRequestActionLink = new SimpleMenuOptionLink("serviceRequest", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.serviceRequest"));
		serviceRequestActionLink.setLinkUrl("/spring/stars/operator/general/serviceRequest?" + queryString);
		MenuOptionProducer serviceRequestAction = new StaticMenuOptionProducer(serviceRequestActionLink, singleUserChecker);
		menuOptionsProducers.add(serviceRequestAction);

		// changeLogin
		SimpleMenuOptionLink changeLoginActionLink = new SimpleMenuOptionLink("changeLogin", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.changeLogin"));
		changeLoginActionLink.setLinkUrl("/spring/stars/operator/general/changeLogin?" + queryString);
		MenuOptionProducer changeLoginAction = new StaticMenuOptionProducer(changeLoginActionLink, singleUserChecker);
		menuOptionsProducers.add(changeLoginAction);
		
		// accountLog
		SimpleMenuOptionLink accountLogActionLink = new SimpleMenuOptionLink("accountLog", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.accountLog"));
		accountLogActionLink.setLinkUrl("/spring/stars/operator/general/account/accountLog?" + queryString);
		MenuOptionProducer accountLogAction = new StaticMenuOptionProducer(accountLogActionLink, singleUserChecker);
		menuOptionsProducers.add(accountLogAction);

		// faq
		SimpleMenuOptionLink faqActionLink = new SimpleMenuOptionLink("faq", new YukonMessageSourceResolvable("yukon.web.modules.operator.accountAction.faq"));
		faqActionLink.setLinkUrl("/spring/stars/operator/general/account/accountLog?" + queryString);
		MenuOptionProducer faqAction = new StaticMenuOptionProducer(faqActionLink, singleUserChecker);
		menuOptionsProducers.add(faqAction);
		
		
		SelectMenuConfiguration selectMenuConfiguration = new SelectMenuConfiguration();
		selectMenuConfiguration.setMenuBase(menuBase);
		selectMenuConfiguration.setHeaderKey("yukon.web.modules.operator.accountAction.operatorTempMenu");
		
		return selectMenuConfiguration;
	}
}
