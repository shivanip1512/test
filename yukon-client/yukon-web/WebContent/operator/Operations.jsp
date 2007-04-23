<!--
	Main portal for operators
-->

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ include file="Consumer/include/StarsHeader.jsp" %>

<cti:standardPage title="Energy Services Operations Center" module="operations">
<cti:standardMenu/>

<script type="text/javascript">
	function confirmDelete() {
		if (confirm("Are you sure you want to delete the energy company and all customer account information belongs to it?")
			&& confirm("Are you really sure you want to delete the energy company?"))
			document.DeleteForm.submit();
	}
</script>

<div id="main">

<!-- Consumer Account Information section -->
<%
	Integer lastAcctOption = null;
	List<StarsSelectionListEntry> selectionList = null;
	
	if (selectionListTable != null) {
		lastAcctOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_ACCOUNT_SEARCH_OPTION);
		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get( YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE );
		
		StarsSelectionListEntry[] selectionListValues = searchByList.getStarsSelectionListEntry();
		selectionList = new ArrayList<StarsSelectionListEntry>();

		for(StarsSelectionListEntry entry : selectionListValues) {
			if(entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_METER_NO){
				selectionList.add(entry);
			}
		}
	}
%>

<c:set var="selectionListTable" scope="page" value="<%=selectionListTable%>" />
<c:set var="lastAcctOption" scope="page" value="<%=lastAcctOption%>" />
<c:set var="custSearchByList" scope="page" value="<%=selectionList%>" />

<cti:checkRole role="ConsumerInfoRole.ROLEID">

	<tags:operationSection sectionName="Consumer Account Information" sectionImageName="ConsumerLogo">
		<tags:sectionLink>
        	<a href="Consumer/New.jsp?Init=true<cti:checkProperty property="ConsumerInfoRole.NEW_ACCOUNT_WIZARD">&amp;Wizard=true</cti:checkProperty>">New Account</a>
        </tags:sectionLink>

		<c:set var="importID" scope="page">
			<cti:getProperty property="ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT"/>
		</c:set>
		<c:choose>
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'STARS')}">
				<c:set var="importUri" scope="page" value="ImportSTARS.jsp"/>
				<c:set var="importLabel" scope="page" value="Import STARS"/>
			</c:when> 
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'DSM')}">
				<c:set var="importUri" scope="page" value="ImportDSM.jsp"/>
				<c:set var="importLabel" scope="page" value="Import DSM"/>
			</c:when> 
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'upload')}">
				<c:set var="importUri" scope="page" value="GenericUpload.jsp"/>
				<c:set var="importLabel" scope="page" value="Upload File"/>
			</c:when>
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'true')}">
				<c:set var="importUri" scope="page" value="ImportAccount.jsp"/>
				<c:set var="importLabel" scope="page" value="Import Account"/>
				<tags:sectionLink>
		        	<a href="Consumer/${pageScope.importUri}">${pageScope.importLabel}</a>
		        </tags:sectionLink>
			</c:when>
		</c:choose>
		
		<!-- Customer search form -->
		<div class="sectionForm">
			<form name="custSearchForm" method="POST" action="<c:url value="/servlet/SOAPClient"/>">
				<input type="hidden" name="action" value="SearchCustAccount" />
				<div class="sectionFormLabel">Search for existing customer:</div>
				<div>
					<select name="SearchBy" onchange="document.custSearchForm.SearchValue.value=''">
						<c:if test="${pageScope.selectionListTable != null}">
							<c:forEach items="${pageScope.custSearchByList}" var="entry">
								<option value="${entry.entryID}" >${entry.content}</option>
							</c:forEach>
						</c:if>
					</select>
		
					<input type="text" name="SearchValue" size="15" value='${LAST_ACCOUNT_SEARCH_VALUE}'>
					<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.custSearchForm.submit();"> 
				</div>
			</form>
		</div>
	</tags:operationSection>
	
</cti:checkRole>

