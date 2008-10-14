<%@ attribute name="resultId" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier var="ccid" prefix="_ccid"/>

<script type="text/javascript">

    function cancelCommands(resultId, url, ccid, cancelingText, finishedText) {
        
        // save button text for restore on error
        var orgCancelButtonText = $F('cancelButton' + ccid);
        
        // swap to wait img, disable button
        $('waitImg' + ccid).show();
        $('cancelButton' + ccid).disable();
        $('cancelButton' + ccid).value = cancelingText;
        
        // setup callbacks
        var onComplete = function(transport, json) {
            
            var errorMsg = json['errorMsg'];
            if (errorMsg != null) {
                handleError(ccid, errorMsg, orgCancelButtonText);
                return;
            } else {
                showCancelResult(ccid, finishedText);
                $('cancelButton' + ccid).hide();
            }
        };
        
        var onFailure = function(transport, json) {
            handleError(ccid, transport.responseText, orgCancelButtonText);
        };

        // run cancel    
        var args = {};
        args.resultId = resultId;
        new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': onComplete, 'onFailure': onFailure, 'onException': onFailure, 'parameters': args});
    }
    
    function handleError(ccid, errorMsg, orgCancelButtonText) {
    
        showCancelResult(ccid, errorMsg);
        $('cancelButton' + ccid).value = orgCancelButtonText;
        $('cancelButton' + ccid).enable();
    }
    
    function showCancelResult(ccid, msg) {
    
        $('waitImg' + ccid).hide();
        $('cancelArea' + ccid).innerHTML = msg;
        $('cancelArea' + ccid).show();
    }
    

</script>

<c:url var="cancelUrl" value="/spring/group/commander/cancelCommands" />
<c:url var="waitImgUrl" value="/WebConfig/yukon/Icons/indicator_arrows.gif" />
<cti:msg var="cancelText" key="yukon.common.device.commander.results.cancelCommands" />
<cti:msg var="cancelingText" key="yukon.common.device.commander.results.cancelingCommands" />
<cti:msg var="finishedText" key="yukon.common.device.commander.results.finishedCancelingCommands" />

<span>
    <input type="button" 
               value="${cancelText}"
               id="cancelButton${ccid}" 
               onclick="cancelCommands('${resultId}','${cancelUrl}','${ccid}','${cancelingText}','${finishedText}');">
    <img id="waitImg${ccid}" src="${waitImgUrl}" style="display:none;">
</span>
           
<div id="cancelArea${ccid}" style="display:none;padding-top:5px;"></div>


