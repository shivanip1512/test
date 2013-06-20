<%--
    Allows T/F parameters: showTabGeneration[default], showTabSetup, showTabSchedule
--%>

<%@ page import="com.cannontech.database.cache.DefaultDatabaseCache"%>
<%@ page import="com.cannontech.yukon.IDatabaseCache"%>
<%@ page import="com.cannontech.billing.FileFormatTypes"%>

<%@ include file="include/billing_header.jsp" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="jsTree" tagdir="/WEB-INF/tags/jsTree" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:verifyRolesAndProperties value="APPLICATION_BILLING"/>

<cti:msg key="yukon.common.role.APPLICATION_BILLING" var="billingTitle"/>

<cti:standardPage module="amr" title="${billingTitle}">
    <cti:standardMenu menuSelection="billing|generation"/>
    <cti:breadCrumbs>

        <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/dashboard" title="${homeLabel}" />
        &gt; ${billingTitle}
    </cti:breadCrumbs>

    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

    <script type="text/javascript">
        function validateBillingGroup() {
            var format = $('fileFormat').options[$('fileFormat').selectedIndex].value;
            var billingGroupSelected = $('billGroup').value.strip() != '';

            if (!billingGroupSelected && format != <%= FileFormatTypes.CURTAILMENT_EVENTS_ITRON %>) {
                alert("<cti:msg key="yukon.web.billing.mustSelectGroup"/>");
                return false;
            } else {
                return true;
            }
        }

        function submit() {
            if(validateBillingGroup()) {
                jQuery('#MForm').submit();
            }
        }

        function editSchedule() {
            if(validateBillingGroup()) {
                jQuery('#MForm').attr('action', '/scheduledBilling/showForm');
                jQuery('#MForm').submit();
            }
        }
    </script>

    <h2>${billingTitle}</h2>
    <br>

<cti:tabbedContentSelector  mode="section">

    <cti:msg key="yukon.web.billing.tab.generation.title" var="tabGen" />
    <cti:tabbedContentSelectorContent selectorName="${tabGen}" initiallySelected="${showTabGeneration}">
    <c:set var="formatMap" value="${FileFormatTypes.getValidFormats()}"></c:set>
    <c:set var="origEndDate" value="${BILLING_BEAN.endDate}"></c:set>
    <c:set var="systemTimezone" value="${tzFormat.format(origEndDate)}"></c:set>

    <cti:msg key="yukon.web.settings" var="settingsLabel"/>
    <tags:boxContainer title="${settingsLabel}" id="billingContainer" hideEnabled="false">

        <form id="MForm" name = "MForm" action="<cti:url value="/servlet/BillingServlet" />" method="post">
    
            <c:if test="${BILLING_BEAN.errorMsg != null}">
                <div class="error">${BILLING_BEAN.errorMsg}</div>
                <br>
            </c:if>
    
            <tags:nameValueContainer>

                <cti:msg key="yukon.web.billing.fileFormat" var="fileFormat" />
                <tags:nameValue name="${fileFormat}" nameColumnWidth="250px">
                    <select id="fileFormat" name="fileFormat">
                        <c:forEach var="format" items="${formatMap}">
                            <option value="${format.value}" ${(format.value == BILLING_BEAN.fileFormat)?'selected':''}>${format.key}</option>
                        </c:forEach>
                    </select>
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.billingEndDate" var="billingEndDate" />
                <tags:nameValue name="${billingEndDate}">
                    <dt:date name="endDate" value="${origEndDate}" />
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.demandDaysPrevious" var="demandDaysPrevious" />
                <tags:nameValue name="${demandDaysPrevious}">
                    <input type="text" name="demandDays" value="${BILLING_BEAN.demandDaysPrev}" size = "8">
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.energyDaysPrevious" var="energyDaysPrevious" />
                <tags:nameValue name="${energyDaysPrevious}">
                    <input type="text" name="energyDays" value="${BILLING_BEAN.energyDaysPrev}" size = "8">
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.removeMultiplier" var="removeMultiplier" />
                <tags:nameValue  name="${removeMultiplier}">
                    <input type="checkbox" name="removeMultiplier" ${(BILLING_BEAN.removeMult)? 'checked':''} >
                </tags:nameValue>
                <cti:msg key="yukon.web.billing.billingGroup" var="billingGroup" />
                <tags:nameValue name="${billingGroup}">

                    <cti:deviceGroupHierarchyJson predicates="NON_HIDDEN" var="dataJson" />
                    <jsTree:nodeValueSelectingInlineTree fieldId="billGroup"
                                                        fieldName="billGroup"
                                                        nodeValueName="groupName"
                                                        multiSelect="true"
                                                        id="billingTree"
                                                        dataJson="${dataJson}"
                                                        includeControlBar="true"
                                                        maxHeight="400"/>
                </tags:nameValue>

            </tags:nameValueContainer>
            <cti:msg key="yukon.web.generate" var="generateLabel" />
            <cti:msg key="yukon.web.billing.schedule" var="scheduleLabel" />
            <input type="button" name="generate" value="${generateLabel}" onclick="submit();">
            <input type="button" name="schedule" value="${scheduleLabel}" onclick="editSchedule();">
        </form>
    </tags:boxContainer>
    </cti:tabbedContentSelectorContent>

    <cti:msg key="yukon.web.billing.tab.setup.title" var="tab_setup" />
    <cti:tabbedContentSelectorContent selectorName="${tab_setup}" initiallySelected="${showTabSetup}">
        <p>Hey, stop that</p>
    </cti:tabbedContentSelectorContent>

    <cti:msg key="yukon.web.billing.tab.schedules.title" var="tab_sched"/>
    <cti:tabbedContentSelectorContent selectorName="${tab_sched}" initiallySelected="${showTabSchedule}">
        <jsp:include page="../../WEB-INF/pages/amr/scheduledBilling/_schedule.jsp"></jsp:include>
    </cti:tabbedContentSelectorContent>
</cti:tabbedContentSelector>
</cti:standardPage>