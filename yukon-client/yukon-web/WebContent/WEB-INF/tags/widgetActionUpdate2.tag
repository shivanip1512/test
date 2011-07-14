<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="container" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!pageScope.hide}">
	<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
	<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
	
	<script type="text/javascript">
		${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
	</script>
	
	<cti:msg2 var="labelText" key="${label}"/>
	<cti:msg2 var="labelBusyText" key="${labelBusy}"/>
	
	<span id="${thisId}">
	<input class="formSubmit" type="button" value="${labelText}" onclick="${widgetParameters.jsWidget}.doActionUpdate('${method}', '${container}', '${thisId}', '${labelBusyText}...', '${uniqueId}')">
	<span class="widgetAction_waiting" style="display:none">
	<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
	</span>
	</span>
</c:if>