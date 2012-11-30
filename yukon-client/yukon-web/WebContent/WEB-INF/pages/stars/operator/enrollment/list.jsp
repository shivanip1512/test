<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="operator" page="enrollmentList">

<tags:simpleDialog id="peDialog"/>
<script type="text/javascript">
var programIdsAlreadyEnrolled = [];
</script>

<tags:boxContainer2 nameKey="enrolledPrograms" styleClass="stacked">
    <c:if test="${empty enrollmentPrograms}">
        <i:inline key=".noEnrolledPrograms"/>
    </c:if>
    <c:if test="${!empty enrollmentPrograms}">
    <table class="compactResultsTable rowHighlighting">
        <thead>
            <tr>
                <th class="programName"><i:inline key=".name"/></th>
                <th class="applianceCategory"><i:inline key=".applianceCategory"/></th>
                <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                    <th class="loadGroup"><i:inline key=".group"/></th>
                </cti:checkEnergyCompanySetting>
                <th class="hardware"><i:inline key=".hardware"/></th>
                <th class="relay"><i:inline key=".relay"/></th>
                <th class="status"><i:inline key=".status"/></th>
                <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                    <th class="actions"><i:inline key=".actions"/></th>
                </cti:checkRolesAndProperties>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                <tr>
                    <td class="programName">
                        <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
                        <script type="text/javascript">programIdsAlreadyEnrolled.push(${programId});</script>
                        <spring:escapeBody htmlEscape="true">
                            ${enrollmentProgram.program.programName}
                        </spring:escapeBody>
                    </td>
                    <td class="applianceCategory">
                        <spring:escapeBody htmlEscape="true">
                            ${enrollmentProgram.applianceCategory.name}
                        </spring:escapeBody>
                    </td>
                    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                        <td class="loadGroup">
                            <c:if test="${enrollmentProgram.loadGroupId != 0}">
                                <spring:escapeBody htmlEscape="true">
                                    ${loadGroupNames[enrollmentProgram.loadGroupId]}
                                </spring:escapeBody><br>
                            </c:if>
                        </td>
                    </cti:checkEnergyCompanySetting>
                    <td class="hardware">
                        <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                            <c:if test="${hardware.enrolled}">
                                <spring:escapeBody htmlEscape="true">
                                    ${hardware.displayName}
                                </spring:escapeBody><br>
                            </c:if>
                        </c:forEach>
                    </td>
                    <td class="relay">
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
                    <td class="status">
                        <c:forEach var="hardware" items="${enrollmentProgram.inventory}">
                            <c:if test="${hardware.enrolled}">
                                <i:inline key=".inService.${hardware.inService}"/><br>
                            </c:if>
                        </c:forEach>
                    </td>
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <td class="actions">
                            <cti:url var="editUrl" value="/stars/operator/enrollment/edit">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="assignedProgramId" value="${programId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="peDialog" nameKey="edit"
                                skipLabel="true" actionUrl="${editUrl}"/>
    
                            <cti:url var="unenrollUrl" value="/stars/operator/enrollment/confirmUnenroll">
                                <cti:param name="accountId" value="${accountId}"/>
                                <cti:param name="assignedProgramId" value="${programId}"/>
                            </cti:url>
                            <tags:simpleDialogLink2 dialogId="peDialog" nameKey="remove"
                                skipLabel="true" actionUrl="${unenrollUrl}"/>
                        </td>
                    </cti:checkRolesAndProperties>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:if>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <script type="text/javascript">
        function addEnrollment(devices) {
            openSimpleDialog('peDialog', $('addEnrollmentForm').action,
                    '<spring:escapeBody javaScriptEscape="true"><cti:msg2 key=".addEnrollmentDialogTitle"/></spring:escapeBody>',
                     $('addEnrollmentForm').serialize(true));
            return true;
        }
        </script>

        <cti:url var="editUrl" value="/stars/operator/enrollment/add"/>
        <form id="addEnrollmentForm" action="${editUrl}">
            <input type="hidden" name="accountId" value="${accountId}"/>
            <div class="actionArea">
                <tags:pickerDialog type="assignedProgramPicker" id="programPicker"
                    memoryGroup="programPicker"
                    destinationFieldName="assignedProgramId"
                    endAction="addEnrollment" styleClass="simpleLink"
                    immediateSelectMode="true" extraArgs="${energyCompanyId}"
                    linkType="button" nameKey="add"/>
                <script type="text/javascript">
                    programPicker.excludeIds = programIdsAlreadyEnrolled;
                </script>
            </div>
        </form>
    </cti:checkRolesAndProperties>
</tags:boxContainer2>

<dr:enrollmentHistory hardwareConfigActions="${hardwareConfigActions}"/>

</cti:standardPage>
