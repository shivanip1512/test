<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="enrollmentList">

<tags:simpleDialog id="peDialog"/>
<script type="text/javascript">
var programIdsAlreadyEnrolled = [];
</script>

<tags:sectionContainer2 nameKey="enrolledPrograms" styleClass="stacked">
    <c:if test="${empty enrollmentPrograms}">
        <i:inline key=".noEnrolledPrograms"/>
    </c:if>
    <c:if test="${not empty enrollmentPrograms}">
        <table class="compact-results-table manual-striping dashed">
            <thead>
                <tr>
                    <th class="programName"><i:inline key=".name"/></th>
                    <th class="applianceCategory"><i:inline key=".applianceCategory"/></th>
                    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                        <th class="loadGroup"><i:inline key=".group"/></th>
                    </cti:checkEnergyCompanySetting>
                    <th class="hardware"><i:inline key=".hardware"/></th>
                    <th class="relay wsnw"><i:inline key=".relay"/></th>
                    <th class="status wsnw"><i:inline key=".status"/></th>
                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                        <th class="actions"><i:inline key=".actions"/></th>
                    </cti:checkRolesAndProperties>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:set var="rowClass" value="alt-row"/>
                <c:forEach var="enrollmentProgram" items="${enrollmentPrograms}">
                    <c:choose>
                        <c:when test="${empty rowClass}">
                            <c:set var="rowClass" value="alt-row" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="rowClass" value="" />
                        </c:otherwise>
                    </c:choose>
                    <c:set var="numRows" value="0"/>
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}" varStatus="status">
                        <c:if test="${hardware.enrolled}">
                            <c:set var="numRows" value="${numRows + 1}" />
                        </c:if>
                    </c:forEach>
                    <c:set var="firstRow" value="true" />
                    <c:forEach var="hardware" items="${enrollmentProgram.inventory}" varStatus="status">
                        <c:if test="${hardware.enrolled}">
                            <tr class="${rowClass}">
                                <c:if test="${firstRow == 'true'}">
                                    <td class="programName" rowspan="${numRows}">
                                        <c:set var="programId" value="${enrollmentProgram.program.programId}"/>
                                        <script type="text/javascript">programIdsAlreadyEnrolled.push(${programId});</script>
                                        ${fn:escapeXml(enrollmentProgram.program.programName)}
                                    </td>
                                    <td class="applianceCategory" rowspan="${numRows}">
                                        ${fn:escapeXml(enrollmentProgram.applianceCategory.name)}
                                    </td>
                                    <cti:checkEnergyCompanySetting value="!TRACK_HARDWARE_ADDRESSING" energyCompanyId="${energyCompanyId}">
                                        <td class="loadGroup" rowspan="${numRows}">
                                            <c:if test="${enrollmentProgram.loadGroupId != 0}">
                                                ${fn:escapeXml(loadGroupNames[enrollmentProgram.loadGroupId])}
                                            </c:if>
                                        </td>
                                    </cti:checkEnergyCompanySetting>
                                </c:if>
                                <td class="hardware">
                                    ${fn:escapeXml(hardware.displayName)}
                                </td>
                                <td class="relay wsnw">
                                    <c:set var="relayStr" value="${hardware.relay}"/>
                                    <c:if test="${hardware.relay == 0}">
                                        <cti:msg2 var="relayStr" key=".noRelay"/>
                                    </c:if>
                                    ${relayStr}
                                </td>
                                <td class="status wsnw">
                                    <i:inline key="${hardware.inService.shortFormatKey}"/>
                                </td>
                                <c:if test="${firstRow == 'true'}">
                                    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
                                        <td class="actions" rowspan="${numRows}">
                                            <cti:url var="editUrl" value="/stars/operator/enrollment/edit">
                                                <cti:param name="accountId" value="${accountId}"/>
                                                <cti:param name="assignedProgramId" value="${programId}"/>
                                            </cti:url>
                                            <cti:url var="unenrollUrl" value="/stars/operator/enrollment/confirmUnenroll">
                                                <cti:param name="accountId" value="${accountId}"/>
                                                <cti:param name="assignedProgramId" value="${programId}"/>
                                            </cti:url>
                                            <tags:simpleDialogLink2 dialogId="peDialog" nameKey="edit"
                                                skipLabel="true" actionUrl="${editUrl}" icon="icon-pencil" styleClass="M0"/>
                                            <tags:simpleDialogLink2 dialogId="peDialog" nameKey="remove"
                                                skipLabel="true" actionUrl="${unenrollUrl}" icon="icon-cross" styleClass="M0"/>
                                        </td>
                                    </cti:checkRolesAndProperties>
                                </c:if>
                            </tr>
                            <c:set var="firstRow" value="false" />
                        </c:if>
                    </c:forEach>
                </c:forEach>
            </tbody>
        </table>
    </c:if>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <script type="text/javascript">
        function addEnrollment(devices) {
            var action = window.location.protocol + '//' +
                window.location.host +
                $('#addEnrollmentForm').attr('action'),
                jqserialized,
                serializedObj = {},
                ind;
            jqserialized = $('#addEnrollmentForm').serializeArray();
            // emulate the prototype serialize(true) functionality
            for(ind = 0; ind < jqserialized.length; ind += 1) {
                serializedObj[jqserialized[ind].name] = jqserialized[ind].value;
                serializedObj[jqserialized[ind].name] = jqserialized[ind].value;
            }
            openSimpleDialog('peDialog', action,
                    '<cti:msg2 key=".addEnrollmentDialogTitle" javaScriptEscape="true"/>',
                    serializedObj);
            return true;
        }
        </script>

        <cti:url var="editUrl" value="/stars/operator/enrollment/add"/>
        <form id="addEnrollmentForm" action="${editUrl}">
            <input type="hidden" name="accountId" value="${accountId}"/>
            <div class="action-area">
                <tags:pickerDialog type="assignedProgramPicker" id="programPicker"
                    memoryGroup="programPicker"
                    destinationFieldName="assignedProgramId"
                    endAction="addEnrollment"
                    immediateSelectMode="true" extraArgs="${energyCompanyId}"
                    linkType="button" nameKey="add" icon="icon-add"/>
                <script type="text/javascript">
                    programPicker.excludeIds = programIdsAlreadyEnrolled;
                </script>
            </div>
        </form>
    </cti:checkRolesAndProperties>
</tags:sectionContainer2>

<dr:enrollmentHistory hardwareConfigActions="${hardwareConfigActions}"/>

</cti:standardPage>
