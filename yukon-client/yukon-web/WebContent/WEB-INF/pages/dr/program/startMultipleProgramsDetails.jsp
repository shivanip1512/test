<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');
    return submitFormViaAjax('drDialog', 'startMultipleProgramsForm');
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !$('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', $('scheduleStopCheckbox').checked);
}

updateSubmitButtons = function() {
    if (!$('autoObserveConstraints').checked) {
        $('okButton').hide();
        $('nextButton').show();
    } else {
        $('okButton').show();
        $('nextButton').hide();
    }
}

allProgramsChecked = function() {
    allChecked = $('allProgramsCheckbox').checked;
	for (index = 0; index < ${fn:length(programs)}; index++) {
		$('startProgramCheckbox' + index).checked = allChecked;
	}
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
}
</script>

<p>
    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.controlArea"
            argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.confirmQuestion.scenario"
            argument="${scenario.name}"/>
    </c:if>
</p><br>

<c:url var="submitUrl" value="/spring/dr/program/startMultipleProgramsConstraints"/>
<form:form id="startMultipleProgramsForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>

    <table width="100%">
        <tr valign="top">
            <td width="25%">
                <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startTime"/><br>

                <form:checkbox path="startNow" id="startNowCheckbox" onclick="startNowChecked()"/>
                <label for="startNowCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startNow"/>
                </label><br>

                <tags:dateTimeInput fieldId="startDate" fieldValue="${backingBean.startDate}"
                    disabled="true"/>
            </td>
            <td width="25%">
                <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.stopTime"/><br>
                <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()"/>
                <label for="scheduleStopCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.scheduleStop"/>
                </label><br>
                <tags:dateTimeInput fieldId="stopDate" fieldValue="${backingBean.stopDate}"/>
            </td>
        </tr>
    </table>
    <br>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.startMultiplePrograms.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <table class="compactResultsTable">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startProgramName"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.gear"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.currentState"/></th>
        </tr>
        <c:forEach var="program" varStatus="status" items="${programs}">
            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
            <c:set var="gears" value="${gearsByProgramId[programId]}"/>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td><form:hidden path="programStartInfo[${status.index}].programId"/>
                <form:checkbox path="programStartInfo[${status.index}].startProgram"
                    id="startProgramCheckbox${status.index}"
                    onclick="singleProgramChecked(this);"/>
                <label for="startProgramCheckbox${status.index}">${program.name}</label></td>
                <td><form:select path="programStartInfo[${status.index}].gearNumber">
                    <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
                        <form:option value="${gearStatus.index + 1}">${gear.gearName}</form:option>
                    </c:forEach>
                </form:select></td>
                <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
            </tr>
        </c:forEach>
    </table>
    </tags:abstractContainer>
    <br>

    <input type="checkbox" id="allProgramsCheckbox" onclick="allProgramsChecked()"/>
    <script type="text/javascript">updateAllProgramsChecked();</script>
    <label for="allProgramsCheckbox">
        <cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.startAllPrograms"/>
    </label><br>
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
            <script type="text/javascript">updateSubmitButtons();</script>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
