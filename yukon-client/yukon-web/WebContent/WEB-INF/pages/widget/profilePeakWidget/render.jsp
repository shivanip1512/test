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
	
	<br/><br/>
	
	<c:if test="${! empty preResult || ! empty postResult}">
		<table class="resultsTable">
			<tr>
				<th>
					Range
				</th>
				<th>
					Avg Daily / Total
				</th>
				<th>
					Peak kWh
				</th>
				<th>
					Peak Date
				</th>
				<th>&nbsp;</th>
			</tr>
				<c:if test="${! empty preResult && !preResult.noData}">
					<c:choose>
						<c:when test="${!preResult.noData}">
							<tr>
								<td>
									${preResult.startDate} - ${preResult.stopDate}
								</td>
								<td>
									${preResult.averageDailyUsage} / ${preResult.totalUsage}
								</td>
								<td>
									${preResult.usage}
								</td>
								<td>
									${preResult.peakDate}
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<c:choose>
											<c:when test="${preResult.days <= 90}">
												<tags:longLoadProfile styleClass="Link1" deviceId="${deviceId}" lpStartDate="${preResult.startDate}" lpStopDate="${preResult.stopDate}">LP</tags:longLoadProfile>
											</c:when>
											<c:otherwise>
												<div title="Load Profile Collection is unavailable for collection periods of more than 90 days.">
													LP N/A
												</div>
											</c:otherwise>
										</c:choose>
									</c:if>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>
									${preResult.startDate} - ${preResult.stopDate}
								</td>					
								<td colspan="4">
									${preResult.error}
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:if>

				<c:if test="${!empty postResult}">
					<c:choose>
						<c:when test="${!postResult.noData}">
							<tr>
								<td>
									${postResult.startDate} - ${postResult.stopDate}
								</td>
								<td>
									${postResult.averageDailyUsage} / ${postResult.totalUsage}
								</td>
								<td>
									${postResult.usage}
								</td>
								<td>
									${postResult.peakDate}
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<c:choose>
											<c:when test="${postResult.days <= 90}">
												<tags:longLoadProfile styleClass="Link1" deviceId="${deviceId}" lpStartDate="${postResult.startDate}" lpStopDate="${postResult.stopDate}">LP</tags:longLoadProfile>
											</c:when>
											<c:otherwise>
												<div title="Load Profile Collection is unavailable for collection periods of more than 90 days.">
													LP N/A
												</div>
											</c:otherwise>
										</c:choose>
									</c:if>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td>
									${postResult.startDate} - ${postResult.stopDate}
								</td>					
								<td colspan="4">
									${postResult.error}
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:if>
		</table>
	</c:if>
	
</span>