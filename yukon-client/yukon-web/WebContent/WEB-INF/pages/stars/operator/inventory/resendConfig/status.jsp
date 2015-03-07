<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="inventory.config.send">

<script type="text/javascript">
$(document).on('click', 'a.js-failed-items', function() {
    $.ajax({
        url: 'viewFailed',
        data: {"taskId": '${task.taskId}'}
    }).done(function (data, textStatus, jqXHR) {
        $('#failedContainer').html(data);
    });
});

function taskFinished() {
    $('#cancelTaskBtn').hide();
}
</script>


    <c:set var="newTask" value="${empty taskId}"/>
    
    <c:if test="${empty task}">
        <tags:sectionContainer2 nameKey="resendConfigContainer">
            <form action="do">
                <cti:inventoryCollection inventoryCollection="${inventoryCollection}"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".disclaimerLabel"><i:inline key=".disclaimer"/></tags:nameValue2>
                </tags:nameValueContainer2>
                
                <div class="page-action-area">
                    <cti:button nameKey="start" type="submit" name="start"/>
                    <cti:button nameKey="cancel" type="submit" name="cancel"/>
                </div>
            </form>
        </tags:sectionContainer2>
    </c:if>

    <c:if test="${not empty task}">
        <form action="do" method="post">
        <cti:csrfToken/>
            <input type="hidden" name="taskId" value="${task.taskId}">
            <tags:sectionContainer2 nameKey="progress">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".progress">
                        <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                    </tags:nameValue2>
                    <tags:nameValue2 nameKey=".successful">
                        <cti:url var="newOperationSuccess" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="SUCCESS"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" styleClass="success fwb"/>
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                            <a href="${newOperationSuccess}"><i:inline key=".newOperation"/></a>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".unsupported">
                        <cti:url var="newOperationUnsupported" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="UNSUPPORTED"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/UNSUPPORTED_COUNT" styleClass="warning fwb"/>
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                            <a href="${newOperationUnsupported}"><i:inline key=".newOperation"/></a>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                    <tags:nameValue2 nameKey=".failed">
                        <cti:url var="newOperationFailed" value="newOperation">
                            <cti:param name="taskId" value="${task.taskId}"/>
                            <cti:param name="type" value="FAILED"/>
                        </cti:url>
                        <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/FAILED_COUNT" styleClass="error fwb"/>
                        
                        <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_FAILED">
                            <a href="javascript:void(0);" class="js-failed-items stacked"><i:inline key=".viewFailureReasons"/></a><br>
                            <a href="${newOperationFailed}"><i:inline key=".newOperation"/></a>
                        </cti:classUpdater>
                    </tags:nameValue2>
                    
                </tags:nameValueContainer2>
                
                <div class="page-action-area stacked">
                    <c:if test="${!task.complete}">
                        <c:set var="buttonClass" value="db"/>
                        <c:set var="linkClass" value="dn"/>
                    </c:if>
                    <c:if test="${task.complete}">
                        <c:set var="buttonClass" value="dn"/>
                        <c:set var="linkClass" value="db"/>
                    </c:if>
                    <cti:button nameKey="cancel" type="submit" name="cancel" id="cancelTaskBtn" classes="${buttonClass}"/>
                </div>
                <cti:dataUpdaterEventCallback function="taskFinished" id="INVENTORY_TASK/${task.taskId}/IS_COMPLETE" />
                
            </tags:sectionContainer2>
        </form>
    </c:if>
        
    <div id="failedContainer"></div>
</cti:standardPage>