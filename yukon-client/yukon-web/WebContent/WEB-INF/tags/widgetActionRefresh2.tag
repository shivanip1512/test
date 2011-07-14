<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>
<%@ attribute name="confirmText" required="false" type="java.lang.String"%>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!pageScope.hide}">
    <cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
    <cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
    <cti:uniqueIdentifier var="buttonId" prefix="widgetRefreshButton_"/>
    
    <script type="text/javascript">
        ${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
    </script>
    
    <span id="${thisId}">
    <cti:msg2 var="labelText" key="${label}"/>
    <cti:msg2 var="labelBusyText" key="${labelBusy}"/>
    <cti:msg2 var="confirmTextMsg" key="${confirmText}"/>
    
    <input type="button" value="${labelText}" id="${buttonId}" class="formSubmit">
    <script type="text/javascript">
        
        $("${buttonId}").observe("click", function() {
            var confirmText = '${cti:escapeJavaScript(pageScope.confirmTextMsg)}';
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
    <img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
    </span>
    </span>
</c:if>