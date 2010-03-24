<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="hardwareConfig">

<tags:simpleDialog id="hardwareConfigEditDialog"/>
<tags:simpleDialog id="hcDialog"/>
<script type="text/javascript">
var programIdsAlreadyEnrolled = [];
</script>

<h1><i:inline key=".header" arguments="${hardware.deviceLabel}"/></h1>
<tags:boxContainer2 key="enrolledPrograms">
    <table class="compactResultsTable rowHighlighting">
        <tr>
            <th><i:inline key=".name"/></th>
            <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                <th><i:inline key=".group"/></th>
            </cti:checkRolesAndProperties>
            <th><i:inline key=".relay"/></th>
            <th><i:inline key=".actions"/></th>
        </tr>

        <c:forEach var="enrollment" items="${enrollments}">
            <c:set var="programId" value="${enrollment.assignedProgramId}"/>
            <script type="text/javascript">programIdsAlreadyEnrolled.push(${programId});</script>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>
                <%-- TODO:  name OR name (displayName)
                    <cti:msg2 var="nameValue" key=""/>
                    <c:if test="">
                    </c:if>
                --%>
                    <spring:escapeBody htmlEscape="true">
                        ${enrollment.programName.programName}
                    </spring:escapeBody>
                </td>
                <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                    <td>
                        <c:if test="${enrollment.loadGroupId != 0}">
                            <spring:escapeBody htmlEscape="true">
                                ${enrollment.loadGroupName}
                            </spring:escapeBody><br>
                        </c:if>
                    </td>
                </cti:checkRolesAndProperties>
                <c:set var="relayStr" value="${enrollment.relay}"/>
                <c:if test="${enrollment.relay == 0}">
                    <cti:msg2 var="relayStr" key=".noRelay"/>
                </c:if>
                <td>${relayStr}</td>
                <td>
                    <cti:url var="editUrl" value="/spring/stars/operator/hardware/config/edit">
                        <cti:param name="accountId" value="${accountId}"/>
                        <cti:param name="energyCompanyId"
                            value="${energyCompanyId}"/>
                        <cti:param name="inventoryId" value="${inventoryId}"/>
                        <cti:param name="assignedProgramId" value="${programId}"/>
                    </cti:url>
                    <tags:simpleDialogLink2 dialogId="hardwareConfigEditDialog" key="edit"
                        skipLabel="true" actionUrl="${editUrl}"/>

                    <cti:url var="removeUrl" value="/spring/stars/operator/hardware/config/remove">
                        <cti:param name="accountId" value="${accountId}"/>
                        <cti:param name="energyCompanyId"
                            value="${energyCompanyId}"/>
                        <cti:param name="inventoryId" value="${inventoryId}"/>
                        <cti:param name="assignedProgramId" value="${programId}"/>
                    </cti:url>
                    <tags:simpleDialogLink2 dialogId="hcDialog" key="remove"
                        skipLabel="true" actionUrl="${removeUrl}"/>
                </td>
            </tr>
        </c:forEach>
    </table>

    <form>
        <script type="text/javascript">
        function addEnrollment(devices) {
            openSimpleDialog('hardwareConfigEditDialog', $('addEnrollmentForm').action,
                    '<cti:msg2 key=".addEnrollmentDialogTitle"/>',
                     $('addEnrollmentForm').serialize(true));
            return true;
        }
        </script>
        <input type="hidden" id="programsToAssign" name="programsToAssign"/>
    </form>

    <cti:url var="editUrl" value="/spring/stars/operator/hardware/config/edit"/>
    <form id="addEnrollmentForm" action="${editUrl}">
        <input type="hidden" name="accountId" value="${accountId}"/>
        <input type="hidden" name="energyCompanyId" value="${energyCompanyId}"/>
        <input type="hidden" name="inventoryId" value="${inventoryId}"/>
        <div class="actionArea">
            <tags:pickerDialog type="assignedProgramPicker" id="programPicker"
                memoryGroup="programPicker"
                destinationFieldName="assignedProgramId"
                endAction="addEnrollment" styleClass="simpleLink"
                immediateSelectMode="true" extraArgs="${accountId}">
                <cti:labeledImg key="add"/>
            </tags:pickerDialog>
            <script type="text/javascript">
                programPicker.excludeIds = programIdsAlreadyEnrolled;
            </script>
        </div>
    </form>
</tags:boxContainer2>

</cti:standardPage>
