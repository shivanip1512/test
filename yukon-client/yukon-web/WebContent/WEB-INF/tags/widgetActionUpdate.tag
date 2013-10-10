<%@ tag dynamic-attributes="linkParameters" %>

<%@ attribute name="method" required="true"%>
<%@ attribute name="container" required="true"%>
<%@ attribute name="nameKey" required="true"%>
<%@ attribute name="showConfirm" required="false" type="java.lang.String"%>
<%@ attribute name="waitingTextLocation" description="jQuery selector to put the busy text in when it shouldn't be next to the button/link (ex: link is in a dropdown)"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>
<%@ attribute name="type" description="The type of this element. Either 'button' or 'link'. Defaults to 'button'"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:default var="type" value="button"/>

<c:if test="${!pageScope.hide}">
	<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
	<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
    <cti:uniqueIdentifier var="buttonId" prefix="widgetUpdateButton_"/>
	
	<script type="text/javascript">
		${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
	</script>

    <span id="${thisId}">
        <c:if test="${showConfirm}">
            <cti:msg2 var="confirmText" key=".${nameKey}.confirmText"/>
        </c:if>
        
        <%-- Prepending '.' and appending '.labelBusy' here to stay consistent with the xml key style of cti:button --%>
        <cti:msg2 var="labelBusyText" key=".${nameKey}.labelBusy"/>
    
        <c:choose>
	        <c:when test="${type == 'button'}">
	            <cti:button nameKey="${nameKey}" id="${buttonId}"/>
	        </c:when>
	        <c:otherwise>
                <a id="${buttonId}" href="javascript:void(0);"><i:inline key=".${nameKey}.label"/></a>
	        </c:otherwise>
        </c:choose>
        <script>
            jQuery(document.getElementById("${buttonId}")).click(function() {
                var confirmText = '${cti:escapeJavaScript(pageScope.confirmText)}';
                var confirmed = true;
                if (confirmText.strip() !== '') {
                    confirmed = confirm(confirmText);
                }
                if (confirmed) {
                    ${widgetParameters.jsWidget}.doActionUpdate({
                        command:        '${method}', 
                        containerID:    '${container}', 
                        buttonID:       '${thisId}', 
                        waitingText:    '${labelBusyText}...',
                        waitingTextLocation:    '${pageScope.waitingTextLocation}',
                        key:            '${uniqueId}'});
                }
            });
        </script>
        
        <span class="widgetAction_waiting" style="display:none">
            <img src="<c:url value="/WebConfig/yukon/Icons/spinner.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>"/>
        </span>
	</span>
</c:if>