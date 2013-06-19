<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.dr.program.startProgram">

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
    if (targetCycleGears[document.getElementById('gearNumber').value] && document.getElementById('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/dr/program/start/gearAdjustments"/>';
    } else {
        url = '<cti:url value="/dr/program/start/constraints"/>';
    }
    return submitFormViaAjax('drDialog', 'startProgramForm', url);
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !document.getElementById('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', document.getElementById('scheduleStopCheckbox').checked);
}

gearChanged = function() {
    if (targetCycleGears[document.getElementById('gearNumber').value]) {
        jQuery('#addAdjustmentsArea').show();
    } else {
        jQuery('#addAdjustmentsArea').hide();
        document.getElementById('addAdjustmentsCheckbox').checked = false;
        updateSubmitButtons();
    }
}

updateSubmitButtons = function() {
    var autoObservingConstraints = false;
    if (${autoObserveConstraintsAllowed}) {
        autoObservingConstraints = true;
        if (${checkConstraintsAllowed}) {
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
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked"><cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/></h4>

<form:form id="startProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="programId"/>
    <input type="hidden" name="from" value="details"/>
    <form:hidden path="now"/>

    <table class="compactResultsTable">
        <thead>
            <tr class="headerRow">
                <th><cti:msg2 key=".gear"/></th>
                <th><cti:msg2 key=".startTime"/></th>
                <th><cti:msg2 key=".stopTime"/></th>
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
                                    <cti:msg2 key=".addAdjustments"/>
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
                                <cti:msg2 key=".startNow"/>
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
                                <cti:msg2 key=".scheduleStop"/>
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
                <cti:msg2 key=".autoObserveConstraints"/>
            </label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg2 key=".constraintsWillBeObserved"/>
            <input type="hidden" name="autoObserveConstraints" value="true"/>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <cti:button id="nextButton" nameKey="next" classes="primary action" type="submit"/>
            <cti:button id="okButton" nameKey="ok" classes="primary action" type="submit"/>
            <script type="text/javascript">updateSubmitButtons();</script>
        </c:if>
    </div>
</form:form>
</cti:msgScope>