<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsTypeMsgKey" required="true" type="java.lang.String"%>
<%@ attribute name="callbackResult" required="true" type="com.cannontech.common.bulk.callbackResult.BackgroundProcessResultHolder"%>

<c:set var="resultsId" value="${callbackResult.resultsId}" />

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

<script type="text/javascript">
    
    function refreshErrors(theDiv) {

        if (theDiv.visible()) {
        
            var url = '/bulk/processingExceptionErrorsRefresh';
            
            var params = $H({
                'resultsId': '${resultsId}'
            });
        
            var updater = new Ajax.Updater (theDiv, url, {
          
              'parameters': params,
              
              'onSuccess': function(response) {
                           },
              
              'onException': function(response) {
                           }
            });
        }
    }
    
    function submitForm(id) {
        $(id).submit();
    }
        
</script>




<%-- NOTE --%>
<table>
    <tr>
        <td valign="top" class="smallBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.noteLabel"/></td>
        <td style="font-size:11px;">
            <cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.noteText"/><br><br>
        </td>
    </tr>
</table>



<%-- PROGRESS --%>
<tags:resultProgressBar totalCount="${callbackResult.totalItems}"
    					countKey="BACKGROUNDPROCESS/${resultsId}/COMPLETED_LINES"
						progressLabelTextKey="yukon.common.device.bulk.${resultsTypeMsgKey}Results.progressLabel"
						statusTextKey="BACKGROUNDPROCESS/${resultsId}/STATUS_TEXT" />

      
<%-- SUCCESS COUNT --%>
<br>
<div class="fwb"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.successLabel" />: <span class="success"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/SUCCESS_COUNT"/></span></div>

<%-- SUCCESS DEVICE COLLECTION --%>
<c:if test="${callbackResult.successDevicesSupported}">

    <div id="successActionsDiv" style="padding:4px;display:none;">
    	
    	<ul style="padding-left:0px; margin-top:0px;">
    	
    	<%-- device collection action --%>
    	<li>
        <cti:link href="/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" class="small">
            <cti:mapParam value="${callbackResult.successDeviceCollection.collectionParameters}"/>
        </cti:link>
        <tags:selectedDevicesPopup deviceCollection="${callbackResult.successDeviceCollection}" />
        </li>
        
        </ul>
    
    </div>
    
    <cti:dataUpdaterCallback function="toggleElementsWhenTrue(['successActionsDiv'],true)" initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_SUCCESSES" />

</c:if>



<%-- FAILURE COUNT --%>
<br>
<div class="fwb"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionLabel" />: <span class="error"><cti:dataUpdaterValue type="BACKGROUNDPROCESS" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/></span></div>

<%-- FAILURE DETAILS --%>
<div id="errorActionsDiv" style="padding:4px;display:none;">

    <ul style="padding-left:0px; margin-top:0px;">

    <%-- FAILURE DEVICE COLLECTION --%> 
    <c:if test="${callbackResult.failureDevicesSupported}">
    	<li style="padding-bottom:4px;">
		<cti:link href="/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" class="small">
			<cti:mapParam value="${callbackResult.failureDeviceCollection.collectionParameters}"/>
        </cti:link>
        <tags:selectedDevicesPopup deviceCollection="${callbackResult.failureDeviceCollection}" /><br>
        </li>
    </c:if>
       
    <%-- FAILURE FILE DOWNLOAD --%>
    <c:if test="${callbackResult.failureFileSupported}">
    	<li style="padding-bottom:4px;">
        <tags:downloadBulkFailuresFile resultsId="${resultsId}" showText="true" />
        </li>
    </c:if>
    
    <%-- FAILURE DETAILS LINK --%>
    <c:if test="${callbackResult.failureReasonsListSupported}">
	    <a href="javascript:void(0);" onclick="$('processingErrorsDiv${resultsId}').toggle();refreshErrors($('processingErrorsDiv${resultsId}'));" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionErrorListLabel" /></a>
	    
	    <div id="processingErrorsDiv${resultsId}" style="display:none;"></div>
	</c:if>
	
	<ul>
	
</div>

<cti:dataUpdaterCallback function="toggleElementsWhenTrue(['errorActionsDiv'],true)" initialize="true" value="BACKGROUNDPROCESS/${resultsId}/IS_COMPLETE_WITH_FAILURES" />