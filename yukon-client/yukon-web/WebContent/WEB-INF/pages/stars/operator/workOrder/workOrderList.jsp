<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"  %>

<cti:url var="delete" value="/WebConfig/yukon/Icons/delete.gif"/>
<cti:url var="deleteOver" value="/WebConfig/yukon/Icons/delete_over.gif"/>
    
<cti:standardPage module="operator" page="workOrder.list">


<script type="text/javascript">
YEvent.observeSelectorClick('a[id^=deleteButton_]', function(event) {
    event.stop();
    
    var parentElement = Event.findElement(event, 'td');
    var confirmMsg = $F(parentElement.down('input[name=confirmMessage]'));
    var accountId = $F(parentElement.down('input[name=accountId]'));
    var orderId = $F(parentElement.down('input[name=deleteWorkOrderId]'));
    
    var confirmPopup = $('confirmPopup');

    var messageContainer = confirmPopup.down('div[id=confirmMessage]');
    messageContainer.innerHTML = confirmMsg;
    var accountIdDest = confirmPopup.down('input[name=accountId]');
    Element.writeAttribute(accountIdDest, 'value', accountId);
    var orderIdDest = confirmPopup.down('input[name=deleteWorkOrderId]');
    Element.writeAttribute(orderIdDest, 'value', orderId);
    confirmPopup.show();
});

YEvent.observeSelectorClick('#confirmCancel', function(event) {
    $('confirmPopup').hide();
});
</script>




<tags:setFormEditMode mode="${mode}"/>

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
                    <cti:url var="viewWorkOrderUrl" value="/spring/stars/operator/workOrder/view">
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
                        <cti:msg2 var="confirmMessage" key=".deleteWorkOrderConfirmation.message" arguments="${workOrder.workOrderBase.orderNumber}"/>
                        <div class="dib">
                            <input type="hidden" name="confirmMessage" value="${confirmMessage}"/>
                            <input type="hidden" name="accountId" value="${accountId}">
                            <input type="hidden" name="deleteWorkOrderId" value="${workOrder.workOrderBase.orderId}">
                            <a href="#" class="icon icon_remove" id="deleteButton_${workOrder.workOrderBase.orderId}">remove</a>
                        </div>
                    </td>
                </cti:displayForPageEditModes>
            
            </tr>
        </c:forEach>
    </table>
    
    <%-- Confirm Dialog for delete work order --%>
    <cti:msg2 key=".deleteWorkOrderConfirmation.title" var="confirmDialogTitle"/>
    <tags:simplePopup title="${confirmDialogTitle}" id="confirmPopup" styleClass="smallSimplePopup">
        <form action="/spring/stars/operator/workOrder/deleteWorkOrder" method="post">
            <input type="hidden" name="accountId" value="">
            <input type="hidden" name="deleteWorkOrderId" value="">
            <div id="confirmMessage"></div>
            <div class="actionArea">
                <cti:button nameKey="ok" type="submit"/> 
                <cti:button nameKey="cancel" id="confirmCancel" />
            </div>
        </form>
    </tags:simplePopup>
    
    <%-- create button --%>
    <cti:displayForPageEditModes modes="CREATE">
        <div class="actionArea">
            <form id="createWorkOrderForm" action="/spring/stars/operator/workOrder/create" method="get">
                <input type="hidden" name="accountId" value="${accountId}">
                <cti:button nameKey="create" type="submit"/>
            </form>
        </div>
    </cti:displayForPageEditModes>
    
    </tags:boxContainer2>
        
</cti:standardPage>