<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>

<%-- ERROR --%>
<c:if test="${not empty statusPointMonitorsWidgetError}">
  	<div class="errorMessage">${statusPointMonitorsWidgetError}</div>
</c:if>

<%-- TABLE --%>
<c:choose>
    <c:when test="${fn:length(monitors) > 0}">
        <table class="compactResultsTable">
        	
        	<tr>
        		<th style="width:20px;">&nbsp;</th>
        		<th><i:inline key=".tableHeader.name"/></th>
        		<th style="text-align:right;"><i:inline key=".tableHeader.monitoring"/></th>
        		<th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
        	</tr>
        
        	<c:forEach var="monitor" items="${monitors}">
        	
        		<c:set var="monitorId" value="${monitor.statusPointMonitorId}"/>
        		<c:set var="monitorName" value="${monitor.statusPointMonitorName}"/>
        
        		<c:set var="tdClass" value=""/>
        		<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
        			<c:set var="tdClass" value="subtleGray"/>
        		</c:if>
        		
        		<tr>
        			<cti:url var="viewStatusPointMonitoringUrl" value="/spring/amr/statusPointMonitoring/viewPage">
        				<cti:param name="statusPointMonitorId" value="${monitorId}"/>
        			</cti:url>
        				
        			<%-- action icons --%>
        			<td>
        				<cti:msg2 var="statusPointMonitoringActionTitleText" key=".actionTitle.statusPointMonitoring"/>
                        <a href="${viewStatusPointMonitoringUrl}" title="${statusPointMonitoringActionTitleText} (${fn:escapeXml(monitorName)})" style="text-decoration:none;">
        					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
        				</a>
        			</td>
        			
        			<%-- monitor name --%>
        			<td class="${tdClass}">
        				<a href="${viewStatusPointMonitoringUrl}" title="${statusPointMonitoringActionTitleText} (${fn:escapeXml(monitorName)})">${fn:escapeXml(monitorName)}</a>
        			</td>
                    			
        			<%-- monitoring count --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${monitorId}/MONITORING_COUNT"/>
        			</td>
        			
        			<%-- enable/disable --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<c:choose>
        					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                                <tags:widgetActionRefreshImage2 title="disable" titleArgument="${monitorName}" method="toggleEnabled" statusPointMonitorId="${monitorId}" />
        					</c:when>
        					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage2 title="enable" titleArgument="${monitorName}" method="toggleEnabled" statusPointMonitorId="${monitorId}" checked="false"/>
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
    <%-- CREATE NEW STATUS POINT MONITOR FORM --%>
    <form id="createNewStatusPointMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/statusPointMonitoring/creationPage" method="get">
        <cti:msg2 var="createNewText" key=".createNew"/>
        <tags:slowInput myFormId="createNewStatusPointMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
        <input type="hidden" value="0" name="statusPointMonitorId">
    </form>
</div>