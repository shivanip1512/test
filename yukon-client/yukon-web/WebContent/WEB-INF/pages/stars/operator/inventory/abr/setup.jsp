<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="operator" page="abr.setup">

<script type="text/javascript">
jQuery(document).delegate('a.failedItems', 'click', function() {
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
    jQuery('#homeLink').show();
}
</script>

<tags:setFormEditMode mode="${mode}"/>

    <cti:includeCss link="/WebConfig/yukon/styles/operator/inventory.css"/>
    
    <cti:dataGrid cols="2" tableClasses="twoColumnLayout">
        <cti:dataGridCell>
    
            <tags:formElementContainer nameKey="settings">
                <form:form action="do" commandName="abr">
                    <tags:hidden path="hardwareTypeId"/>
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".type"><spring:escapeBody htmlEscape="true">${type}</spring:escapeBody></tags:nameValue2>
                        <tags:nameValue2 nameKey=".range">
                            <spring:bind path="from">
                                <c:set var="inputClass" value=""/>
                                <c:if test="${status.error}">
                                    <c:set var="inputClass" value="error"/>
                                </c:if>
                                <cti:displayForPageEditModes modes="CREATE">
                                    <form:input path="from" cssClass="${inputClass} f_focus"/>
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
                            <form:errors path="from" cssClass="errorMessage" element="div"/>
                            <form:errors path="to" cssClass="errorMessage" element="div"/>
                        </tags:nameValue2>
                        
                        <tags:yukonListEntrySelectNameValue nameKey=".status" path="statusTypeId" energyCompanyId="${ecId}" listName="DEVICE_STATUS"/>
                        <tags:yukonListEntrySelectNameValue nameKey=".voltage" path="voltageTypeId" energyCompanyId="${ecId}" listName="DEVICE_VOLTAGE"/>
                        <tags:selectNameValue nameKey=".serviceCompany" path="serviceCompanyId" itemLabel="serviceCompanyName" itemValue="serviceCompanyId" 
                                    items="${serviceCompanies}" defaultItemValue="0" defaultItemLabel="${none}"/>
                        <tags:selectNameValue nameKey=".route" path="routeId"  itemLabel="paoName" itemValue="yukonID" items="${routes}"  defaultItemValue="0" defaultItemLabel="${defaultRoute}"/>
                    </tags:nameValueContainer2>
                    
                    <cti:displayForPageEditModes modes="CREATE">
                        <div class="pageActionArea">
                            <cti:button nameKey="start" type="submit" name="start"/>
                            <cti:button nameKey="cancel" type="submit" name="cancel"/>
                        </div>
                    </cti:displayForPageEditModes>
                </form:form>
            </tags:formElementContainer>
        
        </cti:dataGridCell>
        
        <cti:dataGridCell>
    
            <cti:displayForPageEditModes modes="VIEW">
                
                <cti:url var="newOperationSuccess" value="/spring/stars/operator/inventory/abr/newOperation">
                    <cti:param name="taskId" value="${task.taskId}"/>
                </cti:url>
            
                <form action="do" method="post">
                    <input type="hidden" name="taskId" value="${task.taskId}">
                    <tags:formElementContainer nameKey="progress">
                        <tags:nameValueContainer2>
                            <tags:nameValue2 nameKey=".progress">
                                <c:if test="${not empty task}">
                                    <tags:updateableProgressBar totalCount="${task.totalItems}" countKey="INVENTORY_TASK/${task.taskId}/ITEMS_PROCESSED"/>
                                </c:if>
                            </tags:nameValue2>
                            <tags:nameValue2 nameKey=".successful">
                                <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/SUCCESS_COUNT" styleClass="successMessage normalBoldLabel"/>
                                <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_SUCCESS">
                                    <ul class="resultList">
                                        <li>
                                            <a href="${newOperationSuccess}" class="small"><i:inline key=".newOperation"/></a>
                                        </li>
                                    </ul>
                                </cti:classUpdater>
                            </tags:nameValue2>
                            
                            <tags:nameValue2 nameKey=".failed">
                                <cti:dataUpdaterValue type="INVENTORY_TASK" identifier="${task.taskId}/FAILED_COUNT" styleClass="errorMessage normalBoldLabel"/>
                                
                                <cti:classUpdater type="INVENTORY_TASK" identifier="${task.taskId}/NEW_OPERATION_FOR_FAILED">
                                    <ul class="resultList">
                                        <li>
                                            <a href="javascript:void(0);" class="small failedItems"><i:inline key=".viewFailureReasons"/></a>
                                        </li>
                                    </ul>
                                </cti:classUpdater>
                            </tags:nameValue2>
                            
                        </tags:nameValueContainer2>
                        
                        <div class="pageActionArea marginBottom">
                            <c:if test="${!task.complete}">
                                <c:set var="buttonClass" value="db"/>
                                <c:set var="linkClass" value="dn"/>
                            </c:if>
                            <c:if test="${task.complete}">
                                <c:set var="buttonClass" value="dn"/>
                                <c:set var="linkClass" value="db"/>
                            </c:if>
                            <cti:button nameKey="cancel" type="sumbit" name="cancel" id="cancelTaskBtn" styleClass="${buttonClass}"/>
                            <a id="homeLink" href="/spring/stars/operator/inventory/home" class="${linkClass}"><i:inline key=".inventoryHome"/></a>
                        </div>
                        <cti:dataUpdaterEventCallback function="taskFinished" id="INVENTORY_TASK/${task.taskId}/IS_COMPLETE" />
                        
                    </tags:formElementContainer>
                </form>
            </cti:displayForPageEditModes>
        
        </cti:dataGridCell>
    </cti:dataGrid>
    
    <div id="failedContainer"></div>
</cti:standardPage>