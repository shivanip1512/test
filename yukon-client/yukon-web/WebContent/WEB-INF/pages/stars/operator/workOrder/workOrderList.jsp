<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="workOrder.list">
<cti:checkAccountEnergyCompanyOperator showError="true" >
    <tags:setFormEditMode mode="${mode}"/>

    <c:choose>
        <c:when test="${fn:length(workOrders) <= 0}">
            <span class="empty-list"><i:inline key=".noWorkOrders"/></span>
        </c:when>
        <c:otherwise>
            <table class="compact-results-table row-highlighting">
                <thead>
                    <tr>
                        <th><i:inline key=".workOrderNumber"/></th>
                        <th><i:inline key=".header.eventDate"/></th>
                        <th><i:inline key=".serviceType"/></th>
                        <th><i:inline key=".currentState"/></th>
                        <th><i:inline key=".orderedBy"/></th>
                        <th><i:inline key=".header.assigned"/></th>
                        <th><i:inline key=".description"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                
                    <c:forEach var="workOrder" items="${workOrders}">
                       
                        <tr>
                            <td>
                                <cti:url var="viewWorkOrderUrl" value="/stars/operator/workOrder/view">
                                    <cti:param name="accountId">${accountId}</cti:param>
                                    <cti:param name="workOrderId">${workOrder.workOrderBase.orderId}</cti:param>
                                </cti:url>
                                <a href="${viewWorkOrderUrl}">${fn:escapeXml(workOrder.workOrderBase.orderNumber)}</a>
                            </td>
                            <td><cti:formatDate value="${workOrder.eventDate}" type="BOTH"/></td>
                            <td><tags:yukonListEntry value="${workOrder.workOrderBase.workTypeId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_TYPE"/></td>
                            <td><tags:yukonListEntry value="${workOrder.workOrderBase.currentStateId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS"/></td>
                            <td>${fn:escapeXml(workOrder.workOrderBase.orderedBy)}</td>
                            <td><tags:listItem value="${workOrder.workOrderBase.serviceCompanyId}" items="${allServiceCompanies}" itemLabel="companyName" itemValue="companyId"/></td>
                            <td>${fn:escapeXml(workOrder.workOrderBase.description)}</td>
                        </tr>
                        
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    
    <%-- create button --%>
    <cti:displayForPageEditModes modes="CREATE">
        <div class="page-action-area">
            <form id="createWorkOrderForm" action="<cti:url value="/stars/operator/workOrder/create"/>" method="get">
                <input type="hidden" name="accountId" value="${accountId}">
                <cti:button nameKey="create" icon="icon-plus-green" type="submit"/>
            </form>
        </div>
    </cti:displayForPageEditModes>
</cti:checkAccountEnergyCompanyOperator>
</cti:standardPage>