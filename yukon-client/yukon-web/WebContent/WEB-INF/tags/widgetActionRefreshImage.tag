<%@ attribute name="method" required="true" type="java.lang.String"%>
<%@ attribute name="title" required="true" type="java.lang.String"%>
<%@ attribute name="imgSrc" required="true" type="java.lang.String"%>
<%@ attribute name="imgSrcHover" required="true" type="java.lang.String"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ tag  dynamic-attributes="linkParameters" %>

<cti:uniqueIdentifier var="thisId" prefix="widgetAction_"/>
<cti:uniqueIdentifier var="uniqueId" prefix="widgetLinkId_"/>

<c:url value="${imgSrc}" var="safe_imgSrc"/>
<c:url value="${imgSrcHover}" var="safe_imgSrcHover"/>

<script type="text/javascript">
	${widgetParameters.jsWidget}.setupLink('${uniqueId}', ${cti:jsonString(linkParameters)});
</script>



<span id="${thisId}">
<a href="javascript:void(0);" title="${label}" style="text-decoration:none;background: white;" onclick="${widgetParameters.jsWidget}.doActionRefresh('${method}', '${thisId}', 'n/a', '${uniqueId}')">
	<img src="${safe_imgSrc}" border="0" onClick="this.style.display='none';" onMouseOver="this.src='${safe_imgSrcHover}';" onMouseOut="this.src='${safe_imgSrc}';">
</a>
<span class="widgetAction_waiting" style="display:none">
<img src="<c:url value="/WebConfig/yukon/Icons/indicator_arrows.gif"/>" alt="waiting" >
</span>
</span>


