<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="titleArgument" required="false" type="java.lang.String"%>
<%@ attribute name="confirmText" required="false" type="java.lang.String"%>
<%@ attribute name="confirmTextArgument" required="false" type="java.lang.String"%>

<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});

	widgetActionRefreshImageConfirm_${uniqueId} = function() {

		var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}';
		var confirmed = true;
		if (confirmText != null && confirmText.strip() != '') {
			var confirmed = confirm(confirmText);
			if (!confirmed) {
				$('linkImg_${uniqueId}').show();
			}
		}

		if (confirmed) {
		    $('linkImg_${uniqueId}').hide();
			${widgetParameters.jsWidget}.doActionRefresh('${method}', '${thisId}', 'n/a', '${uniqueId}');
		}
	}
</script>

<span id="${thisId}">
    <cti:button key="${title}" id="linkImg_${uniqueId}" renderMode="image" arguments="${titleArgument}" onclick="widgetActionRefreshImageConfirm_${uniqueId}();"/>
    <span class="widgetAction_waiting" style="display:none">
        <img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>">
    </span>
</span>


