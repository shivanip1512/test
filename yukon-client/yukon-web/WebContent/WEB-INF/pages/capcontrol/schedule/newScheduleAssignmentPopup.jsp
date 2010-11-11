<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

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
                			destinationFieldId="paoIdList" multiSelectMode="true">
                			Choose Device</tags:pickerDialog>
				</td>
			</tr>
		</table>
	</div>
	<div class="actionArea">
		<input type="submit" value="Add"/>
		<input type="button" value="Cancel" onclick="$('tierContentPopup').hide();" />
	</div>
</form>