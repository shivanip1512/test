<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
targetCycleGears = {
	<c:set var="sep" value=""/>
    <c:forEach var="gear" varStatus="status" items="${gears}">
        <c:if test="${gear.targetCycle}">
            ${sep}${status.index + 1} : true
            <c:set var="sep" value=","/>
        </c:if>
    </c:forEach>
};

submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');
    if (targetCycleGears[$('gearNumber').value] && $('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/dr/program/start/gearAdjustments"/>';
    } else {
        url = '<cti:url value="/dr/program/start/constraints"/>';
    }
    return submitFormViaAjax('drDialog', 'startProgramForm', url);
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !$('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', $('scheduleStopCheckbox').checked);
}

gearChanged = function() {
    if (targetCycleGears[$('gearNumber').value]) {
        $('addAdjustmentsArea').show();
    } else {
        $('addAdjustmentsArea').hide();
        $('addAdjustmentsCheckbox').checked = false;
        updateSubmitButtons();
    }
}

updateSubmitButtons = function() {
    var autoObservingConstraints = false;
    if (${autoObserveConstraintsAllowed}) {
        autoObservingConstraints = true;
        if (${checkConstraintsAllowed}) {
            autoObservingConstraints = $('autoObserveConstraints').checked;
        }
    }

    if ($('addAdjustmentsCheckbox').checked || !autoObservingConstraints) {
        $('okButton').disable();
        $('nextButton').enable();
	}
	else {
        $('okButton').enable();
        $('nextButton').disable();
	}
}
</script>

<cti:flashScopeMessages/>

<h1 class="dialogQuestion">
    <cti:msg key="yukon.web.modules.dr.program.startProgram.confirmQuestion"
    	htmlEscape="true" argument="${program.name}"/>
</h1>

<form:form id="startProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="programId"/>
    <input type="hidden" name="from" value="details"/>
    <form:hidden path="now"/>

    <table class="compactResultsTable">
        <thead>
            <tr class="headerRow">
                <th><cti:msg key="yukon.web.modules.dr.program.startProgram.gear"/></th>
                <th><cti:msg key="yukon.web.modules.dr.program.startProgram.startTime"/></th>
                <th><cti:msg key="yukon.web.modules.dr.program.startProgram.stopTime"/></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <tr valign="top">
                <td width="33%">
                    <table>
                        <tr><td>
                            <form:select path="gearNumber" id="gearNumber" onchange="gearChanged()">
                                <c:forEach var="gear" varStatus="status" items="${gears}">
                                    <form:option value="${status.index + 1}"><spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody></form:option>
                                </c:forEach>
                            </form:select>
                        </td></tr>
                        <tr><td>
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
                        </td></tr>
                    </table>
                </td>
                <td width="33%">
                    <table>
                        <tr><td>
                            <form:checkbox path="startNow" id="startNowCheckbox" onclick="startNowChecked()"/>
                            <label for="startNowCheckbox">
                                <cti:msg key="yukon.web.modules.dr.program.startProgram.startNow"/>
                            </label><br>
                        </td></tr>
                        <tr><td>
                            <tags:dateTimeInput path="startDate" fieldValue="${backingBean.startDate}"
                                disabled="true"/>
                        </td></tr>
                    </table>
                </td>
                <td width="33%">
                    <table>
                        <tr><td>
                            <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()"/>
                            <label for="scheduleStopCheckbox">
                                <cti:msg key="yukon.web.modules.dr.program.startProgram.scheduleStop"/>
                            </label><br>
                        </td></tr>
                        <tr><td>
                            <tags:dateTimeInput path="stopDate" fieldValue="${backingBean.stopDate}"/>
                        </td></tr>
                    </table>
                </td>
            </tr>
        </tbody>
    </table>

    <script type="text/javascript">
        startNowChecked();
        scheduleStopChecked();
    </script>
    
    <br>
    <br>

    <c:if test="${autoObserveConstraintsAllowed}">
        <c:if test="${checkConstraintsAllowed}">
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints"
                onclick="updateSubmitButtons();"/>
            <label for="autoObserveConstraints">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.autoObserveConstraints"/>
            </label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg key="yukon.web.modules.dr.program.startProgram.constraintsWillBeObserved"/>
            <input type="hidden" name="autoObserveConstraints" value="true"/>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <input id="nextButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.nextButton"/>"/>
            <input id="okButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.okButton"/>"/>
            <script type="text/javascript">updateSubmitButtons();</script>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
