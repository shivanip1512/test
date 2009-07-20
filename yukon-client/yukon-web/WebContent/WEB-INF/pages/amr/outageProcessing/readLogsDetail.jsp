<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<c:url var="script" value="/WebConfig/yukon/Icons/script.gif"/>
<c:url var="scriptOver" value="/WebConfig/yukon/Icons/script_over.gif"/>

<cti:msg var="outageProcessingPageTitle" key="yukon.web.modules.amr.outageProcessing.pageTitle" />
<cti:msg var="pageTitle" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.pageTitle" />
<cti:msg var="headerText" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.header" />
<cti:msg var="noteLabel" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.noteLabel" />
<cti:msg var="noteText" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.noteText" />
<cti:msg var="successSectionTitle" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.section.success" />
<cti:msg var="failedSectionTitle" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.section.failed" />
<cti:msg var="unsupportedSectionTitle" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.section.unsupported" />
<cti:msg var="viewSuccess" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.section.success.view" />
<cti:msg var="viewFailedReasons" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.section.failed.viewFailReasons" />



<cti:standardPage title="${pageTitle}" module="amr">

    <cti:standardMenu menuSelection=""/>

    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
    
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
        
        <%-- metering home --%>
        <cti:crumbLink url="/spring/meter/start" title="Metering" />
        
        <%-- outage processing --%>
        <cti:url var="outageProcessingUrl" value="/spring/amr/outageProcessing/process/process">
        	<cti:param name="outageMonitorId" value="${outageMonitorId}"/>
        </cti:url>
        <cti:crumbLink url="${outageProcessingUrl}">${outageProcessingPageTitle}</cti:crumbLink>
        
        <%-- read logs detail --%>
        <cti:crumbLink>${pageTitle}</cti:crumbLink>
        
    </cti:breadCrumbs>
    
    <cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>
    
    <script type="text/javascript">
    
    </script>
    
    <h2>${pageTitle}</h2>
    <br>

    <tags:boxContainer title="${headerText}" id="readLogsDetailContainer" hideEnabled="false">
    
    	<br>
        <%-- NOTE 
        <table>
            <tr>
                <td valign="top" class="smallBoldLabel">${noteLabel}</td>
                <td style="font-size:11px;">
                    ${noteText}
                    <br><br>
                </td>
            </tr>
        </table>
        --%>

        <%-- PROGRESS --%>
        <c:set var="totalCount" value="${result.originalDeviceCollectionCopy.deviceCount}" />
        <tags:resultProgressBar totalCount="${totalCount}"
        						 countKey="GROUP_METER_READ/${result.key}/COMPLETED_ITEMS"
        						 progressLabelTextKey="yukon.web.modules.amr.outageProcessing.readLoagsDetail.progressLabel"
        						 statusTextKey="GROUP_METER_READ/${result.key}/STATUS_TEXT"
        						 statusClassKey="GROUP_METER_READ/${result.key}/STATUS_CLASS">
                    
            <%-- device collection action --%>
            <div id="allDevicesActionsDiv" style="display:none;">
                <br>
                <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.allResults" class="small">
                    <cti:mapParam value="${result.originalDeviceCollectionCopy.collectionParameters}"/>
                </cti:link>
                <tags:selectedDevicesPopup deviceCollection="${result.originalDeviceCollectionCopy}" />
            </div>
            
            <%-- cre action --%>
            <div id="creResultsDiv" style="display:none;">
                <br>
                
                <cti:msg var="creResultsText" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.creResults"/>
                
                <cti:url var="creResultsUrl" value="/spring/amr/commandRequestExecution/detail">
                	<cti:param name="commandRequestExecutionId" value="${result.commandRequestExecutionIdentifier.commandRequestExecutionId}"/>
                </cti:url>
                
                <cti:link href="${creResultsUrl}" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.creResults" class="small"/>
                <img onclick="window.location='${creResultsUrl}';" 
							title="${creResultsText}" 
							src="${script}" onmouseover="javascript:this.src='${scriptOver}'" 
							onmouseout="javascript:this.src='${script}'">
                
            </div>
                                
        </tags:resultProgressBar>
        
        <%-- SUCCESS --%>
        <br>
        <div class="normalBoldLabel">${successSectionTitle} <span class="okGreen"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/SUCCESS_COUNT"/></span></div>
        
        <div id="successActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.successResults" class="small">
                <cti:mapParam value="${result.successCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.successCollection}" />
            
        </div>
    
    
        <%-- PROCESSING EXCEPTION --%>
        <br>
        <div class="normalBoldLabel">${failedSectionTitle} <span class="errorRed"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/FAILURE_COUNT"/></span></div>
        
        <div id="errorActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.failureResults" class="small">
                <cti:mapParam value="${result.failureCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.failureCollection}" />
        
        </div> 
        
        <%-- UNSUPPORTED --%>
        <br>
        <div class="normalBoldLabel">${unsupportedSectionTitle} <span class="errorRed"><cti:dataUpdaterValue type="GROUP_METER_READ" identifier="${result.key}/UNSUPPORTED_COUNT"/></span></div>
        
        <div id="unsupportedActionsDiv" style="padding:10px;display:none;">
        
            <%-- device collection action --%>
            <cti:link href="/spring/bulk/collectionActions" key="yukon.web.modules.amr.outageProcessing.readLoagsDetail.unsupportedResults" class="small">
                <cti:mapParam value="${result.unsupportedCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${result.unsupportedCollection}" />
        
        </div> 
        
    </tags:boxContainer>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['allDevicesActionsDiv','creResultsDiv','successActionsDiv','errorActionsDiv','unsupportedActionsDiv'],true)" initialize="true" value="GROUP_METER_READ/${result.key}/IS_COMPLETE" />
      
</cti:standardPage>