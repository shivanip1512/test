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
        $('okButton').enable();
    } else {
        $('relaySelect' + inventoryId).disable();
        updateOKButton();
    }
}

inventoryIds = [];
updateOKButton = function() {
    for (var index = 0; index < inventoryIds.length; index++) {
        if ($('enrolledCB' + inventoryIds[index]).checked) {
            $('okButton').enable();
            return;
        }
    }
    $('okButton').disable();
}
</script>

<cti:msgScope paths="modules.operator.enrollmentEdit,modules.operator.enrollmentList">

<h1 class="dialogQuestion"><i:inline key=".headerMessage" arguments="${assignedProgram.displayName}"/></h1>

<cti:url var="submitUrl" value="/spring/stars/operator/enrollment/confirmSave">
    <cti:param name="isAdd" value="${isAdd}"/>
</cti:url>
<form:form id="inputForm" commandName="programEnrollment" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('peDialog', 'inputForm')">
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>

    <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
        <tags:nameValueContainer2 nameColumnWidth="150px">
            <tags:nameValue2 nameKey=".group">
                <c:if test="${fn:length(loadGroups) == 0}">
                    <i:inline key=".groupNotApplicable"/>
                </c:if>
                <c:if test="${fn:length(loadGroups) > 0}">
                    <c:set var="selectedLoadGroupId" value="${programEnrollment.loadGroupId}"/>
                    <form:select path="loadGroupId" items="${loadGroups}"
                        itemLabel="name" itemValue="paoIdentifier.paoId"/>
                </c:if>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </cti:checkRolesAndProperties>

    <br>
    <tags:boxContainer2 key="hardwareAssigned">
        <div class="dialogScrollArea">
        <table class="compactResultsTable rowHighlighting">
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <th></th>
                <th><i:inline key=".serialNumber"/></th>
                <th><i:inline key=".relay"/></th>
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
                    <td><label for="enrolledCB${inventoryId}">${displayableInventory.displayName}</label></td>
                    <td>
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
                <script type="text/javascript">
                enrollmentChanged(${inventoryId});
                </script>
            </c:forEach>
        </table>
        </div>
    </tags:boxContainer2>

    <div class="actionArea">
        <input id="okButton" type="submit" value="<cti:msg2 key=".ok"/>"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
            onclick="parent.$('peDialog').hide()"/>
    </div>

</form:form>

</cti:msgScope>
