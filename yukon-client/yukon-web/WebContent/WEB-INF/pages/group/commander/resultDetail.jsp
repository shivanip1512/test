<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="groupCommand.details">

    <cti:includeScript link="/resources/js/common/yukon.ui.progressbar.js"/>
    <cti:url var="resultListUrl" value="/group/commander/resultList"/>

    <script type="text/javascript">
        
        function showCmdCanceldMsg() {
            return function(data) {
                if (data.isCanceled === 'true') {
                    $('#cmdCanceldMsg').show();
                }
            };
        }

        function refreshResults(kind, theDiv) {
            if (theDiv.is(':visible')) {
                var url = yukon.url('/group/commander/' + kind);
                $(theDiv).load(url, {'resultKey': '${result.key}'});
            }
        }
    </script>
    
    <tags:sectionContainer id="commanderResultsContainer">
    
        <jsp:attribute name="title">
            Executing '${(result.command)}' on <cti:msg key="${result.deviceCollection.description}"/>
        </jsp:attribute>
        <jsp:body>
    
        <%-- NOTE --%>
        <table>
            <tr>
                <td valign="top" class="strong-label-small">Note:</td>
                <td style="font-size:11px;">
                    Progress is updated periodically. Processing will continue if you wish to navigate away from this page at any time.<br>
                    You may view the progress of all recent and ongoing processes from the <a href="${resultListUrl}">Group Command Processing Results</a> page.<br><br>
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
                                isAbortedKey="COMMANDER/${result.key}/IS_ABORTED"/>
                    
        <%-- CANCEL COMMANDS --%>
        <div id="cancelCommandsDiv" class="clearfix">
            <c:url var="cancelUrl" value="/group/commander/cancelCommands" />
            <cti:msg var="cancelText" key="yukon.common.device.commander.collectionActionOnDevicesLabel.cancelLocateButtonLabel" />
            <tags:cancelCommands resultId="${result.key}" 
                                 cancelUrl="${cancelUrl}"
                                 cancelButtonText="${cancelText}"/>
        </div>
        
        <%-- DEVICE COLLECTION ACTION --%>
        <div id="allDevicesActionsDiv" style="display:none;">
            <br>
            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.allResults" class="small">
                <cti:mapParam value="${result.deviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.deviceCollection}" />
        </div>
        
        <%-- CRE ACTION --%>
        <div id="creResultsDiv" style="display:none;">
            <br>
            
            <cti:msg var="creResultsText" key="yukon.common.device.commander.collectionActionOnDevicesLabel.creResults"/>
            
            <cti:link href="/common/commandRequestExecutionResults/detail" key="yukon.common.device.commander.collectionActionOnDevicesLabel.creResults" class="small">
                <cti:param name="commandRequestExecutionId" value="${result.commandRequestExecutionIdentifier.commandRequestExecutionId}"/>
            </cti:link>
            
        </div>
                                
        <%-- SUCCESS --%>
        <br>
        <div class="fwb">Successfully Executed: <span class="success"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></span></div>
        
        <div id="js-success-actions" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/bulk/collectionActions" key="yukon.common.device.commander.collectionActionOnDevicesLabel.successResults" class="small">
                <cti:mapParam value="${result.successCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successCollection}" />
            
            <%-- success list --%>
            <div style="height:8px;"></div>
            <a href="javascript:void(0);" onclick="$('#successResultsDiv${result.key}').toggle();refreshResults('successList', $('#successResultsDiv${result.key}'));" class="small">View Results</a>
            <div id="successResultsDiv${result.key}" style="display:none;"></div>
            
        </div>
    
        <%-- PROCESSING EXCEPTION --%>
        <br>
        <div class="fwb">Failed To Execute: <span class="error"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></span></div>
        
        <div id="js-error-actions" style="padding:10px;display:none;">
        
            <%-- canceled? --%>
            <div id="cmdCanceldMsg" style="display:none;">
                <span class="error">Commands were canceled.</span>
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
            <a href="javascript:void(0);" onclick="$('#errorsResultsDiv${result.key}').toggle();refreshResults('errorsList', $('#errorsResultsDiv${result.key}'));" class="small">View Failure Reasons</a>
            <div id="errorsResultsDiv${result.key}" style="display:none;"></div>
            
        </div> 
        
        <c:if test="${result.handleUnsupported}">
            <%-- UNSUPPORTED --%>
            <br>
            <div class="fwb">Not Supported: <span class="error"><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/UNSUPPORTED_COUNT"/></span></div>
            
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
        
    </tags:sectionContainer>
    
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['allDevicesActionsDiv','js-success-actions','js-error-actions','creResultsDiv'],true)" initialize="true" value="COMMANDER/${result.key}/IS_COMPLETE" />
    <cti:dataUpdaterCallback function="yukon.ui.progressbar.toggleElementsWhenTrue(['cancelCommandsDiv'],false)" initialize="true" value="COMMANDER/${result.key}/IS_COMPLETE" />
      
</cti:standardPage>