<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('stopDate');
    return submitFormViaAjax('drDialog', 'stopProgramForm');
}

updateComponentAvailability = function() {
    setDateTimeInputEnabled('stopDate', !$('stopNowCheckbox').checked);
}

allProgramsChecked = function() {
    allChecked = $('allProgramsCheckbox').checked;
    for (index = 0; index < ${fn:length(programs)}; index++) {
        $('stopProgramCheckbox' + index).checked =
            allChecked && !$('stopProgramCheckbox' + index).disabled;
    }
}

updateAllProgramsChecked = function() {
    allChecked = true;
    for (index = 0; index < ${fn:length(programs)}; index++) {
        if (!$('stopProgramCheckbox' + index).disabled
                && !$('stopProgramCheckbox' + index).checked) {
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
}

updateProgramState = function(index) {
  //assumes data is of type Hash
    return function(data) {
        if (data.get('state').startsWith('running') || data.get('state').startsWith('scheduled')) {
            $('stopProgramCheckbox' + index).enable();
        } else {
            $('stopProgramCheckbox' + index).disable();
            $('stopProgramCheckbox' + index).checked = false;
        }
        updateAllProgramsChecked();
    }
}
</script>

<cti:flashScopeMessages/>

<h1 class="dialogQuestion">
    <c:if test="${!empty controlArea}">
        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.confirmQuestion.controlArea"
            argument="${controlArea.name}"/>
    </c:if>
    <c:if test="${!empty scenario}">
        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.confirmQuestion.scenario"
            argument="${scenario.name}"/>
    </c:if>
</h1>

<cti:url var="submitUrl" value="/spring/dr/program/stop/stopMultiple"/>
<form:form id="stopProgramForm" commandName="backingBean" action="${submitUrl}"
    onsubmit="return submitForm();">
    <form:hidden path="controlAreaId"/>
    <form:hidden path="scenarioId"/>

    <table class="compactResultsTable">
        <tr>
            <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.stopTime"/></th>
        </tr>
        <tr><td>
            <table>
                <tr><td>
                    <form:checkbox path="stopNow" id="stopNowCheckbox" onclick="updateComponentAvailability()"/>
                    <label for="stopNowCheckbox">
                        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.stopNow"/>
                    </label>
                </td></tr>
                <tr><td class="padded">
                    <tags:dateTimeInput path="stopDate" fieldValue="${backingBean.stopDate}"
                        disabled="true"/>
                </td></tr>
            </table>
        </td></tr>
    </table>
    <br>

    <cti:msg var="boxTitle" key="yukon.web.modules.dr.program.stopMultiplePrograms.programs"/>
    <tags:abstractContainer type="box" title="${boxTitle}">
    <div class="dialogScrollArea">
    <table class="compactResultsTable">
        <tr class="<tags:alternateRow odd="" even="altRow"/>">
            <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.stopProgramName"/></th>
            <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.currentState"/></th>
            <c:if test="${!empty scenarioPrograms}">
                <th><cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.stopOffset"/></th>
            </c:if>
        </tr>
        <c:forEach var="program" varStatus="status" items="${programs}">
            <c:set var="programId" value="${program.paoIdentifier.paoId}"/>
            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                <td><form:hidden path="programStopInfo[${status.index}].programId"/>
                <form:checkbox path="programStopInfo[${status.index}].stopProgram"
                    id="stopProgramCheckbox${status.index}"
                    onclick="singleProgramChecked(this);"/>
                <label for="stopProgramCheckbox${status.index}">${program.name}</label></td>
                <td><cti:dataUpdaterValue identifier="${programId}/STATE" type="DR_PROGRAM"/>
                <cti:dataUpdaterCallback function="updateProgramState(${status.index})" initialize="true" state="DR_PROGRAM/${programId}/SHOW_ACTION"/></td>
                <c:if test="${!empty scenarioPrograms}">
                    <c:set var="scenarioProgram" value="${scenarioPrograms[programId]}"/>
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
        <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.stopAllPrograms"/>
    </label><br>
    <c:if test="${autoObserveConstraintsAllowed}">
        <c:if test="${checkConstraintsAllowed}">
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints"
                onclick="updateSubmitButtons();"/>
            <label for="autoObserveConstraints">
                <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.autoObserveConstraints"/>
            </label>
        </c:if>
        <c:if test="${!checkConstraintsAllowed}">
            <%-- They have to automatically observe constraints. --%>
            <cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.constraintsWillBeObserved"/>
        </c:if>
    </c:if>
    <br>

    <div class="actionArea">
        <input id="okButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.okButton"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>

<script type="text/javascript">
updateComponentAvailability();
</script>
