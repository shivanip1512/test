<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="arguments" required="false" type="java.lang.String"%>
<%@ attribute name="showConfirm" required="false" type="java.lang.String"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<c:if test="${showConfirm}">
    <cti:msg2 var="confirmText" key=".${nameKey}.confirmText" arguments="${arguments}"/>
</c:if>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});

	widgetActionRefreshImageConfirm_${uniqueId} = function() {

		var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}';
		var confirmed = true;
		if (confirmText != null && confirmText.strip() != '') {
			var confirmed = confirm(confirmText);
		}

		if (confirmed) {
			${widgetParameters.jsWidget}.doActionRefresh({
			    command:     '${method}', 
			    buttonID:    '${thisId}', 
			    waitingText: "", 
			    key:         '${uniqueId}'});
		}
	}
</script>

<span id="${thisId}">
    <cti:button nameKey="${nameKey}" id="linkImg_${uniqueId}" renderMode="image" arguments="${arguments}" onclick="widgetActionRefreshImageConfirm_${uniqueId}();"/>
    <span class="widgetAction_waiting" style="display:none">
        <img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>">
    </span>
</span>
