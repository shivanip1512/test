<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form name="startMultipleSchedulesForm" id="startMultipleSchedulesForm" 
	action="/spring/capcontrol/schedule/startMultiple">
	<table class="filterSelection">
		<tr>
			<td>Schedules:</td>
			<td> 
				<select name="startSchedule" id="startSchedule">
					<option value="All" <c:if test="${param.schedule == 'All'}">selected="selected" </c:if> >All Schedules</option>
					<c:forEach var="aSchedule" items="${scheduleList}">
							<option value="${aSchedule.scheduleName}" <c:if test="${param.schedule == aSchedule.scheduleName}">selected="selected" </c:if> > ${aSchedule.scheduleName}</option>
					</c:forEach>
				</select>
			</td>
		</tr>
		<tr>
			<td>Commands:</td>
			<td>
				<select name="startCommand" id="startCommand">
					<option value="All" <c:if test="${param.schedule == 'All'}">selected="selected" </c:if> >All Commands</option>
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
		<input type="submit" value="Start"/>
		<input type="button" value="Cancel" onclick="$('tierContentPopup').hide();" />
	</div>
</form>