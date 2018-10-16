<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<cti:standardPage module="operator" page="abr.setup">

<script type="text/javascript">
$(document).on('click', 'a.js-failed-items', function() {
    $.ajax({
        url: 'viewFailed',
        data: {"taskId": '${task.taskId}'}
    }).done(function (data, textStatus, jqXHR) {
        $('#failed-container').html(data);
    });
});

function taskFinished() {
    $('#cancel-task-btn').hide();
    $('#homeLink').show();
}
</script>

<tags:setFormEditMode mode="${mode}"/>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:sectionContainer2 nameKey="settings">
            <form:form action="do" method= "GET" modelAttribute="abr">
                
                <tags:hidden path="hardwareTypeId"/>
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".type">${fn:escapeXml(type)}</tags:nameValue2>
                    <tags:nameValue2 nameKey=".range">
                        <spring:bind path="from">
                            <c:set var="inputClass" value=""/>
                            <c:if test="${status.error}">
                                <c:set var="inputClass" value="error"/>
                            </c:if>
                            <cti:displayForPageEditModes modes="CREATE">
                                <form:input path="from" cssClass="${inputClass} js-focus"/>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">${abr.from}</cti:displayForPageEditModes>
                        </spring:bind>
                        <i:inline key=".to"/>
                        <spring:bind path="to">
                            <c:set var="inputClass" value=""/>
                            <c:if test="${status.error}">
                                <c:set var="inputClass" value="error"/>
                            </c:if>
                            <cti:displayForPageEditModes modes="CREATE">
                                <form:input path="to" cssClass="${inputClass}"/>
                            </cti:displayForPageEditModes>
                            <cti:displayForPageEditModes modes="VIEW">${abr.to}</cti:displayForPageEditModes>
                        </spring:bind>
                        <form:errors path="from" cssClass="error" element="div"/>
                        <form:errors path="to" cssClass="error" element="div"/>
                    </tags:nameValue2>
                    
                    <tags:yukonListEntrySelectNameValue nameKey=".status" path="statusTypeId" 
                        energyCompanyId="${ecId}" listName="DEVICE_STATUS"/>
                    <c:if test="${showVoltage}">
                        <tags:yukonListEntrySelectNameValue nameKey=".voltage" path="voltageTypeId" 
                            energyCompanyId="${ecId}" listName="DEVICE_VOLTAGE"/>
                    </c:if>
                    <tags:selectNameValue nameKey=".serviceCompany" path="serviceCompanyId" 
                        itemLabel="serviceCompanyName" itemValue="serviceCompanyId" 
                        items="${serviceCompanies}" defaultItemValue="0" defaultItemLabel="${none}"/>
                    <tags:selectNameValue nameKey=".route" path="routeId" 
                        itemLabel="paoName" itemValue="yukonID" items="${routes}" 
                        defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                </tags:nameValueContainer2>
                
                <cti:displayForPageEditModes modes="CREATE">
                    <div class="page-action-area">
                        <cti:button nameKey="start" type="submit" name="start" classes="primary action"/>
                        <cti:button nameKey="cancel" type="submit" name="cancel"/>
                    </div>
                </cti:displayForPageEditModes>
            </form:form>
        </tags:sectionContainer2>
    </div>
    <div class="column two nogutter">
        <cti:displayForPageEditModes modes="VIEW">
            <form action="do" method="GET">
                
                <input type="hidden" name="taskId" value="${task.taskId}">
                <tags:sectionContainer2 nameKey="progress">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".progress">
                            <c:if test="${not empty task}">
                                <tags:updateableProgressBar totalCount="${task.totalItems}" 
                                    countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                            </c:if>
                        </tags:nameValue2>
                        <tags:nameValue2 nameKey=".successful">
                            <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" 
                                styleClass="success fwb"/>
                            <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                                <cti:url var="url" value="/stars/operator/inventory/abr/newOperation">
                                    <cti:param name="taskId" value="${task.taskId}"/>
                                </cti:url>
                                <a href="${url}"><i:inline key=".newOperation"/></a>
                            </cti:classUpdater>
                        </tags:nameValue2>
                        
                        <tags:nameValue2 nameKey=".failed">
                            <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/FAILED_COUNT" 
                                styleClass="error fwb"/>
                            <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_FAILED">
                                <a href="javascript:void(0);" class="js-failed-items">
                                    <i:inline key=".viewFailureReasons"/>
                                </a>
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
                        <cti:button nameKey="cancel" type="submit" name="cancel" id="cancel-task-btn" 
                            classes="${buttonClass}"/>
                        <cti:url var="url" value="/stars/operator/inventory/home"/>
                        <a id="homeLink" href="${url}" class="${linkClass}"><i:inline key=".inventoryHome"/></a>
                    </div>
                    
                </tags:sectionContainer2>
            </form>
        </cti:displayForPageEditModes>
    </div>
</div>

<div id="failed-container"></div>

<cti:dataUpdaterEventCallback function="taskFinished" id="INVENTORY_TASK/${task.taskId}/IS_COMPLETE" />

</cti:standardPage>