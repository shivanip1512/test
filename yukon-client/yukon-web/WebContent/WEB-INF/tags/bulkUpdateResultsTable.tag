<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsTypeMsgKey" required="true" type="java.lang.String"%>
<%@ attribute name="totalCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="bulkUpdateOperationResults" required="true" type="com.cannontech.common.bulk.service.BulkOperationCallbackResults"%>

<c:set var="resultsId" value="${bulkUpdateOperationResults.resultsId}" />

<cti:includeScript link="/JavaScript/bulkDataUpdaterCallbacks.js"/>

<script type="text/javascript">
    
    function refreshErrors(kind, theDiv) {

        if (theDiv.visible()) {
        
            var url = '/spring/bulk/' + kind + 'ExceptionErrorsRefresh';
            
            var params = $H();
            params['resultsId'] = '${resultsId}';
        
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
<tags:bulkResultProgress labelKey="yukon.common.device.bulk.${resultsTypeMsgKey}Results.progressLabel" 
                        inProgressKey="yukon.common.device.bulk.${resultsTypeMsgKey}Results.inProgress" 
                        completeKey="yukon.common.device.bulk.${resultsTypeMsgKey}Results.complete"
                        totalCount="${totalCount}" 
                        updateKey="BULKRESULT/${resultsId}/COMPLETED_LINES">
                        
</tags:bulkResultProgress>

      
<%-- SUCCESS (mass delete has no success group containing devices) --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.successLabel" />: <span style="color:#006633;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/SUCCESS_COUNT"/></span></div>

<c:if test="${bulkUpdateOperationResults.bulkOperationType != 'MASS_DELETE'}">

    <div id="successActionsDiv" style="padding:10px;display:none;">
    
        <%-- device collection action --%>
        <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" class="small">
            <cti:mapParam value="${bulkUpdateOperationResults.successDeviceCollection.collectionParameters}"/>
        </cti:link>
        <tags:selectedDevicesPopup deviceCollection="${bulkUpdateOperationResults.successDeviceCollection}" />
    
    </div>

</c:if>



<%-- PROCESSING EXCEPTION --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionLabel" />: <span style="color:#CC0000;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/></span></div>

<div id="errorActionsDiv" style="padding:10px;display:none;">
    
    <c:choose>
                                
        <%-- MASS CHANGE/DELETE --%>
        <c:when test="${bulkUpdateOperationResults.bulkOperationType == 'MASS_CHANGE' || bulkUpdateOperationResults.bulkOperationType == 'MASS_DELETE'}">
            
            <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" class="small">
                <cti:mapParam value="${bulkUpdateOperationResults.processingExceptionDeviceCollection.collectionParameters}"/>
            </cti:link>
            <tags:selectedDevicesPopup deviceCollection="${bulkUpdateOperationResults.processingExceptionDeviceCollection}" />
            
            <br>
                                    
        </c:when>
        
        <%-- UPDATE/IMPORT --%>
        <c:otherwise>
        
            <tags:downloadBulkFailuresFile resultsId="${resultsId}" showText="true" />
    
        </c:otherwise>
        
    </c:choose>
    
    <%-- errors list --%>
    <br>
    <a href="javascript:void(0);" onclick="$('processingErrorsDiv${resultsId}').toggle();refreshErrors('processing', $('processingErrorsDiv${resultsId}'));" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionErrorListLabel" /></a>
    
    <div id="processingErrorsDiv${resultsId}" style="display:none;"></div>

</div>

<cti:dataUpdaterCallback function="showElementsOnComplete(${totalCount},['successActionsDiv','errorActionsDiv'])" initialize="true" completedCount="BULKRESULT/${resultsId}/COMPLETED_LINES" />