<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="tools" page="bulk.routeLocateSettings">

    <script type="text/javascript">
    
        function setRoute(deviceId, routeId, resultId, deviceRouteLocationId, divId) {
        
            var url = '/bulk/routeLocate/setRoute';
            var args = {};
            args.deviceId = deviceId;
            args.routeId = routeId;
            args.resultId = resultId;
            args.deviceRouteLocationId = deviceRouteLocationId;
            
            $('#' + divId).load(url, args);
        }
    
    </script>
    
    <cti:msg var="boxTitle" key="yukon.web.modules.tools.bulk.routeLocateSettings.boxTitle"/>
        
    <%-- misc table text --%>
    <cti:msg var="currentRoute" key="yukon.web.modules.tools.bulk.routeLocateSettings.tableText.currentRoute" />
    <cti:msg var="hasNoRoute" key="yukon.web.modules.tools.bulk.routeLocateSettings.tableText.hasNoRoute" />
    
    <%-- FOUND ROUTES TABLE --%>
    <div>
        <strong>Found Routes</strong>
        <c:if test="${fn:length(foundRoutes) > 0}">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
        </c:if>
    </div>
    
    <table class="results-table stacked">
        <thead>
            <tr>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.deviceName" /></th>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.currentRoute" /></th>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.foundOnRoute" /></th>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.update" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
        <c:if test="${fn:length(foundRoutes) < 1}">
            <tr><td align="center" colspan="4">None</td></tr>
        </c:if>
        
        <c:forEach var="d" items="${foundRoutes}">
            <tr>
                <%-- device name --%>
                <td>${fn:escapeXml(d.deviceName)}</td>
                
                <%-- current route --%>
                <td class="subtle">
                    <c:choose>
                        <c:when test="${not empty d.initialRouteId}">
                            ${d.initialRouteName}
                        </c:when>
                        <c:otherwise>
                            ${hasNoRoute}
                        </c:otherwise>
                    </c:choose>
                </td>
                
                <%-- route name --%>
                <td>${d.routeName}</td>
                
                <%-- update button --%>
                <td>
                    <c:choose>
                        <c:when test="${d.routeUpdated}">
                            <amr:routeLocateRouteUpdateInfo newRouteName="${d.routeName}" />
                        </c:when>
                        <c:otherwise>
                            <div id="${d.id}">
                                <cti:button nameKey="updateButtonText" classes="js-disable-after-click" onclick="setRoute(${d.device.deviceId}, ${d.routeId}, '${resultId}', ${d.id}, '${d.id}', this);"/>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
                
            </tr>
            
        </c:forEach>
        </tbody>
    </table>
    
    <%-- NOT FOUND ROUTES TABLE --%>
    <div>
        <strong>Not Found</strong>
        <c:if test="${fn:length(notFoundRoutes) > 0}">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
        </c:if>
    </div>
        
    <table class="results-table">
        <thead>
            <tr>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.deviceName" /></th>
                <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateSettings.tableHeader.currentRoute" /></th>
            </tr>
        </thead>
        <tfoot></tfoot>
        <tbody>
            <c:if test="${fn:length(notFoundRoutes) < 1}">
                <tr><td align="center" colspan="2">None</td></tr>
            </c:if>
            
            <c:forEach var="d" items="${notFoundRoutes}">
                <tr>
                    <%-- device name --%>
                    <td>${fn:escapeXml(d.deviceName)}</td>
                    
                    <%-- current route --%>
                    <td class="subtle">
                        <c:choose>
                            <c:when test="${not empty d.initialRouteId}">
                                ${d.initialRouteName}
                            </c:when>
                            <c:otherwise>
                                ${hasNoRoute}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
                </tr>
            </c:forEach>
        </tbody>
    </table>
    
 </cti:standardPage>