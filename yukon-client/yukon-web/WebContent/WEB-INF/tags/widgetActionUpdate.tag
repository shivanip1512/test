<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="container" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!hide}">
	<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
	<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
	
	<script type="text/javascript">
		${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(linkParameters)});
	</script>
	
	
	
	<span id="${thisId}">
	<input type="button" value="${label}" onclick="${widgetParameters.jsWidget}.doActionUpdate('${method}', '${container}', '${thisId}', '${labelBusy}...', '${uniqueId}')">
	<span class="widgetAction_waiting" style="display:none">
	<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
	</span>
	</span>
</c:if>