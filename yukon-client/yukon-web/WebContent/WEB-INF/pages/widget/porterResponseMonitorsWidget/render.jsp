<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%-- ERROR --%>
<c:if test="${not empty porterResponseMonitorError}">
	<div class="errorMessage">${porterResponseMonitorError}</div>
</c:if>

<%-- TABLE --%>
<c:choose>
	<c:when test="${fn:length(monitors) > 0}">
		<table class="compactResultsTable">
            <thead>
    			<tr>
    				<th style="width: 20px;">&nbsp;</th>
    				<th><i:inline key=".name"/></th>
    				<th style="text-align: right; width: 80px;"><i:inline key=".enabled"/></th>
    			</tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
    			<c:forEach var="monitor" items="${monitors}">
    				<c:set var="tdClass" value="" />
    				<c:if test="${monitor.evaluatorStatus == 'DISABLED'}">
    					<c:set var="tdClass" value="subtle" />
    				</c:if>
    
    				<tr>
    					<cti:url var="viewMonitorUrl" value="/amr/porterResponseMonitor/viewPage">
    						<cti:param name="monitorId" value="${monitor.monitorId}" />
    					</cti:url>
    
    					<%-- action icons --%>
    					<td>
                            <cti:button nameKey="edit" renderMode="image" href="${viewMonitorUrl}" arguments="${monitor.name}" icon="icon-cog-go"/>
    					</td>
    
    					<%-- monitor name --%>
    					<td class="${tdClass}">
    						<a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitor.name}"/>">${fn:escapeXml(monitor.name)}</a>
    					</td>
    
    					<%-- enable/disable --%>
    					<td class="${tdClass}" style="text-align: right;">
    						<c:choose>
    							<c:when test="${monitor.evaluatorStatus eq 'ENABLED'}">
    								<tags:widgetActionRefreshImage method="toggleEnabled"
    									nameKey="disable" arguments="${monitor.name}" btnClass="fr"
    									monitorId="${monitor.monitorId}" icon="icon-enabled"/>
    							</c:when>
    							<c:when test="${monitor.evaluatorStatus eq 'DISABLED'}">
    								<tags:widgetActionRefreshImage method="toggleEnabled"
    									nameKey="enable" arguments="${monitor.name}" btnClass="fr"
    									monitorId="${monitor.monitorId}" checked="false" icon="icon-disabled"/>
    							</c:when>
    						</c:choose>
    					</td>
    				</tr>
    			</c:forEach>
            </tbody>
		</table>
	</c:when>
	<c:otherwise>
        <i:inline key=".noMonitors"/>
    </c:otherwise>
</c:choose>

<div class="actionArea">
	<%-- CREATE NEW MONITOR FORM --%>
	<form action="/amr/porterResponseMonitor/createPage" method="get">
        <cti:button nameKey="create" icon="icon-plus-green" type="submit" classes="f_blocker fr"/>
	</form>
</div>