<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage module="tools" page="bulk.routeLocateSettings">
            
    <%-- misc table text --%>
    <cti:msg2 var="hasNoRoute" key=".tableText.hasNoRoute" />
    
    <%-- FOUND ROUTES TABLE --%>
    <c:set var="controls">
        <c:if test="${fn:length(foundRoutes) > 0}">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${foundCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${foundCollection}"/>
        </c:if>
    </c:set>
    <tags:sectionContainer2 nameKey="foundRoutes" controls="${controls}">
        <table class="compact-results-table row-highlighting stacked">
            <thead>
                <tr>
                    <th><cti:msg2 key=".tableHeader.deviceName" /></th>
                    <th><cti:msg2 key=".tableHeader.currentRoute" /></th>
                    <th><cti:msg2 key=".tableHeader.foundOnRoute" /></th>
                    <th><cti:msg2 key=".tableHeader.update" /></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
            <c:if test="${fn:length(foundRoutes) < 1}">
                <tr><td colspan="4"><i:inline key="yukon.common.search.noResultsFound"/></td></tr>
            </c:if>
            <c:forEach var="found" items="${foundRoutes}">
                <tr>
                    <%-- device name --%>
                    <td>${fn:escapeXml(found.deviceName)}</td>
                    
                    <%-- current route --%>
                    <td class="subtle">
                        <c:choose>
                            <c:when test="${not empty found.initialRouteId}">
                                ${found.initialRouteName}
                            </c:when>
                            <c:otherwise>
                                ${hasNoRoute}
                            </c:otherwise>
                        </c:choose>
                    </td>
                    
                    <%-- route name --%>
                    <td>${found.routeName}</td>
                    
                    <%-- update button --%>
                    <td>
                        <c:choose>
                            <c:when test="${found.routeUpdated}">
                                <amr:routeLocateRouteUpdateInfo newRouteName="${found.routeName}" />
                            </c:when>
                            <c:otherwise>
                                <div id="${found.id}">
                                    <cti:button nameKey="updateButtonText" busy="true" classes="js-set-route ML0" data-row-id="${found.id}"
                                        data-device-id="${found.device.deviceId}" data-route-id="${found.routeId}"/>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </tags:sectionContainer2>
    
    <%-- NOT FOUND ROUTES TABLE --%>
    <c:set var="controls">
        <c:if test="${fn:length(notFoundRoutes) > 0}">
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${notFoundCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${notFoundCollection}" />
        </c:if>
    </c:set>
    <tags:sectionContainer2 nameKey="notFoundRoutes" controls="${controls}">
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <th><cti:msg2 key=".tableHeader.deviceName" /></th>
                    <th><cti:msg2 key=".tableHeader.currentRoute" /></th>
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:if test="${fn:length(notFoundRoutes) < 1}">
                    <tr><td colspan="2"><i:inline key="yukon.common.search.noResultsFound"/></td></tr>
                </c:if>
                
                <c:forEach var="notFound" items="${notFoundRoutes}">
                    <tr>
                        <%-- device name --%>
                        <td>${fn:escapeXml(notFound.deviceName)}</td>
                        
                        <%-- current route --%>
                        <td class="subtle">
                            <c:choose>
                                <c:when test="${not empty notFound.initialRouteId}">
                                    ${notFound.initialRouteName}
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
    </tags:sectionContainer2>
    
    <cti:includeScript link="/resources/js/pages/yukon.bulk.route.locate.js"/>
    
 </cti:standardPage>