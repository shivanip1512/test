<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="resultsTypeMsgKey" required="true" type="java.lang.String"%>
<%@ attribute name="resultsId" required="true" type="java.lang.String"%>
<%@ attribute name="lineCount" required="true" type="java.lang.Integer"%>
<%@ attribute name="bulkUpdateOperationResults" required="true" type="com.cannontech.common.bulk.service.BulkOperationCallbackResults"%>


<script type="text/javascript">

    function updateProgressBar(lineCount) {
    
        return function(data) {
        
            var completedLines = data['completedLines'];
            
            var percentDone = Math.round((completedLines / lineCount) * 100);
            
            $('completedLines${resultsId}').innerHTML = data['completedLines']; 
            
            $('progressInner${resultsId}').style.width = percentDone + 'px';
            $('percentComplete${resultsId}').innerHTML = percentDone + '%';  
            
            if (completedLines < lineCount) {
                $('progressDescription${resultsId}').innerHTML = 'In Progress...   ';
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
    <form id="successForm" method="post" action="/spring/bulk/collectionActions">
        <cti:deviceCollection deviceCollection="${bulkUpdateOperationResults.successDeviceCollection}" />
    </form>
         
    <a href="javascript:void(0);" onclick="$('successForm').submit();" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" /></a> <tags:selectedDevicesPopup deviceCollection="${bulkUpdateOperationResults.successDeviceCollection}" />

</div>




<%-- PROCESSING EXCEPTION --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionLabel" />: <span style="color:#CC0000;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/PROCESSING_EXCEPTION_COUNT"/></span></div>

<div style="padding:10px;">
    
    <%-- device collection action --%>
    <form id="processingExceptionForm" method="post" action="/spring/bulk/collectionActions">
        <cti:deviceCollection deviceCollection="${bulkUpdateOperationResults.processingExceptionDeviceCollection}" />
    </form>
         
    <a href="javascript:void(0);" onclick="$('processingExceptionForm').submit();" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.collectionActionOnDevicesLabel" /></a> <tags:selectedDevicesPopup deviceCollection="${bulkUpdateOperationResults.processingExceptionDeviceCollection}" />
    
    <%-- errors list --%>
    <div style="height:8px;"></div>
    <a href="javascript:void(0);" onclick="$('processingErrorsDiv${resultsId}').toggle();refreshErrors('processing', $('processingErrorsDiv${resultsId}'));" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.processingExceptionErrorListLabel" /></a>
    <div id="processingErrorsDiv${resultsId}" style="display:none;"></div>

</div>  




<%-- MAPPING EXCEPTION --%>
<br>
<div class="normalBoldLabel"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.mappingExceptionLabel" />: <span style="color:#CC0000;"><cti:dataUpdaterValue type="BULKRESULT" identifier="${resultsId}/MAPPING_EXCEPTION_COUNT"/></span></div>

<div style="padding:10px;">

    <%-- errors list --%>
    <a href="javascript:void(0);" onclick="$('mappingErrorsDiv${resultsId}').toggle();refreshErrors('mapping', $('mappingErrorsDiv${resultsId}'));" class="small"><cti:msg key="yukon.common.device.bulk.${resultsTypeMsgKey}Results.mappingExceptionErrorListLabel" /></a>
    <div id="mappingErrorsDiv${resultsId}" style="display:none;"></div>
</div>

<cti:dataUpdaterCallback function="updateProgressBar(${lineCount})" initialize="true" completedLines="BULKRESULT/${resultsId}/COMPLETED_LINES" />
