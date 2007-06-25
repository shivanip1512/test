<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/hideReveal.js"/>

<span class="widgetText">
	
	<c:if test="${errorMsg != null}">
		<div style="color: red;margin: 10px 0px;">Error: ${errorMsg}</div>
	</c:if>
	<table width="100%">
		<tr>
			<td>
				Report:
				<select id="reportType" name="reportType">
					<c:choose>
						<c:when test="${reportType == 'day'}">
							<option value="day" selected="true">Peak Daily Usage</option>
						</c:when>
						<c:otherwise>
							<option value="day">Peak Daily Usage</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${reportType == 'hour'}">
							<option value="hour" selected="true">Peak Hour Usage</option>
						</c:when>
						<c:otherwise>
							<option value="hour">Peak Hour Usage</option>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${reportType == 'interval'}">
							<option value="interval" selected="true">Peak Interval</option>
						</c:when>
						<c:otherwise>
							<option value="interval">Peak Interval</option>
						</c:otherwise>
					</c:choose>
				</select>
			</td>
			<td>
				Start Date:
				<input type="text" id="startDate" name="startDate" value="${startDate}" />
			</td>
			<td>
				Days:
				<input type="text" id="days" name="days" size="3" value="${days}" />
			</td>
			<td align="right">
				<tags:widgetActionRefresh method="getReport" label="Get Report" labelBusy="Get Report" />
			</td>
		</tr>
	</table>
	
	<!-- Profile peak results section -->
	<c:if test="${! empty deviceResults}">
		<br><b>Profile Peak Reports</b><br><br>
	</c:if>
	<c:set var="first" value="true" scope="page" />
	<c:forEach var="result" items="${deviceResults}">
	
		<tags:hideReveal title="Report range: ${result.fromDate} - ${result.toDate}" showInitially="${first}">
			<div style="margin: 0px 30px;">
				<c:choose>
					<c:when test="${!result.noData}">
						<tags:nameValueContainer altRowOn="true">
							<tags:nameValue name="Date report run">${result.runDate}</tags:nameValue>
							<tags:nameValue name="Peak Day">${result.peakDate}</tags:nameValue>
							<tags:nameValue name="Usage">${result.usage}</tags:nameValue>
							<tags:nameValue name="Demand">${result.demand}</tags:nameValue>
							<tags:nameValue name="Average daily usage over range">${result.averageDailyUsage}</tags:nameValue>
							<tags:nameValue name="Total usage over range">${result.totalUsage}</tags:nameValue>
						</tags:nameValueContainer>
		
						<!-- Load Profile collection -->
						<c:if test="${collectLPVisible}">
							<br/>
							<tags:longLoadProfile styleClass="Link1" deviceId="${deviceId}" lpStartDate="${result.fromDate}" lpStopDate="${result.toDate}">Collect Long Load Profile for this period</tags:longLoadProfile>
						</c:if>
					</c:when>
					<c:otherwise>
						No results
					</c:otherwise>
				</c:choose>
			</div>
		</tags:hideReveal>
		<br>
		<c:set var="first" value="false" scope="page" />
	</c:forEach>

</span>