<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.routeLocateSettings.pageTitle"/>

<cti:standardPage page="routeLocateSettings" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${result.deviceCollection}" />
        
        <%-- locate route --%>
        <cti:url var="routeLocateHomeUrl" value="/bulk/routeLocate/home">
            <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
        </cti:url>
        <cti:msg var="routeLocateHomePageTitle" key="yukon.web.modules.amr.routeLocateHome.pageTitle"/>
        <cti:crumbLink url="${routeLocateHomeUrl}" title="${routeLocateHomePageTitle}" />
        
        <%-- results --%>
        <cti:url var="routeLocateResultsUrl" value="/bulk/routeLocate/results">
            <cti:param name="resultId" value="${resultId}" />
        </cti:url>
        <cti:msg var="routeLocateResultsPageTitle" key="yukon.web.modules.amr.routeLocateResults.pageTitle"/>
        <cti:crumbLink url="${routeLocateResultsUrl}" title="${routeLocateResultsPageTitle}" />
        
        <%-- settings --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function setRoute(deviceId, routeId, resultId, deviceRouteLocationId, divId, buttonObj) {
        
            buttonObj.disabled = true;
            $('waitImg' + deviceRouteLocationId).show();
        
            var url = '/bulk/routeLocate/setRoute';
            var args = {};
            args.deviceId = deviceId;
            args.routeId = routeId;
            args.resultId = resultId;
            args.deviceRouteLocationId = deviceRouteLocationId;
            
            new Ajax.Updater($(divId), url, {'method': 'post', 'parameters': args});
            
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>
    
    <div style="padding-left:12px;">

    <cti:msg var="boxTitle" key="yukon.web.modules.amr.routeLocateSettings.boxTitle"/>
        
    <%-- misc table text --%>
    <cti:msg var="updateButtonText" key="yukon.web.modules.amr.routeLocateSettings.tableText.updateButtonText" />
    <cti:msg var="noRouteFound" key="yukon.web.modules.amr.routeLocateSettings.tableText.noRouteFound" />
    <cti:msg var="currentRoute" key="yukon.web.modules.amr.routeLocateSettings.tableText.currentRoute" />
    <cti:msg var="hasNoRoute" key="yukon.web.modules.amr.routeLocateSettings.tableText.hasNoRoute" />
    
    <%-- FOUND ROUTES TABLE --%>
    <table>
        <tr>
            <td style="padding-right:16px;"><h3 >Found Routes</h3></td>
            <td>
                <c:if test="${fn:length(foundRoutes) > 0}">
                    <cti:link href="/bulk/collectionActions" key="yukon.web.modules.amr.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                        <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
                    </cti:link>
                    <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
                </c:if>
            </td>
        </tr>
    </table>
    
    <table class="resultsTable">
    
        <%-- found routes header --%>
        <tr>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.deviceName" /></th>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.currentRoute" /></th>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.foundOnRoute" /></th>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.update" /></th>
        </tr>
        
        <c:if test="${fn:length(foundRoutes) < 1}">
            <tr><td align="center" colspan="4">None</td></tr>
        </c:if>
        
        <c:forEach var="d" items="${foundRoutes}">
        
            <tr>
            
                <%-- device name --%>
                <td>${d.deviceName}</td>
                
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
                                <input type="button" value="${updateButtonText}" onclick="setRoute(${d.device.deviceId}, ${d.routeId}, '${resultId}', ${d.id}, '${d.id}', this);">
                                <img id="waitImg${d.id}" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
                
            </tr>
            
        </c:forEach>
        
    </table>
    
    
    <br>
    
    
    
    <%-- NOT FOUND ROUTES TABLE --%>
    <table>
        <tr>
            <td style="padding-right:16px;"><h3 >Not Found</h3></td>
            <td>
                <c:if test="${fn:length(notFoundRoutes) > 0}">
                    <cti:link href="/bulk/collectionActions" key="yukon.web.modules.amr.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                        <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
                    </cti:link>
                    <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
                </c:if>
            </td>
        </tr>
    </table>
    
    <table class="resultsTable">
    
        <%-- found routes header --%>
        <tr>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.deviceName" /></th>
            <th><cti:msg key="yukon.web.modules.amr.routeLocateSettings.tableHeader.currentRoute" /></th>
        </tr>
        
        <c:if test="${fn:length(notFoundRoutes) < 1}">
            <tr><td align="center" colspan="2">None</td></tr>
        </c:if>
        
        <c:forEach var="d" items="${notFoundRoutes}">
        
            <tr>
            
                <%-- device name --%>
                <td>${d.deviceName}</td>
                
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
        
    </table>
    
    </div>
                    
 </cti:standardPage>