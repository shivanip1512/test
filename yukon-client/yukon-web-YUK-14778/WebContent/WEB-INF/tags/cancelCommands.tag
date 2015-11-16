<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="cancelButtonText" required="true" %>
<%@ attribute name="cancelUrl" required="true" %>
<%@ attribute name="resultId" required="true" %>


<cti:uniqueIdentifier var="ccid" prefix="_ccid"/>

<cti:url var="waitImgUrl" value="/WebConfig/yukon/Icons/spinner.gif"/>
<cti:msg var="cancelingText" key="yukon.common.device.commander.results.cancelingCommands"/>
<cti:msg var="finishedText" key="yukon.common.device.commander.results.finishedCancelingCommands"/>

<span>
    <cti:button id="cancelButton${ccid}" label="${cancelButtonText}" onclick="yukon.ui.util.cancelCommands('${resultId}','${cancelUrl}','${ccid}','${cancelButtonText}','${finishedText}');"/>
    <img id="waitImg${ccid}" src="${waitImgUrl}" style="display:none;">
</span>

<div id="cancelArea${ccid}" style="display:none;padding-top:5px;"></div>