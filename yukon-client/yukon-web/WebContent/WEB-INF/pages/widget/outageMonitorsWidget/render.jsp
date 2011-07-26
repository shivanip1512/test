<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>

<%-- CREATE NEW OUTAGE MONITOR FORM --%>
<form id="createNewOutageMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/outageProcessing/monitorEditor/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty outageMonitorsWidgetError}">
  	<div class="errorRed">${outageMonitorsWidgetError}</div>
</c:if>
		
<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compactResultsTable">
	<tr>
		<th style="width:20px;">&nbsp;</th>
		<th><i:inline key=".tableHeader.name"/></th>
		<th style="text-align:right;"><i:inline key=".tableHeader.violations"/></th>
		<th style="text-align:right;"><i:inline key=".tableHeader.monitoring"/></th>
		<th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
	</tr>

	<c:forEach var="monitor" items="${monitors}">
		<c:set var="monitorId" value="${monitor.outageMonitorId}"/>
		<c:set var="monitorName" value="${monitor.outageMonitorName}"/>

		<c:set var="tdClass" value=""/>
		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
		<tr>
			<cti:url var="viewOutageProcessingUrl" value="/spring/amr/outageProcessing/process/process">
				<cti:param name="outageMonitorId" value="${monitorId}"/>
			</cti:url>
				
			<%-- action icons --%>
			<td>
                <cti:button key="actionTitle.outageProcessing" renderMode="image" href="${viewOutageProcessingUrl}" arguments="${monitorName}"/>
			</td>
			
			<%-- monitor name --%>
			<td class="${tdClass}">
				<cti:msg2 var="outageProcessingActionTitleText" key=".actionTitle.outageProcessing"/>
				<a href="${viewOutageProcessingUrl}" title="${outageProcessingActionTitleText} (${fn:escapeXml(monitorName)})">${fn:escapeXml(monitorName)}</a>
			</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="OUTAGE_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                        <tags:widgetActionRefreshImage2 title="disable" method="toggleEnabled" outageMonitorId="${monitorId}" titleArgument="${monitorName}"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                        <tags:widgetActionRefreshImage2 title="enable" method="toggleEnabled" outageMonitorId="${monitorId}" titleArgument="${monitorName}" checked="false"/>
					</c:when>
				</c:choose>
			</td>
		</tr>
	</c:forEach>
</table>
</c:when>

<c:otherwise>
	<i:inline key=".noMonitorsSetup"/>
</c:otherwise>
</c:choose>

<div style="text-align:right;padding-top:5px;">
	<cti:msg2 var="createNewText" key=".createNew"/>
    <tags:slowInput myFormId="createNewOutageMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
</div>