<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.groupDetail">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">
<cti:includeScript link="/resources/js/pages/yukon.curtailment.js"/>

<form id="group" name="group" data-group-id="${group.id}" method="POST" action="<cti:url value="/dr/cc/groupSave/${group.id}"/>">
<cti:csrfToken/>

<div class="column-12-12">
    <h3><i:inline key=".groupDetail"/></h3>
    <h3><i:inline key=".groupName"/>
        <input id="group-name" type="text" name="groupName" value="${group.name}" data-group-id="${group.id}"/>
    </h3>
    <div class="column one">
        <h3><i:inline key=".groupCustomers"/></h3>
        <div>
            <table class="compact-results-table" id="assigned-customers">
                <thead>
                    <tr>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="assignedCustomer" items="${assignedCustomers}">
                    <tr>
                        <td>${assignedCustomer.customer.companyName}&nbsp;</td>
                        <td>
                            <input type="checkbox" name="emails" <c:if test="${assignedCustomer.notifMap.sendEmails}">checked</c:if> />&nbsp;
                            <i:inline key=".groupEmails"/>
                        </td>
                        <td>
                            <input type="checkbox" name="voice" <c:if test="${assignedCustomer.notifMap.sendOutboundCalls}">checked</c:if> />&nbsp;
                            <i:inline key=".groupVoice"/>
                        </td>
                        <td>
                            <input type="checkbox" name="sms" <c:if test="${assignedCustomer.notifMap.sendSms}">checked</c:if> />&nbsp;
                            <i:inline key=".groupSms"/>
                        </td>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-customer-id="${assignedCustomer.customer.id}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <h3><i:inline key=".groupAvailableCustomers"/></h3>
        <table class="compact-results-table" id="unassigned-customers">
            <thead>
                <tr>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach var="availableCustomer" items="${availableCustomers}">
                <tr>
                    <td>${availableCustomer.customer.companyName}&nbsp;</td>
                    <td><cti:button renderMode="buttonImage" icon="icon-add" data-customer-id="${availableCustomer.customer.id}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <cti:url value="/dr/cc/groupSave/${group.id}" var="saveUrl"/>
    <cti:button nameKey="save" classes="action primary" type="submit" id="group-save" href="${saveUrl}"/>
    <cti:url value="/dr/cc/groupList" var="cancelUrl"/>
    <cti:button nameKey="cancel" href="${cancelUrl}"/>
 
</div>
</form>
</cti:msgScope>
</cti:standardPage>