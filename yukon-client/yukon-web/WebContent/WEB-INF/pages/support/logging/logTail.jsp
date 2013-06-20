<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url var="updateUrl" value='/support/logging/view/update?file=/${file}' />

<cti:standardPage module="support" page="logTail">

	<cti:includeScript link="/JavaScript/logUpdater.js"/>
	<input type="hidden" id="updateUrl" value="${updateUrl}"/>
	<input type="hidden" id="file" value="${file}"/>

	<tags:nameValueContainer2 tableClass="stacked">
		<tags:nameValue2 nameKey=".fileName">${fn:escapeXml(logFile.name)}</tags:nameValue2>
		<tags:nameValue2 nameKey=".lastModified"><span id="lastMod"><cti:msg2 key="yukon.common.loading"/><!-- AJAX --></span></tags:nameValue2>
		<tags:nameValue2 nameKey=".fileSize"><span id="fileLength"><cti:msg2 key="yukon.common.loading"/><!-- AJAX --></span></tags:nameValue2>
		<tags:nameValue2 nameKey=".numberOfLines">
			<a id="decrementLinesBtn" class="labeled_icon prev fn" href="javascript:void(0)"></a>
			<input id="numLines" type="text" size="3" value="${numLines}"/>
			<a id="incrementLinesBtn" class="labeled_icon next fn" href="javascript:void(0)"></a>
		</tags:nameValue2>
	</tags:nameValueContainer2>

	<div class="stacked clearfix">
		<cti:button nameKey="download" type="button" href="download?file=${file}"/>
		<cti:button id="pauseBtn" type="button" nameKey="pause" />
		<cti:button id="startBtn" type="button" nameKey="resume" />
	</div>
	<div id="logOutput" class="monospace liteContainer"></div>

</cti:standardPage>
