<!--
	Main portal for operators
-->

<%@ page import="com.cannontech.roles.operator.InventoryRole" %>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:verifyRolesAndProperties value="
    ConsumerInfoRole, 
    operator.MeteringRole,
    application.BillingRole,
    TrendingRole,
    WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED,
    loadcontrol.DirectLoadcontrolRole,
    OddsForControlRole,
    CICurtailmentRole,
    CBCSettingsRole,
    InventoryRole,
    WorkOrderRole,
    ReportingRole, 
    CommanderRole,
    operator.AdministratorRole"/>

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

<cti:starsOperations/>

<cti:checkRole role="ConsumerInfoRole.ROLEID">

	<tags:operationSection sectionName="Consumer Account Information" sectionImageName="ConsumerLogo">
		<cti:checkProperty property="ConsumerInfoRole.NEW_ACCOUNT_WIZARD">
			<tags:sectionLink>
	        	<a href="Consumer/New.jsp?Init=true<cti:checkProperty property="ConsumerInfoRole.NEW_ACCOUNT_WIZARD">&amp;Wizard=true</cti:checkProperty>">New Account</a>
	        </tags:sectionLink>
		</cti:checkProperty>
		<c:set var="importID" scope="page">
			<cti:getProperty property="ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT"/>
		</c:set>
		<c:choose>
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'true')}">
				<c:set var="importUri" scope="page" value="ImportAccount.jsp"/>
				<c:set var="importLabel" scope="page" value="Import Account"/>
				<tags:sectionLink>
		        	<a href="Consumer/${pageScope.importUri}">${pageScope.importLabel}</a>
		        </tags:sectionLink>
			</c:when>
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'EnrollMigration')}">
				<c:set var="importUri" scope="page" value="MigrateEnrollment.jsp"/>
				<c:set var="importLabel" scope="page" value="Migrate Enrollment Information"/>
				<tags:sectionLink>
		        	<a href="Consumer/${pageScope.importUri}">${pageScope.importLabel}</a>
		        </tags:sectionLink>
			</c:when> 
			<c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'upload')}">
				<c:set var="importUri" scope="page" value="GenericUpload.jsp"/>
				<c:set var="importLabel" scope="page" value="Upload File"/>
			</c:when>
		</c:choose>
		
		<!-- Customer search form -->
		<c:if test="${showStarsList}">
			<div class="sectionForm">
				<form name="custSearchForm" method="POST" action="<c:url value="/servlet/SOAPClient"/>">
					<input type="hidden" name="action" value="SearchCustAccount" />
					<div class="sectionFormLabel">Search for existing customer:</div>
					<div>
						<select name="SearchBy" onchange="document.custSearchForm.SearchValue.value=''">
							<c:forEach items="${customerSearchList}" var="entry">
								<option value="${entry.entryID}" >${entry.content}</option>
							</c:forEach>
						</select>
			
						<input type="text" name="SearchValue" size="15" value=''>
						<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.custSearchForm.submit();"> 
					</div>
				</form>
			</div>
        </c:if>
	</tags:operationSection>
	
</cti:checkRole>

<!-- Metering section -->
<cti:checkMultiRole roles="operator.MeteringRole.ROLEID,application.BillingRole.ROLEID,TrendingRole.ROLEID,operator.DeviceActionsRole.ROLEID">
	<tags:operationSection sectionName="Metering" sectionImageName="MeteringLogo">
		<cti:checkRole role="TrendingRole.ROLEID">
			<tags:sectionLink>
	        	<a href="Metering/Metering.jsp">All Trends</a>
	        </tags:sectionLink>
        </cti:checkRole>
        <cti:checkRole role="MeteringRole.ROLEID">
			<cti:checkProperty property="operator.MeteringRole.IMPORTER_ENABLED">
				<tags:sectionLink>
		        	<a href="/spring/amr/bulkimporter/home">Bulk Importer</a>
		        </tags:sectionLink>
	        </cti:checkProperty>
        </cti:checkRole>
        <cti:checkMultiRole roles="operator.MeteringRole.ROLEID,application.BillingRole.ROLEID,SchedulerRole.ROLEID,CommanderRole.ROLEID">
			<tags:sectionLink>
    	    	<a href="<c:url value="/spring/csr/search"/>">Metering</a>
	        </tags:sectionLink>
        </cti:checkMultiRole>
        
        <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
        <tags:sectionLink>
            <a href="<c:url value="/spring/bulk/bulkHome"/>">Bulk Operations</a>
        </tags:sectionLink>
        </cti:checkRole>
        
	</tags:operationSection>

</cti:checkMultiRole>

