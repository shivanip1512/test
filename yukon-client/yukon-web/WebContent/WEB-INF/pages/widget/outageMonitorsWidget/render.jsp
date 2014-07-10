<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%-- ERROR --%>
<c:if test="${not empty outageMonitorsWidgetError}">
      <div class="error"><i:inline key="${outageMonitorsWidgetError}"/></div>
</c:if>
        
<%-- TABLE --%>
<c:choose>
<c:when test="${fn:length(monitors) > 0}">

<table class="compact-results-table">
    <thead>
        <tr>
            <th style="width:20px;">&nbsp;</th>
            <th><i:inline key=".tableHeader.name"/></th>
            <th style="text-align:right;"><i:inline key=".tableHeader.violations"/></th>
            <th style="text-align:right;"><i:inline key=".tableHeader.monitoring"/></th>
            <th style="text-align:right;width:80px;"><i:inline key=".tableHeader.enabled"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="monitor" items="${monitors}">
            <c:set var="monitorId" value="${monitor.outageMonitorId}"/>
            <c:set var="monitorName" value="${monitor.outageMonitorName}"/>
    
            <c:set var="tdClass" value=""/>
            <c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
                <c:set var="tdClass" value="subtle"/>
            </c:if>
            <tr>
                <cti:url var="viewOutageProcessingUrl" value="/amr/outageProcessing/process/process">
                    <cti:param name="outageMonitorId" value="${monitorId}"/>
                </cti:url>
                    
                <%-- action icons --%>
                <td>
                    <cti:button nameKey="edit" renderMode="image" href="${viewOutageProcessingUrl}" arguments="${fn:escapeXml(monitorName)}" icon="icon-cog-go"/>
                </td>
                
                <%-- monitor name --%>
                <td class="${tdClass}">
                    <a href="${viewOutageProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitorName}"/>">${fn:escapeXml(monitorName)}</a>
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
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           outageMonitorId="${monitorId}" 
                                                           nameKey="disable"
                                                           arguments="${monitorName}"
                                                           icon="icon-enabled"
                                                           btnClass="fr"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           outageMonitorId="${monitorId}"
                                                           nameKey="enable"
                                                           arguments="${monitorName}"
                                                           icon="icon-disabled"
                                                           btnClass="fr"/>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</c:when>

<c:otherwise>
    <span class="empty-list"><i:inline key=".noMonitors"/></span>
</c:otherwise>
</c:choose>

<div class="action-area">
    <form action="/amr/outageProcessing/monitorEditor/edit" method="get">
        <cti:button nameKey="create" icon="icon-plus-green" type="submit" classes="js-blocker fr"/>
    </form>
</div>