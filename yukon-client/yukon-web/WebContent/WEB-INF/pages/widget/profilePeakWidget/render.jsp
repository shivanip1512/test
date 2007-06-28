<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/hideReveal.js"/>
<cti:includeScript link="/JavaScript/longLoadProfile.js"/>

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
				End Date:
				<input type="text" id="stopDate" name="stopDate" value="${stopDate}" />
			</td>
			<td align="right">
				<tags:widgetActionRefresh method="getReport" label="Get Report" labelBusy="Get Report" />
			</td>
		</tr>
	</table>
	
	<!-- Profile peak results section -->
	<c:if test="${!empty preResult || !empty postResult}">
		<br><b>Previous Profile Peak Reports:</b><br>
	</c:if>
	<c:if test="${! empty preResult}">
		<tags:hideReveal title="Report range: ${preResult.startDate} - ${preResult.stopDate}" showInitially="${true}">
			<div style="margin: 0px 30px;">
				<c:choose>
					<c:when test="${!preResult.noData}">
						<tags:nameValueContainer altRowOn="true">
							<tags:nameValue name="Date report run" rowHighlight="${highlight.reportRun}">${preResult.runDate}</tags:nameValue>
							<tags:nameValue name="Peak Day" rowHighlight="${highlight.peakDay}">${preResult.peakDate}</tags:nameValue>
							<tags:nameValue name="Usage" rowHighlight="${highlight.usage}">${preResult.usage}</tags:nameValue>
							<tags:nameValue name="Demand" rowHighlight="${highlight.demand}">${preResult.demand}</tags:nameValue>
							<tags:nameValue name="Average daily usage over range" rowHighlight="${highlight.averageUsage}">${preResult.averageDailyUsage}</tags:nameValue>
							<tags:nameValue name="Total usage over range" rowHighlight="${highlight.totalUsage}">${preResult.totalUsage}</tags:nameValue>
						</tags:nameValueContainer>
		
						<!-- Load Profile collection -->
						<c:if test="${widgetParameters.collectLPVisible && (preResult.days <= 90)}">
							<br/>
							<tags:longLoadProfile styleClass="Link1" deviceId="${deviceId}" lpStartDate="${preResult.startDate}" lpStopDate="${preResult.stopDate}">Collect Long Load Profile for this period</tags:longLoadProfile>
						</c:if>
					</c:when>
					<c:otherwise>
						No results
					</c:otherwise>
				</c:choose>
				${preResult.error}
			</div>
		</tags:hideReveal>
	</c:if>
	<br/>
	<c:if test="${! empty postResult}">
		<tags:hideReveal title="Report range: ${postResult.startDate} - ${postResult.stopDate}" showInitially="${true}">
			<div style="margin: 0px 30px;">
				<c:choose>
					<c:when test="${!postResult.noData}">
						<tags:nameValueContainer altRowOn="true">
							<tags:nameValue name="Date report run" rowHighlight="${highlight.reportRun}">${postResult.runDate}</tags:nameValue>
							<tags:nameValue name="Peak Day" rowHighlight="${highlight.peakDay}">${postResult.peakDate}</tags:nameValue>
							<tags:nameValue name="Usage" rowHighlight="${highlight.usage}">${postResult.usage}</tags:nameValue>
							<tags:nameValue name="Demand" rowHighlight="${highlight.demand}">${postResult.demand}</tags:nameValue>
							<tags:nameValue name="Average daily usage over range" rowHighlight="${highlight.averageUsage}">${postResult.averageDailyUsage}</tags:nameValue>
							<tags:nameValue name="Total usage over range" rowHighlight="${highlight.totalUsage}">${postResult.totalUsage}</tags:nameValue>
						</tags:nameValueContainer>
		
						<!-- Load Profile collection -->
						<c:if test="${widgetParameters.collectLPVisible && (postResult.days <= 90)}">
							<br/>
							<tags:longLoadProfile styleClass="Link1" deviceId="${deviceId}" lpStartDate="${postResult.startDate}" lpStopDate="${postResult.stopDate}">Collect Long Load Profile for this period</tags:longLoadProfile>
						</c:if>
					</c:when>
					<c:otherwise>
						No results
					</c:otherwise>
				</c:choose>
				${postResult.error}
			</div>
		</tags:hideReveal>
	</c:if>

</span>