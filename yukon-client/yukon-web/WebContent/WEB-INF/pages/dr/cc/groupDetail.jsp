<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.groupDetail">
<cti:includeScript link="/resources/js/pages/yukon.dr.curtailment.js"/>

<cti:url var="formUrl" value="/dr/cc/groupSave"/>
<form:form id="group" modelAttribute="group" action="${formUrl}">
<cti:csrfToken/>

<div class="column-12-12">
    <h3>
        <i:inline key=".groupName"/>
        <form:input path="name"/>
        <form:hidden path="id"/>
    </h3>
    <div class="column one">
        <h3><i:inline key=".customers"/></h3>
        <div>
            <table class="compact-results-table" id="assigned-customers">
                <tbody>
                <c:forEach var="assignedCustomer" items="${group.assignedCustomers}" varStatus="status">
                    <tr>
                        <td>${assignedCustomer.companyName}&nbsp;</td>
                        <td class="js-hidable-input">
                            <form:checkbox path="assignedCustomers[${status.index}].emails"/>&nbsp;
                            <i:inline key=".emails"/>
                        </td>
                        <td class="js-hidable-input">
                            <form:checkbox path="assignedCustomers[${status.index}].voice"/>&nbsp;
                            <i:inline key=".voice"/>
                        </td>
                        <td class="js-hidable-input">
                            <form:checkbox path="assignedCustomers[${status.index}].sms"/>&nbsp;
                            <i:inline key=".sms"/>
                        </td>
                        <td>
                            <form:hidden path="assignedCustomers[${status.index}].companyName"/>
                            <form:hidden path="assignedCustomers[${status.index}].id"/>
                            <cti:button renderMode="buttonImage" icon="icon-delete" data-customer-id="${assignedCustomer.id}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <h3><i:inline key=".availableCustomers"/></h3>
        <table class="compact-results-table" id="unassigned-customers">
            <tbody>
            <c:forEach var="availableCustomer" items="${group.availableCustomers}" varStatus="status">
                <tr>
                    <td>${availableCustomer.companyName}&nbsp;</td>
                    <td class="js-hidable-input dn">
                        <form:checkbox path="availableCustomers[${status.index}].emails"/>&nbsp;
                        <i:inline key=".emails"/>
                    </td>
                    <td class="js-hidable-input dn">
                        <form:checkbox path="availableCustomers[${status.index}].voice"/>&nbsp;
                        <i:inline key=".voice"/>
                    </td>
                    <td class="js-hidable-input dn">
                        <form:checkbox path="availableCustomers[${status.index}].sms"/>&nbsp;
                        <i:inline key=".sms"/>
                    </td>
                    <td>
                        <form:hidden path="availableCustomers[${status.index}].companyName"/>
                        <form:hidden path="availableCustomers[${status.index}].id"/>
                        <cti:button renderMode="buttonImage" icon="icon-add" data-customer-id="${availableCustomer.id}"/>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
    <cti:url value="/dr/cc/groupSave" var="saveUrl"/>
    <cti:button nameKey="save" classes="action primary" type="submit" id="group-save" href="${saveUrl}"/>
    <cti:url value="/dr/cc/groupList" var="cancelUrl"/>
    <cti:button nameKey="cancel" href="${cancelUrl}"/>
</div>
</form:form>
</cti:standardPage>