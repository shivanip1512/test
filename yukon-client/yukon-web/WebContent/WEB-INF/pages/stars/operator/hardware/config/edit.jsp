<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="operator" page="hardwareConfig">
<cti:includeCss link="/WebConfig/yukon/styles/operator/hardware.css"/>

<c:set var="configurable" value="${hardware.hardwareType.configurable}"/>
<cti:checkRolesAndProperties value="OPERATOR_DISABLE_SWITCH_SENDING">
    <c:set var="configurable" value="false"/>
</cti:checkRolesAndProperties>

<cti:url var="submitUrl" value="/spring/stars/operator/hardware/config/commit"/>
<form:form id="editForm" name="editForm" action="${submitUrl}" commandName="configuration">
    <input id="actionInput" type="hidden" name="action" value=""/>
    <tags:hidden path="accountId"/>
    <tags:hidden path="inventoryId"/>

    <tags:formElementContainer nameKey="enrolledPrograms">
        <c:if test="${fn:length(enrollments) == 0}">
            <i:inline key=".noEnrolledPrograms"/>
        </c:if>
        <c:if test="${fn:length(enrollments) > 0}">
        <table class="compactResultsTable rowHighlighting">
            <tr>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".applianceCategory"/></th>
                <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                    <th><i:inline key=".group"/></th>
                </cti:checkRolesAndProperties>
                <th><i:inline key=".relay"/></th>
            </tr>

            <c:forEach var="enrollment" varStatus="status" items="${enrollments}">
                <tags:hidden path="programEnrollments[${status.index}].assignedProgramId"/>
                <c:set var="programId" value="${enrollment.assignedProgramId}"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <c:set var="assignedProgram" value="${assignedPrograms[enrollment.assignedProgramId]}"/>
                        <dr:assignedProgramName assignedProgram="${assignedPrograms[enrollment.assignedProgramId]}"/>
                    </td>
                    <td>
                        <spring:escapeBody htmlEscape="true">
                            ${applianceCategories[assignedPrograms[enrollment.assignedProgramId].applianceCategoryId].name}
                        </spring:escapeBody>
                    </td>
                    <cti:checkRolesAndProperties value="!TRACK_HARDWARE_ADDRESSING">
                        <td>
                            <c:set var="loadGroups" value="${loadGroupsByProgramId[programId]}"/>
                            <c:if test="${fn:length(loadGroups) == 0}">
                                <i:inline key=".groupNotApplicable"/>
                            </c:if>
                            <c:if test="${fn:length(loadGroups) > 0}">
                                <tags:selectWithItems path="programEnrollments[${status.index}].loadGroupId"
                                    items="${loadGroups}" itemLabel="name"
                                    itemValue="loadGroupId"/>
                            </c:if>
                        </td>
                    </cti:checkRolesAndProperties>
                    <td>
                        <form:select path="programEnrollments[${status.index}].relay">
                            <form:option value="0"><i:inline key=".noRelay"/></form:option>
                            <c:forEach var="relayNumber" begin="1" end="${hardware.numRelays}">
                                <form:option value="${relayNumber}">${relayNumber}</form:option>
                            </c:forEach>
                        </form:select>
                    </td>
            </c:forEach>
        </table>
        </c:if>
    </tags:formElementContainer>

    <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
        <cti:checkRolesAndProperties value="TRACK_HARDWARE_ADDRESSING">
            <br>
            <dr:hardwareAddressingInfo/>
        </cti:checkRolesAndProperties>
    </cti:checkRolesAndProperties>

    <div class="pageActionArea">
        <cti:checkRolesAndProperties value="OPERATOR_ALLOW_ACCOUNT_EDITING">
            <c:if test="${fn:length(enrollments) > 0}">
                <c:if test="${configurable}">
                    <input type="submit" value="<cti:msg2 key=".config"/>"
                           title="<cti:msg2 key=".config.description"/>"
                           onclick="$('actionInput').value = 'config';" class="formSubmit">
                    <input type="submit" value="<cti:msg2 key=".saveToBatch"/>"
                           title="<cti:msg2 key=".saveToBatch.description"/>"
                           onclick="$('actionInput').value = 'saveToBatch';" class="formSubmit">
                </c:if>
                <input type="submit" value="<cti:msg2 key=".saveConfigOnly"/>"
                       title="<cti:msg2 key=".saveConfigOnly.description"/>"
                       onclick="$('actionInput').value = 'saveConfigOnly';" class="formSubmit">
            </c:if>
        </cti:checkRolesAndProperties>
        <cti:url var="cancelUrl" value="/spring/stars/operator/hardware/list">
            <cti:param name="accountId" value="${accountId}"/>
        </cti:url>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
               onclick="window.location='${cancelUrl}'" class="formSubmit">
    </div>
</form:form>

<c:if test="${configurable}">
    <br>
    <br>
    <tags:boxContainer2 nameKey="otherDeviceActions">
        <cti:url var="disableUrl" value="/spring/stars/operator/hardware/config/disable">
           <cti:param name="accountId" value="${accountId}"/>
           <cti:param name="inventoryId" value="${param.inventoryId}"/>
        </cti:url>
        <input type="button" value="<cti:msg2 key=".disable"/>" onclick="window.location='${disableUrl}'" class="formSubmit">
        <cti:url var="enableUrl" value="/spring/stars/operator/hardware/config/enable">
           <cti:param name="accountId" value="${accountId}"/>
           <cti:param name="inventoryId" value="${param.inventoryId}"/>
        </cti:url>
        <input type="button" value="<cti:msg2 key=".enable"/>" onclick="window.location='${enableUrl}'" class="formSubmit">
        <c:if test="${inService}">
            <i:inline key=".inService"/>
        </c:if>
        <c:if test="${!inService}">
            <i:inline key=".outOfService"/>
        </c:if>
    </tags:boxContainer2>
</c:if>

</cti:standardPage>
