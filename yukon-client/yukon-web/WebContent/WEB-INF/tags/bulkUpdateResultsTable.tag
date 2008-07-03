<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsTypeMsgKey" required="true" type="java.lang.String"%>
<%@ attribute name="lineCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="bulkUpdateOperationResults" required="true" type="com.cannontech.common.bulk.service.BulkOperationCallbackResults"%>

<c:set var="resultsId" value="${bulkUpdateOperationResults.resultsId}" />

<script type="text/javascript">

    function updateProgressBar(lineCount) {
    
        return function(data) {
        
            var completedLines = data['completedLines'];
            
            var percentDone = Math.round((completedLines / lineCount) * 100);
            
            $('completedLines${resultsId}').innerHTML = data['completedLines']; 
            
            $('progressInner${resultsId}').style.width = percentDone + 'px';
            $('percentComplete${resultsId}').innerHTML = percentDone + '%';  
            
            if (completedLines < lineCount) {
                $('progressDescription${resultsId}').innerHTML = 'Running...   ';
            }
            else {
                $('progressDescription${resultsId}').innerHTML = 'Complete.   ';
            }

        };
    }
    
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
<span class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.progressLabel" />:</span>

<span id="progressDescription${resultsId}"></span>
        
<div style="padding:10px;">
    <table cellpadding="0px" border="0px">
        <tr>
            <td>
                <div id="progressBorder${resultsId}" style="height:12px; width:100px; border:1px solid black; padding:0px; background-color:#CCCCCC;" align="left">
                    <div id="progressInner${resultsId}" style="height: 10px; width: 0px; padding:1px; overflow:hidden; background-color:#006633; ">
                    </div>
                </div>
            </td>
            <td>
                <span id="percentComplete${resultsId}" style="padding-left:6px;padding-right:10px;display:inline;font-size:11px;">0%</span>
            </td>
            <td>
                <span style="font-size:11px;display:inline;"><span id="completedLines${resultsId}"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/COMPLETED_LINES"/></span>/${lineCount}</span>
            </td>
        </tr>
    </table>
</div>



      
<%-- SUCCESS --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.successLabel" />: <span style="color:#006633;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/SUCCESS_COUNT"/></span></div>

<div style="padding:10px;">

    <%-- device collection action --%>
    <cti:link href="/spring/bulk/collectionActions" key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" class="small">
        <cti:mapParam value="${bulkUpdateOperationResults.successDeviceCollection.collectionParameters}"/>
    </cti:link>
    <tags:selectedDevicesPopup deviceCollection="${bulkUpdateOperationResults.successDeviceCollection}" />

</div>




<%-- PROCESSING EXCEPTION --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionLabel" />: <span style="color:#CC0000;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/></span></div>

<div style="padding:10px;">
    
    
    <div style="height:4px;"></div>
    
    <c:choose>
                                
        <%-- MASS CHANGE --%>
        <c:when test="${bulkUpdateOperationResults.bulkOperationType == 'MASS_CHANGE'}">
            
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

<cti:dataUpdaterCallback function="updateProgressBar(${lineCount})" initialize="true" completedLines="BULKRESULT/${resultsId}/COMPLETED_LINES" />