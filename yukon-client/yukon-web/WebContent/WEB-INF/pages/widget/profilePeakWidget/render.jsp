<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/hideReveal.js"/>
<cti:includeScript link="/JavaScript/longLoadProfile.js"/>
<cti:includeScript link="/JavaScript/calendarControl.js"/>

<cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>

<script type="text/javascript"> 
	
	function toggleLP(theDiv){
		$(theDiv).toggle();
	}

</script>

<span class="widgetText">
	
	<c:if test="${errorMsg != null}">
		<div style="color: red;margin: 10px 0px;">Error: ${errorMsg}</div>
	</c:if>
	<table width="95%">
		<tr>
			<td>
				Report: Peak Daily Usage
				<input type="hidden" id="reportType" name="reportType" value="day">
			</td>
			<td>
				Start Date:
			</td>
			<td>
				<input type="text" id="startDate" name="startDate" value="${startDate}" size="10" />
				<a href="javascript:showCalendarControl($('startDate'))"> 
					<img src="<c:url value="/WebConfig/yukon/Icons/StartCalendar.gif"/>" width="20" height="15" align="ABSMIDDLE" border="0">
				</a>
			</td>
			<td>
				End Date:
			</td>
			<td>
				<input type="text" id="stopDate" name="stopDate" value="${stopDate}" size="10" />
				<a href="javascript:showCalendarControl($('stopDate'))"> 
					<img src="<c:url value="/WebConfig/yukon/Icons/StartCalendar.gif"/>" width="20" height="15" align="ABSMIDDLE" border="0">
				</a>
			</td>
			<td align="right">
				<tags:widgetActionRefresh method="getReport" label="Get Report" labelBusy="Get Report" />
			</td>
		</tr>
	</table>
	
	<!-- Profile peak results section -->
	<c:if test="${!empty preResult || !empty postResult}">
		<br><b>Previous Profile Reports:</b>
	</c:if>
	
	<br/><br/>
	
	<c:if test="${! empty preResult || ! empty postResult}">
		<table class="miniResultsTable" width="100%">
			<tr>
				<th width="150px">
					Period
				</th>
				<th width="130px">
					Avg Daily / <br /> Total Usage
				</th>
				<th>
					Peak ${preResult.peakTypeDisplayName}
				</th>
				<th width="200px">
					Peak Day Total Usage
				</th>
				<th>&nbsp;</th>
			</tr>
				<c:if test="${! empty preResult}">
					<c:choose>
						<c:when test="${!preResult.noData && preResult.deviceError == ''}">
							<tr>
								<td width="150px">
									${preResult.periodStartDateDisplay} -<br/>
									${preResult.periodStopDateDisplay}
								</td>
								<td>
									${preResult.averageDailyUsage} kWH / ${preResult.totalUsage} kWH
								</td>
								<td width="150px">
									${preResult.peakValue}
								</td>
								<td>
									${preResult.usage}
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<c:choose>
											<c:when test="${preCommandDays <= 90}">
												<tags:longLoadProfile styleClass="Link1" deviceId="${widgetParameters.deviceId}" lpStartDate="${preResult.periodStartDateDisplay}" lpStopDate="${preResult.periodStopDateDisplay}" profileRequestOrigin="${widgetParameters.loadProfileRequestOrigin}">Profile</tags:longLoadProfile>
											</c:when>
											<c:otherwise>
												<span onmouseover="javascript:toggleLP($('lpDiv'))" onmouseout="javascript:toggleLP($('lpDiv'))">Profile N/A</span>
												<div id="lpDiv"  class="widgetPopup" style="top 0; display:none;">
													The current load profile request will gather more than 90 days of load profile.  Multiple requests of this size could affect overall system performance while being completed.   Please contact the system administrator or break the request into smaller data ranges.
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
									${preResult.periodStartDateDisplay} -<br/>
									${preResult.periodStopDateDisplay}
								</td>					
								<td colspan="4">
									${preResult.deviceError}
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:if>

				<c:if test="${!empty postResult}">
					<c:choose>
						<c:when test="${!postResult.noData && postResult.deviceError == ''}">
							<tr>
								<td width="150px">
									${postResult.periodStartDateDisplay} - ${postResult.periodStopDateDisplay}
								</td>
								<td>
									${postResult.averageDailyUsage} kWH / ${postResult.totalUsage} kWH
								</td>
								<td width="150px">
									${postResult.peakValue}
								</td>
								<td>
									${postResult.usage}
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<c:choose>
											<c:when test="${postCommandDays <= 90}">
												<tags:longLoadProfile styleClass="Link1" deviceId="${widgetParameters.deviceId}" lpStartDate="${postResult.periodStartDateDisplay}" lpStopDate="${postResult.periodStopDateDisplay}" profileRequestOrigin="${widgetParameters.loadProfileRequestOrigin}">Profile</tags:longLoadProfile>
											</c:when>
											<c:otherwise>
												<span onmouseover="javascript:toggleLP($('lpDiv2'))" onmouseout="javascript:toggleLP($('lpDiv2'))">Profile N/A</span>
												<div id="lpDiv2"  class="widgetPopup" style="top 0; display:none;">
													The current load profile request will gather more than 90 days of load profile.  Multiple requests of this size could affect overall system performance while being completed.   Please contact the system administrator or break the request into smaller data ranges.
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
									${postResult.periodStartDateDisplay} - ${postResult.periodStopDateDisplay}
								</td>					
								<td colspan="4">
									${postResult.deviceError}
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:if>
		</table>
	</c:if>
	
</span>