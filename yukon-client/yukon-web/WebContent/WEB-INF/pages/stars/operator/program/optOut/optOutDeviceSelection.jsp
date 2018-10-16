<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="optOut.inventoryList">
<cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
<script type="text/javascript">
function createJSON () {
    var array = [],
        index = 0,
        inputElement;

    $('input').each(function (index, input) {
        var name,
            inventoryId,
            checked;
        if (input) {
            name = $(input).attr('name');
            if (name == 'inventoryId') {
                inventoryId = $(input).val();
                checked = $('#check_' + inventoryId).prop('checked');
                if (checked) {
                    array[index++] = inventoryId;
                }    
            }
        }
    });


    // error message here
    
    inputElement = document.createElement('input');
    inputElement.type = 'hidden';
    inputElement.name = 'jsonInventoryIds';
    inputElement.value = JSON.stringify(array);
    
    $('form')[0].appendChild(inputElement);
}

</script>    

<c:set var="showNextButton" value="false" />
<cti:url var="optOutUrl" value="/stars/operator/program/optOut/optOutQuestions"/>
<form:form id="form" modelAttribute="optOutBackingBean" action="${optOutUrl}" 
           method="POST" onsubmit="createJSON();">
    <cti:csrfToken/>       
    <input type="hidden" name="accountId" value="${accountId}" />

    <tags:sectionContainer2 nameKey="selectDevice">
        <div id="controlEventsDiv" style="height: auto;">
            <table class="compact-results-table row-highlighting selectDeviceTable dashed">
                <thead>
                <tr>
                    <th><i:inline key=".device"/></th>
                    <th><i:inline key=".programAssigned"/></th>
                    <th><i:inline key=".information"/></th>
                </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                <c:forEach var="displayableInventory" items="${displayableInventories}">
                    <c:set var="inventoryId" value="${displayableInventory.inventoryId}"/>
                    <c:set var="optOutCount" value="${optOutCounts[inventoryId]}"/>

                    <tr>
                        <td title="${displayableInventory.serialNumber}">
                            <label for="inventory_${inventoryId}">
                                <c:choose>
                                    <c:when test="${displayableInventory.currentlyOptedOut && isSameDay}">
                                        <input type="checkbox" disabled="disabled" >
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="showNextButton" value="true" />
                                        <input type="checkbox" id="inventory_${inventoryId}" name="inventoryIds" value="${inventoryId}"/>
                                    </c:otherwise>
                                </c:choose>
                                <spring:escapeBody htmlEscape="true">${displayableInventory.displayName}</spring:escapeBody>
                            </label>
                        </td>
                        <td>
                            <c:set var="programsList" value="${displayableInventory.programs}"/>
                            <c:set var="count" value="0"/>

                            <c:forEach var="program" items="${programsList}">
                                <c:set var="count" value="${count + 1}"/>
                                <spring:escapeBody htmlEscape="true">
                                    <cti:msg key="${program.displayName}" htmlEscape="true"/>
                                </spring:escapeBody>
                                <c:if test="${fn:length(programsList) != count}"><br></c:if>
                            </c:forEach>
                        </td>
                        <td>
                            <c:if test="${!optOutCount.optOutsRemaining}">
                                <i:inline key=".noMoreOptOutsAvailable"/><br>
                            </c:if>
                            <c:if test="${displayableInventory.currentlyOptedOut}">
                               <i:inline key=".currentlyOptedOut"/><br>
                            </c:if>
                            <c:if test="${not empty displayableInventory.currentlyScheduledOptOut}">
                               <i:inline key=".currentlyScheduledOptOut"/><br>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </tags:sectionContainer2>

    <div class="page-action-area">
        <c:if test="${showNextButton}">
            <cti:button nameKey="save" type="submit" classes="primary action" busy="true" blockPage="true"/>
        </c:if>
        <cti:button nameKey="cancel" name="cancel" type="submit"/>
    </div>

    <form:hidden path="durationInDays"/>
    <form:hidden path="startDate"/>
</form:form>
</cti:checkEnergyCompanyOperator>
</cti:standardPage>