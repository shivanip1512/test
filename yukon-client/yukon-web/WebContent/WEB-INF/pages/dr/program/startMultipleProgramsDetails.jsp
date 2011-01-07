<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
autoObserveConstraintsAllowed = ${autoObserveConstraintsAllowed};
checkConstraintsAllowed = ${checkConstraintsAllowed};
targetPrograms = ${cti:jsonString(targetGearMap)};
numPrograms = ${fn:length(programs)};

submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');

    if ($('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/spring/dr/program/start/multipleGearAdjustments"/>';
    } else {
        url = '<cti:url value="/spring/dr/program/start/multipleConstraints"/>';
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
    var autoObservingConstraints = false;
    if (autoObserveConstraintsAllowed) {
        autoObservingConstraints = true;
        if (checkConstraintsAllowed) {
            autoObservingConstraints = $('autoObserveConstraints').checked;
        }
    }

    if ($('addAdjustmentsCheckbox').checked || !autoObservingConstraints) {
        $('okButton').disable();
        $('nextButton').enable();
    } else {
        $('okButton').enable();
        $('nextButton').disable();
    }
}

allProgramsChecked = function() {
    allChecked = $('allProgramsCheckbox').checked;
    for (index = 0; index < numPrograms; index++) {
        $('startProgramCheckbox' + index).checked =
            allChecked && !$('startProgramCheckbox' + index).disabled;
    }
    gearChanged();
}

updateAllProgramsChecked = function() {
    allChecked = true;
    for (index = 0; index < numPrograms; index++) {
        if (!$('startProgramCheckbox' + index).disabled
                && !$('startProgramCheckbox' + index).checked) {
            allChecked = false;
            break;
        }
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

    for (index = 0; index < numPrograms; index++) {
        var gearNum = $('programGear' + index).value;
        var programChecked = $('startProgramCheckbox' + index).checked;
        if (targetPrograms[index][gearNum] && programChecked) {
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

updateProgramState = function(index) {
  //assumes data is of type Hash
    return function(data) {
        if (data.get('state').startsWith('running')) {
            $('startProgramCheckbox' + index).disable();
            $('startProgramCheckbox' + index).checked = false;
            $('programGear' + index).disable();
        } else {
            $('startProgramCheckbox' + index).enable();
            $('programGear' + index).enable();
        }
        updateAllProgramsChecked();
    }
}
</script>

<cti:flashScopeMessages/>

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

<form:form id="startMultipleProgramsForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" name="from" value="details"/>
    <form:hidden path="now"/>

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
                        <tags:dateTimeInput path="startDate" fieldValue="${backingBean.startDate}"
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
                        <tags:dateTimeInput path="stopDate" fieldValue="${backingBean.stopDate}"/>
                    </td></tr>
                </table>
            </td>
        </tr>
    </table>
    <br>

    <script type="text/javascript">
        startNowChecked();
        scheduleStopChecked();
    </script>

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
                <label for="startProgramCheckbox${status.index}"><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></label></td>
                <td><form:select path="programStartInfo[${status.index}].gearNumber" onchange="gearChanged()" id="programGear${status.index}">
                    <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
                        <form:option value="${gearStatus.index + 1}"><spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody></form:option>
                    </c:forEach>
                </form:select></td>
                <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                <cti:dataUpdaterCallback function="updateProgramState(${status.index})" initialize="true" state="DR_PROGRAM/${programId}/SHOW_ACTION"/></td>
                <c:if test="${!empty scenarioPrograms}">
                    <c:set var="scenarioProgram" value="${scenarioPrograms[programId]}"/>
                    <td><cti:formatPeriod type="HM_SHORT" value="${scenarioProgram.startOffset}"/></td>
                    <td><cti:formatPeriod type="HM_SHORT" value="${scenarioProgram.stopOffset}"/></td>
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
            <input type="hidden" name="autoObserveConstraints" value="true"/>
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
