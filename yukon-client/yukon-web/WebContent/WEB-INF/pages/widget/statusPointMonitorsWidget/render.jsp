<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:url var="cog" value="/WebConfig/yukon/Icons/cog_go.gif"/>
<c:url var="cogOver" value="/WebConfig/yukon/Icons/cog_go_over.gif"/>

<%-- ERROR --%>
<c:if test="${not empty statusPointMonitorsWidgetError}">
  	<div class="errorMessage"><i:inline key="${statusPointMonitorsWidgetError}"/></div>
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
        			<c:set var="tdClass" value="subtle"/>
        		</c:if>
        		<tr>
        			<cti:url var="viewStatusPointMonitoringUrl" value="/amr/statusPointMonitoring/viewPage">
        				<cti:param name="statusPointMonitorId" value="${monitorId}"/>
        			</cti:url>
        				
        			<%-- action icons --%>
        			<td>
                        <cti:button nameKey="edit" renderMode="image" href="${viewStatusPointMonitoringUrl}" arguments="${monitorName}"/>
        			</td>
        			
        			<%-- monitor name --%>
        			<td class="${tdClass}">
        				<a href="${viewStatusPointMonitoringUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitorName}"/>">${fn:escapeXml(monitorName)}</a>
        			</td>
                    			
        			<%-- monitoring count --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<cti:dataUpdaterValue type="STATUS_POINT_MONITORING" identifier="${monitorId}/MONITORING_COUNT"/>
        			</td>
        			
        			<%-- enable/disable --%>
        			<td class="${tdClass}" style="text-align:right;">
        				<c:choose>
        					<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabled" statusPointMonitorId="${monitorId}" 
                                                               nameKey="disable" arguments="${monitorName}"/>
        					</c:when>
        					<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                                <tags:widgetActionRefreshImage method="toggleEnabled" statusPointMonitorId="${monitorId}"
                                                               nameKey="enable" arguments="${monitorName}"/>
        					</c:when>
        				</c:choose>
        			</td>
        		</tr>
        	</c:forEach>
        
        </table>
    </c:when>
    <c:otherwise>
    	<i:inline key=".noMonitors"/>
    </c:otherwise>
</c:choose>

<div class="actionArea">
    <%-- CREATE NEW STATUS POINT MONITOR FORM --%>
    <form action="/amr/statusPointMonitoring/creationPage" method="get">
        <cti:button nameKey="create" type="submit" styleClass="f_blocker"/>
        <input type="hidden" value="0" name="statusPointMonitorId">
    </form>
</div>