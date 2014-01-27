<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:if test="${! empty dateErrorMessage}">
    <br/>
    <div style="color:#FF0000;font-weight:bold;">${dateErrorMessage}</div>
    <br/>
</c:if>

<c:if test="${! empty peakResult}">
	<br/>
	<c:choose>
		<c:when test="${!peakResult.noData && peakResult.deviceError == ''}">
			<table class="compact-results-table">
				<tr>
					<cti:msg2 key="${peakResult.peakType.reportTypeDisplayNameKey}" var="reportType"/>
					<th align="left"><i:inline key=".report" arguments="${reportType}"/></th>
					<th align="left" style="font-weight:normal;color:#666666;">
                        <cti:formatDate value="${peakResult.runDate}" type="DATE" />
                    </th>
				</tr>
		
				<tr>
					<td><i:inline key=".period"/></td>
					<td><cti:formatDate value="${peakResult.rangeStartDate}" type="DATE" /> - 
                        <cti:formatDate value="${peakResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
                    </td>
				</tr>
				
				<tr>
					<td><i:inline key=".avgDailyVsTotalUsage"/></td>
					<td>${avgVsTotal}</td>
				</tr>
				<tr>
					<cti:msg2 key="${peakResult.peakType.formatKey}" var="peakType"/>
					<td><i:inline key=".peak" arguments="${peakType}"/></td>
					<td>${peakResult.peakValue}</td>
				</tr>
				<c:choose>
					<c:when test="${peakResult.peakType == 'INTERVAL'}">
						<tr>
							<td><i:inline key=".peakInterval"/></td>
							<td><i:inline key=".kw" arguments="${peakResult.demand}"/></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td><i:inline key=".peakDay"/></td>
							<td><i:inline key=".kwh" arguments="${peakResult.usage}"/></td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</c:when>
		<c:otherwise>
			<cti:msg2 var="errorPeakReport" key=".errorRead"/>
			<tags:hideReveal styleClass="error" title="${errorPeakReport}" showInitially="true">
      			<c:forEach items="${peakResult.errors}" var="error">
        			${error.description} (${error.errorCode})<br>
                    ${error.porter}<br>
        			${error.troubleshooting}<br>
      			</c:forEach>
			</tags:hideReveal><br>
		</c:otherwise>
	</c:choose>
</c:if>

