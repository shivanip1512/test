<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.program.stopProgram">

<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('stopDate');
    url = '<cti:url value="/dr/program/stop/stop"/>';
    <c:if test="${stopGearAllowed}">
        if (!document.getElementById('stopNowCheckbox').checked && document.getElementById('useStopGearCheckbox').checked) {
            url = '<cti:url value="/dr/program/stop/constraints"/>';
        }
    </c:if>
    return submitFormViaAjax('drDialog', 'stopProgramForm', url);
}

updateComponentAvailability = function() {
    setDateTimeInputEnabled('stopDate', !document.getElementById('stopNowCheckbox').checked);
    <c:if test="${stopGearAllowed}">
    document.getElementById('useStopGearCheckbox').disabled = document.getElementById('stopNowCheckbox').checked;

        if (!document.getElementById('stopNowCheckbox').checked && document.getElementById('useStopGearCheckbox').checked) {
            document.getElementById('gearNumber').disabled = false;
            jQuery('#okButton').hide();
            jQuery('#nextButton').show();
        } else {
            document.getElementById('gearNumber').disabled = true;
            jQuery('#okButton').show();
            jQuery('#nextButton').hide();
        }
    </c:if>
}
</script>

<cti:flashScopeMessages/>

<h4 class="dialogQuestion stacked">
    <cti:msg2 key=".confirmQuestion" htmlEscape="true" argument="${program.name}"/>
</h4>

<form:form id="stopProgramForm" commandName="backingBean" onsubmit="return submitForm();">
    <form:hidden path="programId"/>
    <c:if test="${!stopGearAllowed}">
        <form:hidden path="useStopGear"/>
        <form:hidden path="gearNumber"/>
    </c:if>

    <table class="compactResultsTable">
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
                            <tags:dateTimeInput path="stopDate" fieldValue="${backingBean.stopDate}"
                                disabled="true"/>
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
                                    <form:option value="${status.index + 1}"><spring:escapeBody htmlEscape="true">${gear.gearName}</spring:escapeBody></form:option>
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


    <div class="actionArea">
        <cti:button nameKey="ok" classes="primary action" type="submit"/>
        <c:if test="${stopGearAllowed}">
            <cti:button id="nextButton" nameKey="next" classes="primary action" type="submit"/>
            <script type="text/javascript">updateComponentAvailability();</script>
        </c:if>
        <cti:button nameKey="cancel" onclick="jQuery('#drDialog').dialog('close');"/>
    </div>
</form:form>

<script type="text/javascript">
updateComponentAvailability();
</script>
</cti:msgScope>