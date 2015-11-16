<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.capcontrol.scheduleAssignments">

<form id="start-multiple-schedules-form">
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".schedules">
            <select name="startSchedule" id="start-schedule">
                <option value="All"<c:if test="${param.schedule == 'All'}"> selected="selected"</c:if>><cti:msg2 key=".allSchedules"/></option>
                <c:forEach var="schedule" items="${scheduleList}">
                    <c:choose>
                        <c:when test="${param.schedule == schedule.name}">
                            <c:set var="selected">selected="selected"</c:set>
                        </c:when>
                        <c:otherwise><c:set var="selected" value=""/></c:otherwise>
                    </c:choose>
                    <option value="${schedule.name}" ${selected}>${fn:escapeXml(schedule.name)}</option>
                </c:forEach>
            </select>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".commands">
            <select name="startCommand" id="startCommand">
                <option value="All"<c:if test="${param.schedule == 'All'}"> selected="selected"</c:if>><i:inline key=".allCommands"/></option>
                <c:forEach var="aCommand" items="${commandList}">
                    <c:choose>
                        <c:when test="${param.command == 'All'}">
                            <option value="${aCommand}">${aCommand.commandName}</option>
                        </c:when>
                        <c:otherwise>
                            <option value="${aCommand}"<c:if test="${param.command == aCommand}"> selected="selected"</c:if>>${aCommand.commandName}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form>

</cti:msgScope>