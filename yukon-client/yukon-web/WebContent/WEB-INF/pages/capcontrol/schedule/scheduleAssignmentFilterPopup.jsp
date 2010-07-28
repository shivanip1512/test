<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<%-- Filtering popup --%>
<tags:simplePopup id="filterPopup" title="Schedule Assignment Filters">
	<form name="filterForm" action="/spring/capcontrol/schedule/scheduleAssignments">
		<table class="filterSelection">
			<tr>
				<td>Schedules:</td>
				<td> 
					<select name="schedule" id="scheduleSelection">
						<option value="All">All Schedules</option>
						<c:forEach var="aSchedule" items="${scheduleList}">
								<option value="${aSchedule.scheduleName}" <c:if test="${param.schedule == aSchedule.scheduleName}">selected="selected" </c:if> > ${aSchedule.scheduleName}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Commands:</td>
				<td>
					<select name="command" id="commandSelection">
						<option value="All">All Commands</option>
						<c:forEach var="aCommand" items="${commandList}">
							<c:choose>
								<c:when test="${param.command == 'All'}">
									<option value="${aCommand}">${aCommand.commandName}</option>
								</c:when>
								<c:otherwise>
									<option value="${aCommand}" <c:if test="${param.command == aCommand}">selected="selected" </c:if> >${aCommand.commandName}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>
		<div class="actionArea">
			<input type="submit" value="Filter" />
			<input type="button" value="Clear Filters" onclick="javascript:clearFilter()" />
		</div>
	</form>
</tags:simplePopup>