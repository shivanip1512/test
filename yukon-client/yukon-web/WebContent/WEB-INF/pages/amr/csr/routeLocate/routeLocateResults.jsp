<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msg var="pageTitle" key="yukon.common.device.routeLocate.results.pageTitle"/>

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
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <script type="text/javascript">
    
        function updateProgress(deviceCount) {
    
            return function(data) {
            
                var completedCount = data['completedCount'];
                
                // update progress bar
                var percentDone = Math.round((completedCount / deviceCount) * 100);
                if (deviceCount == 0) {
                    percentDone = 100;
                }
                
                $('completedCount').innerHTML = completedCount; 
                $('progressInner').style.width = percentDone + 'px';
                $('percentComplete').innerHTML = percentDone + '%';  
                
                if (completedCount < deviceCount) {
                    $('progressDescription').innerHTML = 'Running...   ';
                }
                else {
                    $('progressDescription').innerHTML = 'Complete.   ';
                }
                
                // enable set routes button?
                if (percentDone == 100) {
                    $('setViewRoutesButton').disabled = false;
                }
            };
        }
        
        function slowInput(buttonObj) {
        
            buttonObj.disabled = true;
            $('waitImg').show();
        }
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <c:choose>
        <c:when test="${result.autoUpdateRoute}">
            <c:set var="noteTextKey" value="yukon.common.device.routeLocate.results.noteTextAutoSetEnabled"/>
        </c:when>
        <c:otherwise>
            <c:set var="noteTextKey" value="yukon.common.device.routeLocate.results.noteTextAutoSetDisabled"/>
        </c:otherwise>
    </c:choose>
    <tags:bulkActionContainer titleKey="yukon.common.device.routeLocate.results.boxTitle"
                                noteLabelKey="yukon.common.device.routeLocate.results.noteLabel"
                                noteTextKey="${noteTextKey}"
                                deviceCollection="${result.deviceCollection}">
        
        
        <%-- PROGRESS --%>
        <span class="normalBoldLabel"><cti:msg key="yukon.common.device.routeLocate.results.progressLabel" />:</span>
        
        <span id="progressDescription"></span>
                
        <div style="padding:10px;">
            <table cellpadding="0px" border="0px">
                <tr>
                    <td>
                        <div id="progressBorder" class="progressBarBorder" align="left">
                            <div id="progressInner" class="progressBarInner">
                            </div>
                        </div>
                    </td>
                    <td>
                        <span id="percentComplete" class="progressBarPercentComplete">0%</span>
                    </td>
                    <td>
                        <span class="progressBarCompletedCount"><span id="completedCount"></span>/${deviceCount}</span>
                    </td>
                </tr>
            </table>
            
            <%-- set/view routes --%>
            <br>
            <form action="<c:url value="/spring/csr/routeLocate/routeSettings" />" method="get">
                <input type="hidden" name="resultId" value="${resultId}">
                
                <c:choose>
                    <c:when test="${result.autoUpdateRoute}">
                        <cti:msg var="viewRoutesButtonLabel" key="yukon.common.device.routeLocate.results.viewRoutesButtonLabel" />
                        <input type="submit" id="setViewRoutesButton" value="${viewRoutesButtonLabel}" onclick="slowInput(this);" disabled>
                    </c:when>
                    <c:otherwise>
                        <cti:msg var="setRoutesButtonLabel" key="yukon.common.device.routeLocate.results.setRoutesButtonLabel" />
                        <input type="submit" id="setViewRoutesButton" value="${setRoutesButtonLabel}" onclick="slowInput(this);" disabled>
                    </c:otherwise>
                </c:choose>
                <img id="waitImg" src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" style="display:none;">
            </form>
            
        </div>
        
        
        
        
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.common.device.routeLocate.results.successLabel" />: <span class="okGreen"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/LOCATED_COUNT"/></span></div>
        
        <div style="padding:10px;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
            
        </div>
        
        
        <%-- FAILURE --%>
        <br>
        <div class="normalBoldLabel"><cti:msg key="yukon.common.device.routeLocate.results.failureLabel" />: <span class="errorRed"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/NOT_FOUND_COUNT"/></span></div>
        
        <div style="padding:10px;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.routeLocate.results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
            
        </div>
                    
    </tags:bulkActionContainer>
    
    <%-- DATA UPDATER --%>
    <cti:dataUpdaterCallback function="updateProgress(${deviceCount})" initialize="true" completedCount="ROUTELOCATE/${resultId}/COMPLETED_COUNT" />
    
 </cti:standardPage>