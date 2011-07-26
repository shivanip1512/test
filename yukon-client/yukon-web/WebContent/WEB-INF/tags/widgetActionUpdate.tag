<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="container" required="true" type="java.lang.String"%>
<%@ attribute name="label" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="hide" type="java.lang.Boolean" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<c:if test="${!pageScope.hide}">
	<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
	<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>
	
	<script type="text/javascript">
		${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
	</script>
	
    <%-- Prepending '.' and appending '.label' here to stay consistent with the xml key style of cti:button --%>
	<cti:msg2 var="labelBusyText" key=".${labelBusy}.label"/> 
	
	<span id="${thisId}">
    <cti:button key="${label}" type="button" onclick="${widgetParameters.jsWidget}.doActionUpdate('${method}', '${container}', '${thisId}', '${labelBusyText}...', '${uniqueId}')"/>
	<span class="widgetAction_waiting" style="display:none">
	<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>"/>
	</span>
	</span>
</c:if>