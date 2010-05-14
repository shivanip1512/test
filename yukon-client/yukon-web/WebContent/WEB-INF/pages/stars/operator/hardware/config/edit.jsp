<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>


<cti:msgScope paths="modules.operator.hardwareConfigEdit,modules.operator.hardwareConfig">

<h1 class="dialogQuestion"><i:inline key=".headerMessage" arguments="${enrollment.programName.programName}"/></h1>

<cti:url var="submitUrl" value="/spring/stars/operator/hardware/config/enroll"/>
<script type="text/javascript">
submitForm = function(action) {
	$('actionInput').value = action;
    return submitFormViaAjax('hardwareConfigEditDialog', 'editForm', '${submitUrl}');
}
</script>
<form id="editForm" action="${submitUrl}">
    <input type="hidden" name="action" id="actionInput" value=""/>
    <input type="hidden" name="accountId" value="${accountId}"/>
    <input type="hidden" name="inventoryId" value="${param.inventoryId}"/>
    <input type="hidden" name="assignedProgramId" value="${param.assignedProgramId}"/>
    <tags:nameValueContainer2 id="editArea">
        <tags:nameValue2 nameKey=".group">
            <c:if test="${fn:length(loadGroups) == 0}">
                <i:inline key=".groupNotApplicable"/>
            </c:if>
            <c:if test="${fn:length(loadGroups) > 0}">
                <c:set var="selectedLoadGroupId" value="${enrollment.loadGroupId}"/>
                <select name="loadGroupId">
                    <c:forEach var="loadGroup" items="${loadGroups}">
                        <c:set var="selected" value=""/>
                        <c:if test="${selectedLoadGroupId == loadGroup.paoIdentifier.paoId}">
                            <c:set var="selected" value=" selected=\"selected\""/>
                        </c:if>
                        <option value="${loadGroup.paoIdentifier.paoId}"${selected}>
                            <spring:escapeBody htmlEscape="true">${loadGroup.name}</spring:escapeBody>
                        </option>
                    </c:forEach>
                </select>
            </c:if>
        </tags:nameValue2>
        <tags:nameValue2 nameKey=".relay">
            <select name="relay">
                <option value="0"><i:inline key=".noRelay"/></option>
                <c:forEach var="relayNumber" begin="1" end="${hardware.numRelays}">
                    <c:set var="selected" value=""/>
                    <c:if test="${relayNumber == enrollment.relay}">
                        <c:set var="selected" value=" selected=\"selected\""/>
                    </c:if>
                    <option value="${relayNumber}"${selected}>${relayNumber}</option>
                </c:forEach>
            </select>
        </tags:nameValue2>
    </tags:nameValueContainer2>

    <div class="actionArea">
        <input type="button" value="<cti:msg2 key=".config"/>"
               title="<cti:msg2 key=".config.description"/>"
               onclick="submitForm('config')"/>
        <input type="button" value="<cti:msg2 key=".saveToBatch"/>"
               title="<cti:msg2 key=".saveToBatch.description"/>"
               onclick="submitForm('saveToBatch')"/>
        <input type="button" value="<cti:msg2 key=".saveConfigOnly"/>"
               title="<cti:msg2 key=".saveConfigOnly.description"/>"
               onclick="submitForm('saveConfigOnly')"/>
        <input type="button" value="<cti:msg2 key=".cancel"/>"
               onclick="parent.$('hardwareConfigEditDialog').hide()"/>
    </div>

</form>

</cti:msgScope>
