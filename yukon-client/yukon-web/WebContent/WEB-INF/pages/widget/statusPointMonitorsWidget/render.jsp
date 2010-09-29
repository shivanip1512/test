<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_over.gif"/>
<c:url var="enabledImg" value="/WebConfig/yukon/Icons/green_circle.gif"/>
<c:url var="disabledImg" value="/WebConfig/yukon/Icons/gray_circle.gif"/>

<cti:msg var="noMonitorsSetupText" key="yukon.web.modules.amr.statusPointMonitorsWidget.noMonitorsSetup"/>
<cti:msg var="nameHeaderText" key="yukon.web.modules.amr.statusPointMonitorsWidget.tableHeader.name"/>
<cti:msg var="monitoringHeaderText" key="yukon.web.modules.amr.statusPointMonitorsWidget.tableHeader.monitoring"/>
<cti:msg var="enabledHeaderText" key="yukon.web.modules.amr.statusPointMonitorsWidget.tableHeader.enabled"/>
<cti:msg var="createNewText" key="yukon.web.modules.amr.statusPointMonitorsWidget.createNew"/>
<cti:msg var="statusPointMonitoringActionTitleText" key="yukon.web.modules.amr.statusPointMonitorsWidget.actionTitle.statusPointMonitoring"/>
<cti:msg var="enableText" key="yukon.common.enable"/> 
<cti:msg var="disableText" key="yukon.common.disable"/> 

<%-- ERROR --%>
<c:if test="${not empty statusPointMonitorsWidgetError}">
  	<div class="errorRed">${statusPointMonitorsWidgetError}</div>
</c:if>

<%-- TABLE --%>
<c:choose>
    <c:when test="${fn:length(monitors) > 0}">
        <table class="compactResultsTable">
        	
        	<tr>
        		<th style="width:20px;">&nbsp;</th>
        		<th>${nameHeaderText}</th>
        		<th style="text-align:right;">${monitoringHeaderText}</th>
        		<th style="text-align:right;width:80px;">${enabledHeaderText}</th>
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
        				<a href="${viewStatusPointMonitoringUrl}" title="${statusPointMonitoringActionTitleText} (${monitorName})" style="text-decoration:none;">
        					<img src="${cog}" onmouseover="javascript:this.src='${cogOver}'" onmouseout="javascript:this.src='${cog}'">
        				</a>
        			</td>
        			
        			<%-- monitor name --%>
        			<td class="${tdClass}">
        				<a href="${viewStatusPointMonitoringUrl}" title="${statusPointMonitoringActionTitleText} (${monitorName})">${monitorName}</a>
        			</td>
                    			
        			<%-- monitoring count --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${monitorId}/MONITORING_COUNT"/>
        			</td>
        			
        			<%-- enable/disable --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<c:choose>
        					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
        						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${enabledImg}" imgSrcHover="${enabledImg}" statusPointMonitorId="${monitorId}" title="${disableText} (${monitorName})"/>
        					</c:when>
        					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
        						<tags:widgetActionRefreshImage method="toggleEnabled" imgSrc="${disabledImg}" imgSrcHover="${disabledImg}" statusPointMonitorId="${monitorId}" title="${enableText} (${monitorName})" checked="false"/>
        					</c:when>
        				</c:choose>
        			</td>
        		</tr>
        	</c:forEach>
        
        </table>
    </c:when>
    <c:otherwise>
    	${noMonitorsSetupText}
    </c:otherwise>
</c:choose>

<div style="text-align:right;padding-top:5px;">
    <%-- CREATE NEW STATUS POINT MONITOR FORM --%>
    <form id="createNewStatusPointMonitorForm_${widgetParameters.widgetId}" action="/spring/amr/statusPointMonitoring/creationPage" method="get">
        <tags:slowInput myFormId="createNewStatusPointMonitorForm_${widgetParameters.widgetId}" labelBusy="${createNewText}" label="${createNewText}"/>
        <input type="hidden" value="0" name="statusPointMonitorId">
    </form>
</div>