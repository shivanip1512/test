<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
targetCycleGears = {
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
        url = '<cti:url value="/spring/dr/program/startProgramGearAdjustments"/>';
    } else {
        url = '<cti:url value="/spring/dr/program/startProgramConstraints"/>';
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
    if ($('addAdjustmentsCheckbox').checked
        || !$('autoObserveConstraints').checked) {
        $('okButton').hide();
        $('nextButton').show();
    } else {
        $('okButton').show();
        $('nextButton').hide();
    }
}
</script>

<p>
    <cti:msg key="yukon.web.modules.dr.program.startProgram.confirmQuestion"
        argument="${program.name}"/>
</p><br>

<form:form id="startProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="programId"/>

    <table width="100%">
        <tr valign="top">
            <td width="33%">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.gear"/><br>
                <form:select path="gearNumber" id="gearNumber" onchange="gearChanged()">
                    <c:forEach var="gear" varStatus="status" items="${gears}">
                        <form:option value="${status.index + 1}">${gear.gearName}</form:option>
                    </c:forEach>
                </form:select><br>
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
            </td>
            <td width="33%">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.startTime"/><br>

                <form:checkbox path="startNow" id="startNowCheckbox" onclick="startNowChecked()"/>
                <label for="startNowCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.startProgram.startNow"/>
                </label><br>

                <tags:dateTimeInput fieldId="startDate" fieldValue="${backingBean.startDate}"
                    disabled="true"/>
            </td>
            <td width="33%">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.stopTime"/><br>
                <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()"/>
                <label for="scheduleStopCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.startProgram.scheduleStop"/>
                </label><br>
                <tags:dateTimeInput fieldId="stopDate" fieldValue="${backingBean.stopDate}"/>
            </td>
        </tr>
    </table>

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