<!-- Metering section -->
<cti:checkRole role="CommercialMeteringRole.ROLEID">
	
	<tags:operationSection sectionName="Metering" sectionImageName="CommercialMeteringLogo">
		<cti:checkProperty property="CommercialMeteringRole.BILLING_ENABLED">
			<tags:sectionLink>
	        	<a href="Metering/Billing.jsp"><cti:getProperty property="BillingRole.HEADER_LABEL" defaultvalue="Billing"/></a>
	        </tags:sectionLink>
        </cti:checkProperty>
		<cti:checkProperty property="CommercialMeteringRole.TRENDING_ENABLED">
			<tags:sectionLink>
	        	<a href="Metering/Metering.jsp">All Trends</a>
	        </tags:sectionLink>
        </cti:checkProperty>
		<cti:checkProperty property="CommercialMeteringRole.IMPORTER_ENABLED">
			<tags:sectionLink>
	        	<a href="../bulk/importer.jsp">Bulk Importer</a>
	        </tags:sectionLink>
        </cti:checkProperty>
	</tags:operationSection>

</cti:checkRole>

<!-- Client Launcher section -->
<cti:checkProperty property="WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED">
    <c:import url="/jws/opspage"/>
</cti:checkProperty>

<!-- Load Response section -->
<cti:checkMultiRole roles="DirectLoadcontrolRole.ROLEID,DirectCurtailmentRole.ROLEID,EnergyBuybackRole.ROLEID,OddsForControlRole.ROLEID">

	<tags:operationSection sectionName="Load Response" sectionImageName="LoadResponseLogo">
		<cti:isPropertyTrue property="DirectLoadcontrolRole.DIRECT_CONTROL">
            <tags:sectionLink>
            	<a href="LoadControl/oper_direct.jsp">Direct</a>
            </tags:sectionLink>
		</cti:isPropertyTrue>

		<cti:checkRole role="DirectCurtailmentRole.ROLEID"> 
            <tags:sectionLink>
            	<a href="LoadControl/oper_mand.jsp"><cti:getProperty property="DirectCurtailmentRole.CURTAILMENT_LABEL"/></a>
            </tags:sectionLink>
		</cti:checkRole>

		<cti:checkRole role="EnergyBuybackRole.ROLEID"> 
            <tags:sectionLink>
            	<a href="LoadControl/oper_ee.jsp"><cti:getProperty property="EnergyBuybackRole.ENERGY_BUYBACK_LABEL"/></a>
            </tags:sectionLink>
		</cti:checkRole>
 
		<cti:checkRole role="CICurtailmentRole.ROLEID"> 
            <tags:sectionLink>
            	<a href="<c:url value="/cc/programSelect.jsf"/>"><cti:getProperty property="com.cannontech.roles.operator.CICurtailmentRole.CURTAILMENT_LABEL"/></a>
            </tags:sectionLink>
		</cti:checkRole>

		<cti:checkRole role="OddsForControlRole.ROLEID"> 
            <tags:sectionLink>
            	<a href="Consumer/Odds.jsp">Odds for Control</a>
            </tags:sectionLink>
		</cti:checkRole>
		<cti:isPropertyTrue property="DirectLoadcontrolRole.THREE_TIER_DIRECT"> 
            <tags:sectionLink>
            	<a href="../loadmgmt/controlareas.jsp">3-Tier Direct</a>
            </tags:sectionLink>
		</cti:isPropertyTrue>
	</tags:operationSection>

</cti:checkMultiRole>

<!-- Capacitor Control section -->
<cti:checkRole role="CBCSettingsRole.ROLEID">

	<tags:operationSection sectionName="Capacitor Control" sectionImageName="CapControlLogo">
	    <tags:sectionLink>
	    	<a href="../capcontrol/subareas.jsp">Cap Control</a>
	    </tags:sectionLink>
	</tags:operationSection>
	
</cti:checkRole>

<!-- Hardware Inventory section -->
<%
	Integer lastInvOption = null;
	List<StarsSelectionListEntry> invSelectionList = null;

	if (selectionListTable != null) {
		lastInvOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_INVENTORY_SEARCH_OPTION);
		YukonListEntry devTypeMCT = liteEC.getYukonListEntry(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_MCT);
		
		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY);
		invSelectionList = new ArrayList<StarsSelectionListEntry>();
		
		for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
			if (entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_METER_NO 
				&& entry.getYukonDefID() != YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_DEVICE_NAME 
				&& devTypeMCT != null)
			{
				invSelectionList.add(entry);
			}
		}
	}

	String hardwarePage = "Hardware/Inventory.jsp";
	if(((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)) == null || 
		((ArrayList)session.getAttribute(ServletUtil.FILTER_INVEN_LIST)).size() < 1) { 
		hardwarePage = "Hardware/Filter.jsp";

	}
