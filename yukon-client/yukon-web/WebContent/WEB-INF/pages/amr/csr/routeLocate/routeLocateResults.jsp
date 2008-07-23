<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.routeLocateResults.pageTitle"/>

<cti:standardPage page="routeLocateResults" module="amr">

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
        <cti:msg var="routeLocateHomePageTitle" key="yukon.web.modules.amr.routeLocateHome.pageTitle"/>
        <cti:crumbLink url="${routeLocateHomeUrl}" title="${routeLocateHomePageTitle}" />
        
        <%-- results --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function enableButton(totalCount) {

            return function(data) {
            
                var completedCount = data['completedCount'];
                
                if (completedCount == totalCount) {
                    $('setViewRoutesButton').disabled = false;
                }
            };
        }
        
        function slowInput(buttonObj, formId) {
            buttonObj.disabled = true;
            $('waitImg').show();
            $(formId).submit();
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <c:choose>
        <c:when test="${result.autoUpdateRoute}">
            <c:set var="noteTextKey" value="yukon.web.modules.amr.routeLocateResults.noteTextAutoSetEnabled"/>
        </c:when>
        <c:otherwise>
            <c:set var="noteTextKey" value="yukon.web.modules.amr.routeLocateResults.noteTextAutoSetDisabled"/>
        </c:otherwise>
    </c:choose>
    <tags:bulkActionContainer titleKey="yukon.web.modules.amr.routeLocateResults.boxTitle"
                                noteLabelKey="yukon.web.modules.amr.routeLocateResults.noteLabel"
                                noteTextKey="${noteTextKey}"
                                deviceCollection="${result.deviceCollection}">
        
        
        
        <tags:bulkResultProgress labelKey="yukon.web.modules.amr.routeLocateResults.progressLabel" 
                                inProgressKey="yukon.web.modules.amr.routeLocateResults.inProgress" 
                                completeKey="yukon.web.modules.amr.routeLocateResults.complete"
                                totalCount="${deviceCount}" 
                                updateKey="ROUTELOCATE/${resultId}/COMPLETED_COUNT">
                                
            <%-- set/view routes --%>
            <br>
            <form id="routeLocateSettingsForm" action="<c:url value="/spring/csr/routeLocate/routeSettings" />" method="get">
                <input type="hidden" name="resultId" value="${resultId}">
                
                <c:choose>
                    <c:when test="${result.autoUpdateRoute}">
                        <cti:msg var="viewRoutesButtonLabel" key="yukon.web.modules.amr.routeLocateResults.viewRoutesButtonLabel" />
                        <input type="button" id="setViewRoutesButton" value="${viewRoutesButtonLabel}" onclick="slowInput(this,'routeLocateSettingsForm');" <c:if test="${not result.complete}">disabled</c:if>>
                    </c:when>
                    <c:otherwise>
                        <cti:msg var="setRoutesButtonLabel" key="yukon.web.modules.amr.routeLocateResults.setRoutesButtonLabel" />
                        <input type="button" id="setViewRoutesButton" value="${setRoutesButtonLabel}" onclick="slowInput(this,'routeLocateSettingsForm');" <c:if test="${not result.complete}">disabled</c:if>>
                    </c:otherwise>
                </c:choose>
                <img id="waitImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
            </form>
                                
        </tags:bulkResultProgress>
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.web.modules.amr.routeLocateResults.successLabel" />: <span class="okGreen"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/LOCATED_COUNT"/></span></div>
        
        <div id="successActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
            
        </div>
        
        
        <%-- FAILURE --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.web.modules.amr.routeLocateResults.failureLabel" />: <span class="errorRed"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/NOT_FOUND_COUNT"/></span></div>
        
        <div id="errorActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
            
        </div>
                    
    </tags:bulkActionContainer>
    
    <cti:dataUpdaterCallback function="enableButton(${deviceCount})" initialize="true" completedCount="ROUTELOCATE/${resultId}/COMPLETED_COUNT" />
    <cti:dataUpdaterCallback function="showElementsOnComplete(${deviceCount},['successActionsDiv','errorActionsDiv'])" initialize="true" completedCount="ROUTELOCATE/${resultId}/COMPLETED_COUNT" />
    
 </cti:standardPage>