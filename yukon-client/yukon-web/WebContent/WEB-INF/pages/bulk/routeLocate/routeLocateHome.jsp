<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="tools" page="bulk.routeLocateHome">

<script type="text/javascript">
function submitForm(id) {
    document.getElementById(id).submit();
}
</script>

    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.routeLocateHome" deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="formErrorSummary">${errorMsg}</div><br>
        </c:if>
        
        <form id="executeLocateForm" action="<cti:url value="/bulk/routeLocate/executeRouteLocation" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- AUTO UPDATE OPTION --%>
            <label><input type="checkbox" name="autoUpdateRoute" <c:if test="${autoUpdateRoute}">checked</c:if>> <cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.autoUpdateRouteText"/></label><br>
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
        <cti:msg var="recentRouteLocateOperationsHeaderTitle" key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.header"/>
        <tags:boxContainer title="${recentRouteLocateOperationsHeaderTitle}" id="recentRouteLocateOperationsContainer" hideEnabled="false">
        
            <cti:msg var="performNewActionLinkTitle" key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.performNewActionLinkTitle"/>
            
            <div style="width:75%">
            <table class="compact-results-table" style="vertial-align:bottom;border-width:40px;">
    
                <tr>
                    <th><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.updateTime"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.located"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.notLocated"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.resultsDetail"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.status"/></th>
                </tr>
            
                <c:forEach var="b" items="${routeLocateResultsList}" varStatus="resultStatus">
                
                    <tr valign="bottom">
                    
                        <%-- START/STOP TIME --%>
                        <td>
                            <cti:formatDate value="${b.startTime}" type="BOTH"/> - <br>
                            <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/STOP_TIME"/>
                        </td>
                        
                        <%-- SUCCESS --%>
                        <td align="right">
                            <c:set var="successFormName" value="processingExceptionForm${b.resultId}"/>
                            
                            <a href="javascript:submitForm('${successFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/LOCATED_COUNT"/></a> 
                            <tags:selectedDevicesPopup deviceCollection="${b.successDeviceCollection}" />
                            
                            <form id="${successFormName}" method="post" action="/bulk/collectionActions">
                            <cti:csrfToken/>
                                <cti:deviceCollection deviceCollection="${b.successDeviceCollection}" />
                            </form>
                        </td>
                        
                        
                        
                        <%-- PROCESSING EXCEPTION --%>
                        <td align="right">
                                
                            <c:set var="notFoundCollectionActionFormName" value="notFoundCollectionActionFormName${b.resultId}"/>
                        
                            <a href="javascript:submitForm('${notFoundCollectionActionFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/NOT_FOUND_COUNT"/></a> 
                            
                            <tags:selectedDevicesPopup deviceCollection="${b.failureDeviceCollection}" />
                            
                            <form id="${notFoundCollectionActionFormName}" method="post" action="/bulk/collectionActions">
                            <cti:csrfToken/>
                                <cti:deviceCollection deviceCollection="${b.failureDeviceCollection}" />
                            </form>
                            
                        </td>
                        
                        
                        <%-- DEATIL LINK --%>
                        <cti:url var="resultDetailUrl" value="/bulk/routeLocate/results">
                            <cti:param name="resultId" value="${b.resultId}" />
                        </cti:url>
                
                        <td align="right">
                            <a href="${resultDetailUrl}"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateHome.recentRouteLocateResults.detailLink"/></a>
                        </td>
                        
                        <%-- COMPLETE? --%>
                        <td align="right">
                            <cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/STATUS_TEXT"/>
                        </td>
                    </tr>
                
                </c:forEach>
            
            </table>
            </div>
        
        </tags:boxContainer>
        
    </c:if>

</cti:standardPage>