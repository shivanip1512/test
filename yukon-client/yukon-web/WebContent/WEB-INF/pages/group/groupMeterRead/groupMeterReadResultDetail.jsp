<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>

<cti:msg var="resultListPageTitle" key="yukon.common.device.groupMeterRead.results.pageTitle" />
<cti:msg var="pageTitle" key="yukon.common.device.groupMeterRead.resultDetail.pageTitle" />
<cti:msg var="recentResultsTitle" key="yukon.common.device.groupMeterRead.resultDetail.recentResultsTitle" />
<cti:msg var="noteLabel" key="yukon.common.device.groupMeterRead.resultDetail.noteLabel" />
<cti:msg var="noteText" key="yukon.common.device.groupMeterRead.resultDetail.noteText" />
<cti:msg var="successSectionTitle" key="yukon.common.device.groupMeterRead.resultDetail.section.success" />
<cti:msg var="failedSectionTitle" key="yukon.common.device.groupMeterRead.resultDetail.section.failed" />
<cti:msg var="unsupportedSectionTitle" key="yukon.common.device.groupMeterRead.resultDetail.section.unsupported" />
<cti:msg var="viewSuccess" key="yukon.common.device.groupMeterRead.resultDetail.section.success.view" />
<cti:msg var="viewFailedReasons" key="yukon.common.device.groupMeterRead.resultDetail.section.failed.viewFailReasons" />



<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection="devicegroups|groupMeterRead"/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- results list --%>
        <cti:crumbLink url="/spring/group/groupMeterRead/resultsList">${recentResultsTitle}</cti:crumbLink>
        
        <%-- read logs detail --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
    
    <script type="text/javascript">
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <tags:boxContainer id="readLogsDetailContainer" hideEnabled="false">
    
    	<jsp:attribute name="title">
            Reading '${resultWrapper.attributesDescription}' on <cti:msg key="${resultWrapper.result.deviceCollection.description}"/>
        </jsp:attribute>
        
        <jsp:body>
        
        <c:set var="resultKey" value="${resultWrapper.result.key}"/>
        
        <%-- NOTE --%>
        <table>
            <tr>
                <td valign="top" class="smallBoldLabel">${noteLabel}</td>
                <td style="font-size:11px;">
                    ${noteText}
                </td>
            </tr>
        </table>

        <%-- PROGRESS --%>
        <c:set var="totalCount" value="${resultWrapper.result.originalDeviceCollectionCopy.deviceCount}" />
        <tags:resultProgressBar totalCount="${totalCount}"
        						 countKey="GROUP_METER_READ/${resultKey}/COMPLETED_ITEMS"
        						 progressLabelTextKey="yukon.common.device.groupMeterRead.resultDetail.progressLabel"
        						 statusTextKey="GROUP_METER_READ/${resultKey}/STATUS_TEXT"
        						 statusClassKey="GROUP_METER_READ/${resultKey}/STATUS_CLASS">
                    
            <%-- device collection action --%>
            <div id="allDevicesActionsDiv" style="display:none;">
                <br>
                <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.groupMeterRead.resultDetail.allResults" class="small">
                    <cti:mapParam value="${resultWrapper.result.originalDeviceCollectionCopy.collectionParameters}"/>
                </cti:link>
                <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.originalDeviceCollectionCopy}" />
            </div>
            
            <%-- cre action --%>
            <div id="creResultsDiv" style="display:none;">
            
            	<c:if test="${not empty resultWrapper.result.commandRequestExecutionIdentifier}">
            
	                <br>
	                
	                <cti:msg var="creResultsText" key="yukon.common.device.groupMeterRead.resultDetail.creResults"/>
	                
	                <cti:url var="creResultsUrl" value="/spring/common/commandRequestExecutionResults/detail">
	                	<cti:param name="commandRequestExecutionId" value="${resultWrapper.result.commandRequestExecutionIdentifier.commandRequestExecutionId}"/>
	                </cti:url>
	                
	                <cti:link href="${creResultsUrl}" key="yukon.common.device.groupMeterRead.resultDetail.creResults" class="small"/>
	                <img onclick="window.location='${creResultsUrl}';" 
								title="${creResultsText}" 
								src="${script}" onmouseover="javascript:this.src='${scriptOver}'" 
								onmouseout="javascript:this.src='${script}'">
							
				</c:if>
                
            </div>
                                
        </tags:resultProgressBar>
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel">${successSectionTitle} <span class="okGreen"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/SUCCESS_COUNT"/></span></div>
        
        <div id="successActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.groupMeterRead.resultDetail.successResults" class="small">
                <cti:mapParam value="${resultWrapper.result.successCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.successCollection}" />
            
        </div>
    
    
        <%-- PROCESSING EXCEPTION --%>
        <br>
        <div class="normalBoldLabel">${failedSectionTitle} <span class="errorRed"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/FAILURE_COUNT"/></span></div>
        
        <div id="errorActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.groupMeterRead.resultDetail.failureResults" class="small">
                <cti:mapParam value="${resultWrapper.result.failureCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.failureCollection}" />
        
        </div> 
        
        <%-- UNSUPPORTED --%>
        <br>
        <div class="normalBoldLabel">${unsupportedSectionTitle} <span class="errorRed"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${resultKey}/UNSUPPORTED_COUNT"/></span></div>
        
        <div id="unsupportedActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.groupMeterRead.resultDetail.unsupportedResults" class="small">
                <cti:mapParam value="${resultWrapper.result.unsupportedCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${resultWrapper.result.unsupportedCollection}" />
        
        </div> 
        
        </jsp:body>
        
    </tags:boxContainer>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['allDevicesActionsDiv','creResultsDiv','successActionsDiv','errorActionsDiv','unsupportedActionsDiv'],true)" initialize="true" value="GROUP_METER_READ/${resultKey}/IS_COMPLETE" />
      
</cti:standardPage>