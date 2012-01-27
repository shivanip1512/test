<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="labelBusy" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>

<%@ attribute name="selected" required="false" type="java.lang.String"%>

<%@ attribute name="container" required="false" type="java.lang.String"%>

<%@ tag  dynamic-attributes="linkParameters" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>

<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});
</script>

<span id="${thisId}">
	<span class="widgetActionLink">
		<c:choose>
			<c:when test="${pageScope.selected != null && pageScope.selected}">
				<jsp:doBody/>
			</c:when>
			<c:otherwise>
				<a class="actionLinkAnchor" title="${title}" href="javascript:${widgetParameters.jsWidget}.doActionLinkRefresh('${method}', '${thisId}', '${labelBusy}...', '${uniqueId}', '${pageScope.container}')"><jsp:doBody/></a>
			</c:otherwise>
		</c:choose>
	</span>
<span class="widgetAction_waiting" style="display:none">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
</span>
</span>