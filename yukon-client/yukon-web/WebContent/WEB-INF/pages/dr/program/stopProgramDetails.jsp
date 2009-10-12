<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('stopDate');
    url = '<cti:url value="/spring/dr/program/stopProgram"/>';
    <c:if test="${stopGearAllowed}">
        if (!$('stopNowCheckbox').checked && $('useStopGearCheckbox').checked) {
            url = '<cti:url value="/spring/dr/program/stopProgramConstraints"/>';
        }
    </c:if>
    return submitFormViaAjax('drDialog', 'stopProgramForm', url);
}

updateComponentAvailability = function() {
    setDateTimeInputEnabled('stopDate', !$('stopNowCheckbox').checked);
    <c:if test="${stopGearAllowed}">
        $('useStopGearCheckbox').disabled = $('stopNowCheckbox').checked;

        if (!$('stopNowCheckbox').checked && $('useStopGearCheckbox').checked) {
            $('gearNumber').disabled = false;
            $('okButton').hide();
            $('nextButton').show();
        } else {
            $('gearNumber').disabled = true;
            $('okButton').show();
            $('nextButton').hide();
        }
    </c:if>
}
</script>

<h1 class="dialogQuestion">
    <cti:msg key="yukon.web.modules.dr.program.stopProgram.confirmQuestion"
        argument="${program.name}"/>
</h1>

<form:form id="stopProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="programId"/>
    <c:if test="${!stopGearAllowed}">
        <form:hidden path="useStopGear"/>
        <form:hidden path="gearNumber"/>
    </c:if>

    <table class="compactResultsTable">
        <tr>
            <th><cti:msg key="yukon.web.modules.dr.program.stopProgram.stopTime"/></th>
            <c:if test="${stopGearAllowed}">
                <th><cti:msg key="yukon.web.modules.dr.program.stopProgram.stopGear"/></th>
            </c:if>
        </tr>
        <tr valign="top">
            <td width="50%">
                <form:checkbox path="stopNow" id="stopNowCheckbox" onclick="updateComponentAvailability()"/>
                <label for="stopNowCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.stopProgram.stopNow"/>
                </label><br>

                <tags:dateTimeInput fieldId="stopDate" fieldValue="${backingBean.stopDate}"
                    disabled="true"/>
            </td>
            <c:if test="${stopGearAllowed}">
            <td width="50%">
                <form:checkbox path="useStopGear" id="useStopGearCheckbox" onclick="updateComponentAvailability()"/>
                <label for="useStopGearCheckbox">
                    <cti:msg key="yukon.web.modules.dr.program.stopProgram.useStopGear"/>
                </label><br>

                <form:select path="gearNumber" id="gearNumber" onchange="gearChanged()">
                    <c:forEach var="gear" varStatus="status" items="${gears}">
                        <form:option value="${status.index + 1}">${gear.gearName}</form:option>
                    </c:forEach>
                </form:select><br>
                <script type="text/javascript">updateComponentAvailability();</script>
            </td>
            </c:if>
        </tr>
    </table>

    <br>
    <br>

    <div class="actionArea">
        <input id="okButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.okButton"/>"/>
        <c:if test="${stopGearAllowed}">
            <input id="nextButton" type="submit" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.nextButton"/>"/>
            <script type="text/javascript">updateComponentAvailability();</script>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.stopProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
