<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="period" required="true"%>
<%@ attribute name="unitOfMeasure" required="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:url  var="amline_settingsFile" scope="page" value="/spring/chart/settings">
	<c:param name="title" value="${title}" />
	<c:param name="unitOfMeasure" value="${unitOfMeasure}" />
</c:url>
<c:url  var="amline_dataFile" scope="page" value="/spring/chart/chart">
	<c:param name="pointIds" value="${pointIds}" />
	<c:param name="startDate" value="${startDate}" />
	<c:param name="period" value="${period}" />
</c:url>

<c:url  var="amlineSrc" scope="page" value="/JavaScript/amChart/amline.swf">
	<c:param name="amline_path" value="/JavaScript/amChart/" />
	<c:param name="amline_flashWidth" value="100%" />
	<c:param name="amline_flashHeight" value="90%" />
	<c:param name="amline_preloaderColor" value="#000000" />
	<c:param name="amline_settingsFile" value="${amline_settingsFile}" />
	<c:param name="amline_dataFile" value="${amline_dataFile}" />

</c:url>

<object classid="clsid:d27cdb6e-ae6d-11cf-96b8-444553540000" codebase="http://fpdownload.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=7,0,0,0" width="100%" height="90%" id="amline" align="middle">
	<param name="allowScriptAccess" value="sameDomain" />
	<param name="movie" value="${amlineSrc}" />
	<param name="quality" value="high" />
	<param name="scale" value="noscale" />
	<param name="salign" value="lt" />
	<param name="bgcolor" value="#FFFFFF" />
	<embed src="${amlineSrc}" quality="high" scale="noscale" salign="lt" bgcolor="#FFFFFF" width="100%" height="80%" name="amline" align="middle" allowScriptAccess="sameDomain" type="application/x-shockwave-flash" pluginspage="http://www.macromedia.com/go/getflashplayer" />
</object>