<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="period" required="true"%>
<%@ attribute name="graphType" required="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url  var="amline_settingsFile" scope="page" value="/spring/chart/settings">
	<c:param name="title" value="${title}" />
	<c:param name="graphType" value="${graphType}" />
	<c:param name="pointId" value="${pointIds}" />
</c:url>
<c:url  var="amline_dataFile" scope="page" value="/spring/chart/chart">
	<c:param name="pointIds" value="${pointIds}" />
	<c:param name="startDate" value="${startDate}" />
	<c:param name="period" value="${period}" />
	<c:param name="graphType" value="${graphType}" />
</c:url>

<c:url  var="amlineSrc" scope="page" value="/JavaScript/amChart/amline.swf">
	<c:param name="amline_path" value="/JavaScript/amChart/" />
	<c:param name="amline_flashWidth" value="100%" />
	<c:param name="amline_flashHeight" value="90%" />
	<c:param name="amline_preloaderColor" value="#000000" />
	<c:param name="amline_settingsFile" value="${amline_settingsFile}" />
	<c:param name="amline_dataFile" value="${amline_dataFile}" />

</c:url>

<object type="application/x-shockwave-flash" data="<c:out value="${amlineSrc}"/>" width="100%" height="90%" align="middle">
	<param name="allowScriptAccess" value="sameDomain" />
	<param name="movie" value="<c:out value="${amlineSrc}"/>" />
	<param name="scale" value="noscale" />
	<param name="salign" value="lt" />
	<param name="bgcolor" value="#FFFFFF" />
	<param name="wmode" value="transparent" />
</object>