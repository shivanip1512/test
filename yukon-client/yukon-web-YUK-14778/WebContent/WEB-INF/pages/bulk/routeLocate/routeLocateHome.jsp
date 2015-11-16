<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="tools" page="bulk.routeLocateHome">

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.routeLocateHome" deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="error stacked">${errorMsg}</div>
        </c:if>
        
        <form id="executeLocateForm" action="<cti:url value="/bulk/routeLocate/executeRouteLocation"/>">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}"/>
            
            <%-- AUTO UPDATE OPTION --%>
            <label><input type="checkbox" name="autoUpdateRoute" <c:if test="${autoUpdateRoute}">checked</c:if>> <i:inline key=".autoUpdateRouteText"/></label><br>
            <br>
                    
            <%-- ROUTE OPTIONS --%>
            <select multiple name="routesSelect" id="routesSelect" size="12" style="min-width:400px;">
                <c:forEach var="routeOption" items="${routeOptions}">
                    <option value="${routeOption.key}">${routeOption.value}</option>
                </c:forEach>
            </select>
            
            <%-- LOCATE BUTTON --%>
            <cti:checkRolesAndProperties value="DEVICE_ACTIONS">
            <cti:checkRolesAndProperties value="LOCATE_ROUTE">
                <div class="page-action-area">
                    <cti:button nameKey="locateRoute" type="submit" classes="primary action" busy="true"/>
                </div>
            </cti:checkRolesAndProperties>
            </cti:checkRolesAndProperties>
            
        </form>
    
    </tags:bulkActionContainer>
                    
    <%-- ROUTE LOCATE RESULTS --%>
    <c:if test="${not empty routeLocateResultsList}">
    
        <br>
        <cti:msg2 var="recentRouteLocateOperationsHeaderTitle" key=".recentRouteLocateResults.header"/>
        <tags:boxContainer title="${recentRouteLocateOperationsHeaderTitle}" id="recentRouteLocateOperationsContainer" hideEnabled="false">
        
            <cti:msg2 var="performNewActionLinkTitle" key=".recentRouteLocateResults.performNewActionLinkTitle"/>
            
            <table class="compact-results-table">
                <thead>
                    <tr>
                        <th><i:inline key=".recentRouteLocateResults.updateTime"/></th>
                        <th class="tar"><i:inline key=".recentRouteLocateResults.located"/></th>
                        <th class="tar"><i:inline key=".recentRouteLocateResults.notLocated"/></th>
                        <th class="tar"><i:inline key=".recentRouteLocateResults.resultsDetail"/></th>
                        <th class="tar"><i:inline key=".recentRouteLocateResults.status"/></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="b" items="${routeLocateResultsList}" varStatus="resultStatus">
                    
                        <tr>
                            <%-- START/STOP TIME --%>
                            <td>
                                <cti:formatDate value="${b.startTime}" type="BOTH"/> - <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/STOP_TIME"/>
                            </td>
                            
                            <%-- SUCCESS --%>
                            <td class="tar">
                                <c:set var="successFormName" value="processingExceptionForm${b.resultId}"/>
                                
                                <a href="javascript:$('#${successFormName}').submit();" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/LOCATED_COUNT"/></a> 
                                <tags:selectedDevicesPopup deviceCollection="${b.successDeviceCollection}"/>
                                
                                <form id="${successFormName}" method="post" action="/bulk/collectionActions">
                                    <cti:csrfToken/>
                                    <cti:deviceCollection deviceCollection="${b.successDeviceCollection}"/>
                                </form>
                            </td>
                            
                            
                            
                            <%-- PROCESSING EXCEPTION --%>
                            <td class="tar">
                                    
                                <c:set var="notFoundCollectionActionFormName" value="notFoundCollectionActionFormName${b.resultId}"/>
                            
                                <a href="javascript:$('#${notFoundCollectionActionFormName}').submit();" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/NOT_FOUND_COUNT"/></a> 
                                
                                <tags:selectedDevicesPopup deviceCollection="${b.failureDeviceCollection}"/>
                                
                                <form id="${notFoundCollectionActionFormName}" method="post" action="/bulk/collectionActions">
                                    <cti:csrfToken/>
                                    <cti:deviceCollection deviceCollection="${b.failureDeviceCollection}"/>
                                </form>
                                
                            </td>
                            
                            
                            <%-- DEATIL LINK --%>
                            <cti:url var="resultDetailUrl" value="/bulk/routeLocate/results">
                                <cti:param name="resultId" value="${b.resultId}"/>
                            </cti:url>
                    
                            <td class="tar">
                                <a href="${resultDetailUrl}"><i:inline key=".recentRouteLocateResults.detailLink"/></a>
                            </td>
                            
                            <%-- COMPLETE? --%>
                            <td class="tar">
                                <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/STATUS_TEXT"/>
                            </td>
                        </tr>
                    
                    </c:forEach>
                </tbody>
            </table>
        </tags:boxContainer>
        
    </c:if>

</cti:standardPage>