%>

<c:set var="lastInvOption" scope="page" value="<%=lastInvOption%>" />
<c:set var="invSearchByList" scope="page" value="<%=invSelectionList%>" />
<c:set var="hardwarePage" scope="page" value="<%=hardwarePage%>" />	

<cti:checkRole roleid="<%= InventoryRole.ROLEID %>">

	<tags:operationSection sectionName="Hardware Inventory" sectionImageName="HardwareInventoryLogo">
    	<cti:checkProperty property="InventoryRole.INVENTORY_SHOW_ALL"> 
        	<tags:sectionLink>
            	<a href="${hardwarePage}">Inventory</a>
            </tags:sectionLink>
        </cti:checkProperty>
        <tags:sectionLink>
        	<a href="Hardware/PurchaseTrack.jsp">Purchasing</a>
        </tags:sectionLink>
        
        <!-- Hardware search form -->
		<div class="sectionForm">
			<form name="invSearchForm" method="POST" action="<c:url value="/servlet/InventoryManager"/>">
				<input type="hidden" name="action" value="SearchInventory">
				<input type="hidden" name="REDIRECT" value="<c:url value="/operator/Hardware/ResultSet.jsp"/>">
				<div class="sectionFormLabel">Search for existing hardware:</div>
				<div>
					<select name="SearchBy" onchange="document.invSearchForm.SearchValue.value=''">
						<c:if test="${pageScope.selectionListTable != null}">
							<c:forEach items="${pageScope.invSearchByList}" var="entry">
								<option value="${entry.yukonDefID}" >${entry.content}</option>
							</c:forEach>
						</c:if>
					</select>
		
					<input type="text" name="SearchValue" size="15" value="${LAST_INVENTORY_SEARCH_VALUE}">
					<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onclick="Javascript:document.invSearchForm.submit();" >
				</div>
			</form>
		</div>
	</tags:operationSection>

</cti:checkRole>

<!-- Work Orders section -->

<%
	Integer lastSrvcOption = null;
	List<StarsSelectionListEntry> srvcSearchByList = null;

	if (selectionListTable != null) {

		lastSrvcOption = (Integer) session.getAttribute(ServletUtils.ATT_LAST_SERVICE_SEARCH_OPTION);

		StarsCustSelectionList searchByList = (StarsCustSelectionList) selectionListTable.get(YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY);
		srvcSearchByList = new ArrayList<StarsSelectionListEntry>();
		
		for (int i = 0; i < searchByList.getStarsSelectionListEntryCount(); i++) {
			StarsSelectionListEntry entry = searchByList.getStarsSelectionListEntry(i);
			srvcSearchByList.add(entry);
		}
	}
%>

<c:set var="lastSrvcOption" scope="page" value="<%=lastSrvcOption%>" />
<c:set var="srvcSearchByList" scope="page" value="<%=srvcSearchByList%>" />

<cti:checkRole roleid="<%= WorkOrderRole.ROLEID %>">

	<tags:operationSection sectionName="Work Orders" sectionImageName="WorkOrdersLogo">
    	<cti:checkProperty property="WorkOrderRole.WORK_ORDER_SHOW_ALL"> 
            <tags:sectionLink>
            	<a href="WorkOrder/WOFilter.jsp">Service Order List</a>
            </tags:sectionLink>
		</cti:checkProperty>
		
		<!-- Service order search form -->
		<div class="sectionForm">
			<form name="soSearchForm" method="post" action="<c:url value="/servlet/WorkOrderManager"/>">
				<input type="hidden" name="action" value="SearchWorkOrder">
				<input type="hidden" name="REDIRECT" value="<c:url value="/operator/WorkOrder/SearchResults.jsp"/>">
				
				<div class="sectionFormLabel">Search for existing service order:</div>
				<div>
					<select name="SearchBy" onchange="document.soSearchForm.SearchValue.value=''">
						<c:if test="${pageScope.selectionListTable != null}">
							<c:forEach items="${pageScope.srvcSearchByList}" var="entry">
								<option value="${entry.yukonDefID}" >${entry.content}</option>
							</c:forEach>
						</c:if>
					</select>
		
					<input type="text" name="SearchValue" size="15" value="${LAST_SERVICE_SEARCH_VALUE}">
					<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.soSearchForm.submit();" >
				</div>
			</form>
		</div>
	</tags:operationSection>

