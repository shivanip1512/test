<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<script type="text/javascript">
overrideAllChecked = function() {
    allChecked = $('overrideAllConstraints').checked;
    for (index = 0; index < ${numProgramsToStart}; index++) {
    	var checkbox = $('programStartInfoOverride' + index);
        if (checkbox) checkbox.checked = allChecked;
    }
}

updateAllOverridesChecked = function() {
    allChecked = true;
    for (index = 0; index < ${numProgramsToStart}; index++) {
    	var checkbox = $('programStartInfoOverride' + index);
        if (checkbox) {
            allChecked &= checkbox.checked;
            if (!allChecked) break;
        }
    }
    $('overrideAllConstraints').checked = allChecked;
}

singleOverrideChecked = function(boxChecked) {
    if ($(boxChecked).checked) {
    	updateAllOverridesChecked();
    } else {
        $('overrideAllConstraints').checked = false;
    }
}
</script>

<cti:url var="submitUrl" value="/dr/program/start/multipleStart"/>
<form:form id="startMultipleProgramsForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitFormViaAjax('drDialog', 'startMultipleProgramsForm');">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" name="from" value="constraints"/>
    <form:hidden path="startNow"/>
    <form:hidden path="now"/>
    <form:hidden path="startDate"/>
    <form:hidden path="scheduleStop"/>
    <form:hidden path="stopDate"/>
    <form:hidden path="autoObserveConstraints"/>
    <form:hidden path="addAdjustments"/>
    <c:if test="${backingBean.addAdjustments}">
        <c:forEach var="index" begin="0" end="${fn:length(backingBean.gearAdjustments) - 1}">
            <form:hidden path="gearAdjustments[${index}].beginTime"/>
            <form:hidden path="gearAdjustments[${index}].endTime"/>
            <form:hidden path="gearAdjustments[${index}].adjustmentValue"/>
        </c:forEach>
    </c:if>

    <h1 class="dialogQuestion">
    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.controlArea"
        	htmlEscape="true" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.scenario"
        	htmlEscape="true" argument="${scenario.name}"/>
    </c:if>
    </h1>

    <dr:programStartInfo page="startMultiplePrograms"/>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.startMultiplePrograms.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable" id="startMultipleProgramsOverridePrograms">
        <tr>
            <c:if test="${constraintsViolated}">
                <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.overrideProgramName"/></th>
                <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.violations"/></th>
            </c:if>
            <c:if test="${!constraintsViolated}">
                <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.programName"/></th>
            </c:if>
        </tr>
        <c:set var="index" value="0"/>
        <c:forEach var="programStartInfo" varStatus="status" items="${backingBean.programStartInfo}">
            <c:if test="${backingBean.programStartInfo[status.index].startProgram}">
                <c:set var="program" value="${programsByProgramId[backingBean.programStartInfo[status.index].programId]}"/>
                <c:set var="violationsForProgram" value="${violationsByProgramId[backingBean.programStartInfo[status.index].programId]}"/>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <c:if test="${constraintsViolated}">
                        <td class="override">
                            <c:if test="${!empty violationsForProgram && overrideAllowed}">
                                <form:checkbox
                                   id="programStartInfoOverride${index}"
                                   path="programStartInfo[${status.index}].overrideConstraints"
                                   onclick="singleOverrideChecked(this)"/>
                            </c:if>
                            <label for="programStartInfoOverride${index}">
                            	<spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody>
                            </label>
                        </td>
                        <td class="violations">
                            <c:if test="${empty violationsForProgram}">
                                <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.noConstraintsViolatedForProgram"/>
                            </c:if>
                            <c:if test="${!empty violationsForProgram}">
                                    <ul>
                                        <c:forEach var="violation" items="${violationsForProgram.constraintContainers}">
                                            <li><spring:escapeBody htmlEscape="true"><cti:msg2 key="${violation.constraintTemplate}"/></spring:escapeBody></li>
                                        </c:forEach>
                                    </ul>
                            </c:if>
                        </td>
                    </c:if>
                    <c:if test="${!constraintsViolated}">
                        <td><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></td>
                    </c:if>
                </tr>
                <c:set var="index" value="${index + 1}"/>
            </c:if>
        </c:forEach>
    </table>
    </div>
    </tags:abstractContainer>

    <c:forEach var="programStartInfo" varStatus="status" items="${backingBean.programStartInfo}">
        <form:hidden path="programStartInfo[${status.index}].programId"/>
        <form:hidden path="programStartInfo[${status.index}].startProgram"/>
        <form:hidden path="programStartInfo[${status.index}].gearNumber"/>
    </c:forEach>

    <br>
    <c:if test="${!constraintsViolated}">
        <p><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.noConstraintsViolated"/></p>
    </c:if>
    <c:if test="${constraintsViolated}">
        <c:if test="${overrideAllowed}">
            <p><input type="checkbox" id="overrideAllConstraints"
                name="overrideAllConstraints" onclick="overrideAllChecked();">
            <label for="overrideAllConstraints">
                <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.overrideAllConstraints"/>
            </label></p>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <cti:url var="backUrl" value="/dr/program/start/multipleDetails">
            <cti:param name="fromBack" value="true"/>
        </cti:url>
        <c:if test="${backingBean.addAdjustments}">
            <cti:url var="backUrl" value="/dr/program/start/multipleGearAdjustments">
                <cti:param name="fromBack" value="true"/>
            </cti:url>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.backButton"/>"
            onclick="submitFormViaAjax('drDialog', 'startMultipleProgramsForm', '${backUrl}')"/>
        <c:if test="${!constraintsViolated || overrideAllowed}">
            <input type="submit" id="okButton" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.okButton"/>"/>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
