<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%-- ERROR --%>
<c:if test="${not empty porterResponseMonitorError}">
    <div class="error">${porterResponseMonitorError}</div>
</c:if>

<%-- TABLE --%>
<c:choose>
    <c:when test="${fn:length(monitors) > 0}">
        <table class="compact-results-table">
            <thead>
                <tr>
                    <th style="width:20px;">&nbsp;</th>
                    <th><i:inline key=".name"/></th>
                    <th><i:inline key=".violations"/></th>
                    <th><i:inline key=".monitoring"/></th>
                    <th style="text-align: right; width: 80px;"><i:inline key=".enabled"/></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="monitor" items="${monitors}">
                    <tr>
                        <cti:url var="viewMonitorUrl" value="/amr/deviceDataMonitor/view">
                            <cti:param name="monitorId" value="${monitor.id}" />
                        </cti:url>

                        <%-- action icons --%>
                        <td>
                            <cti:button nameKey="edit" renderMode="image" href="${viewMonitorUrl}" arguments="${fn:escapeXml(monitor.name)}" 
                                icon="icon-cog-go"/>
                        </td>

                        <%-- monitor name --%>
                        <td>
                            <a href="${viewMonitorUrl}" title="<cti:msg2 key=".edit.hoverText" arguments="${monitor.name}"/>">${fn:escapeXml(monitor.name)}</a>
                        </td>
                                    
                        <%-- violations count --%>
                        <td>
                            <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="VIOLATIONS_COUNT/${monitor.id}"/>
                        </td>
                                    
                        <%-- monitoring count --%>
                        <td>
                            <cti:dataUpdaterValue type="DEVICE_DATA_MONITOR" identifier="MONITORING_COUNT/${monitor.id}"/>
                        </td>
    
                        <%-- enable/disable --%>
                        <td class="tar">
                            <c:choose>
                                <c:when test="${monitor.enabled}">
                                    <tags:widgetActionRefreshImage method="toggleEnabled"
                                                                   nameKey="disable"
                                                                   arguments="${monitor.name}"
                                                                   monitorId="${monitor.id}"
                                                                   icon="icon-enabled"
                                                                   btnClass="fr"/>
                                </c:when>
                                <c:otherwise>
                                    <tags:widgetActionRefreshImage method="toggleEnabled"
                                                                   nameKey="enable"
                                                                   arguments="${monitor.name}"
                                                                   monitorId="${monitor.id}"
                                                                   checked="false"
                                                                   icon="icon-disabled"
                                                                   btnClass="fr"/>
                                </c:otherwise>
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
    <cti:button nameKey="create" icon="icon-plus-green" href="/amr/deviceDataMonitor/createPage" classes="fr"/>
</div>