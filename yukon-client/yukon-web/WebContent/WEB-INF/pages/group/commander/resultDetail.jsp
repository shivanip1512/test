<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>

<cti:standardPage title="Group Command Processing Result Detail" module="amr">

    <cti:standardMenu menuSelection="devicegroups|commander"/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- commander from location --%>
        <cti:crumbLink url="/group/commander/resultList" title="Recent Group Command Processing Results" />
        
        <%-- this result --%>
        <cti:crumbLink title="Group Command Processing Result Detail"/>
    
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
    
    <script type="text/javascript">
        
        function showCmdCanceldMsg() {
            return function(data) {
                if (data.get('isCanceled') == 'true') {
                    $('cmdCanceldMsg').show();
                }
            };
        }

        function refreshResults(kind, theDiv) {

            if (theDiv.visible()) {
            
                var url = '/group/commander/' + kind;
                
                var params = $H({
                    'resultKey': '${result.key}'
                });
            
                var updater = new Ajax.Updater (theDiv, url, {'parameters': params});
            }
        }
    
    </script>
    
    <h2>Group Command Processing Result Detail</h2>
    <br>
  
    <tags:boxContainer id="commanderResultsContainer" hideEnabled="false">
    
        <jsp:attribute name="title">
            Executing '${result.command}' on <cti:msg key="${result.deviceCollection.description}"/>
        </jsp:attribute>
        
        <jsp:body>
    
        <%-- NOTE --%>
        <table>
            <tr>
                <td valign="top" class="smallBoldLabel">Note:</td>
                <td style="font-size:11px;">
                    Progress is updated periodically. Processing will continue if you wish to navigate away from this page at any time.<br>
                    You may view the progress of all recent and ongoing processes from the <a href="/group/commander/resultList">Group Command Processing Results</a> page.<br><br>
                </td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <c:set var="totalCount" value="${result.deviceCollection.deviceCount}" />
        <tags:resultProgressBar totalCount="${totalCount}"
        						 countKey="COMMANDER/${result.key}/COMPLETED_ITEMS"
        						 progressLabelTextKey="yukon.common.device.commander.results.progressLabel"
        						 statusTextKey="COMMANDER/${result.key}/STATUS_TEXT"
        						 statusClassKey="COMMANDER/${result.key}/STATUS_CLASS"
        						 isAbortedKey="COMMANDER/${result.key}/IS_ABORTED">
                    
            <%-- cancel commands --%>
            <div id="cancelCommandsDiv">
                <br>
                <c:url var="cancelUrl" value="/group/commander/cancelCommands" />
                <cti:msg var="cancelText" key="yukon.common.device.commander.collectionActionOnDevicesLabel.cancelLocateButtonLabel" />
                <tags:cancelCommands resultId="${result.key}" 
                                     cancelUrl="${cancelUrl}"
                                     cancelButtonText="${cancelText}"/>
            </div>
            
            <%-- device collection action --%>
            <div id="allDevicesActionsDiv" style="display:none;">
                <br>
                <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.allResults" class="small">
                    <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
                </cti:link>
                <tags:selectedDevicesPopup deviceCollection="${result.deviceCollection}" />
            </div>
            
            <%-- cre action --%>
            <div id="creResultsDiv" style="display:none;">
                <br>
                
                <cti:msg var="creResultsText" key="yukon.common.device.commander.collectionActionOnDevicesLabel.creResults"/>
                
                <cti:url var="creResultsUrl" value="/common/commandRequestExecutionResults/detail">
                	<cti:param name="commandRequestExecutionId" value="${result.commandRequestExecutionIdentifier.commandRequestExecutionId}"/>
                </cti:url>
                
                <cti:link href="${creResultsUrl}" key="yukon.common.device.commander.collectionActionOnDevicesLabel.creResults" class="small"/>
                
            </div>
                                
        </tags:resultProgressBar>
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel">Successfully Executed: <span class="okGreen"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></span></div>
        
        <div id="successActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.successResults" class="small">
                <cti:mapParam value="${result.successCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successCollection}" />
            
            <%-- success list --%>
            <div style="height:8px;"></div>
            <a href="javascript:void(0);" onclick="$('successResultsDiv${result.key}').toggle();refreshResults('successList', $('successResultsDiv${result.key}'));" class="small">View Results</a>
            <div id="successResultsDiv${result.key}" style="display:none;"></div>
            
        </div>
    
    
    
        <%-- PROCESSING EXCEPTION --%>
        <br>
        <div class="normalBoldLabel">Failed To Execute: <span class="errorRed"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></span></div>
        
        <div id="errorActionsDiv" style="padding:10px;display:none;">
        
            <%-- canceled? --%>
            <div id="cmdCanceldMsg" style="display:none;">
                <span class="errorRed">Commands were canceled.</span>
                <br><br>
            </div>
            <cti:dataUpdaterCallback function="showCmdCanceldMsg()" initialize="true" isCanceled="COMMANDER/${result.key}/IS_CANCELED" />
            
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.failureResults" class="small">
                <cti:mapParam value="${result.failureCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureCollection}" />
            
            <%-- errors list --%>
            <div style="height:8px;"></div>
            <a href="javascript:void(0);" onclick="$('errorsResultsDiv${result.key}').toggle();refreshResults('errorsList', $('errorsResultsDiv${result.key}'));" class="small">View Failure Reasons</a>
            <div id="errorsResultsDiv${result.key}" style="display:none;"></div>
            
        </div> 
        
        <c:if test="${result.handleUnsupported}">
	        <%-- UNSUPPORTED --%>
	        <br>
	        <div class="normalBoldLabel">Not Supported: <span class="errorRed"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/UNSUPPORTED_COUNT"/></span></div>
	        
	        <c:if test="${not empty result.unsupportedCollection.deviceList}">
		        <div id="unsupportedActionsDiv" style="padding:10px;">
		        
		            <%-- device collection action --%>
		            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.unsupportedResults" class="small">
		                <cti:mapParam value="${result.unsupportedCollection.collectionParameters}"/>
		            </cti:link>
		            <tags:selectedDevicesPopup deviceCollection="${result.unsupportedCollection}" />
		            
		        </div>
	        </c:if>
        </c:if>

        </jsp:body>
        
    </tags:boxContainer>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['allDevicesActionsDiv','successActionsDiv','errorActionsDiv','creResultsDiv'],true)" initialize="true" value="COMMANDER/${result.key}/IS_COMPLETE" />
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['cancelCommandsDiv'],false)" initialize="true" value="COMMANDER/${result.key}/IS_COMPLETE" />
      
</cti:standardPage>