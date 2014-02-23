<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib  prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:url var="updateUrl" value='/support/logging/view/update?file=/${file}' />

<cti:standardPage module="support" page="logTail">

<style>.logLine {white-space: pre-wrap;word-break: break-all}</style>

    <cti:includeScript link="/JavaScript/yukon.support.logs.js"/>
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
        <cti:button nameKey="download" href="download?file=${file}" icon="icon-download"/>
        <cti:button id="pauseBtn" nameKey="pause" icon="icon-control-pause"/>
        <cti:button id="startBtn" nameKey="resume" icon="icon-control-play"/>
    </div>
    <div id="logOutput" class="monospace lite-container" style="overflow: auto;"></div>

</cti:standardPage>
