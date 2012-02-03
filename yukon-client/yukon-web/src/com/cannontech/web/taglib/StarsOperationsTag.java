package com.cannontech.web.taglib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.general.model.OperatorAccountSearchBy;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.serialize.StarsCustSelectionList;
import com.cannontech.stars.xml.serialize.StarsCustomerSelectionLists;
import com.cannontech.stars.xml.serialize.StarsEnergyCompanySettings;
import com.cannontech.stars.xml.serialize.StarsSelectionListEntry;
import com.cannontech.util.ServletUtil;

/**
 * Tag used in place of StarsHeader and scriptlets on Operations.jsp to provide 
 * the dropdown lists for Stars searches.
 */
public class StarsOperationsTag extends YukonTagSupport {

	@Override
	public void doTag() throws JspException, IOException {

		HttpSession session = getRequest().getSession();
		
		StarsEnergyCompanySettings settings = (StarsEnergyCompanySettings) session.getAttribute(
				ServletUtils.ATT_ENERGY_COMPANY_SETTINGS);
		
		if (settings != null) {
			StarsYukonUser user = (StarsYukonUser) session
					.getAttribute(ServletUtils.ATT_STARS_YUKON_USER);
			LiteStarsEnergyCompany liteEC = StarsDatabaseCache.getInstance()
					.getEnergyCompany(user.getEnergyCompanyID());

			StarsCustomerSelectionLists starsCustomerSelectionLists = settings
					.getStarsCustomerSelectionLists();

			List<StarsSelectionListEntry> customerSearchList = new ArrayList<StarsSelectionListEntry>();
			List<StarsSelectionListEntry> inventorySearchList = new ArrayList<StarsSelectionListEntry>();
			List<StarsSelectionListEntry> serviceOrderSearchList = new ArrayList<StarsSelectionListEntry>();

			for (int i = 0; i < starsCustomerSelectionLists.getStarsCustSelectionListCount(); i++) {
				StarsCustSelectionList custSelectionList = starsCustomerSelectionLists
						.getStarsCustSelectionList(i);

				// Customer search by list
				if (YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE
						.equals(custSelectionList.getListName())) {
					StarsSelectionListEntry[] selectionListValues = custSelectionList
							.getStarsSelectionListEntry();
					for (StarsSelectionListEntry entry : selectionListValues) {
						if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO) {
							customerSearchList.add(entry);
						}
					}
				}

				// Inventory search by list
				if (YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY
						.equals(custSelectionList.getListName())) {
					YukonListEntry devTypeMCT = liteEC
							.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);

					StarsSelectionListEntry[] selectionListValues = custSelectionList
							.getStarsSelectionListEntry();
					for (StarsSelectionListEntry entry : selectionListValues) {
						if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_METER_NO
								&& entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME
								&& devTypeMCT != null) {
							inventorySearchList.add(entry);
						}
					}
				}
				
				// Service order search by list
				if (YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY.equals(custSelectionList.getListName())) {
					StarsSelectionListEntry[] selectionListValues = custSelectionList
					.getStarsSelectionListEntry();
					for (StarsSelectionListEntry entry : selectionListValues) {
						serviceOrderSearchList.add(entry);
					}
					
				}
				
			}
			
			getJspContext().setAttribute("customerSearchList", customerSearchList);
			getJspContext().setAttribute("inventorySearchList", inventorySearchList);
			getJspContext().setAttribute("serviceOrderSearchList", serviceOrderSearchList);
			getJspContext().setAttribute("operatorAccountSearchBys", OperatorAccountSearchBy.values());
		}

		String hardwarePage = "Hardware/Inventory.jsp";
		List<?> inventoryList = (List<?>) session.getAttribute(ServletUtil.FILTER_INVEN_LIST);
		if (inventoryList == null || inventoryList.size() < 1) {
			hardwarePage = "Hardware/Filter.jsp";
		}
		getJspContext().setAttribute("hardwarePage", hardwarePage);

		String serviceOrderPage = "WorkOrder/WorkOrder.jsp";
		List<?> serviceOrderList = (List<?>) session.getAttribute(ServletUtil.FILTER_WORKORDER_LIST);
		if (serviceOrderList == null || serviceOrderList.size() < 1) {
			serviceOrderPage = "WorkOrder/WOFilter.jsp";
		}
		getJspContext().setAttribute("serviceOrderPage", serviceOrderPage);

	}

}
