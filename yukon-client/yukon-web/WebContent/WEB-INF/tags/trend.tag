<%@ attribute name="title" required="true"%>
<%@ attribute name="pointIds" required="true"%>
<%@ attribute name="startDate" required="true"%>
<%@ attribute name="endDate" required="true"%>
<%@ attribute name="period" required="true"%>
<%@ attribute name="graphType" required="false"%>
<%@ attribute name="converterType" required="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<%--<!--  Set name of amCharts program to use (amline or amcolumn) use to form name of swf and jsp to be used -->--%>
<c:choose>
	<c:when test="${graphType == 'LINE'}">
		<c:set var="amChartsProduct" value="amline"/>
	</c:when>
	<c:when test="${graphType == 'COLUMN'}">
		<c:set var="amChartsProduct" value="amcolumn"/>
	</c:when>
</c:choose>

<c:url var="amSettingsFile" scope="page" value="/spring/chart/settings">
	<c:param name="amChartsProduct" value="${amChartsProduct}" />
	<c:param name="title" value="${title}" />
	<c:param name="converterType" value="${converterType}" />
	<c:param name="pointId" value="${pointIds}" />
	
	<!-- require in order to determine a good frequency setting because bar charts do not have ability to do this themselves -->
	<c:param name="startDate" value="${startDate}" />
	<c:param name="endDate" value="${endDate}" />
	<c:param name="period" value="${period}" />
</c:url>

<c:url var="amDataFile" scope="page" value="/spring/chart/chart">
	<c:param name="amChartsProduct" value="${amChartsProduct}" />
	<c:param name="pointIds" value="${pointIds}" />
	<c:param name="startDate" value="${startDate}" />
	<c:param name="endDate" value="${endDate}" />
	<c:param name="period" value="${period}" />
	<c:param name="graphType" value="${graphType}" />
	<c:param name="converterType" value="${converterType}" />
</c:url>

<c:url var="amSrc" scope="page" value="/JavaScript/amChart/${amChartsProduct}.swf">
	<c:param name="${amChartsProduct}_path" value="/JavaScript/amChart/" />
	<c:param name="${amChartsProduct}_flashWidth" value="100%" />
	<c:param name="${amChartsProduct}_flashHeight" value="90%" />
	<c:param name="${amChartsProduct}_preloaderColor" value="#000000" />
	<c:param name="${amChartsProduct}_settingsFile" value="${amSettingsFile}" />
	<c:param name="${amChartsProduct}_dataFile" value="${amDataFile}" />

</c:url>


<c:url var="expressInstallSrc" scope="page" value="/JavaScript/expressinstall.swf" />
<cti:includeScript link="/JavaScript/swfobject.js"/>

<div id="flashcontent">
    <div style="width:90%;text-align:center;">
        <br>
        <br>
        <h4>The Adobe Flash Player is required to view this graph.</h4>
        <br>
        Please download the latest version of the Flash Player by following the link below.
        <br>
        <br>
        <a href="http://www.adobe.com" target="_blank"><img border="0" src="<c:url value="/WebConfig/yukon/Icons/visitadobe.gif"/>" /></a>
        <br>
    </div>
</div>

<script type="text/javascript">
   var so = new SWFObject("${amSrc}", "dataGraph", "100%", "100%", "8", "#FFFFFF");
   so.useExpressInstall('${expressInstallSrc}');
   so.write("flashcontent");
</script>