</cti:checkRole> 

<!-- Analysis section -->
<cti:checkMultiRole roles="ReportingRole.ROLEID, CommanderRole.ROLEID">

	<tags:operationSection sectionName="Analysis" sectionImageName="AnalysisLogo">
        <cti:checkRole role="ReportingRole.ROLEID">
            <tags:sectionLink>
	            <a href="../analysis/Reports.jsp">Reporting</a>
            </tags:sectionLink>
        </cti:checkRole>
        <cti:checkRole role="CommanderRole.ROLEID">
            <tags:sectionLink>
            	<a href="../apps/SelectDevice.jsp">Commander</a>
            </tags:sectionLink>
        </cti:checkRole>
	</tags:operationSection>

</cti:checkMultiRole>

<!-- Administration section -->
<cti:checkRole role="operator.AdministratorRole.ROLEID">

	<tags:operationSection sectionName="Administration" sectionImageName="AdministrationLogo">
		
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_CONFIG_ENERGY_COMPANY"> 
	            <tags:sectionLink>
	            	<a href="Admin/AdminTest.jsp">Config Energy Company</a>
	            </tags:sectionLink>
			</cti:checkProperty>
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_MANAGE_MEMBERS"> 
	            <tags:sectionLink>
	            	<a href="Admin/ManageMembers.jsp">Member <br/> Management</a>
	            </tags:sectionLink>
			</cti:checkProperty>
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_CREATE_ENERGY_COMPANY"> 
	            <tags:sectionLink>
	            	<a href="Admin/NewEnergyCompany.jsp">New Energy <br/> Company</a>
	            </tags:sectionLink>
			</cti:checkProperty> 
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_DELETE_ENERGY_COMPANY"> 
	        	<form name="DeleteForm" method="post" action="<c:url value="/servlet/StarsAdmin"/>">
	            	<input type="hidden" name="action" value="DeleteEnergyCompany">
	            	<input type="hidden" name="REDIRECT" value="<c:url value="/servlet/LoginController?ACTION=LOGOUT"/>">
            		<tags:sectionLink>
              			<a href="javascript:confirmDelete()">Delete Energy Company</a>
	            	</tags:sectionLink>
	        	</form>
			</cti:checkProperty>
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS"> 
	            <tags:sectionLink>
	            	<a href="Admin/SwitchCommands.jsp">View Batch Commands</a>
	            </tags:sectionLink>
			</cti:checkProperty>
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS"> 
	            <tags:sectionLink>
	            	<a href="Admin/OptOutEvents.jsp">View Scheduled <cti:getProperty property="ConsumerInfoRole.WEB_TEXT_OPT_OUT_NOUN" defaultvalue="opt out" format="all_capital"/> Events</a>
	            </tags:sectionLink>
			</cti:checkProperty> 
			<cti:checkProperty property="operator.AdministratorRole.ADMIN_MULTISPEAK_SETUP"> 
	            <tags:sectionLink>
	            	<a href="../msp_setup.jsp">Multispeak Setup</a>
	            </tags:sectionLink>
			</cti:checkProperty>
			<cti:checkProperty property="AdministratorRole.ADMIN_LM_USER_ASSIGN"> 
	            <tags:sectionLink>
	            	<a href="/spring/editor/userGroupSelector">User/Group Editor</a>
	            </tags:sectionLink>
			</cti:checkProperty>
            <tags:sectionLink>
            	<a href="/logging/">View Logs</a>
            </tags:sectionLink>
            <tags:sectionLink>
            	<a href="/index/manage">Manage Indexes</a>
            </tags:sectionLink>
	</tags:operationSection>
	
</cti:checkRole>

</div>
</cti:standardPage>