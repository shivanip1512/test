<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:includeScript link="/JavaScript/hideReveal.js"/>
<cti:includeScript link="/JavaScript/longLoadProfile.js"/>
<cti:includeScript link="/JavaScript/peakDayProfile.js"/>
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
				<tags:widgetActionRefresh hide="${!readable}" method="getReport" label="Get Report" labelBusy="Get Report" />
			</td>
		</tr>
	</table>
	
	<!-- Daily Usage Report -->
	<br>
	<b>Daily Usage Report:</b>
	<cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="htmlView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${startDateDate}" stopDate="${stopDateDate}">HTML</cti:simpleReportLinkFromNameTag>
    |
    <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="csvView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${startDateDate}" stopDate="${stopDateDate}">CSV</cti:simpleReportLinkFromNameTag>
    |
    <cti:simpleReportLinkFromNameTag definitionName="dailyUsageDefinition" viewType="pdfView" module="amr" showMenu="true" menuSelection="deviceselection" pointId="${pointId}" startDate="${startDateDate}" stopDate="${stopDateDate}">PDF</cti:simpleReportLinkFromNameTag>
	<br/><br/>
	
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
					Peak ${displayName}
				</th>
				<th width="200px">
					Peak Day Total Usage
				</th>
				<th>Profile</th>
			</tr>
				<c:if test="${! empty preResult}">
					<c:choose>
						<c:when test="${!preResult.noData && preResult.deviceError == ''}">
							<tr>
								<td width="150px">
									${prePeriodStartDateDisplay} -<br/>
									${prePeriodStopDateDisplay}
								</td>
								<td>
									${preResult.averageDailyUsage} / ${preResult.totalUsage} kWH
								</td>
								<td width="150px">
									${prePeakValue}
								</td>
								<td>
									${preResult.usage} kWH
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<tags:peakDayProfile isReadable="${readable}" deviceId="${widgetParameters.deviceId}" peakDate="${prePeakValue}" startDate="${prePeriodStartDateDisplay}" stopDate="${prePeriodStopDateDisplay}" styleClass="Link1" profileRequestOrigin="${widgetParameters.loadProfileRequestOrigin}">Profile</tags:peakDayProfile>
									</c:if>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="5">
                                There was an error reading the meter<br>
                                <c:forEach items="${preResult.errors}" var="error">
                                    <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                    ${error.porter}<br>
                                    ${error.troubleshooting}<br>
                                    </tags:hideReveal><br>
                                </c:forEach>
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
									${postPeriodStartDateDisplay} - ${postPeriodStopDateDisplay}
								</td>
								<td>
									${postResult.averageDailyUsage} / ${postResult.totalUsage} kWH
								</td>
								<td width="150px">
									${postPeakValue}
								</td>
								<td>
									${postResult.usage} kWH
								</td>
								<td>
									<!-- Load Profile collection -->
									<c:if test="${widgetParameters.collectLPVisible}">
										<br/>
										<tags:peakDayProfile isReadable="${readable}" deviceId="${widgetParameters.deviceId}" peakDate="${postPeakValue}" startDate="${postPeriodStartDateDisplay}" stopDate="${postPeriodStopDateDisplay}" styleClass="Link1" profileRequestOrigin="${widgetParameters.loadProfileRequestOrigin}">Profile</tags:peakDayProfile>
									</c:if>
								</td>
							</tr>
						</c:when>
						<c:otherwise>
							<tr>
								<td colspan="5">
                                There was an error reading the meter<br>
                                <c:forEach items="${postResult.errors}" var="error">
                                    <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
                                    ${error.porter}<br>
                                    ${error.troubleshooting}<br>
                                    </tags:hideReveal><br>
                                </c:forEach>
								</td>
							</tr>
						</c:otherwise>
					</c:choose>
				</c:if>
		</table>
	</c:if>
	
</span>