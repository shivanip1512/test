<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<spring:nestedPath path="tou">

	<table width="100%">
		<tr>
			<td width="60%" align="left" valign="top">

	<!-- Iterate through each of the 4 schedules -->
	<c:forEach var="i" begin="0" end="3">
		<c:set var="showSchedule" value="false" />

		<c:set var="key" value="scheduleList[${i}]" />
		<spring:nestedPath path="${key}">
			
			<!-- Decide whether the schedule should be shown initially -->
			<c:forEach var="j" begin="0" end="5">
				<c:set var="trkey" value="timeRateList[${j}]" />
				<spring:bind path="${trkey}.time">
					<c:if test="${status.error}">
						<c:set var="showSchedule" value="true" />
					</c:if>
				</spring:bind>
				<spring:bind path="${trkey}.rate">
					<c:if test="${status.error}">
						<c:set var="showSchedule" value="true" />
					</c:if>
				</spring:bind>
			</c:forEach>
			
			
			
			<tags:hideReveal title="Schedule ${i+1}" showInitially="${showSchedule}">
				<cti:renderInput input="${input.inputMap[key]}" />
			</tags:hideReveal>
			<br />

		</spring:nestedPath>

	</c:forEach>
	
			</td>
			<td width="40%" align="right">
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['defaultRate']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['holiday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['monday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['tuesday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['wednesday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['thursday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['friday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['saturday']}" /><br/>
				</div>
				<div style="margin-bottom: 5px;">
					<cti:renderInput input="${input.inputMap['sunday']}" /><br/>
				</div>
			</td>
		</tr>
	</table>
	
	
</spring:nestedPath>
