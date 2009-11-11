<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">

targetPrograms = ${cti:jsonString(targetGearMap)}

submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');

    if ($('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/spring/dr/program/startMultipleProgramsGearAdjustments"/>';
    } else {
        url = '<cti:url value="/spring/dr/program/startMultipleProgramsConstraints"/>';
    }

    return submitFormViaAjax('drDialog', 'startMultipleProgramsForm', url);
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !$('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', $('scheduleStopCheckbox').checked);
}

updateSubmitButtons = function() {
    if ($('addAdjustmentsCheckbox').checked ||
        !$('autoObserveConstraints').checked) {
        $('okButton').disable();
        $('nextButton').enable();
    } else {
        $('okButton').enable();
        $('nextButton').disable();
    }
}

allProgramsChecked = function() {
    allChecked = $('allProgramsCheckbox').checked;
    for (index = 0; index < ${fn:length(programs)}; index++) {
        $('startProgramCheckbox' + index).checked = allChecked;
    }
    gearChanged();
}

updateAllProgramsChecked = function() {
    allChecked = true;
    for (index = 0; index < ${fn:length(programs)}; index++) {
        allChecked &= $('startProgramCheckbox' + index).checked;
        if (!allChecked) break;
    }
    $('allProgramsCheckbox').checked = allChecked;
}

singleProgramChecked = function(boxChecked) {
    if ($(boxChecked).checked) {
        updateAllProgramsChecked();
    } else {
        $('allProgramsCheckbox').checked = false;
    }
    gearChanged();
}

gearChanged = function() {
    var adjustButtonShown = false;

    for (index = 0; index < ${fn:length(programs)}; index++) {
        var gearNum = $('programStartInfo'+index+'.gearNumber').value;
        var programChecked = $('startProgramCheckbox'+index).checked;
        if (targetPrograms[index][gearNum] &&
            programChecked) {
            adjustButtonShown = true;
            break;
        }
    }

    if (adjustButtonShown) {
        $('addAdjustmentsArea').show();
    } else {
        $('addAdjustmentsArea').hide();
        $('addAdjustmentsCheckbox').checked = false;
    }
    updateSubmitButtons();
}

</script>

<h1 class="dialogQuestion">
    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.controlArea"
            argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.scenario"
            argument="${scenario.name}"/>
    </c:if>
</h1>

<form:form id="startMultipleProgramsForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>

    <table class="compactResultsTable">
        <tr>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startTime"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.stopTime"/></th>
        </tr>
        <tr valign="top">
            <td width="50%" class="padded">
                <table>
                    <tr><td>
                        <form:checkbox path="startNow" id="startNowCheckbox" onclick="startNowChecked()"/>
                        <label for="startNowCheckbox">
                            <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startNow"/>
                        </label>
                    </td></tr>
                    <tr><td class="padded">
                        <tags:dateTimeInput fieldId="startDate" fieldValue="${backingBean.startDate}"
                    disabled="true"/>
                    </td></tr>
                </table>
            </td>
            <td width="50%" class="padded">
                <table>
                    <tr><td>
                        <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()"/>
                        <label for="scheduleStopCheckbox">
                            <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.scheduleStop"/>
                        </label>
                    </td></tr>
                    <tr><td class="padded">
                        <tags:dateTimeInput fieldId="stopDate" fieldValue="${backingBean.stopDate}"/>
                    </td></tr>
                </table>
            </td>
        </tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.startMultiplePrograms.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startProgramName"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.gear"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.currentState"/></th>
            <c:if test="${!empty scenarioPrograms}">
                <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startOffset"/></th>
                <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.stopOffset"/></th>
            </c:if>
        </tr>
        <c:forEach var="program" varStatus="status" items="${programs}">
            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
            <c:set var="gears" value="${gearsByProgramId[programId]}"/>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td><form:hidden path="programStartInfo[${status.index}].programId"/>
                <form:checkbox path="programStartInfo[${status.index}].startProgram"
                    id="startProgramCheckbox${status.index}"
                    onclick="singleProgramChecked(this);"/>
                <label for="startProgramCheckbox${status.index}"><spring:escapeBody>${program.name}</spring:escapeBody></label></td>
                <td><form:select path="programStartInfo[${status.index}].gearNumber" onchange="gearChanged()">
                    <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
                        <form:option value="${gearStatus.index + 1}"><spring:escapeBody>${gear.gearName}</spring:escapeBody></form:option>
                    </c:forEach>
                </form:select></td>
                <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
                <c:if test="${!empty scenarioPrograms}">
                    <c:set var="scenarioProgram" value="${scenarioPrograms[programId]}"/>
                    <td><cti:formatDate type="TIME24H" value="${scenarioProgram.startOffset}"/></td>
                    <td><cti:formatDate type="TIME24H" value="${scenarioProgram.stopOffset}"/></td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
    </div>
    </tags:abstractContainer>
    <br>

    <input type="checkbox" id="allProgramsCheckbox" onclick="allProgramsChecked()"/>
    <script type="text/javascript">updateAllProgramsChecked();</script>
    <label for="allProgramsCheckbox">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startAllPrograms"/>
    </label><br>

    <c:set var="addAdjustmentAreaStyle" value="none"/>
    <c:if test="${!empty gears && gears[0].targetCycle}">
        <c:set var="addAdjustmentAreaStyle" value="block"/>
    </c:if>
    <div id="addAdjustmentsArea" style="display: ${addAdjustmentAreaStyle};">
        <form:checkbox path="addAdjustments" id="addAdjustmentsCheckbox"
            onclick="updateSubmitButtons();"/>
        <label for="addAdjustmentsCheckbox">
            <cti:msg key="yukon.web.modules.dr.program.startProgram.addAdjustments"/>
        </label><br>
    </div>

    <c:if test="${autoObserveConstraintsAllowed}">
        <c:if test="${checkConstraintsAllowed}">
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints"
                onclick="updateSubmitButtons();"/>
            <label for="autoObserveConstraints">
                <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.autoObserveConstraints"/>
            </label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.constraintsWillBeObserved"/>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <input id="nextButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.nextButton"/>"/>
            <input id="okButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.okButton"/>"/>
            <script type="text/javascript">gearChanged();</script>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
