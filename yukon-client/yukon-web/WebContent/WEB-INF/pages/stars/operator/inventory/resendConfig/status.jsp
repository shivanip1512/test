<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="resendConfig">
<cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>

<script type="text/javascript">
jQuery(document).on('click', 'a.failedItems', function() {
    jQuery.ajax({
        url: 'viewFailed',
        data: {"taskId": '${task.taskId}'},
        success: function(data) {
            jQuery('#failedContainer').html(data);
        }
    });
});

function taskFinished() {
    jQuery('#cancelTaskBtn').hide();
}
</script>


    <c:set var="newTask" value="${empty taskId}"/>
    
    <c:if test="${empty task}">
        <tags:formElementContainer nameKey="resendConfigContainer">
            <form action="do">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".disclaimerLabel"><i:inline key=".disclaimer"/></tags:nameValue2>
                </tags:nameValueContainer2>
                
                <div class="pageActionArea">
                    <cti:button nameKey="start" type="submit" name="start"/>
                    <cti:button nameKey="cancel" type="submit" name="cancel"/>
                </div>
            </form>
        </tags:formElementContainer>
    </c:if>

    <c:if test="${not empty task}">
        <form action="do" method="post">
            <input type="hidden" name="taskId" value="${task.taskId}">
            <tags:formElementContainer nameKey="progress">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".progress">
                        <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".successful">
                        <cti:url var="newOperationSuccess" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="SUCCESS"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" styleClass="successMessage fwb"/>
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                            <ul class="resultList">
                                <li>
                                    <a href="${newOperationSuccess}" class="small"><i:inline key=".newOperation"/></a>
                                </li>
                            </ul>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".unsupported">
                        <cti:url var="newOperationUnsupported" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="UNSUPPORTED"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/UNSUPPORTED_COUNT" styleClass="warningMessage fwb"/>
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                            <ul class="resultList">
                                <li>
                                    <a href="${newOperationUnsupported}" class="small"><i:inline key=".newOperation"/></a>
                                </li>
                            </ul>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".failed">
                        <cti:url var="newOperationFailed" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="FAILED"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/FAILED_COUNT" styleClass="errorMessage fwb"/>
                        
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_FAILED">
                            <ul class="resultList">
                                <li>
                                    <a href="javascript:void(0);" class="small failedItems"><i:inline key=".viewFailureReasons"/></a>
                                </li>
                                <li>
                                    <a href="${newOperationFailed}" class="small"><i:inline key=".newOperation"/></a>
                                </li>
                            </ul>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <div class="pageActionArea stacked">
                    <c:if test="${!task.complete}">
                        <c:set var="buttonClass" value="db"/>
                        <c:set var="linkClass" value="dn"/>
                    </c:if>
                    <c:if test="${task.complete}">
                        <c:set var="buttonClass" value="dn"/>
                        <c:set var="linkClass" value="db"/>
                    </c:if>
                    <cti:button nameKey="cancel" type="sumbit" name="cancel" id="cancelTaskBtn" styleClass="${buttonClass}"/>
                </div>
                <cti:dataUpdaterEventCallback function="taskFinished" id="INVENTORY_TASK/${task.taskId}/IS_COMPLETE" />
                
            </tags:formElementContainer>
        </form>
    </c:if>
        
    <div id="failedContainer"></div>
</cti:standardPage>