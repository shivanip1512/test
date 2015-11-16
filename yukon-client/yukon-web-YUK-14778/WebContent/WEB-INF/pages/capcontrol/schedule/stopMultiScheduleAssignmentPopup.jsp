<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="yukon.web.modules.capcontrol.scheduleAssignments">

<form id="stop-multiple-schedules-form">
    
    <tags:nameValueContainer2>
        <tags:nameValue2 nameKey=".schedules">
            <select name="stopSchedule" id="stop-schedule">
                <option value="All" <c:if test="${param.schedule == 'All'}">selected="selected" </c:if>><cti:msg2 key=".allSchedules"/></option>
                <c:forEach var="schedule" items="${scheduleList}">
                    <c:if test="${param.schedule == schedule.name}">
                        <c:set var="selected">selected="selected"</c:set>
                    </c:if>
                    <c:if test="${param.schedule != schedule.name}">
                        <c:set var="selected" value=""/>
                    </c:if>
                    <option value="${schedule.name}" ${selected}>${fn:escapeXml(schedule.name)}</option>
                </c:forEach>
            </select>
        </tags:nameValue2>
        
        <tags:nameValue2 nameKey=".commands">
            <select name="stopCommand" id="stopCommand">
                <option value="All"<c:if test="${param.command == 'All'}"> selected="selected"</c:if>><cti:msg2 key=".allCommands"/></option>
                <c:forEach var="aCommand" items="${verifyCommandsList}">
                    <c:choose>
                        <c:when test="${param.command == 'All'}">
                            <option value="${aCommand}">${aCommand.commandName}</option>
                        </c:when>
                        <c:otherwise>
                            <c:if test="${param.command == aCommand}"><c:set var="selected">selected="selected"</c:set></c:if>
                            <c:if test="${param.command != aCommand}"><c:set var="selected" value=""/></c:if>
                            <option value="${aCommand}" ${selected}>${fn:escapeXml(aCommand.commandName)}</option>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
            </select>
        </tags:nameValue2>
    </tags:nameValueContainer2>
</form>
</cti:msgScope>