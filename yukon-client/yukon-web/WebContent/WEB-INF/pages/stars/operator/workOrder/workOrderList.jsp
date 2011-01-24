<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags/i18n" prefix="i"%>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
    
<cti:standardPage module="operator" page="workOrder.list">
<tags:setFormEditMode mode="${mode}"/>

    <cti:includeCss link="/WebConfig/yukon/styles/operator/callTracking.css"/>
  
    <form id="createWorkOrderForm" action="/spring/stars/operator/workOrder/viewWorkOrder" method="get">
        <input type="hidden" name="accountId" value="${accountId}">
    </form>

    <tags:boxContainer2 nameKey="workOrdersBox">
    <table class="compactResultsTable callListTable rowHighlighting">
    
        <tr>
            <th><i:inline key=".workOrderNumber"/></th>
            <th><i:inline key=".header.eventDate"/></th>
            <th><i:inline key=".serviceType"/></th>
            <th><i:inline key=".currentState"/></th>
            <th><i:inline key=".orderedBy"/></th>
            <th><i:inline key=".header.assigned"/></th>
            <th class="description"><i:inline key=".description"/></th>
            
            <%-- delete header --%>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <th class="removeCol"><i:inline key=".header.remove"/></th>
            </cti:displayForPageEditModes>
        </tr>
        
        <c:if test="${fn:length(workOrders) <= 0}">
            <cti:displayForPageEditModes modes="VIEW">
                <tr><td colspan="7" class="noCalls subtleGray"><i:inline key=".noWorkOrders"/></td></tr>
            </cti:displayForPageEditModes>
            <cti:displayForPageEditModes modes="EDIT,CREATE">
                <tr><td colspan="8" class="noCalls subtleGray"><i:inline key=".noWorkOrders"/></td></tr>
            </cti:displayForPageEditModes>
        </c:if>

        <c:forEach var="workOrder" items="${workOrders}">
           
            <tr>
                <td>
                    <cti:url var="viewWorkOrderUrl" value="/spring/stars/operator/workOrder/viewWorkOrder">
                        <cti:param name="accountId">${accountId}</cti:param>
                        <cti:param name="workOrderId">${workOrder.workOrderBase.orderId}</cti:param>
                    </cti:url>
                    <a href="${viewWorkOrderUrl}"><spring:escapeBody htmlEscape="true">${workOrder.workOrderBase.orderNumber}</spring:escapeBody></a>
                </td>
                <td><cti:formatDate value="${workOrder.eventDate}" type="BOTH"/></td>
                <td><tags:yukonListEntry value="${workOrder.workOrderBase.workTypeId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_TYPE"/></td>
                <td><tags:yukonListEntry value="${workOrder.workOrderBase.currentStateId}" energyCompanyId="${energyCompanyId}" listName="SERVICE_STATUS"/></td>
                <td><spring:escapeBody htmlEscape="true">${workOrder.workOrderBase.orderedBy}</spring:escapeBody></td>
                <td><tags:listItem value="${workOrder.workOrderBase.serviceCompanyId}" items="${allServiceCompanies}" 
                                   itemLabel="companyName" itemValue="companyId"/></td>
                <td class="description">
                    <spring:escapeBody htmlEscape="true">${workOrder.workOrderBase.description}</spring:escapeBody>
                </td>
                
                <%-- delete icon --%>
                <cti:displayForPageEditModes modes="EDIT,CREATE">
                    <td class="removeCol">
                         <form id="deleteWorkOrderForm_${workOrder.workOrderBase.orderId}" action="/spring/stars/operator/workOrder/deleteWorkOrder" method="post">
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="deleteWorkOrderId" value="${workOrder.workOrderBase.orderId}">
                            <input type="image" id="deleteButton_${workOrder.workOrderBase.orderId}" onclick="return false" src="${delete}" onmouseover="javascript:this.src='${deleteOver}'" onmouseout="javascript:this.src='${delete}'">
                            <tags:confirmDialog nameKey=".deleteWorkOrderConfirmation" arguments="${workOrder.workOrderBase.orderNumber}" on="#deleteButton_${workOrder.workOrderBase.orderId}"/>
                         </form>
                    </td>
                    
                </cti:displayForPageEditModes>
            
            </tr>
        </c:forEach>
    </table>
    </tags:boxContainer2>
        
    <%-- create button --%>
    <cti:displayForPageEditModes modes="CREATE">
        <br>
        <tags:slowInput2 formId="createWorkOrderForm" key="create"/>
    </cti:displayForPageEditModes>

</cti:standardPage>