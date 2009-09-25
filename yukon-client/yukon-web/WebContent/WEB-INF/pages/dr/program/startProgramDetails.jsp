<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<script type="text/javascript">
submitForm = function() {
    combineDateAndTimeFields('startDate');
    combineDateAndTimeFields('stopDate');
    submitFormViaAjax('drDialog', 'startProgramForm');
}

startNowChecked = function() {
    setDateTimeInputEnabled('startDate', !$('startNowCheckbox').checked);
}

scheduleStopChecked = function() {
    setDateTimeInputEnabled('stopDate', $('scheduleStopCheckbox').checked);
}
</script>

<p>
    <cti:msg key="yukon.web.modules.dr.program.startProgram.confirmQuestion"
        argument="${program.name}"/>
</p><br>

<cti:url var="submitUrl" value="/spring/dr/program/startProgramConstraints"/>
<form:form id="startProgramForm" commandName="backingBean" action="${submitUrl}">
    <form:hidden path="programId"/>

    <table width="100%">
        <tr valign="top">
            <td width="33%">
                <cti:msg key="yukon.web.modules.dr.program.startProgram.gear"/><br>
                <form:select path="gearNumber">
                    <c:forEach var="gear" varStatus="status" items="${gears}">
                        <form:option value="${status.index + 1}">${gear.gearName}</form:option>
                    </c:forEach>
                </form:select><br>
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
            <form:checkbox path="autoObserveConstraints" id="autoObserveConstraints"/>
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
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.okButton"/>"
                onclick="submitForm()"/>
        </c:if>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.program.startProgram.cancelButton"/>"
            onclick="parent.$('drDialog').hide()"/>
    </div>
</form:form>
