<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ page import="com.cannontech.roles.operator.InventoryRole"%>
<%@ page import="com.cannontech.roles.operator.WorkOrderRole"%>

<cti:verifyRolesAndProperties
    value="
    CONSUMER_INFO, 
    METERING,
    APPLICATION_BILLING,
    TRENDING,
    JAVA_WEB_START_LAUNCHER_ENABLED,
    LM_DIRECT_LOADCONTROL,
    ODDS_FOR_CONTROL,
    CI_CURTAILMENT,
    CBC_SETTINGS,
    INVENTORY,
    WORK_ORDER,
    REPORTING, 
    COMMANDER,
    OPERATOR_ADMINISTRATOR" />

<cti:standardPage title="Energy Services Operations Center" module="operations">
    <cti:standardMenu />

    <script type="text/javascript">
	function confirmDelete() {
		if (confirm("Are you sure you want to delete the energy company and all customer account information belongs to it?")
			&& confirm("Are you really sure you want to delete the energy company?"))
			document.DeleteForm.submit();
	}
</script>

    <div id="main">

        <cti:operationsSetup />
        <cti:starsOperations />

        <cti:checkRole role="ConsumerInfoRole.ROLEID">

            <tags:operationSection sectionName="Consumer Account Information" sectionImageName="ConsumerLogo">
                <cti:checkRolesAndProperties value="OPERATOR_NEW_ACCOUNT_WIZARD">
                    <tags:sectionLink>
                        <a href="/spring/stars/operator/account/accountCreate"><cti:msg key="yukon.web.menu.portal.consumerAccountInformation.newAccount" />
                        </a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <c:set var="importID" scope="page">
                    <cti:getProperty property="ConsumerInfoRole.IMPORT_CUSTOMER_ACCOUNT" />
                </c:set>
                <c:choose>
                    <c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'true')}">
                        <c:set var="importUri" scope="page" value="/spring/stars/operator/account/accountImport" />
                        <c:set var="importLabel" scope="page" value="Import Account" />
                        <tags:sectionLink>
                            <a href="${pageScope.importUri}">${pageScope.importLabel}</a>
                        </tags:sectionLink>
                    </c:when>
                    <c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'EnrollMigration')}">
                        <c:set var="importUri" scope="page" value="MigrateEnrollment.jsp" />
                        <c:set var="importLabel" scope="page" value="Migrate Enrollment Information" />
                        <tags:sectionLink>
                            <a href="Consumer/${pageScope.importUri}">${pageScope.importLabel}</a>
                        </tags:sectionLink>
                    </c:when>
                    <c:when test="${cti:equalsIgnoreCase(pageScope.importID, 'upload')}">
                        <c:set var="importUri" scope="page" value="GenericUpload.jsp" />
                        <c:set var="importLabel" scope="page" value="Upload File" />
                    </c:when>
                </c:choose>

                <cti:checkMultiProperty
                    property="ConsumerInfoRole.OPT_OUT_ADMIN_STATUS,ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_ENABLE,ConsumerInfoRole.OPT_OUT_ADMIN_CANCEL_CURRENT,ConsumerInfoRole.OPT_OUT_ADMIN_CHANGE_COUNTS">
                    <tags:sectionLink>
                        <a href="/spring/stars/operator/optOut/admin"><cti:msg key="yukon.web.menu.portal.consumerAccountInformation.optOutAdmin" />
                        </a>
                    </tags:sectionLink>
                </cti:checkMultiProperty>

                <!-- Customer search form -->
                <cti:checkRolesAndProperties value="OPERATOR_ACCOUNT_SEARCH">
                    <div class="sectionForm">
                        <form id="accountSearchForm" action="/spring/stars/operator/account/search" method="get">
                            <div class="sectionFormLabel">
                                <cti:msg key="yukon.web.modules.operator.search.searchPrompt" />
                            </div>
                            <div>
                                <select name="searchBy" onchange="$('accountSearchValue').value = ''">
                                    <c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}">
                                        <option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == searchBy}">selected</c:if>>
                                            <cti:msg key="${operatorAccountSearchBy.formatKey}" />
                                        </option>
                                    </c:forEach>
                                </select> <input type="text" name="searchValue" id="accountSearchValue" value="" size="15"> 
                                <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" 
                                     alt="search" onClick="$('accountSearchForm').submit();">
                            </div>
                        </form>
                    </div>
                </cti:checkRolesAndProperties>
            </tags:operationSection>
        </cti:checkRole>

        <!-- Metering section -->
        <cti:checkMultiRole roles="operator.MeteringRole.ROLEID,application.BillingRole.ROLEID,TrendingRole.ROLEID,operator.DeviceActionsRole.ROLEID,SchedulerRole.ROLEID">
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
                <cti:checkMultiRole roles="operator.MeteringRole.ROLEID,application.BillingRole.ROLEID,SchedulerRole.ROLEID">
                    <tags:sectionLink>
                        <a href="<cti:url value="/spring/meter/start"/>">Metering</a>
                    </tags:sectionLink>
                </cti:checkMultiRole>

                <cti:checkRole role="operator.DeviceActionsRole.ROLEID">
                    <tags:sectionLink>
                        <a href="<cti:url value="/spring/bulk/bulkHome"/>">Bulk Operations</a>
                    </tags:sectionLink>
                </cti:checkRole>

            </tags:operationSection>

        </cti:checkMultiRole>

        <!-- Client Launcher section -->
        <cti:checkProperty property="WebClientRole.JAVA_WEB_START_LAUNCHER_ENABLED">
            <c:import url="/jws/opspage" />
        </cti:checkProperty>

        <!-- Load Response section -->
        <cti:checkMultiRole roles="loadcontrol.DirectLoadcontrolRole.ROLEID,OddsForControlRole.ROLEID,CICurtailmentRole.ROLEID">

            <tags:operationSection sectionName="Load Response" sectionImageName="LoadResponseLogo">
                <cti:checkRole role="loadcontrol.DirectLoadcontrolRole.ROLEID">
                    <cti:isPropertyTrue property="loadcontrol.DirectLoadcontrolRole.DIRECT_CONTROL">
                        <tags:sectionLink>
                            <a href="LoadControl/oper_direct.jsp">Direct</a>
                        </tags:sectionLink>
                    </cti:isPropertyTrue>
                </cti:checkRole>
                <cti:checkRole role="CICurtailmentRole.ROLEID">
                    <tags:sectionLink>
                        <a href="<cti:url value="/cc/programSelect.jsf"/>"><cti:getProperty property="com.cannontech.roles.operator.CICurtailmentRole.CURTAILMENT_LABEL" />
                        </a>
                    </tags:sectionLink>
                </cti:checkRole>
                <cti:checkRole role="OddsForControlRole.ROLEID">
                    <tags:sectionLink>
                        <a href="Consumer/Odds.jsp">Odds for Control</a>
                    </tags:sectionLink>
                </cti:checkRole>
                <cti:isPropertyTrue property="loadcontrol.DirectLoadcontrolRole.DEMAND_RESPONSE">
                    <tags:sectionLink>
                        <a href="/spring/dr/home"><cti:msg key="yukon.web.operations.demandResponse" /></a>
                    </tags:sectionLink>
                </cti:isPropertyTrue>
            </tags:operationSection>

        </cti:checkMultiRole>

        <!-- Distribution Automation section -->
        <cti:checkRolesAndProperties value="CAP_CONTROL_ACCESS,OPERATOR_ESUBSTATION_DRAWINGS_VIEW">

            <tags:operationSection sectionName="Distribution Automation" sectionImageName="DistributionAutomationLogo">
                <cti:checkRolesAndProperties value="CAP_CONTROL_ACCESS">
                    <tags:sectionLink>
                        <a href="/spring/capcontrol/tier/areas">Volt/Var Management</a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="OPERATOR_ESUBSTATION_DRAWINGS_VIEW">
                    <tags:sectionLink>
                        <a href="/esub/home">eSubstation</a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Hardware Inventory section -->
        <cti:checkRole roleid="<%= InventoryRole.ROLEID %>">

            <tags:operationSection sectionName="Hardware Inventory" sectionImageName="HardwareInventoryLogo">
                <cti:checkRole role="InventoryRole.ROLEID">
                    <tags:sectionLink>
                        <a href="/spring/stars/operator/inventory/home"><cti:msg key="yukon.web.operations.inventory"/></a>
                    </tags:sectionLink>
                </cti:checkRole>
                <cti:checkProperty property="InventoryRole.PURCHASING_ACCESS">
                    <tags:sectionLink>
                        <a href="Hardware/PurchaseTrack.jsp">Purchasing</a>
                    </tags:sectionLink>
                </cti:checkProperty>


                <!-- Hardware search form -->
				<cti:checkRolesAndProperties value="INVENTORY_SEARCH">
					<div class="sectionForm">
						<form id="invSearchForm"
							action="/spring/stars/operator/inventory/search" method="get">
						  <div class="sectionFormLabel">
                                <cti:msg key="yukon.web.modules.operator.search.hardwareSearchPrompt" />
                            </div>
							<div>
								<select name="searchBy"
									onchange="$('invSearchValue').value = ''">
									<c:forEach var="operatorInventorySearchBy" items="${operatorInventorySearchBys}">
										<option value="${operatorInventorySearchBy}">
											<cti:msg key="${operatorInventorySearchBy.formatKey}" />
										</option>
									</c:forEach>
								</select> <input type="text" name="searchValue" id="invSearchValue"
									value="" size="15"> <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>"
									alt="search" onClick="$('invSearchForm').submit();">
							</div>
						</form>
					</div>
				</cti:checkRolesAndProperties>
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
                <div class="sectionForm">
                    <form name="soSearchForm" method="post" action="<cti:url value="/servlet/WorkOrderManager"/>">
                        <input type="hidden" name="action" value="SearchWorkOrder"> <input type="hidden" name="REDIRECT" value="<cti:url value="/operator/WorkOrder/SearchResults.jsp"/>">

                        <div class="sectionFormLabel">Search for existing service order:</div>
                        <div>
                            <select name="SearchBy" onchange="document.soSearchForm.SearchValue.value=''">
                                <c:forEach items="${serviceOrderSearchList}" var="entry">
                                    <option value="${entry.yukonDefID}">${entry.content}</option>
                                </c:forEach>
                            </select> <input type="text" name="SearchValue" size="15" value=""> <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>"
                                alt="search" onClick="Javascript:document.soSearchForm.submit();">
                        </div>
                    </form>
                </div>
            </tags:operationSection>

        </cti:checkRole>

        <!-- Analysis section -->
        <cti:checkRolesAndProperties value="REPORTING,ENABLE_WEB_COMMANDER,MSP_LM_MAPPING_SETUP">

            <tags:operationSection sectionName="Analysis" sectionImageName="AnalysisLogo">
                <cti:checkRole role="ReportingRole.ROLEID">
                    <tags:sectionLink>
                        <a href="../analysis/Reports.jsp">Reporting</a>
                    </tags:sectionLink>
                </cti:checkRole>
                <cti:checkProperty property="CommanderRole.ENABLE_WEB_COMMANDER">
                    <tags:sectionLink>
                        <a href="../apps/SelectDevice.jsp">Commander</a>
                    </tags:sectionLink>
                </cti:checkProperty>
                <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                    <tags:sectionLink>
                        <a href="/spring/amr/archivedValuesExporter/view">Archived Data Export</a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkProperty property="MultispeakRole.MSP_LM_MAPPING_SETUP">
                    <tags:sectionLink>
                        <a href="/spring/multispeak/visualDisplays/loadManagement/home"><cti:msg key="yukon.web.menu.portal.analysis.visualDisplays" />
                        </a>
                    </tags:sectionLink>
                </cti:checkProperty>
                <cti:checkRolesAndProperties value="OPERATOR_SURVEY_EDIT">
                    <tags:sectionLink>
                        <a href="/spring/stars/survey/list"><cti:msg key="yukon.web.menu.portal.analysis.surveys" />
                        </a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Administration section -->
        <cti:checkRole role="operator.AdministratorRole.ROLEID">

            <tags:operationSection sectionName="Administration" sectionImageName="AdministrationLogo">

                <c:if test="${showSystemAdmin}">
                    <tags:sectionLink>
                        <a href="/spring/adminSetup/systemAdmin"> <cti:msg key="yukon.web.menu.portal.administration.systemAdministration" /> </a>
                    </tags:sectionLink>
                </c:if>

                <cti:checkProperty property="operator.AdministratorRole.ADMIN_VIEW_BATCH_COMMANDS">
                    <tags:sectionLink>
                        <a href="Admin/SwitchCommands.jsp">View Batch Commands</a>
                    </tags:sectionLink>
                </cti:checkProperty>

                <cti:checkProperty property="operator.AdministratorRole.ADMIN_VIEW_OPT_OUT_EVENTS">
                    <tags:sectionLink>
                        <a href="/spring/stars/operator/optOut/admin/viewScheduled"> <cti:msg key="yukon.web.menu.portal.administration.viewScheduledOptOutEvents" /> </a>
                    </tags:sectionLink>
                </cti:checkProperty>

                <cti:checkRolesAndProperties value="OPERATOR_ADMINISTRATOR">
                    <tags:sectionLink>
                        <a href="/spring/support/">Support</a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ADMIN_VIEW_LOGS">
                    <tags:sectionLink>
                        <a href="/spring/support/logging/menu?file=/">View Logs</a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkProperty property="AdministratorRole.ADMIN_MANAGE_INDEXES">
                    <tags:sectionLink>
                        <a href="/index/manage">Manage Indexes</a>
                    </tags:sectionLink>
                </cti:checkProperty>

                <cti:checkProperty property="AdministratorRole.ADMIN_VIEW_CONFIG">
                    <tags:sectionLink>
                        <a href="<cti:url value="/spring/deviceConfiguration?home"/>">Device Configuration</a>
                    </tags:sectionLink>
                </cti:checkProperty>

            </tags:operationSection>

        </cti:checkRole>

    </div>
</cti:standardPage>