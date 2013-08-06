<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.startMultiplePrograms">

<script type="text/javascript">
autoObserveConstraintsAllowed = ${autoObserveConstraintsAllowed};
checkConstraintsAllowed = ${checkConstraintsAllowed};
targetPrograms = ${cti:jsonString(targetGearMap)};
numPrograms = ${fn:length(programs)};

submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');

    if (document.getElementById('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/dr/program/start/multipleGearAdjustments"/>';
    } else {
        url = '<cti:url value="/dr/program/start/multipleConstraints"/>';
    }

    return submitFormViaAjax('drDialog', 'startMultipleProgramsForm', url);
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !document.getElementById('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', document.getElementById('scheduleStopCheckbox').checked);
}

updateSubmitButtons = function() {
    var autoObservingConstraints = false;
    if (autoObserveConstraintsAllowed) {
        autoObservingConstraints = true;
        if (checkConstraintsAllowed) {
            autoObservingConstraints = document.getElementById('autoObserveConstraints').checked;
        }
    }

    if (document.getElementById('addAdjustmentsCheckbox').checked || !autoObservingConstraints) {
        jQuery('#okButton').hide();
        jQuery('#nextButton').show();
    } else {
        jQuery('#okButton').show();
        jQuery('#nextButton').hide();
    }
}

allProgramsChecked = function() {
    allChecked = document.getElementById('allProgramsCheckbox').checked;
    for (index = 0; index < numPrograms; index++) {
        document.getElementById('startProgramCheckbox' + index).checked =
            allChecked && !document.getElementById('startProgramCheckbox' + index).disabled;
    }
    gearChanged();
}

updateAllProgramsChecked = function() {
    allChecked = true;
    for (index = 0; index < numPrograms; index++) {
        if (!document.getElementById('startProgramCheckbox' + index).disabled
                && !document.getElementById('startProgramCheckbox' + index).checked) {
            allChecked = false;
            break;
        }
    }
    document.getElementById('allProgramsCheckbox').checked = allChecked;
}

singleProgramChecked = function(boxChecked) {
    if (document.getElementById(boxChecked).checked) {
        updateAllProgramsChecked();
    } else {
        document.getElementById('allProgramsCheckbox').checked = false;
    }
    gearChanged();
}

gearChanged = function() {
    var adjustButtonShown = false;

    for (index = 0; index < numPrograms; index++) {
        var gearNum = document.getElementById('programGear' + index).value;
        var programChecked = document.getElementById('startProgramCheckbox' + index).checked;
        if(targetPrograms[index]) {
	        if (targetPrograms[index][gearNum] && programChecked) {
	            adjustButtonShown = true;
	            break;
	        }
        }
    }

    if (adjustButtonShown) {
        jQuery('#addAdjustmentsArea').show();
    } else {
        jQuery('#addAdjustmentsArea').hide();
        document.getElementById('addAdjustmentsCheckbox').checked = false;
    }
    updateSubmitButtons();
}
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked">
    <c:if test="${!empty controlArea}">
        <cti:msg2 key=".confirmQuestion.controlArea" htmlEscape="true" argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg2 key=".confirmQuestion.scenario" htmlEscape="true" argument="${scenario.name}"/>
    </c:if>
</h4>

