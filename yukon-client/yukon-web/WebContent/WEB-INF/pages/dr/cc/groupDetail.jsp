<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.groupDetail">
<cti:includeScript link="/resources/js/pages/yukon.curtailment.js"/>

<cti:url var="submitUrl" value="/dr/cc/groupSave/${group.id}"/>
<form id="group" name="group" data-group-id="${group.id}" method="POST" action="${submitUrl}">
<cti:csrfToken/>

<div class="column-12-12">
    <h3><i:inline key=".groupName"/>
        <input id="group-name" type="text" name="groupName" value="${group.name}" data-group-id="${group.id}"/>
    </h3>
    <div class="column one">
        <h3><i:inline key=".customers"/></h3>
        <div>
            <table class="compact-results-table" id="assigned-customers">
                <c:forEach var="assignedCustomer" items="${assignedCustomers}">
                    <tr>
                        <td>${assignedCustomer.customer.companyName}&nbsp;</td>
                        <td class="emails">
                            <input type="checkbox" name="emails" <c:if test="${assignedCustomer.notifMap.sendEmails}">checked</c:if> />&nbsp;
                            <i:inline key=".emails"/>
                        </td>
                        <td class="voice">
                            <input type="checkbox" name="voice" <c:if test="${assignedCustomer.notifMap.sendOutboundCalls}">checked</c:if> />&nbsp;
                            <i:inline key=".voice"/>
                        </td>
                        <td class="sms">
                            <input type="checkbox" name="sms" <c:if test="${assignedCustomer.notifMap.sendSms}">checked</c:if> />&nbsp;
                            <i:inline key=".sms"/>
                        </td>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-customer-id="${assignedCustomer.customer.id}"/>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <h3><i:inline key=".availableCustomers"/></h3>
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
                    <td class="emails dn">
                        <input type="checkbox" name="emails" <c:if test="${availableCustomer.notifMap.sendEmails}">checked</c:if> />&nbsp;
                        <i:inline key=".emails"/>
                    </td>
                    <td class="voice dn">
                        <input type="checkbox" name="voice" <c:if test="${availableCustomer.notifMap.sendOutboundCalls}">checked</c:if> />&nbsp;
                        <i:inline key=".voice"/>
                    </td>
                    <td class="sms dn">
                        <input type="checkbox" name="sms" <c:if test="${availableCustomer.notifMap.sendSms}">checked</c:if> />&nbsp;
                        <i:inline key=".sms"/>
                    </td>
                    <td>
                        <cti:button renderMode="buttonImage" icon="icon-add" data-customer-id="${availableCustomer.customer.id}"/>
                    </td>
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
</cti:standardPage>