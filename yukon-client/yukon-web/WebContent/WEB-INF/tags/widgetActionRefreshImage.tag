<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="imgSrc" required="true" type="java.lang.String"%>
<%@ attribute name="imgSrcHover" required="true" type="java.lang.String"%>
<%@ attribute name="confirmText" required="false" type="java.lang.String"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<c:url value="${imgSrc}" var="safe_imgSrc"/>
<c:url value="${imgSrcHover}" var="safe_imgSrcHover"/>

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
<a href="javascript:void(0);" title="${title}" style="text-decoration:none;" onclick="widgetActionRefreshImageConfirm_${uniqueId}();">
	<img id="linkImg_${uniqueId}" src="${safe_imgSrc}" border="0" onMouseOver="this.src='${safe_imgSrcHover}';" onMouseOut="this.src='${safe_imgSrc}';">
</a>
<span class="widgetAction_waiting" style="display:none">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
</span>
</span>


