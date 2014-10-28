<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:standardPage module="dr" page="cc.groupDetail">
<cti:msgScope paths="yukon.web.modules.commercialcurtailment.ccurtSetup">

<div class="column-12-12">
    <h3><i:inline key=".groupDetail"/></h3>
    <h3><i:inline key=".groupName"/>&nbsp;${group.name}</h3>
    <div class="column one">
        <h3><i:inline key=".groupCustomers"/></h3>
        <div>
            <table class="compact-results-table">
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
                            <input type="checkbox" ${assignedCustomer.notifMap.sendEmails ? "checked" : ""} />&nbsp;
                            <i:inline key=".groupEmails"/>
                        </td>
                        <td>
                            <input type="checkbox" ${assignedCustomer.notifMap.sendOutboundCalls ? "checked" : ""} />&nbsp;
                            <i:inline key=".groupVoice"/>
                        </td>
                        <td>
                            <input type="checkbox" ${assignedCustomer.notifMap.sendSms ? "checked" : ""} />&nbsp;
                            <i:inline key=".groupSms"/>
                        </td>
                        <td>
                            <cti:button renderMode="buttonImage" icon="icon-delete"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
    <div class="column two nogutter">
        <h3><i:inline key=".groupAvailableCustomers"/></h3>
        <table class="compact-results-table">
            <thead>
                <tr>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:forEach var="availableCustomer" items="${availableCustomers}">
                <tr>
                    <td>${availableCustomer.customer.companyName}&nbsp;</td>
                    <td><cti:button renderMode="buttonImage" icon="icon-add"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>
</cti:msgScope>
</cti:standardPage>