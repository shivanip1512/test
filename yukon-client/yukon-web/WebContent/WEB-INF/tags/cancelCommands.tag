<%@ attribute name="resultId" required="true" type="java.lang.String"%>
<%@ attribute name="cancelButtonText" required="true" type="java.lang.String"%>
<%@ attribute name="cancelUrl" required="true" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier var="ccid" prefix="_ccid"/>

<c:url var="waitImgUrl" value="/WebConfig/yukon/Icons/spinner.gif" />
<cti:msg var="cancelingText" key="yukon.common.device.commander.results.cancelingCommands" />
<cti:msg var="finishedText" key="yukon.common.device.commander.results.finishedCancelingCommands" />

<span>
    <input type="button" 
               value="${cancelButtonText}"
               id="cancelButton${ccid}" 
               onclick="cancelCommands('${resultId}','${cancelUrl}','${ccid}','${cancelButtonText}','${finishedText}');">
    <img id="waitImg${ccid}" src="${waitImgUrl}" style="display:none;">
</span>
           
<div id="cancelArea${ccid}" style="display:none;padding-top:5px;"></div>


