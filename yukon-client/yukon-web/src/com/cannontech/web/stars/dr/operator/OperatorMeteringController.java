package com.cannontech.web.stars.dr.operator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.dao.CustomerGraphDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerGraph;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.common.flashScope.FlashScope;
import com.cannontech.web.security.annotation.CheckRoleProperty;
import com.cannontech.web.stars.dr.operator.general.AccountInfoFragment;
import com.cannontech.web.stars.dr.operator.service.AccountInfoFragmentHelper;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

@Controller
@RequestMapping(value = "/operator/metering/*")
@CheckRoleProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_METERING_INTERVAL_DATA)
public class OperatorMeteringController {
	
	private RolePropertyDao rolePropertyDao;
	private IDatabaseCache databaseCache;
	private CustomerAccountDao customerAccountDao;
	private CustomerGraphDao customerGraphDao;
	
	// VIEW TREND
	@RequestMapping
    public String viewTrend(Integer gdefid,
    						ModelMap modelMap, 
	    					YukonUserContext userContext,
	    					AccountInfoFragment accountInfoFragment) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// disclaimer
		String disclaimer = rolePropertyDao.getPropertyStringValue(YukonRoleProperty.TRENDING_DISCLAIMER, userContext.getYukonUser());
		modelMap.addAttribute("disclaimer", disclaimer);
		
		// customerGraphWrappers
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
		List<CustomerGraph> customerGraphs = customerGraphDao.getByCustomerId(customerAccount.getCustomerId());
		
		// no trends, go to the select trends page
		if (customerGraphs.size() <= 0) {
			return "redirect:selectTrends";
		}
		
		List<CustomerGraphWrapper> customerGraphWrappers = Lists.newArrayListWithCapacity(customerGraphs.size());
		for (CustomerGraph customerGraph :customerGraphs) {
			String graphName = customerGraphDao.getGraphName(customerGraph.getGraphDefinitionId());
			customerGraphWrappers.add(new CustomerGraphWrapper(customerGraph, graphName));
		}
		modelMap.addAttribute("customerGraphWrappers", customerGraphWrappers);
		
		return "operator/metering/viewTrend.jsp";
	}
	
	// SELECT TRENDS
	@RequestMapping
	public String selectTrends(ModelMap modelMap, 
								YukonUserContext userContext,
								AccountInfoFragment accountInfoFragment) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// trends
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
		List<CustomerGraph> customerGraphs = customerGraphDao.getByCustomerId(customerAccount.getCustomerId());
		List<LiteGraphDefinition> allGraphDefinitions = databaseCache.getAllGraphDefinitions();
		
		List<Integer> customerGraphDefinitionIds = Lists.newArrayListWithCapacity(customerGraphs.size());
		for (CustomerGraph customerGraph :customerGraphs) {
			customerGraphDefinitionIds.add(customerGraph.getGraphDefinitionId());
		}
		
		// availablTrendWrapper
		List<AvailablTrendWrapper> availablTrendWrappers = Lists.newArrayListWithCapacity(allGraphDefinitions.size());
		for (LiteGraphDefinition gd : allGraphDefinitions) {
			availablTrendWrappers.add(new AvailablTrendWrapper(gd.getGraphDefinitionID(), gd.getName(), customerGraphDefinitionIds.contains(gd.getGraphDefinitionID())));
		}
		modelMap.addAttribute("availablTrendWrappers", availablTrendWrappers);
		
		return "operator/metering/selectTrends.jsp";
	}
	
	// SAVE SELECTED TRENDS
	@RequestMapping
	public String saveSelectedTrends(Integer[] graphDefinitionId,
									ModelMap modelMap, 
									YukonUserContext userContext,
									AccountInfoFragment accountInfoFragment,
									FlashScope flashScope) {
		
		AccountInfoFragmentHelper.setupModelMapBasics(accountInfoFragment, modelMap);
		
		// delete all trends for customer
		CustomerAccount customerAccount = customerAccountDao.getById(accountInfoFragment.getAccountId());
		int customerId = customerAccount.getCustomerId();
		customerGraphDao.deleteAllCustomerGraphsByCustomerId(customerId);
		
		// add back selected trends
		if (graphDefinitionId != null) {
			for (int i = 0; i < graphDefinitionId.length; i++) {
				
				int graphId = graphDefinitionId[i];
				
				CustomerGraph customerGraph = new CustomerGraph();
				customerGraph.setCustomerId(customerId);
				customerGraph.setGraphDefinitionId(graphId);
				customerGraph.setCustomerOrder(i);
				
				customerGraphDao.insert(customerGraph);
			}
		}
		
		flashScope.setConfirm(new YukonMessageSourceResolvable("yukon.web.modules.operator.selectTrends.availableTrends.trendsUpdated"));
		
		return "redirect:selectTrends";
	}
	
	public class CustomerGraphWrapper {
		
		private CustomerGraph customerGraph;
		private String name;
		
		public CustomerGraphWrapper(CustomerGraph customerGraph, String name) {
			this.customerGraph = customerGraph;
			this.name = name;
		}
		
		public CustomerGraph getCustomerGraph() {
			return customerGraph;
		}
		public String getName() {
			return name;
		}
	}
	
	public class AvailablTrendWrapper {
		
		private int graphDefinitionId;
		private String name;
		private boolean selected;
		
		public AvailablTrendWrapper(int graphDefinitionId, String name, boolean selected) {
			this.graphDefinitionId = graphDefinitionId;
			this.name = name;
			this.selected = selected;
		}

		public int getGraphDefinitionId() {
			return graphDefinitionId;
		}
		public String getName() {
			return name;
		}
		public boolean isSelected() {
			return selected;
		}
	}
	
	@Autowired
	public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
	
	@Autowired
	public void setDatabaseCache(IDatabaseCache databaseCache) {
		this.databaseCache = databaseCache;
	}
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
	@Autowired
	public void setCustomerGraphDao(CustomerGraphDao customerGraphDao) {
		this.customerGraphDao = customerGraphDao;
	}
}
