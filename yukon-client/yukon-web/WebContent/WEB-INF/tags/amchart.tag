<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag body-content="empty" %>

<%@ attribute name="chartId" required="true" rtexprvalue="true"%>
<%@ attribute name="settingsUrl" required="true" rtexprvalue="true" description="This settings url CAN also contain the data, which is why this is required and the dataUrl is not"%>
<%@ attribute name="dataUrl" required="false" rtexprvalue="true" description="Not required b/c the data CAN go into the settings file"%>
<%@ attribute name="chartType" required="true" rtexprvalue="true" description="The type of graph you want (amline, amcolumn, ampie, amxy)"%>
<%@ attribute name="cssClass" rtexprvalue="true" description="The css class of the graph container"%>

<cti:includeScript link="/JavaScript/swfobject.js"/>

<c:url var="expressInstallSrc" scope="page" value="/JavaScript/expressinstall.swf"/>
<cti:uniqueIdentifier var="uniqueId" prefix="flashDiv_"/>

<c:url var="amSrc" scope="page"
	value="/JavaScript/amChart/${chartType}.swf">
	<c:param name="${chartType}_path" value="/JavaScript/amChart/"/>
	<c:param name="${chartType}_flashWidth" value="100%"/>
	<c:param name="${chartType}_flashHeight" value="100%"/>
	<c:param name="${chartType}_preloaderColor" value="#000000"/>
	<c:param name="${chartType}_settingsFile" value="${settingsUrl}"/>
	<c:if test="${not empty dataUrl}">
	   <c:param name="${chartType}_dataFile" value="${dataUrl}"/>
	</c:if>
</c:url>

<div id="${uniqueId}" class="${cssClass}">
	<div style="width: 90%; text-align: center;">
		<br>
		<br>
		<h4>The Adobe Flash Player is required to view this graph.</h4>
		<br>Please download the latest version of the Flash Player by following the link below.
		<br>
		<br>
		<a href="http://www.adobe.com" target="_blank">
            <img border="0" src="<c:url value="/WebConfig/yukon/Icons/visitadobe.gif"/>"/>
		</a>
		<br>
	</div>
</div>

<c:set var="swfWidth" value="100%"/>

<script type="text/javascript">
    var so = new SWFObject("${amSrc}", "${chartId}", "${swfWidth}", "300", "8", "#FFFFFF");
    so.useExpressInstall('${expressInstallSrc}');
    so.addVariable("chart_id", "${chartId}");
    so.write("${uniqueId}");
</script>
