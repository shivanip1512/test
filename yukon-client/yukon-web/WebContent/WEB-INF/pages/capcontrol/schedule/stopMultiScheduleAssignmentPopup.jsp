<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<form name="stopMultipleSchedulesForm" id="stopMultipleSchedulesForm" 
	action="/spring/capcontrol/schedule/stopMultiple">
	<table class="filterSelection">
		<tr>
			<td>Schedules:</td>
			<td> 
				<select name="stopSchedule" id="stopSchedule">
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
				<select name="stopCommand" id="stopCommand">
					<option value="All" <c:if test="${param.command == 'All'}">selected="selected" </c:if> >All Commands</option>
					<c:forEach var="aCommand" items="${verifyCommandsList}">
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
		<input type="submit" value="Stop" id="actionSubmitButton" />
		<input type="button" value="Cancel" onclick="$('tierContentPopup').hide();" />
	</div>
</form>