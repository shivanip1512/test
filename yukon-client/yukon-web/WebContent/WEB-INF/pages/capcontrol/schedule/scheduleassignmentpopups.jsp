<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:url var="startMultiUrl" value="/spring/capcontrol/schedule/startMultiple" />
<cti:url var="stopMultiUrl" value="/spring/capcontrol/schedule/stopMultiple" />

<%-- Filtering popup --%>
<tags:simplePopup id="filterPopup" title="Schedule Assignment Filters">
	<form name="filterForm" action="">
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


<%-- start multiple schedules popup --%>
<tags:simplePopup id="startMultipleSchedulesPopup" title="Start Multiple Schedules">
	<form name="startMultipleSchedulesForm" id="startMultipleSchedulesForm" action="${startMultiUrl}">
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
			<input type="button" value="Cancel" onclick="$('startMultipleSchedulesPopup').toggle();" />
		</div>
	</form>
</tags:simplePopup>


<%-- stop multiple schedules popup --%>
<tags:simplePopup id="stopMultipleSchedulesPopup" title="Stop Multiple Schedules">
	<form name="stopMultipleSchedulesForm" id="stopMultipleSchedulesForm" action="${stopMultiUrl}">
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
			<input type="button" value="Cancel" onclick="$('stopMultipleSchedulesPopup').toggle();" />
		</div>
	</form>
</tags:simplePopup>


<%-- new schedule assignment popup --%>
<tags:simplePopup id="newScheduleAssignmentPopup" title="New Schedule Assignment">
	<form name="newScheduleAssignmentForm" id="newScheduleAssignmentForm" method="post" action="addPao">
		<input type="hidden" name="paoIdList" id="paoIdList" />
		<input type="hidden" name="filterCommand" value="${param.command}" />
		<input type="hidden" name="filterSchedule" value="${param.schedule}" />
		<table class="filterSelection">
			<tr>
				<td>Schedules:</td>
				<td> 
					<select name="addSchedule" id="addSchedule">
						<c:forEach var="aSchedule" items="${scheduleList}">
								<option value="${aSchedule.scheduleID}" <c:if test="${param.schedule == aSchedule.scheduleName}">selected="selected" </c:if> > ${aSchedule.scheduleName}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>Commands:</td>
				<td>
					<select name="addCommand" id="addCommand">
						<c:forEach var="aCommand" items="${commandList}">
							<c:choose>
								<c:when test="${param.command == 'All'}">
									<option value="${aCommand}">${aCommand.commandName}</option>
								</c:when>
								<c:otherwise>
									<c:if test="${aCommand != 'VerifyNotOperatedIn'}">
										<option value="${aCommand}" <c:if test="${param.command == aCommand}">selected="selected" </c:if> >${aCommand.commandName}</option>
									</c:if>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</td>
			</tr>
		</table>	
		<div id="deviceSelectionDiv">
			<table>
				<tr>
					<td>Cap Banks:&nbsp;</td>
					<td>
						<tags:pickerDialog id="cbcSubBusPicker" type="cbcSubBusPicker"
                 			destinationFieldId="paoIdList" multiSelectMode="true" endAction="">
                 			Choose Devices</tags:pickerDialog>
					</td>
				</tr>
			</table>
		</div>
		<div class="actionArea">
			<input type="submit" value="Add"/>
			<input type="button" value="Cancel" onclick="$('newScheduleAssignmentPopup').toggle();" />
		</div>
	</form>
</tags:simplePopup>
