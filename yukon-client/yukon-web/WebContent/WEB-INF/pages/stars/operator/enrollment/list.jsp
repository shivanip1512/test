<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="enrollmentList">

<tags:simpleDialog id="peDialog"/>
<script type="text/javascript">
var programIdsAlreadyEnrolled = [];
</script>

<tags:boxContainer2 nameKey="enrolledPrograms">
    <table class="compactResultsTable rowHighlighting">
        <tr>
            <th><i:inline key=".name"/></th>
            <th><i:inline key=".applianceCategory"/></th>
            <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                <th><i:inline key=".group"/></th>
            </cti:checkRolesAndProperties>
            <th><i:inline key=".hardware"/></th>
            <th><i:inline key=".relay"/></th>
            <th><i:inline key=".status"/></th>
            <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                <th><i:inline key=".actions"/></th>
            </cti:checkRolesAndProperties>
        </tr>

        <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
            <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
            <script type="text/javascript">
            programIdsAlreadyEnrolled.push(${programId});
            </script>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td>
                    <spring:escapeBody htmlEscape="true">
                        ${enrollmentProgram.program.programName}
                    </spring:escapeBody>
                </td>
                <td>
                    <spring:escapeBody htmlEscape="true">
                        ${enrollmentProgram.applianceCategory.name}
                    </spring:escapeBody>
                </td>
                <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                    <td>
                        <c:if test="${enrollmentProgram.loadGroupId != 0}">
                            <spring:escapeBody htmlEscape="true">
                                ${loadGroupNames[enrollmentProgram.loadGroupId]}
                            </spring:escapeBody><br>
                        </c:if>
                    </td>
                </cti:checkRolesAndProperties>
                <td>
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                        <c:if test="${hardware.enrolled}">
                            <spring:escapeBody htmlEscape="true">
                                ${hardware.displayName}
                            </spring:escapeBody><br>
                        </c:if>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                        <c:if test="${hardware.enrolled}">
                            <c:set var="relayStr" value="${hardware.relay}"/>
                            <c:if test="${hardware.relay == 0}">
                                <cti:msg2 var="relayStr" key=".noRelay"/>
                            </c:if>
                            ${relayStr}<br>
                        </c:if>
                    </c:forEach>
                </td>
                <td>
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                        <c:if test="${hardware.enrolled}">
                            <i:inline key=".inService.${hardware.inService}"/><br>
                        </c:if>
                    </c:forEach>
                </td>
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <td>
                        <cti:url var="editUrl" value="/spring/stars/operator/enrollment/edit">
                            <cti:param name="accountId" value="${accountId}"/>
                            <cti:param name="assignedProgramId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="peDialog" key="edit"
                            skipLabel="true" actionUrl="${editUrl}"/>

                        <cti:url var="unenrollUrl" value="/spring/stars/operator/enrollment/confirmUnenroll">
                            <cti:param name="accountId" value="${accountId}"/>
                            <cti:param name="assignedProgramId" value="${programId}"/>
                        </cti:url>
                        <tags:simpleDialogLink2 dialogId="peDialog" key="remove"
                            skipLabel="true" actionUrl="${unenrollUrl}"/>
                    </td>
                </cti:checkRolesAndProperties>
            </tr>
        </c:forEach>
    </table>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <script type="text/javascript">
        function addEnrollment(devices) {
            openSimpleDialog('peDialog', $('addEnrollmentForm').action,
                    '<spring:escapeBody javaScriptEscape="true"><cti:msg2 key=".addEnrollmentDialogTitle"/></spring:escapeBody>',
                     $('addEnrollmentForm').serialize(true));
            return true;
        }
        </script>

        <cti:url var="editUrl" value="/spring/stars/operator/enrollment/add"/>
        <form id="addEnrollmentForm" action="${editUrl}">
            <input type="hidden" name="accountId" value="${accountId}"/>
            <div class="actionArea">
                <tags:pickerDialog type="assignedProgramPicker" id="programPicker"
                    memoryGroup="programPicker"
                    destinationFieldName="assignedProgramId"
                    endAction="addEnrollment" styleClass="simpleLink"
                    immediateSelectMode="true" extraArgs="${accountId}"
                    asButton="true"><cti:msg2 key=".add"/></tags:pickerDialog>
                <script type="text/javascript">
                    programPicker.excludeIds = programIdsAlreadyEnrolled;
                </script>
            </div>
        </form>
    </cti:checkRolesAndProperties>
</tags:boxContainer2>

<br>
<br>

<tags:boxContainer2 nameKey="history">
    <table class="compactResultsTable rowHighlighting">
        <tr>
            <th><i:inline key=".date"/></th>
            <th><i:inline key=".action"/></th>
            <th><i:inline key=".program"/></th>
            <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                <th><i:inline key=".group"/></th>
            </cti:checkRolesAndProperties>
            <th><i:inline key=".hardware"/></th>
            <th><i:inline key=".relay"/></th>
        </tr>
        <c:set var="maxActions" value="2"/>
        <c:if test="${param.showAllHistory}">
            <c:set var="maxActions" value="${fn:length(hardwareConfigActions) - 1}"/>
        </c:if>
        <c:forEach var="action" items="${hardwareConfigActions}" end="${maxActions}">
            <tr>
                <td><cti:formatDate value="${action.date}" type="BOTH"/></td>
                <td><cti:msg key="${action.actionType}"/></td>
                <td><spring:escapeBody htmlEscape="true">${action.programName}</spring:escapeBody></td>
                <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                    <td><spring:escapeBody htmlEscape="true">${action.loadGroupName}</spring:escapeBody></td>
                </cti:checkRolesAndProperties>
                <c:if test="${empty action.hardwareSerialNumber}">
                    <td><i:inline key=".deviceRemoved"/></td>
                    <td>&nbsp;</td>
                </c:if>
                <c:if test="${!empty action.hardwareSerialNumber}">
                    <td>
                        <spring:escapeBody htmlEscape="true">${action.hardwareSerialNumber}</spring:escapeBody>
                    </td>
                    <td>
                        <c:if test="${action.relay != 0}">${action.relay}</c:if>
                        <c:if test="${action.relay == 0}"><i:inline key=".noRelay"/></c:if>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
    <c:if test="${!param.showAllHistory && fn:length(hardwareConfigActions) > 3}">
        <div class="actionArea">
            <cti:url var="showAllHistoryUrl" value="/spring/stars/operator/enrollment/list">
                <cti:param name="accountId" value="${accountId}"/>
                <cti:param name="showAllHistory" value="true"/>
            </cti:url>
            <input type="button" value="<cti:msg2 key=".showAllHistory"/>" onclick="window.location='${showAllHistoryUrl}'">
        </div>
    </c:if>
</tags:boxContainer2>

</cti:standardPage>
