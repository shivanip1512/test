<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="modules.operator.enrollmentEdit,modules.operator.enrollmentList">
<script type="text/javascript">
    inventoryIds = [];
    <c:forEach var="item" items="${programEnrollment.inventoryEnrollments}">
        inventoryIds.push(${item.inventoryId});
    </c:forEach>
    updateOKButton = function () {
        var index;
        for (index = 0; index < inventoryIds.length; index++) {
            if ($('#enrolledCB' + inventoryIds[index]).is(":checked")) {
                $('#okBtn').prop('disabled', false);
                return;
            }
        }
        $('#okBtn').prop ('disabled', true);
    };
    enrollmentChanged = function (inventoryId) {
        var isEnrolled = $('#enrolledCB' + inventoryId).is(":checked");
        if (isEnrolled) {
            $('#relaySelect' + inventoryId).prop('disabled', false);
            if (0 < $('#okBtn').length) {
                $('#okBtn').prop('disabled', false);
            }
        } else {
            $('#relaySelect' + inventoryId).prop('disabled', true);
            updateOKButton();
        }
    };
    $(function () {
        var index,
            inventoryId,
            idsArr = $('input[name$="inventoryId"]').map(function(index, el) {
                return $(el).val();
            });
        for (index = 0; index < idsArr.length; index++) {
            inventoryId = idsArr[index];
            if ($('#enrolledCB' + inventoryId).is(":checked")) {
                $('#relaySelect' + inventoryId).prop('disabled', false);
            } else {
                $('#relaySelect' + inventoryId).prop('disabled', true);
            }
        }
        $('#okBtn').prop('disabled', !$('#okBtn').prop('disabled'));
    });
</script>


<div><i:inline key=".headerMessage" arguments="${assignedProgram.displayName}"/></div>

<cti:url var="submitUrl" value="/stars/operator/enrollment/confirmSave">
    <cti:param name="isAdd" value="${isAdd}"/>
</cti:url>
<form:form id="inputForm" commandName="programEnrollment" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('peDialog', 'inputForm')">
    <cti:csrfToken/>
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>

    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}" >
        <tags:nameValueContainer2 tableClass="stacked">
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

    <tags:sectionContainer2 nameKey="hardwareAssigned">
        <div class="scroll-md">
        <table id="hardwareAssignedTable" class="compact-results-table with-form-controls clearfix">
            <thead>
                <tr>
                    <th></th>
                    <th class="deviceLabel"><i:inline key=".deviceLabel"/></th>
                    <th class="relay"><i:inline key=".relay"/></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="item" varStatus="status"
                    items="${programEnrollment.inventoryEnrollments}">
                    <c:set var="inventoryId" value="${item.inventoryId}"/>
                    <c:set var="displayableInventory" value="${inventoryById[inventoryId]}"/>
                    <tr>
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
                                onchange="enrollmentChanged(${inventoryId});"
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
            </tbody>
        </table>
        </div>
    </tags:sectionContainer2>

    <div class="action-area">
        <cti:button id="okBtn" nameKey="ok" type="submit" classes="primary action"/>
        <cti:button nameKey="cancel" onclick="$('#peDialog').dialog('close');"/>
    </div>

</form:form>

</cti:msgScope>
