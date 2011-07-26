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
			<table class="compactResultsTable">
				<tr>
					<th align="left"><i:inline key=".report"/><div style="font-weight:normal;display:inline;">${peakResult.peakType.reportTypeDisplayName}</div></th>
					<th align="left" style="font-weight:normal;color:#666666;">
                        <cti:formatDate value="${peakResult.runDate}" type="DATE" />
                    </th>
				</tr>
		
				<tr>
					<td class="label"><i:inline key=".period"/></td>
					<td><cti:formatDate value="${peakResult.rangeStartDate}" type="DATE" /> - 
                        <cti:formatDate value="${peakResult.rangeStopDate}" type="DATE_MIDNIGHT_PREV" />
                    </td>
				</tr>
				
				<tr>
					<td class="label"><i:inline key=".avgDailyTotalUsage"/></td>
					<td><i:inline key=".kwh" arguments="${peakResult.averageDailyUsage} / ${peakResult.totalUsage}"/></td>
				</tr>
				<tr>
					<td class="label"><i:inline key=".peak"/> ${peakResult.peakType.displayName}:</td>
					<td>${peakResult.peakValue}</td>
				</tr>
				<c:choose>
					<c:when test="${peakResult.peakType == 'INTERVAL'}">
						<tr>
							<td class="label"><i:inline key=".peakInterval"/></td>
							<td><i:inline key=".kw" arguments="${peakResult.demand}"/></td>
						</tr>
					</c:when>
					<c:otherwise>
						<tr>
							<td class="label"><i:inline key=".peakDay"/></td>
							<td><i:inline key=".kwh" arguments="${peakResult.usage}"/></td>
						</tr>
					</c:otherwise>
				</c:choose>
			</table>
		</c:when>
		<c:otherwise>
			<i:inline key=".errorRead"/><br>
  			<c:forEach items="${peakResult.errors}" var="error">
    			<tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
    			${error.porter}<br>
    			${error.troubleshooting}<br>
    			</tags:hideReveal><br>
  			</c:forEach>
		</c:otherwise>
	</c:choose>
</c:if>

