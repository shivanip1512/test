<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti"uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<%-- ERROR --%>
<c:if test="${not empty porterResponseMonitorError}">
	<div class="errorRed">${porterResponseMonitorError}</div>
</c:if>

<%-- TABLE --%>
<c:choose>
	<c:when test="${fn:length(monitors) > 0}">
		<table class="compactResultsTable">

			<tr>
				<th style="width: 20px;">&nbsp;</th>
				<th><i:inline key=".name"/></th>
				<th style="text-align: right; width: 80px;"><i:inline key=".enabled"/></th>
			</tr>

			<c:forEach var="monitor" items="${monitors}">
				<c:set var="tdClass" value="" />
				<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
					<c:set var="tdClass" value="subtleGray" />
				</c:if>

				<tr>
					<cti:url var="viewMonitorUrl" value="/spring/amr/porterResponseMonitor/viewPage">
						<cti:param name="monitorId" value="${monitor.monitorId}" />
					</cti:url>

					<%-- action icons --%>
					<td>
                        <cti:msg2 var="viewResponseMonitor" key=".porterResponseMonitoring"/>
						<a href="${viewMonitorUrl}" title="${viewResponseMonitor} (${monitor.name})" 
							style="text-decoration: none;">
							<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" 
								onmouseout="javascript:this.src='${cog}'">
						</a>
					</td>

					<%-- monitor name --%>
					<td class="${tdClass}">
                        
						<a href="${viewMonitorUrl}" title="${viewResponseMonitor} (${monitor.name})">${monitor.name}</a>
					</td>

					<%-- enable/disable --%>
					<td class="${tdClass}" style="text-align: right;">
						<c:choose>
							<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
								<tags:widgetActionRefreshImage2 method="toggleEnabled"
									imgSrc="${enabledImg}" imgSrcHover="${enabledImg}"
									monitorId="${monitor.monitorId}"
									title=".disable" titleArgument="${monitor.name}" />
							</c:when>
							<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
								<tags:widgetActionRefreshImage2 method="toggleEnabled"
									imgSrc="${disabledImg}" imgSrcHover="${disabledImg}"
									monitorId="${monitor.monitorId}"
									title=".enable" titleArgument="${monitor.name}" checked="false" />
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

<div class="widgetActionArea">
	<%-- CREATE NEW MONITOR FORM --%>
	<form id="createNewPorterResponseMonitorForm_${widgetParameters.widgetId}"
		action="/spring/amr/porterResponseMonitor/createPage" method="get">
        <tags:slowInput2 formId="createNewPorterResponseMonitorForm_${widgetParameters.widgetId}" key="createNew"/>
	</form>
</div>