<form:form id="startMultipleProgramsForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>
    <input type="hidden" name="from" value="details"/>
    <form:hidden path="now"/>

    <table class="compactResultsTable">
        <thead>
	        <tr>
	            <th><cti:msg2 key=".startTime"/></th>
	            <th><cti:msg2 key=".stopTime"/></th>
	        </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
	        <tr valign="top">
	            <td width="50%">
	                <table>
	                    <tr><td>
	                        <form:checkbox path="startNow" id="startNowCheckbox" onclick="startNowChecked()"/>
	                        <label for="startNowCheckbox">
	                            <cti:msg2 key=".startNow"/>
	                        </label>
	                    </td></tr>
	                    <tr><td>
	                        <tags:dateTimeInput path="startDate" fieldValue="${backingBean.startDate}"
	                            disabled="true"/>
	                    </td></tr>
	                </table>
	            </td>
	            <td width="50%">
	                <table>
	                    <tr><td>
	                        <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()"/>
	                        <label for="scheduleStopCheckbox">
	                            <cti:msg2 key=".scheduleStop"/>
	                        </label>
	                    </td></tr>
	                    <tr><td>
	                        <tags:dateTimeInput path="stopDate" fieldValue="${backingBean.stopDate}"/>
	                    </td></tr>
	                </table>
	            </td>
	        </tr>
        </tbody>
    </table>
    <br>

    <script type="text/javascript">
        startNowChecked();
        scheduleStopChecked();
    </script>

    <cti:msg2 var="boxTitle" key=".programs"/>
    <tags:boxContainer title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <thead>
	        <tr>
	            <th><cti:msg2 key=".startProgramName"/></th>
	            <th><cti:msg2 key=".gear"/></th>
	            <th><cti:msg2 key=".currentState"/></th>
	            <c:if test="${!empty scenarioPrograms}">
	                <th><cti:msg2 key=".startOffset"/></th>
	                <th><cti:msg2 key=".stopOffset"/></th>
	            </c:if>
	        </tr>
	    </thead>
	    <tfoot></tfoot>
	    <tbody>
	        <c:forEach var="program" varStatus="status" items="${programs}">
	            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
	            <c:set var="gears" value="${gearsByProgramId[programId]}"/>
	            <tr>
	                <td>
	                <form:hidden path="programStartInfo[${status.index}].programId"/>
	                <form:checkbox path="programStartInfo[${status.index}].startProgram"
	                    id="startProgramCheckbox${status.index}"
	                    onclick="singleProgramChecked(this);"/>
	                <label for="startProgramCheckbox${status.index}"><spring:escapeBody htmlEscape="true">${program.name}</spring:escapeBody></label></td>
	                <td><form:select path="programStartInfo[${status.index}].gearNumber" onchange="gearChanged()" id="programGear${status.index}">
	                    <c:forEach var="gear" varStatus="gearStatus" items="${gears}">
	                        <form:option value="${gearStatus.index + 1}"><spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody></form:option>
	                    </c:forEach>
	                </form:select></td>
	                <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/></td>
	                <c:if test="${!empty scenarioPrograms}">
	                    <c:set var="scenarioProgram" value="${scenarioPrograms[programId]}"/>
	                    <td><cti:formatPeriod type="HM_SHORT" value="${scenarioProgram.startOffset}"/></td>
	                    <td><cti:formatPeriod type="HM_SHORT" value="${scenarioProgram.stopOffset}"/></td>
	                </c:if>
	            </tr>
	        </c:forEach>
	    </tbody>
    </table>
    </div>
    </tags:boxContainer>

    <input type="checkbox" id="allProgramsCheckbox" onclick="allProgramsChecked()"/>
    <script type="text/javascript">updateAllProgramsChecked();</script>
    <label for="allProgramsCheckbox"><cti:msg2 key=".startAllPrograms"/></label><br>

    <c:set var="addAdjustmentAreaStyle" value="none"/>
    <c:if test="${!empty gears && gears[0].targetCycle}">
        <c:set var="addAdjustmentAreaStyle" value="block"/>
    </c:if>
    <div id="addAdjustmentsArea" style="display: ${addAdjustmentAreaStyle};">
        <form:checkbox path="addAdjustments" id="addAdjustmentsCheckbox" onclick="updateSubmitButtons();"/>
        <label for="addAdjustmentsCheckbox">
            <cti:msg2 key="yukon.web.modules.dr.program.startProgram.addAdjustments"/>
        </label><br>
    </div>

    <c:if test="${autoObserveConstraintsAllowed}">
        <c:if test="${checkConstraintsAllowed}">
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints" onclick="updateSubmitButtons();"/>
            <label for="autoObserveConstraints"><cti:msg2 key=".autoObserveConstraints"/></label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg2 key=".constraintsWillBeObserved"/>
            <input type="hidden" name="autoObserveConstraints" value="true"/>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <cti:button id="nextButton" nameKey="next" classes="primary action" type="submit"/>
            <cti:button id="okButton" nameKey="ok" classes="primary action" type="submit"/>
            <script type="text/javascript">gearChanged();</script>
        </c:if>
        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>