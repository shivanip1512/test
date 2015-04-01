<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.config.send">

<cti:includeScript link="/JavaScript/yukon.assets.config.js"/>
    
<div class="stacked-md"><tags:selectedInventory inventoryCollection="${inventoryCollection}"/></div>
<div class="stacked-md">
    <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
    <i:inline key=".note"/>
</div>
    
<c:if test="${empty task}">
    <form action="do">
        
        <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
        
        <label><input type="checkbox" name="inService"><i:inline key=".inService"/></label>
        
        <div class="page-action-area">
            <cti:button nameKey="start" type="submit" name="start" classes="action primary"/>
            <cti:url var="url" value="/stars/operator/inventory/inventoryConfiguration">
                <c:forEach items="${inventoryCollection.collectionParameters}" var="entry">
                    <cti:param name="${entry.key}" value="${entry.value}"/>
                </c:forEach>
            </cti:url>
            <cti:button nameKey="cancel" href="${url}"/>
        </div>
        
    </form>
</c:if>

<c:if test="${not empty task}">
    <form action="do" method="post">
        
        <cti:csrfToken/>
        <input type="hidden" name="taskId" value="${task.taskId}">
        
        <tags:sectionContainer2 nameKey="progress">
            
            <table class="name-value-container">
                <tr>
                    <td class="name"><i:inline key=".progress"/>:</td>
                    <td class="value" colspan="2">
                        <tags:updateableProgressBar totalCount="${task.totalItems}" 
                            countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                    </td>
                </tr>
                
                <tr style="height: 5px;"><td colspan="3"></td></tr>
                
                <tr>
                    <td class="name"><i:inline key=".successful"/>:</td>
                    <td class="value">
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" 
                            styleClass="success fwb"/>
                    </td>
                    <td class="value">
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                            <cti:url var="url" value="/stars/operator/inventory/resendConfig/newOperation">
                                <cti:param name="taskId" value="${task.taskId}"/>
                                <cti:param name="type" value="SUCCESS"/>
                            </cti:url>
                            <a href="${url}"><i:inline key=".newOperation"/></a>
                        </cti:classUpdater>
                    </td>
                </tr>
                
                <tr style="height: 5px;"><td colspan="3"></td></tr>
                
                <tr>
                    <td class="name"><i:inline key=".unsupported"/>:</td>
                    <td class="value">
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/UNSUPPORTED_COUNT" 
                            styleClass="warning fwb"/>
                    </td>
                    <td class="value">
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                            <cti:url var="url" value="/stars/operator/inventory/resendConfig/newOperation">
                                <cti:param name="taskId" value="${task.taskId}"/>
                                <cti:param name="type" value="UNSUPPORTED"/>
                            </cti:url>
                            <a href="${url}"><i:inline key=".newOperation"/></a>
                        </cti:classUpdater>
                    </td>
                </tr>
                
                <tr style="height: 5px;"><td colspan="3"></td></tr>
                
                <tr>
                    <td class="name"><i:inline key=".failed"/>:</td>
                    <td class="value">
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/FAILED_COUNT" 
                            styleClass="error fwb fl"/>
                    </td>
                    <td class="value">
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_FAILED">
                            <ul class="list-piped di">
                                <cti:url var="url" value="/stars/operator/inventory/resendConfig/newOperation">
                                    <cti:param name="taskId" value="${task.taskId}"/>
                                    <cti:param name="type" value="FAILED"/>
                                </cti:url>
                                <li><a href="${url}"><i:inline key=".newOperation"/></a></li>
                                <li><a href="javascript:void(0);" class="js-failed-items stacked"
                                        data-task-id="${task.taskId}"><i:inline key=".viewFailureReasons"/></a>
                                </li>
                            </ul>
                        </cti:classUpdater>
                    </td>
                </tr>
                
            </table>
            
            <div class="page-action-area stacked">
                <c:if test="${!task.complete}">
                    <c:set var="buttonClass" value="dib"/>
                </c:if>
                <c:if test="${task.complete}">
                    <c:set var="buttonClass" value="dn"/>
                </c:if>
                <cti:button nameKey="cancel" type="submit" name="cancel" id="cancel-task-btn" classes="${buttonClass}"/>
            </div>
            <cti:dataUpdaterEventCallback function="yukon.assets.config.taskFinished" id="INVENTORY_TASK/${task.taskId}/IS_COMPLETE" />
            
        </tags:sectionContainer2>
    </form>
</c:if>

<div id="failed-container" class="dn"></div>
    
</cti:standardPage>