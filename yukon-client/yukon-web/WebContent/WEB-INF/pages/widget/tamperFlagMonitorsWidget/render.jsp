<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<%-- CREATE NEW TAMPERFLAG MONITOR FORM --%>
<form id="createNewTamperFlagMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/tamperFlagProcessing/edit" method="get">
</form>

<%-- ERROR --%>
<c:if test="${not empty tamperFlagMonitorsWidgetError}">
  	<div class="errorRed">${tamperFlagMonitorsWidgetError}</div>
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
	
		<c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
		<c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>

		<c:set var="tdClass" value=""/>
		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
			<c:set var="tdClass" value="subtleGray"/>
		</c:if>
		
		<tr>
			
			<cti:url var="viewTamperFlagProcessingUrl" value="/spring/amr/tamperFlagProcessing/process/process">
				<cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
			</cti:url>
				
			<%-- action icons --%>
			<td>
			    <cti:msg2 var="tamperFlagProcessingActionTitleText" key=".actionTitle.tamperFlagProcessing"/>
				<a href="${viewTamperFlagProcessingUrl}" title="${tamperFlagProcessingActionTitleText} (${monitorName})" style="text-decoration:none;">
					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
				</a>
				
			</td>
			
			<%-- monitor name --%>
			<td class="${tdClass}">
				<a href="${viewTamperFlagProcessingUrl}" title="${tamperFlagProcessingActionTitleText} (${monitorName})">${monitorName}</a>
			</td>
			
			<%-- violations count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/VIOLATIONS_COUNT"/>
			</td>
			
			<%-- monitoring count --%>
			<td class="${tdClass}" style="text-align:right;">
				<cti:dataUpdaterValue type="TAMPER_FLAG_PROCESSING" identifier="${monitorId}/MONITORING_COUNT"/>
			</td>
			
			<%-- enable/disable --%>
			<td class="${tdClass}" style="text-align:right;">
				<c:choose>
					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                        <cti:msg2 var="disableText" key="yukon.common.disable"/>
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" tamperFlagMonitorId="${monitorId}" title="${disableText} (${monitorName})"/>
					</c:when>
					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                        <cti:msg2 var="enableText" key="yukon.common.enable"/> 
						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" tamperFlagMonitorId="${monitorId}" title="${enableText} (${monitorName})" checked="false"/>
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
    <tags:slowInput myFormId="createNewTamperFlagMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
</div>



