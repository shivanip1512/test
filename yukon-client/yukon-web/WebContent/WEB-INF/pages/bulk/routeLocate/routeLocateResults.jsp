<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.web.modules.amr.routeLocateResults.pageTitle"/>

<cti:standardPage page="routeLocateResults" module="amr">

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
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function enableButton() {

          //assumes data is of type Hash
            return function(data) {
                var isComplete = data.get('isComplete');
                var isCanceled = data.get('isCanceled');

                if (isComplete == 'true') {
                    $('setViewRoutesButton').enable();
                    $('cancelLocateDiv').hide();
                }

                if (isCanceled == 'true') {
                    $('setViewRoutesButton').enable();
                    $('cancelLocateDiv').hide();

                    $('commandsCanceledDiv').show();
                }
            };
        }

        function slowInput(buttonObj, formId) {
            buttonObj.disable();
            $('waitImg').show();
            $(formId).submit();
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <tags:bulkActionContainer key="yukon.web.modules.amr.routeLocateResults"
                              deviceCollection="${result.deviceCollection}">
        
        <tags:resultProgressBar totalCount="${deviceCount}"
        						 countKey="ROUTELOCATE/${resultId}/COMPLETED_COUNT"
        						 progressLabelTextKey="yukon.web.modules.amr.routeLocateResults.progressLabel"
        						 statusTextKey="ROUTELOCATE/${resultId}/STATUS_TEXT"
                                 statusClassKey="ROUTELOCATE/${resultId}/STATUS_CLASS"
                                 isAbortedKey="ROUTELOCATE/${resultId}/IS_CANCELED">
            <%-- set/view routes --%>
            <br>
            <form id="routeLocateSettingsForm" action="<cti:url value="/bulk/routeLocate/routeSettings" />" method="get">
                <input type="hidden" name="resultId" value="${resultId}">
                
                <%-- cancel commands --%>
                <div id="cancelLocateDiv" style="margin-bottom:20px;">
                    <c:url var="cancelUrl" value="/bulk/routeLocate/cancelCommands" />
                    <cti:msg var="cancelText" key="yukon.web.modules.amr.routeLocateResults.cancelLocateButtonLabel" />
                    <tags:cancelCommands resultId="${resultId}"
                                         cancelUrl="${cancelUrl}"
                                         cancelButtonText="${cancelText}"/>
                </div>
                <c:choose>
                    <c:when test="${result.autoUpdateRoute}">
                        <span id="setRoutesSpan">
                            <cti:msg var="viewRoutesButtonLabel" key="yukon.web.modules.amr.routeLocateResults.viewRoutesButtonLabel" />
                            <input type="button" id="setViewRoutesButton" value="${viewRoutesButtonLabel}" onclick="slowInput(this,'routeLocateSettingsForm');" <c:if test="${not result.complete}">disabled</c:if>/>
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span id="setRoutesSpan">
                            <cti:msg var="setRoutesButtonLabel" key="yukon.web.modules.amr.routeLocateResults.setRoutesButtonLabel" />
                            <input type="button" id="setViewRoutesButton" value="${setRoutesButtonLabel}" onclick="slowInput(this,'routeLocateSettingsForm');" <c:if test="${not result.complete}">disabled</c:if>/>
                        </span>
                    </c:otherwise>
                </c:choose>
                <img id="waitImg" src="<cti:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
            </form>
                                
        </tags:resultProgressBar>

        <div id="AllDevicesActionsDiv" style="padding:10px;display:none;">
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.amr.routeLocateResults.collectionActionOnAllDevicesLabel" class="small">
                <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.deviceCollection}" />
            
        </div>
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.web.modules.amr.routeLocateResults.successLabel" />: <span class="okGreen"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/LOCATED_COUNT"/></span></div>
        
        <div id="successActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.amr.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
            
        </div>
        
        
        <%-- FAILURE --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.web.modules.amr.routeLocateResults.failureLabel" />: <span class="errorRed"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/NOT_FOUND_COUNT"/></span></div>
        <div id="commandsCanceledDiv" style="display:none;">
            <br>
            <span class="errorRed"><cti:msg key="yukon.web.modules.amr.routeLocateResults.commandsCanceled" /></span>
        </div>
        <div id="errorActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.amr.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
            
        </div>
                    
    </tags:bulkActionContainer>
    
    <cti:dataUpdaterCallback function="enableButton()" initialize="true" isComplete="ROUTELOCATE/${resultId}/IS_COMPLETE" isCanceled="ROUTELOCATE/${resultId}/IS_CANCELED"/>
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['AllDevicesActionsDiv','successActionsDiv','errorActionsDiv'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_COMPLETE" />
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['AllDevicesActionsDiv','successActionsDiv','errorActionsDiv'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_CANCELED" />
    
 </cti:standardPage>