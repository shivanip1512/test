<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="checked" required="true" type="java.lang.Boolean"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<c:url value="${imgSrc}" var="safe_imgSrc"/>
<c:url value="${imgSrcHover}" var="safe_imgSrcHover"/>

<script type="text/javascript">

	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(pageScope.linkParameters)});

	widgetActionRefreshCheckbox_${uniqueId} = function() {

		${widgetParameters.jsWidget}.doActionRefresh({
		    command:      '${method}', 
		    buttonID:     '${thisId}', 
		    waitingText:  'n/a', 
		    key:          '${uniqueId}'});
	}

</script>



<span id="${thisId}">
<input type="checkbox" <c:if test="${checked}">checked</c:if> onclick="this.style.display='none'; widgetActionRefreshCheckbox_${uniqueId}();" title="${title}">

<span class="widgetAction_waiting" style="display:none">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
</span>
</span>


