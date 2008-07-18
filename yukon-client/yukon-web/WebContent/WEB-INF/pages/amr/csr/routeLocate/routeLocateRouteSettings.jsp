<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:msg var="pageTitle" key="yukon.common.device.routeLocate.settings.pageTitle"/>

<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:includeScript link="/JavaScript/bulkOperations.js"/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/spring/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <c:url var="collectionActionsUrl" value="/spring/bulk/collectionActions">
            <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
        </c:url>
        <cti:msg var="collectionActionsPageTitle" key="yukon.common.device.bulk.collectionActions.pageTitle"/>
        <cti:crumbLink url="${collectionActionsUrl}" title="${collectionActionsPageTitle}" />
        
        <%-- locate route --%>
        <c:url var="routeLocateHomeUrl" value="/spring/csr/routeLocate/home">
            <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
        </c:url>
        <cti:msg var="routeLocateHomePageTitle" key="yukon.common.device.routeLocate.home.pageTitle"/>
        <cti:crumbLink url="${routeLocateHomeUrl}" title="${routeLocateHomePageTitle}" />
        
        <%-- results --%>
        <c:url var="routeLocateResultsUrl" value="/spring/csr/routeLocate/results">
            <c:param name="resultId" value="${resultId}" />
        </c:url>
        <cti:msg var="routeLocateResultsPageTitle" key="yukon.common.device.routeLocate.results.pageTitle"/>
        <cti:crumbLink url="${routeLocateResultsUrl}" title="${routeLocateResultsPageTitle}" />
        
        <%-- settings --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function setRoute(deviceId, routeId, resultId, deviceRouteLocationId, divId, buttonObj) {
        
            buttonObj.disabled = true;
            $('waitImg' + deviceRouteLocationId).show();
        
            var url = '/spring/csr/routeLocate/setRoute';
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

    <cti:msg var="boxTitle" key="yukon.common.device.routeLocate.settings.boxTitle"/>
        
    <%-- misc table text --%>
    <cti:msg var="updateButtonText" key="yukon.common.device.routeLocate.settings.tableText.updateButtonText" />
    <cti:msg var="noRouteFound" key="yukon.common.device.routeLocate.settings.tableText.noRouteFound" />
    <cti:msg var="currentRoute" key="yukon.common.device.routeLocate.settings.tableText.currentRoute" />
    <cti:msg var="hasNoRoute" key="yukon.common.device.routeLocate.settings.tableText.hasNoRoute" />
    
    <%-- FOUND ROUTES TABLE --%>
    <table>
        <tr>
            <td style="padding-right:16px;"><h3 >Found Routes</h3></td>
            <td>
                <c:if test="${fn:length(foundRoutes) > 0}">
                    <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.routeLocate.results.collectionActionOnDevicesLabel" class="small">
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
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.deviceName" /></th>
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.currentRoute" /></th>
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.foundOnRoute" /></th>
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.update" /></th>
        </tr>
        
        <c:if test="${fn:length(foundRoutes) < 1}">
            <tr><td align="center" colspan="4">None</td></tr>
        </c:if>
        
        <c:forEach var="d" items="${foundRoutes}">
        
            <tr>
            
                <%-- device name --%>
                <td>${d.deviceName}</td>
                
                <%-- current route --%>
                <td class="subtleGray">
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
                            <amr:routeLocateRouteUpdateInfo oldRouteName="${d.initialRouteName}" newRouteName="${d.routeName}" />
                        </c:when>
                        <c:otherwise>
                            <div id="${d.id}">
                                <input type="button" value="${updateButtonText}" onclick="setRoute(${d.device.deviceId}, ${d.routeId}, '${resultId}', ${d.id}, '${d.id}', this);">
                                <img id="waitImg${d.id}" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
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
                    <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.routeLocate.results.collectionActionOnDevicesLabel" class="small">
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
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.deviceName" /></th>
            <th><cti:msg key="yukon.common.device.routeLocate.settings.tableHeader.currentRoute" /></th>
        </tr>
        
        <c:if test="${fn:length(notFoundRoutes) < 1}">
            <tr><td align="center" colspan="2">None</td></tr>
        </c:if>
        
        <c:forEach var="d" items="${notFoundRoutes}">
        
            <tr>
            
                <%-- device name --%>
                <td>${d.deviceName}</td>
                
                <%-- current route --%>
                <td class="subtleGray">
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