<!-- Client Launcher section -->
<cti:checkProperty property="WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED">
    <c:import url="/jws/opspage"/>
</cti:checkProperty>

<!-- Load Response section -->
<cti:checkMultiRole roles="loadcontrol.DirectLoadcontrolRole.ROLEID,OddsForControlRole.ROLEID,CICurtailmentRole.ROLEID">

	<tags:operationSection sectionName="Load Response" sectionImageName="LoadResponseLogo">
		<cti:isPropertyTrue property="loadcontrol.DirectLoadcontrolRole.DIRECT_CONTROL">
            <tags:sectionLink>
            	<a href="LoadControl/oper_direct.jsp">Direct</a>
            </tags:sectionLink>
		</cti:isPropertyTrue>
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
		<cti:isPropertyTrue property="loadcontrol.DirectLoadcontrolRole.THREE_TIER_DIRECT"> 
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
<cti:checkRole roleid="<%= InventoryRole.ROLEID %>">

	<tags:operationSection sectionName="Hardware Inventory" sectionImageName="HardwareInventoryLogo">
    	<cti:checkProperty property="InventoryRole.INVENTORY_SHOW_ALL"> 
        	<tags:sectionLink>
            	<a href="${hardwarePage}">Inventory</a>
            </tags:sectionLink>
        </cti:checkProperty>
        <cti:checkProperty property="InventoryRole.PURCHASING_ACCESS"> 
	        <tags:sectionLink>
	        	<a href="Hardware/PurchaseTrack.jsp">Purchasing</a>
	        </tags:sectionLink>
	    </cti:checkProperty>
        
        <!-- Hardware search form -->
        <c:if test="${showStarsList}">
			<div class="sectionForm">
				<form name="invSearchForm" method="POST" action="<c:url value="/servlet/InventoryManager"/>">
					<input type="hidden" name="action" value="SearchInventory">
					<input type="hidden" name="REDIRECT" value="<c:url value="/operator/Hardware/ResultSet.jsp"/>">
					<div class="sectionFormLabel">Search for existing hardware:</div>
					<div>
						<select name="SearchBy" onchange="document.invSearchForm.SearchValue.value=''">
							<c:forEach items="${inventorySearchList}" var="entry">
								<option value="${entry.yukonDefID}" >${entry.content}</option>
							</c:forEach>
						</select>
			
						<input type="text" name="SearchValue" size="15" value="">
						<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onclick="Javascript:document.invSearchForm.submit();" >
					</div>
				</form>
			</div>
		</c:if>
	</tags:operationSection>

</cti:checkRole>

<!-- Work Orders section -->
<cti:checkRole roleid="<%= WorkOrderRole.ROLEID %>">

	<tags:operationSection sectionName="Work Orders" sectionImageName="WorkOrdersLogo">
    	<cti:checkProperty property="WorkOrderRole.WORK_ORDER_SHOW_ALL"> 
            <tags:sectionLink>
            	<a href="${serviceOrderPage}">Service Order List</a>
            </tags:sectionLink>
		</cti:checkProperty>
		
		<!-- Service order search form -->
		<c:if test="${showStarsList}">
			<div class="sectionForm">
				<form name="soSearchForm" method="post" action="<c:url value="/servlet/WorkOrderManager"/>">
					<input type="hidden" name="action" value="SearchWorkOrder">
					<input type="hidden" name="REDIRECT" value="<c:url value="/operator/WorkOrder/SearchResults.jsp"/>">
					
					<div class="sectionFormLabel">Search for existing service order:</div>
					<div>
						<select name="SearchBy" onchange="document.soSearchForm.SearchValue.value=''">
							<c:forEach items="${serviceOrderSearchList}" var="entry">
								<option value="${entry.yukonDefID}" >${entry.content}</option>
							</c:forEach>
						</select>
			
						<input type="text" name="SearchValue" size="15" value="">
						<img class="cssicon" src="<c:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" alt="search" onClick="Javascript:document.soSearchForm.submit();" >
					</div>
				</form>
			</div>
		</c:if>
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
	            	<a href="Admin/ConfigEnergyCompany.jsp">Config Energy Company</a>
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
	            	<a href="../msp_setup.jsp">MultiSpeak Setup</a>
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
            <cti:checkProperty property="AdministratorRole.ADMIN_VIEW_CONFIG"> 
	            <tags:sectionLink>
		   	    	<a href="<c:url value="/spring/deviceConfiguration?home"/>">Device Configuration</a>
		        </tags:sectionLink>
	        </cti:checkProperty>
	</tags:operationSection>
	
</cti:checkRole>

</div>
</cti:standardPage>