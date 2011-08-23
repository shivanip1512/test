<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="showConfirm" required="false" type="java.lang.String"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!pageScope.hide}">
    <cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
    <cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
    <cti:uniqueIdentifier var="buttonId" prefix="widgetRefreshButton_"/>
    
    <script type="text/javascript">
        ${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
    </script>
    
    <span id="${thisId}">
        <cti:msg2 var="labelBusyText" key=".${nameKey}.labelBusy"/>
        <c:if test="${showConfirm}">
            <cti:msg2 var="confirmText" key=".${nameKey}.confirmText"/>
        </c:if>
        <cti:button nameKey="${nameKey}" id="${buttonId}"/>
        <script type="text/javascript">
            $("${buttonId}").observe("click", function() {
                var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}';
                var confirmed = true;
                if (confirmText != null && confirmText.strip() != '') {
                    confirmed = confirm(confirmText);
                }
                if (confirmed) {
                    ${widgetParameters.jsWidget}.doActionRefresh('${method}', '${thisId}', '${labelBusyText}...', '${uniqueId}');
                }
            });
        </script>
        <span class="widgetAction_waiting" style="display:none">
            <img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>">
        </span>
    </span>
</c:if>