<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.routeLocateHome.pageTitle" />

<cti:standardPage page="routeLocateHome" module="amr">

    <cti:standardMenu menuSelection="" />
    
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- bulk home --%>
        <cti:msg var="bulkOperationsPageTitle" key="yukon.common.device.bulk.bulkHome.pageTitle"/>
        <cti:crumbLink url="/bulk/bulkHome" title="${bulkOperationsPageTitle}" />
        
        <%-- device selection --%>
        <cti:msg var="deviceSelectionPageTitle" key="yukon.common.device.bulk.deviceSelection.pageTitle"/>
        <cti:crumbLink url="/bulk/deviceSelection" title="${deviceSelectionPageTitle}"/>
        
        <%-- collection actions --%>
        <tags:collectionActionsCrumbLink deviceCollection="${deviceCollection}" />
        
        <%-- locate route --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>

    <script type="text/javascript">
        function submitForm(id) {
            $(id).submit();
        }
    </script>

    <h2>${pageTitle}</h2>
    <br>
                
    <tags:bulkActionContainer   key="yukon.web.modules.amr.routeLocateHome" 
                                deviceCollection="${deviceCollection}">
        
        <%-- ERROR MSG --%>
        <c:if test="${not empty errorMsg}">
            <div class="formErrorSummary">${errorMsg}</div><br>
        </c:if>
        
        <form id="executeLocateForm" action="<cti:url value="/bulk/routeLocate/executeRouteLocation" />">
        
            <%-- DEVICE COLLECTION --%>
            <cti:deviceCollection deviceCollection="${deviceCollection}" />
            
            <%-- AUTO UPDATE OPTION --%>
            <label><input type="checkbox" name="autoUpdateRoute" <c:if test="${autoUpdateRoute}">checked</c:if>> <cti:msg key="yukon.web.modules.amr.routeLocateHome.autoUpdateRouteText"/></label><br>
            <br>
                    
            <%-- ROUTE OPTIONS --%>
            <select multiple name="routesSelect" id="routesSelect" size="12" style="min-width:400px;">
            
                <c:forEach var="routeOption" items="${routeOptions}">
                
                    <option value="${routeOption.key}">${routeOption.value}</option>
                
                </c:forEach>
                
            </select>
            
            <%-- LOCATE BUTTON --%>
            <cti:checkRole role="DeviceActionsRole.ROLEID">
            <cti:checkProperty property="DeviceActionsRole.LOCATE_ROUTE">
                <br><br>
                <cti:msg var="locateButtonLabel" key="yukon.web.modules.amr.routeLocateHome.locateButton"/>
                <tags:slowInput myFormId="executeLocateForm" labelBusy="${locateButtonLabel}" label="${locateButtonLabel}" />
            </cti:checkProperty>
            </cti:checkRole>
            
        </form>
    
    </tags:bulkActionContainer>
                    
    <%-- ROUTE LOCATE RESULTS --%>
    <c:if test="${not empty routeLocateResultsList}">
    
        <br>
        <cti:msg var="recentRouteLocateOperationsHeaderTitle" key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.header"/>
        <tags:boxContainer title="${recentRouteLocateOperationsHeaderTitle}" id="recentRouteLocateOperationsContainer" hideEnabled="false">
        
            <cti:msg var="performNewActionLinkTitle" key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.performNewActionLinkTitle"/>
            
            <div style="width:75%">
            <table class="compactResultsTable" style="vertial-align:bottom;border-width:40px;">
    
                <tr>
                    <th><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.updateTime"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.located"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.notLocated"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.resultsDetail"/></th>
                    <th style="text-align:right;"><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.status"/></th>
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
                                <cti:deviceCollection deviceCollection="${b.successDeviceCollection}" />
                            </form>
                        </td>
                        
                        
                        
                        <%-- PROCESSING EXCEPTION --%>
                        <td align="right">
                                
                            <c:set var="notFoundCollectionActionFormName" value="notFoundCollectionActionFormName${b.resultId}"/>
                        
                            <a href="javascript:submitForm('${notFoundCollectionActionFormName}');" class="small" title="${performNewActionLinkTitle}"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${b.resultId}/NOT_FOUND_COUNT"/></a> 
                            
                            <tags:selectedDevicesPopup deviceCollection="${b.failureDeviceCollection}" />
                            
                            <form id="${notFoundCollectionActionFormName}" method="post" action="/bulk/collectionActions">
                                <cti:deviceCollection deviceCollection="${b.failureDeviceCollection}" />
                            </form>
                            
                        </td>
                        
                        
                        <%-- DEATIL LINK --%>
                        <cti:url var="resultDetailUrl" value="/bulk/routeLocate/results">
                            <cti:param name="resultId" value="${b.resultId}" />
                        </cti:url>
                
                        <td align="right">
                            <a href="${resultDetailUrl}"><cti:msg key="yukon.web.modules.amr.routeLocateHome.recentRouteLocateResults.detailLink"/></a>
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