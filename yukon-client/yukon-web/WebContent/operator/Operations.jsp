<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<%@ page import="com.cannontech.core.roleproperties.YukonRoleProperty"%>

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

<cti:msg key="yukon.web.module.operations.pageTitle" var="pageTitle" />

<cti:standardPage title="${pageTitle}" module="operations">
    <cti:standardMenu />

<script type="text/javascript">
	function confirmDelete() {
		if (confirm("<cti:msg key="yukon.web.operations.confirmDeleteEnergyCompany"/>")
			&& confirm("<cti:msg key="yukon.web.operations.secondConfirmDeleteEnergyCompany"/>"))
			document.DeleteForm.submit();
	}
	
	function resetFieldOnChange(e){
	    jQuery(jQuery(this).attr('data-input')).val('');
	}
	
	jQuery(function(){
	    jQuery(document).on('change', '.resetFieldOnChange', resetFieldOnChange);
	});

</script>

    <div id="main"><!-- start main -->

        <cti:operationsSetup />
        <cti:starsOperations />
        
        <%-- 
        <cti:checkRolesAndProperties value="CONSUMER_INFO">

            <tags:operationSection sectionName="Consumer Account Information" sectionImageName="ConsumerLogo">
                <cti:checkRolesAndProperties value="OPERATOR_NEW_ACCOUNT_WIZARD">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="/stars/operator/account/accountCreate"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.menu.portal.consumerAccountInformation.newAccount" />
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="OPERATOR_IMPORT_CUSTOMER_ACCOUNT">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="/stars/operator/account/accountImport"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.menu.portal.consumerAccountInformation.accountImport" />
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ENABLE_MIGRATE_ENROLLMENT">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="Consumer/MigrateEnrollment.jsp"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.migrateEnrollmentInformation" />
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ENABLE_GENERIC_UPLOAD">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="Consumer/GenericUpload.jsp"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.migrateEnrollmentInformation" />
                </cti:checkRolesAndProperties>
						
                <cti:checkRolesAndProperties
                    value="OPERATOR_OPT_OUT_ADMIN_STATUS,OPERATOR_OPT_OUT_ADMIN_CHANGE_ENABLE,OPERATOR_OPT_OUT_ADMIN_CANCEL_CURRENT,OPERATOR_OPT_OUT_ADMIN_CHANGE_COUNTS, ADMIN_VIEW_OPT_OUT_EVENTS">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="/stars/operator/optOut/admin"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.menu.portal.consumerAccountInformation.optOutAdmin" />
                </cti:checkRolesAndProperties>

                <!-- Customer search form -->
                 <c:if test="${isEnergyCompanyOperator}">
                    <cti:checkRolesAndProperties value="OPERATOR_ACCOUNT_SEARCH">
                        <div class="sectionForm">
                            <form id="accountSearchForm" action="/stars/operator/account/search" method="get">
                                <div class="sectionFormLabel">
                                    <cti:msg key="yukon.web.modules.operator.search.searchPrompt" />
                                </div>
                                <div>
                                    <select name="searchBy" data-input="#accountSearchValue" class="resetFieldOnChange">
                                        <c:forEach var="operatorAccountSearchBy" items="${operatorAccountSearchBys}">
                                            <option value="${operatorAccountSearchBy}" <c:if test="${operatorAccountSearchBy == searchBy}">selected</c:if>>
                                                <cti:msg key="${operatorAccountSearchBy.formatKey}" />
                                            </option>
                                        </c:forEach>
                                    </select> <input type="text" name="searchValue" id="accountSearchValue" value="" size="15"> 
                                    <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>" 
                                         alt="search" onClick="jQuery('#accountSearchForm')[0].submit();">
                                </div>
                            </form>
                        </div>
                    </cti:checkRolesAndProperties>
                 </c:if>
            </tags:operationSection>
        </cti:checkRolesAndProperties>

        <!-- Metering section -->
        <cti:checkRolesAndProperties value="METERING,APPLICATION_BILLING,TRENDING,DEVICE_ACTIONS,SCHEDULER">
        	<cti:msg key="yukon.web.metering" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="MeteringLogo">
                <cti:checkRolesAndProperties value="TRENDING">
                    <tags:sectionLink>
                        <a href="Metering/Metering.jsp"><cti:msg key="yukon.web.allTrends"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="METERING">
                    <cti:checkRolesAndProperties value="IMPORTER_ENABLED">
                        <tags:sectionLink>
                            <a href="/amr/bulkimporter/home"><cti:msg key="yukon.web.bulkImporter"/></a>
                        </tags:sectionLink>
                    </cti:checkRolesAndProperties>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="METERING,APPLICATION_BILLING,SCHEDULER">
                    <tags:sectionLink>
                        <a href="<cti:url value="/meter/start"/>"><cti:msg key="yukon.web.metering"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                    <tags:sectionLink>
                        <a href="<cti:url value="/bulk/bulkHome"/>"><cti:msg key="yukon.web.bulkOperations"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Client Launcher section -->
        <cti:checkRolesAndProperties value="JAVA_WEB_START_LAUNCHER_ENABLED">
            <c:import url="/jws/opspage" />
        </cti:checkRolesAndProperties>

        <!-- Load Response section -->
        <cti:checkRolesAndProperties value="LM_DIRECT_LOADCONTROL,ODDS_FOR_CONTROL,CI_CURTAILMENT">
			<cti:msg key="yukon.web.loadResponse" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="LoadResponseLogo">
                <cti:checkRolesAndProperties value="LM_DIRECT_LOADCONTROL">
                    <cti:isPropertyTrue property="DIRECT_CONTROL">
                        <tags:sectionLink>
                            <a href="LoadControl/oper_direct.jsp"><cti:msg key="yukon.web.direct"/></a>
                        </tags:sectionLink>
                    </cti:isPropertyTrue>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CI_CURTAILMENT">
                    <tags:sectionLink>
                        <a href="<cti:url value="/cc/programSelect.jsf"/>"><cti:getProperty property="CURTAILMENT_LABEL" />
                        </a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="ODDS_FOR_CONTROL">
                    <tags:sectionLink>
                        <a href="Consumer/Odds.jsp"><cti:msg key="yukon.web.oddsForControl"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:isPropertyTrue property="DEMAND_RESPONSE">
                    <tags:sectionLink>
                        <a href="/dr/home"><cti:msg key="yukon.web.operations.demandResponse" /></a>
                    </tags:sectionLink>
                </cti:isPropertyTrue>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Distribution Automation section -->
        <cti:checkRolesAndProperties value="CAP_CONTROL_ACCESS,OPERATOR_ESUBSTATION_DRAWINGS_VIEW">
			<cti:msg key="yukon.web.distributionAutomation" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="DistributionAutomationLogo">
                <cti:checkRolesAndProperties value="CAP_CONTROL_ACCESS">
                    <tags:sectionLink>
                        <a href="/capcontrol/tier/areas"><cti:msg key="yukon.web.voltVarManagement"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="OPERATOR_ESUBSTATION_DRAWINGS_VIEW">
                    <tags:sectionLink>
                        <a href="/esub/home"><cti:msg key="yukon.web.esubstation"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="CAP_CONTROL_IMPORTER">
                    <tags:sectionLink>
                        <a href="<cti:url value="/capcontrol/import/view"/>"><cti:msg key="yukon.web.voltVarImporter"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Hardware Inventory section -->
        <cti:checkRolesAndProperties value="INVENTORY">
			<cti:msg key="yukon.web.hardwareInventory" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="HardwareInventoryLogo">
                <cti:checkRolesAndProperties value="INVENTORY">
                    <tags:sectionLink enabled="${isEnergyCompanyOperator}" href="/stars/operator/inventory/home"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.operations.inventory" />
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="PURCHASING_ACCESS">
                    <tags:sectionLink>
                        <a href="Hardware/PurchaseTrack.jsp"><cti:msg key="yukon.web.purchasing"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <c:if test="${isEnergyCompanyOperator}">
                <!-- Hardware search form -->
    				<cti:checkRolesAndProperties value="INVENTORY_SEARCH">
    					<div class="sectionForm">
    						<form id="invSearchForm"
    							action="/stars/operator/inventory/search" method="get">
    						  <div class="sectionFormLabel">
                                    <cti:msg key="yukon.web.modules.operator.search.hardwareSearchPrompt" />
                                </div>
    							<div>
    								<select name="searchBy" data-input="#invSearchValue" class="resetFieldOnChange">
    									<c:forEach var="operatorInventorySearchBy" items="${operatorInventorySearchBys}">
    										<option value="${operatorInventorySearchBy}">
    											<cti:msg key="${operatorInventorySearchBy.formatKey}" />
    										</option>
    									</c:forEach>
    								</select> <input type="text" name="searchValue" id="invSearchValue"
    									value="" size="15"> <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>"
    									alt="search" onClick="$('invSearchForm').submit();" />
    							</div>
    						</form>
    					</div>
    				</cti:checkRolesAndProperties>
                 </c:if>
			</tags:operationSection>
 
        </cti:checkRolesAndProperties>

        <!-- Work Orders section -->
        <cti:checkRolesAndProperties value="WORK_ORDER">
			<cti:msg key="yukon.web.workOrders" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="WorkOrdersLogo">
                <cti:checkRolesAndProperties value="WORK_ORDER_SHOW_ALL">
                    <tags:sectionLink>
                        <a href="${serviceOrderPage}"><cti:msg key="yukon.web.serviceOrderList"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <!-- Service order search form -->
                <div class="sectionForm">
                    <form name="soSearchForm" method="post" action="<cti:url value="/servlet/WorkOrderManager"/>">
                        <input type="hidden" name="action" value="SearchWorkOrder"> <input type="hidden" name="REDIRECT" value="<cti:url value="/operator/WorkOrder/SearchResults.jsp"/>">

                        <div class="sectionFormLabel"><cti:msg key="yukon.web.operations.searchForExistingServiceOrder"/></div>
                        <div>
                            <select name="SearchBy" data-input="#SearchValue" class="resetFieldOnChange">
                                <c:forEach items="${serviceOrderSearchList}" var="entry">
                                    <option value="${entry.yukonDefID}">${entry.content}</option>
                                </c:forEach>
                            </select> <input type="text" name="SearchValue" size="15" value=""> <img class="cssicon" src="<cti:url value="/WebConfig/yukon/Icons/clearbits/search.gif"/>"
                                alt="search" onClick="Javascript:document.soSearchForm.submit();">
                        </div>
                    </form>
                </div>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Analysis section -->
        <cti:checkRolesAndProperties value="REPORTING,ENABLE_WEB_COMMANDER,MSP_LM_MAPPING_SETUP">
			<cti:msg key="yukon.web.analysis" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="AnalysisLogo">
                <cti:checkRolesAndProperties value="REPORTING">
                    <tags:sectionLink>
                        <a href="../analysis/Reports.jsp"><cti:msg key="yukon.web.reporting"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="ENABLE_WEB_COMMANDER">
                    <tags:sectionLink>
                        <a href="<cti:url value="/commander/select"/>"><cti:msg key="yukon.web.commander"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkRolesAndProperties value="ARCHIVED_DATA_EXPORT">
                    <tags:sectionLink>
                        <a href="/amr/archivedValuesExporter/view"><cti:msg key="yukon.web.archivedDataExport"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
                <cti:checkGlobalSetting setting="MSP_LM_MAPPING_SETUP">
                    <tags:sectionLink>
                        <a href="/multispeak/visualDisplays/loadManagement/home"><cti:msg key="yukon.web.menu.portal.analysis.visualDisplays" />
                        </a>
                    </tags:sectionLink>
                </cti:checkGlobalSetting>
                <cti:checkRolesAndProperties value="OPERATOR_SURVEY_EDIT">
                    <tags:sectionLink>
                        <a href="/stars/survey/list"><cti:msg key="yukon.web.menu.portal.analysis.surveys" />
                        </a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>
            </tags:operationSection>

        </cti:checkRolesAndProperties>

        <!-- Administration section -->
        <cti:checkRolesAndProperties value="OPERATOR_ADMINISTRATOR">
			<cti:msg key="yukon.web.administration" var="sectionTitle" />
            <tags:operationSection sectionName="${sectionTitle}" sectionImageName="AdministrationLogo">

                <c:if test="${showSystemAdmin}">
                    <tags:sectionLink>
                        <a href="/adminSetup/systemAdmin"> <cti:msg key="yukon.web.menu.portal.administration.systemAdministration" /> </a>
                    </tags:sectionLink>
                </c:if>

                <cti:checkRolesAndProperties value="ADMIN_VIEW_BATCH_COMMANDS">
                    <tags:sectionLink href="Admin/SwitchCommands.jsp" enabled="${isEnergyCompanyOperator}"
                        disabledMessageKey="yukon.web.taglib.CheckEnergyCompanyOperatorTag.userIsNotECOperator"
                        textKey="yukon.web.viewBatchCommands" />
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="CONSUMER_INFO">
	                <cti:checkRolesAndProperties value="ADMIN_VIEW_OPT_OUT_EVENTS">
	                    <tags:sectionLink>
	                        <a href="/stars/operator/optOut/admin/viewScheduled"> <cti:msg key="yukon.web.menu.portal.administration.viewScheduledOptOutEvents" /> </a>
	                    </tags:sectionLink>
	                </cti:checkRolesAndProperties>
				</cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="OPERATOR_ADMINISTRATOR">
                    <tags:sectionLink>
                        <a href="/support/"><cti:msg key="yukon.web.support"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ADMIN_VIEW_LOGS">
                    <tags:sectionLink>
                        <a href="/support/logging/menu"><cti:msg key="yukon.web.viewLogs"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ADMIN_MANAGE_INDEXES">
                    <tags:sectionLink>
                        <a href="/index/manage"><cti:msg key="yukon.web.manageIndexes"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

                <cti:checkRolesAndProperties value="ADMIN_VIEW_CONFIG">
                    <tags:sectionLink>
                        <a href="<cti:url value="/deviceConfiguration/home"/>"><cti:msg key="yukon.web.deviceConfiguration"/></a>
                    </tags:sectionLink>
                </cti:checkRolesAndProperties>

            </tags:operationSection>
<!-- end operationSection -->
        </cti:checkRolesAndProperties>
<!-- end cti:checkRolesAndProperties -->
        --%>
    </div>
<!-- end div -->
</cti:standardPage>
<!-- end cti:standardPage -->
