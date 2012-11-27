<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<script type="text/javascript">
enrollmentChanged = function(inventoryId) {
    var isEnrolled = $('enrolledCB' + inventoryId).checked;
    if (isEnrolled) {
        $('relaySelect' + inventoryId).enable();
        if ($('okBtn')) {
            $('okBtn').disabled = false;
        }
    } else {
        $('relaySelect' + inventoryId).disable();
        updateOKButton();
    }
}

inventoryIds = [];
updateOKButton = function() {
    for (var index = 0; index < inventoryIds.length; index++) {
        if ($('enrolledCB' + inventoryIds[index]).checked) {
            $('okBtn').disabled = false;
            return;
        }
    }
    $('okBtn').disabled = true;
}
</script>

<cti:msgScope paths="modules.operator.enrollmentEdit,modules.operator.enrollmentList">

<div><i:inline key=".headerMessage" arguments="${assignedProgram.displayName}"/></div>

<cti:url var="submitUrl" value="/stars/operator/enrollment/confirmSave">
    <cti:param name="isAdd" value="${isAdd}"/>
</cti:url>
<form:form id="inputForm" commandName="programEnrollment" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('peDialog', 'inputForm')">
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>

    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}" >
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".group">
                <c:if test="${empty loadGroups}">
                    <i:inline key=".groupNotApplicable"/>
                </c:if>
                <c:if test="${!empty loadGroups}">
                    <c:set var="selectedLoadGroupId" value="${programEnrollment.loadGroupId}"/>
                    <form:select path="loadGroupId" items="${loadGroups}"
                        itemLabel="name" itemValue="paoIdentifier.paoId"/>
                </c:if>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </cti:checkEnergyCompanySetting>

    <br>
    <tags:boxContainer2 nameKey="hardwareAssigned">
        <div class="dialogScrollArea">
        <table id="hardwareAssignedTable" class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th></th>
                <th class="deviceLabel"><i:inline key=".deviceLabel"/></th>
                <th class="relay"><i:inline key=".relay"/></th>
            </tr>
            <c:forEach var="item" varStatus="status"
                items="${programEnrollment.inventoryEnrollments}">
                <c:set var="inventoryId" value="${item.inventoryId}"/>
                <script type="text/javascript">inventoryIds.push(${item.inventoryId});</script>
                <c:set var="displayableInventory" value="${inventoryById[inventoryId]}"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <form:hidden path="inventoryEnrollments[${status.index}].inventoryId"/>
                        <form:checkbox id="enrolledCB${inventoryId}"
                            path="inventoryEnrollments[${status.index}].enrolled"
                            onclick="enrollmentChanged(${inventoryId});"/>
                    </td>
                    <td class="deviceLabel"><label for="enrolledCB${inventoryId}">
                        <spring:escapeBody htmlEscape="true">${displayableInventory.displayName}</spring:escapeBody>
                    </label></td>
                    <td class="relay">
                        <form:select id="relaySelect${inventoryId}"
                            path="inventoryEnrollments[${status.index}].relay">
                            <form:option value="0"><cti:msg2 key=".noRelay"/></form:option>
                            <c:forEach var="relayNumber" begin="1"
                                end="${displayableInventory.numRelays}">
                                <form:option value="${relayNumber}">${relayNumber}</form:option>
                            </c:forEach>
                        </form:select>
                    </td>
                </tr>
            </c:forEach>
        </table>
        </div>
    </tags:boxContainer2>

    <div class="actionArea">
        <cti:button id="okBtn" nameKey="ok" type="submit" styleClass="f_blocker"/>
        <input class="formSubmit" type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('peDialog').hide()"/>
    </div>
    <script type="text/javascript">
    var okEnabled = false;
    for (var index = 0; index < inventoryIds.length; index++) {
        var inventoryId = inventoryIds[index];
        if ($('enrolledCB' + inventoryId).checked) {
        	okEnabled = true;
            $('relaySelect' + inventoryId).enable();
        } else {
            $('relaySelect' + inventoryId).disable();
        }
    }
    $('okBtn').disabled = !okEnabled;
    </script>

</form:form>

</cti:msgScope>
