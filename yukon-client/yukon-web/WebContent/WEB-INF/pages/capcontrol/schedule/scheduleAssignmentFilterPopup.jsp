<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<%-- Filtering popup --%>
<i:simplePopup id="filterPopup" titleKey=".filterTitle">
	<form name="filterForm" action="/capcontrol/schedule/scheduleAssignments">
		<tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".schedules">
                <select name="schedule" id="scheduleSelection">
                    <option value="All"><cti:msg2 key=".allSchedules"/></option>
                    <c:forEach var="aSchedule" items="${scheduleList}">
                        <option value="${aSchedule.scheduleName}"<c:if test="${param.schedule == aSchedule.scheduleName}"> selected="selected" </c:if>>
                            <spring:escapeBody htmlEscape="true">${aSchedule.scheduleName}</spring:escapeBody>
                        </option>
                    </c:forEach>
                </select>
            </tags:nameValue2>
            <tags:nameValue2 nameKey=".commands">
                <select name="command" id="commandSelection">
                    <option value="All"><cti:msg2 key=".allCommands"/></option>
                    <c:forEach var="aCommand" items="${commandList}">
                        <c:choose>
                            <c:when test="${param.command == 'All'}">
                                <option value="${aCommand}">${aCommand.commandName}</option>
                            </c:when>
                            <c:otherwise>
                                <option value="${aCommand}"<c:if test="${param.command == aCommand}"> selected="selected"</c:if>>
                                    <spring:escapeBody htmlEscape="true">${aCommand.commandName}</spring:escapeBody>
                                </option>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                </select>
            </tags:nameValue2>
        </tags:nameValueContainer2>
		<div class="actionArea">
            <cti:button nameKey="filter" type="submit"/>
            <cti:button nameKey="clearFilter" onclick="clearFilter()"/>
		</div>
	</form>
</i:simplePopup>