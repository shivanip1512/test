<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="bulk.routeLocateResults">

<script type="text/javascript">
    function enableButton() {
        return function(data) {
            var isComplete = data.isComplete;
            var isCanceled = data.isCanceled;

            if (isComplete == 'true') {
                jQuery('#setViewRoutesButton').removeAttr("disabled");
                jQuery('#cancelLocateDiv').hide();
            }

            if (isCanceled == 'true') {
                jQuery('#setViewRoutesButton').removeAttr("disabled");
                jQuery('#cancelLocateDiv').hide();
                jQuery('#commandsCanceledDiv').show();
            }
        };
    }
</script>
    
    <tags:bulkActionContainer key="yukon.web.modules.tools.bulk.routeLocateResults" deviceCollection="${result.deviceCollection}">
        
        <div class="clearfix stacked">
          <tags:resultProgressBar totalCount="${deviceCount}"
                                  countKey="ROUTELOCATE/${resultId}/COMPLETED_COUNT"
                                  progressLabelTextKey="yukon.web.modules.tools.bulk.routeLocateResults.progressLabel"
                                  statusTextKey="ROUTELOCATE/${resultId}/STATUS_TEXT"
                                  statusClassKey="ROUTELOCATE/${resultId}/STATUS_CLASS"
                                  isAbortedKey="ROUTELOCATE/${resultId}/IS_CANCELED"/>
          <%-- set/view routes --%>
          <form id="routeLocateSettingsForm" action="<cti:url value="/bulk/routeLocate/routeSettings" />" method="get">
              <input type="hidden" name="resultId" value="${resultId}">
              
              <%-- cancel commands --%>
              <div id="cancelLocateDiv" class="action-area">
                  <c:url var="cancelUrl" value="/bulk/routeLocate/cancelCommands" />
                  <cti:msg var="cancelText" key="yukon.web.modules.tools.bulk.routeLocateResults.cancelLocateButton.label" />
                  <tags:cancelCommands resultId="${resultId}" cancelUrl="${cancelUrl}" cancelButtonText="${cancelText}"/>
              </div>
              <c:choose>
                  <c:when test="${result.autoUpdateRoute}">
                      <cti:button nameKey="viewRoutesButton" type="submit" classes="f-disable-after-click" disabled="${not result.complete}"/>
                  </c:when>
                  <c:otherwise>
                      <cti:button nameKey="setRoutesButton" type="submit" classes="f-disable-after-click" disabled="${not result.complete}"/>
                  </c:otherwise>
              </c:choose>
          </form>
                                  
        </div>
        
        <div id="AllDevicesActionsDiv" class="clearfix stacked">
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnAllDevicesLabel" class="small">
                <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.deviceCollection}" />
            
        </div>
        
        <%-- SUCCESS --%>
        <div class="fwb"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.successLabel" />: <span class="success"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/LOCATED_COUNT"/></span></div>
        
        <div id="successActionsDiv" class="clearfix stacked">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.successDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successDeviceCollection}" />
            
        </div>
        
        
        <%-- FAILURE --%>
        <br>
        <div class="fwb"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.failureLabel" />: <span class="error"><cti:dataUpdaterValue type="ROUTELOCATE" identifier="${resultId}/NOT_FOUND_COUNT"/></span></div>
        <div id="commandsCanceledDiv" style="display:none;">
            <br>
            <span class="error"><cti:msg key="yukon.web.modules.tools.bulk.routeLocateResults.commandsCanceled" /></span>
        </div>
        <div id="errorActionsDiv" class="clearfix stacked">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.web.modules.tools.bulk.routeLocateResults.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${result.failureDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureDeviceCollection}" />
            
        </div>
                    
    </tags:bulkActionContainer>
    
    <cti:dataUpdaterCallback function="enableButton()" initialize="true" isComplete="ROUTELOCATE/${resultId}/IS_COMPLETE" isCanceled="ROUTELOCATE/${resultId}/IS_CANCELED"/>
    <cti:dataUpdaterCallback function="Yukon.ui.progressBar.toggleElementsWhenTrue(['AllDevicesActionsDiv','successActionsDiv','errorActionsDiv'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_COMPLETE" />
    <cti:dataUpdaterCallback function="Yukon.ui.progressBar.toggleElementsWhenTrue(['AllDevicesActionsDiv','successActionsDiv','errorActionsDiv'],true)" initialize="true" value="ROUTELOCATE/${resultId}/IS_CANCELED" />
    
 </cti:standardPage>