<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="inventory.config.new.status">
    
<c:set var="taskId" value="${task.taskId}"/>
<c:set var="newAction" value="/stars/operator/inventory/actions/config/new-action"/>

<div class="stacked-md">
    <span class="label label-info"><i:inline key="yukon.common.note"/></span>&nbsp;
    <i:inline key=".inventory.actions.note"/>
</div>
    
<table class="name-value-table natural-width">
    
    <%-- PROGRESS --%>
    <tr>
        <td class="name"><i:inline key=".progress"/>:</td>
        <td class="value" colspan="2">
            <tags:updateableProgressBar totalCount="${task.totalItems}" containerClasses="vam"
                countKey="NEW_CONFIG/${taskId}/ITEMS_PROCESSED"/>
        </td>
    </tr>
    
    <tr style="height: 5px;"><td colspan="3"></td></tr>
    
    <%-- SUCESS --%>
    <tr>
        <td class="name"><i:inline key=".successCount"/>:</td>
        <td class="value">
            <cti:dataUpdaterValue type="NEW_CONFIG" identifier="${taskId}/SUCCESS_COUNT" styleClass="success fwb"/>
        </td>
        <td class="value">
            <cti:classUpdater type="NEW_CONFIG" identifier="${taskId}/NEW_OPERATION_FOR_SUCCESS">
                <cti:url var="url" value="${newAction}">
                    <cti:param name="type" value="SUCCESSFUL"/>
                    <cti:param name="taskId" value="${taskId}"/>
                </cti:url>
                <a href="${url}"><i:inline key=".newOperation"/></a>
            </cti:classUpdater>
        </td>
    </tr>
    
    <tr style="height: 5px;"><td colspan="3"></td></tr>
    
    <%-- UNSUPPORTED --%>
    <tr>
        <td class="name"><i:inline key=".unsupportedCount"/>:</td>
        <td class="value">
            <cti:dataUpdaterValue type="NEW_CONFIG" identifier="${taskId}/UNSUPPORTED_COUNT" styleClass="warning fwb"/>
        </td>
        <td class="value">
            <cti:classUpdater type="NEW_CONFIG" identifier="${taskId}/NEW_OPERATION_FOR_UNSUPPORTED">
                <cti:url var="url" value="${newAction}">
                    <cti:param name="type" value="UNSUPPORTED"/>
                    <cti:param name="taskId" value="${taskId}"/>
                </cti:url>
                <a href="${url}"><i:inline key=".newOperation"/></a>
            </cti:classUpdater>
        </td>
    </tr>
    
    <tr style="height: 5px;"><td colspan="3"></td></tr>
    
    <%-- FAILED --%>
    <tr>
        <td class="name"><i:inline key=".failedCount"/>:</td>
        <td class="value">
            <cti:dataUpdaterValue type="NEW_CONFIG" identifier="${taskId}/FAILED_COUNT" styleClass="error fwb"/>
        </td>
        <td class="value">
            <cti:classUpdater type="NEW_CONFIG" identifier="${taskId}/NEW_OPERATION_FOR_FAILED">
                <cti:url var="url" value="${newAction}">
                    <cti:param name="type" value="FAILED"/>
                    <cti:param name="taskId" value="${taskId}"/>
                </cti:url>
                <a href="${url}"><i:inline key=".newOperation"/></a>
            </cti:classUpdater>
        </td>
    </tr>
    
</table>

</cti:standardPage>