<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.stopProgram">

<script type="text/javascript">
submitForm = function() {
    var url = '<cti:url value="/dr/program/stop/stop"/>';
    <c:if test="${stopGearAllowed}">
        if (!document.getElementById('stopNowCheckbox').checked && document.getElementById('useStopGearCheckbox').checked) {
            url = '<cti:url value="/dr/program/stop/constraints"/>';
        }
    </c:if>
    return submitFormViaAjax('drDialog', 'stopProgramForm', url);
}

updateComponentAvailability = function() {
    // reset date value to now if checkbox checked, since it may have been changed
    if (true === $('#stopNowCheckbox').prop('checked')) {
        $('#stopDate').val(yukon.ui.initialNowVal);
    }
    $('#stopDate').prop('disabled', $('#stopNowCheckbox').prop('checked'));
    <c:if test="${stopGearAllowed}">
    document.getElementById('useStopGearCheckbox').disabled = document.getElementById('stopNowCheckbox').checked;

        if (!document.getElementById('stopNowCheckbox').checked && document.getElementById('useStopGearCheckbox').checked) {
            document.getElementById('gearNumber').disabled = false;
            $('#okButton').hide();
            $('#nextButton').show();
        } else {
            document.getElementById('gearNumber').disabled = true;
            $('#okButton').show();
            $('#nextButton').hide();
        }
    </c:if>
}
$( function () {
    // init dateTime fields dynamically brought onto page after initial page load
    yukon.ui.initDateTimePickers();
    // save off initial value of date in case we must reinstate later
    yukon.ui.initialNowVal = $('#stopDate').val();
});
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked">
    <cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/>
</h4>

<form:form id="stopProgramForm" modelAttribute="backingBean" onsubmit="return submitForm();">
    <cti:csrfToken/>
    <form:hidden path="programId"/>
    <c:if test="${!stopGearAllowed}">
        <form:hidden path="useStopGear"/>
        <form:hidden path="gearNumber"/>
    </c:if>

    <table class="compact-results-table">
        <thead>
            <tr>
                <th><cti:msg2 key=".stopTime"/></th>
                <c:if test="${stopGearAllowed}">
                    <th><cti:msg2 key=".stopGear"/></th>
                </c:if>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <tr valign="top">
                <td width="50%">
                    <table>
                        <tr><td>
                            <form:checkbox path="stopNow" id="stopNowCheckbox" onclick="updateComponentAvailability()"/>
                            <label for="stopNowCheckbox">
                                <cti:msg2 key=".stopNow"/>
                            </label>
                        </td></tr>
                        <tr><td>
                            <dt:dateTime id="stopDate" path="stopDate" disabled="true" value="${backingBean.stopDate}"/>
                        </td></tr>
                    </table>
                </td>
                <c:if test="${stopGearAllowed}">
                <td width="50%">
                    <table>
                        <tr><td>
                            <form:checkbox path="useStopGear" id="useStopGearCheckbox" onclick="updateComponentAvailability()"/>
                            <label for="useStopGearCheckbox">
                                <cti:msg2 key=".useStopGear"/>
                            </label>
                        </td></tr>
                        <tr><td>
                            <form:select path="gearNumber" id="gearNumber" onchange="gearChanged()">
                                <c:forEach var="gear" varStatus="status" items="${gears}">
                                    <form:option value="${status.index + 1}">${fn:escapeXml(gear.gearName)}</form:option>
                                </c:forEach>
                            </form:select><br>
                            <script type="text/javascript">updateComponentAvailability();</script>
                        </td></tr>
                    </table>
                </td>
                </c:if>
            </tr>
        </tbody>
    </table>

    <div class="action-area">
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
        <c:if test="${stopGearAllowed}">
            <cti:button id="nextButton" nameKey="next" classes="primary action" type="submit"/>
            <script type="text/javascript">updateComponentAvailability();</script>
        </c:if>
        <cti:button nameKey="cancel" onclick="$('#drDialog').dialog('close');"/>
    </div>
</form:form>

<script type="text/javascript">
$( function () {
    updateComponentAvailability();
});
</script>
</cti:msgScope>