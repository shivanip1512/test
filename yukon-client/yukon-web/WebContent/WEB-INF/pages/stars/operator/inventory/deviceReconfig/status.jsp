<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.config.schedule.status">
    
    <c:set var="taskId" value="${task.inventoryConfigTaskId}"/>
    
    <cti:url var="newOperationFailed" value="/stars/operator/inventory/deviceReconfig/newOperation">
        <cti:param name="type" value="FAIL"/>
        <cti:param name="taskId" value="${taskId}"/>
    </cti:url>
    
    <cti:url var="newOperationSuccess" value="/stars/operator/inventory/deviceReconfig/newOperation">
        <cti:param name="type" value="SUCCESS"/>
        <cti:param name="taskId" value="${taskId}"/>
    </cti:url>
    
    <cti:url var="newOperationUnsupported" value="/stars/operator/inventory/deviceReconfig/newOperation">
        <cti:param name="type" value="UNSUPPORTED"/>
        <cti:param name="taskId" value="${taskId}"/>
    </cti:url>
        
<div class="stacked-md">
    <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
    <i:inline key=".notes"/>
</div>
    
    <table class="name-value-container">
        <tr>
            <td class="name"><i:inline key=".progress"/>:</td>
            <td class="value" colspan="2">
                <cti:classUpdater type="DEVICE_RECONFIG" identifier="${taskId}/STATUS_CLASS">
                    <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${taskId}/STATUS_TEXT" styleClass="dib"/>
                </cti:classUpdater>
                <tags:updateableProgressBar totalCount="${task.numberOfItems}" containerClasses="vam"
                    countKey="DEVICE_RECONFIG/${taskId}/ITEMS_PROCESSED"/>
            </td>
        </tr>
        
        <tr style="height: 5px;"><td colspan="3"></td></tr>
        
        <tr>
            <td class="name"><i:inline key=".successCount"/>:</td>
            <td class="value">
                <cti:dataUpdaterValue type="DEVICE_RECONFIG" 
                    identifier="${taskId}/SUCCESS_COUNT" styleClass="success fwb"/>
            </td>
            <td class="value">
                <cti:classUpdater type="DEVICE_RECONFIG" 
                        identifier="${taskId}/NEW_OPERATION_FOR_SUCCESS">
                    <a href="${newOperationSuccess}"><i:inline key=".newOperation"/></a>
                </cti:classUpdater>
            </td>
        </tr>
        
        <tr style="height: 5px;"><td colspan="3"></td></tr>
        
        <tr>
            <td class="name"><i:inline key=".unsupportedCount"/>:</td>
            <td class="value">
                <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${taskId}/UNSUPPORTED_COUNT" 
                    styleClass="warning fwb"/>
            </td>
            <td class="value">
                <cti:classUpdater type="DEVICE_RECONFIG" 
                        identifier="${taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                    <a href="${newOperationUnsupported}"><i:inline key=".newOperation"/></a>
                </cti:classUpdater>
            </td>
        </tr>
        
        <tr style="height: 5px;"><td colspan="3"></td></tr>
        
        <tr>
            <td class="name"><i:inline key=".failedCount"/>:</td>
            <td class="value">
                <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${taskId}/FAILED_COUNT" 
                    styleClass="error fwb"/>
            </td>
            <td class="value">
                <cti:classUpdater type="DEVICE_RECONFIG" 
                        identifier="${taskId}/NEW_OPERATION_FOR_FAILED">
                    <a href="${newOperationFailed}"><i:inline key=".newOperation"/></a>
                </cti:classUpdater>
            </td>
        </tr>
        
    </table>
    
    <cti:url value="/stars/operator/inventory/inventoryActions/deviceReconfig/delete" var="deleteURL"/>
    <div class="page-action-area">
        <form action="${deleteURL}" method="post">
            <cti:csrfToken/>
            <input type="hidden" value="${taskId}" name="taskId">
            
            <cti:button id="deleteButton" nameKey="delete" type="submit" classes="delete"/>
            <d:confirm on="#deleteButton" nameKey="confirmDelete" argument="${task.taskName}"/>
            
            <cti:url var="url" value="/stars/operator/inventory/home"/>
            <cti:button nameKey="back" href="${url}"/>
        </form>
    </div>

</cti:standardPage>