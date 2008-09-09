<%@ attribute name="resultId" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier var="ccid" prefix="_ccid"/>

<script type="text/javascript">

    function cancelCommands(resultId, url, ccid, cancelingText, finishedText) {
        
        // swap to wait img
        $('waitImg' + ccid).show();
        $('cancelButton' + ccid).disable();
        $('cancelButton' + ccid).value = cancelingText;
        
        // setup callbacks
        var onComplete = function(transport, json) {
            $('waitImg' + ccid).hide();
            $('cancelArea' + ccid).innerHTML = finishedText;
        };
        
        var onFailure = function(transport, json) {
            alert(json['errorMsg']);
        };

        // run cancel    
        var args = {};
        args.resultId = resultId;
        new Ajax.Request(url, {'method': 'post', 'evalScripts': true, 'onComplete': onComplete, 'onFailure': onFailure, 'onException': onFailure, 'parameters': args});
    }
    

</script>

<c:url var="cancelUrl" value="/spring/group/commander/cancelCommands" />
<c:url var="waitImgUrl" value="/WebConfig/yukon/Icons/indicator_arrows.gif" />
<cti:msg var="cancelText" key="yukon.common.device.commander.results.cancelCommands" />
<cti:msg var="cancelingText" key="yukon.common.device.commander.results.cancelingCommands" />
<cti:msg var="finishedText" key="yukon.common.device.commander.results.finishedCancelingCommands" />

<div id="cancelArea${ccid}">
    <input type="button" 
           value="${cancelText}"
           id="cancelButton${ccid}" 
           onclick="cancelCommands('${resultId}','${cancelUrl}','${ccid}','${cancelingText}','${finishedText}');">
    
    <img id="waitImg${ccid}" src="${waitImgUrl}" style="display:none;">
</div>


