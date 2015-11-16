<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<%-- ERROR --%>
<c:if test="${not empty tamperFlagMonitorsWidgetError}">
      <div class="error">${tamperFlagMonitorsWidgetError}</div>
</c:if>

<%-- TABLE --%>
<cti:url var="submitUrl" value="/amr/tamperFlagProcessing/edit"/>
<form action="${submitUrl}" method="get">
<c:if test="${empty monitors}">
    <span class="empty-list"><i:inline key=".noMonitors"/></span>
</c:if>
<c:if test="${not empty monitors}">

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
        
            <c:set var="monitorId" value="${monitor.tamperFlagMonitorId}"/>
            <c:set var="monitorName" value="${monitor.tamperFlagMonitorName}"/>
    
            <c:set var="tdClass" value=""/>
            <c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
                <c:set var="tdClass" value="subtle"/>
            </c:if>
            
            <tr>
                
                <cti:url var="viewTamperFlagProcessingUrl" value="/amr/tamperFlagProcessing/process/process">
                    <cti:param name="tamperFlagMonitorId" value="${monitorId}"/>
                </cti:url>
                    
                <%-- action icons --%>
                <td>
                        <cti:button nameKey="edit" renderMode="image" href="${viewTamperFlagProcessingUrl}" arguments="${monitorName}" icon="icon-cog-go"/>
                </td>
                
                <%-- monitor name --%>
                <td class="${tdClass}">
                    <a href="${viewTamperFlagProcessingUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitorName}"/>">${fn:escapeXml(monitorName)}</a>
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
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           tamperFlagMonitorId="${monitorId}"
                                                           nameKey="disable"
                                                           arguments="${monitorName}"
                                                           icon="icon-enabled"
                                                           btnClass="fr"/>
                        </c:when>
                        <c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
                            <tags:widgetActionRefreshImage method="toggleEnabled"
                                                           tamperFlagMonitorId="${monitorId}" 
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
</c:if>


<div class="action-area">
    <cti:button nameKey="create" icon="icon-plus-green" type="submit" classes="js-blocker fr"/>
</div>
</form>
