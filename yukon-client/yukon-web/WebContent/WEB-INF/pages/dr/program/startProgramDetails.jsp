<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.startProgram">

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

submitForm = function () {
    if (targetCycleGears[document.getElementById('gearNumber').value] && document.getElementById('addAdjustmentsCheckbox').checked) {
        url = '<cti:url value="/dr/program/start/gearAdjustments"/>';
    } else {
        url = '<cti:url value="/dr/program/start/constraints"/>';
    }
    return submitFormViaAjax('drDialog', 'startProgramForm', url);
}

startNowChecked = function () {
    $('#startDate').prop('disabled', $('#startNowCheckbox').prop('checked'));
}

scheduleStopChecked = function () {
    $('#stopDate').prop('disabled', !$('#scheduleStopCheckbox').prop('checked'));
}

gearChanged = function () {
    if (targetCycleGears[document.getElementById('gearNumber').value]) {
        $('#addAdjustmentsArea').show();
    } else {
        $('#addAdjustmentsArea').hide();
        document.getElementById('addAdjustmentsCheckbox').checked = false;
        updateSubmitButtons();
    }
}

updateSubmitButtons = function () {
    var autoObservingConstraints = false;
    if (${autoObserveConstraintsAllowed}) {
        autoObservingConstraints = true;
        if (${checkConstraintsAllowed}) {
            autoObservingConstraints = document.getElementById('autoObserveConstraints').checked;
        }
    }

    if (document.getElementById('addAdjustmentsCheckbox').checked || !autoObservingConstraints) {
        $('#okButton').hide();
        $('#nextButton').show();
	} else {
	    $('#okButton').show();
	    $('#nextButton').hide();
	}
}
$( function () {
    // init dateTime fields dynamically brought onto page after initial page load
    yukon.ui.initDateTimePickers();
})
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked"><cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/></h4>

<form:form id="startProgramForm" modelAttribute="backingBean" onsubmit="return submitForm();">
    <cti:csrfToken/>
    <form:hidden path="programId"/>
    <input type="hidden" name="from" value="details"/>
    <form:hidden path="now"/>

    <table class="compact-results-table no-stripes">
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
                                    <form:option value="${status.index + 1}">${fn:escapeXml(gear.gearName)}</form:option>
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
                        <tr>
                            <td>
                                <dt:dateTime id="startDate" path="startDate" value="${backingBean.startDate}" disabled="true"/>
                            </td>
                        </tr>
                    </table>
                </td>
                <td width="33%">
                    <table>
                        <tr><td>
                            <c:set var="disabledCheckbox" value="${program.paoIdentifier.paoType == 'LM_NEST_PROGRAM'}"/>
                            <form:checkbox path="scheduleStop" id="scheduleStopCheckbox" onclick="scheduleStopChecked()" disabled="${disabledCheckbox}"/>
                            <c:if test="${disabledCheckbox}">
                                <input type="hidden" name="scheduleStop" value="true"/>
                            </c:if>
                            <label for="scheduleStopCheckbox">
                                <cti:msg2 key=".scheduleStop"/>
                            </label><br>
                        </td></tr>
                        <tr>
                            <td>
                                <dt:dateTime id="stopDate" path="stopDate" value="${backingBean.stopDate}"/>
                            </td>
                        </tr>
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

    <div class="action-area">
        <c:if test="${autoObserveConstraintsAllowed || checkConstraintsAllowed}">
            <cti:button id="nextButton" nameKey="next" classes="primary action" type="submit"/>
            <cti:button id="okButton" nameKey="ok" classes="primary action" type="submit"/>
            <script type="text/javascript">updateSubmitButtons();</script>
        </c:if>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form:form>
</cti:msgScope>