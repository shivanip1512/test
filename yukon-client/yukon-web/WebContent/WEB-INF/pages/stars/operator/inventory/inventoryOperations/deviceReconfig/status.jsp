<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="deviceReconfigStatus">
    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <cti:url var="newOperationFailed" value="/spring/stars/operator/inventory/inventoryOperations/deviceReconfig/newOperation">
        <cti:param name="type" value="FAIL"/>
        <cti:param name="taskId" value="${task.inventoryConfigTaskId}"/>
    </cti:url>
    
    <cti:url var="newOperationSuccess" value="/spring/stars/operator/inventory/inventoryOperations/deviceReconfig/newOperation">
        <cti:param name="type" value="SUCCESS"/>
        <cti:param name="taskId" value="${task.inventoryConfigTaskId}"/>
    </cti:url>

        <tags:boxContainer2 nameKey="statusContainer" hideEnabled="false" arguments="${task.taskName}">
        
            <div class="containerHeader">
                <span class="smallBoldLabel"><i:inline key=".notesLabel"/></span>
                <span><i:inline key=".notes"/></span>
            </div>
        
            <br>
            
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".progress">
                    <div>
                        <cti:classUpdater type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/STATUS_CLASS">
                            <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/STATUS_TEXT" styleClass="statusPart"/>
                        </cti:classUpdater>
                    </div>
                </tags:nameValue2>
            
            </tags:nameValueContainer2>
            
            <ul class="resultList">
                <li>
                    <tags:updateableProgressBar totalCount="${task.numberOfItems}" countKey="DEVICE_RECONFIG/${task.inventoryConfigTaskId}/ITEMS_PROCESSED"/>
                </li>
            </ul>
            
            <br>
            
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".successCount">
                    <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/SUCCESS_COUNT" styleClass="successMessage normalBoldLabel"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>

            <cti:classUpdater type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/NEW_OPERATION_FOR_SUCCESS">
                <ul class="resultList">
                    <li>
                        <a href="${newOperationSuccess}" class="small"><i:inline key=".newOperation"/></a>
                    </li>
                </ul>
            </cti:classUpdater>
            
            <tags:nameValueContainer2>
                
                <tags:nameValue2 nameKey=".failedCount">
                    <cti:dataUpdaterValue type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/FAILED_COUNT" styleClass="errorMessage normalBoldLabel"/>
                </tags:nameValue2>
                
            </tags:nameValueContainer2>
            
            <cti:classUpdater type="DEVICE_RECONFIG" identifier="${task.inventoryConfigTaskId}/NEW_OPERATION_FOR_FAILED">
                <ul class="resultList">
                    <li>
                        <a href="${newOperationFailed}" class="small"><i:inline key=".newOperation"/></a>
                    </li>
                </ul>
            </cti:classUpdater>
            
            <br>
            
            <form action="/spring/stars/operator/inventory/inventoryActions/deviceReconfig/delete" method="post">
                <input type="hidden" value="${task.inventoryConfigTaskId}" name="taskId">
                <cti:button nameKey="delete" type="submit"/>
            </form>
            
        </tags:boxContainer2>

</cti:standardPage>