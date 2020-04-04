<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="support" page="logTail">
<cti:includeScript link="/resources/js/pages/yukon.support.logs.js"/>
<style>
.logLine { white-space: pre-wrap; word-break: break-all; }
.spaced { margin-right: 50px; }
</style>
<div id="error-message" class="user-message error dn" >
    <cti:msg2 key="yukon.web.error.pageUpdateFailed"/>
</div>
<cti:url var="updateUrl" value='/support/logging/view/update?file=/${file}' />

<div id="page-buttons" class="dn">
    <cti:button nameKey="downloadZip" href="download?file=${file}&compressed=true" icon="icon-download"/>
    <cti:button nameKey="download" href="download?file=${file}" icon="icon-download"/>
    <cti:button id="pauseBtn" nameKey="pause" icon="icon-control-pause"/>
    <cti:button id="startBtn" nameKey="resume" icon="icon-control-play"/>
</div>

<input type="hidden" id="updateUrl" value="${updateUrl}"/>
<input type="hidden" id="file" value="${file}"/>

<div class="clearfix stacked">
    <span class="fwb spaced">${fn:escapeXml(logFile.name)}</span>
    
    <span class="name"><cti:msg2 key=".lastModified"/>:</span>&nbsp;
    <span class="value spaced" id="lastMod"><cti:msg2 key="yukon.common.loading"/></span>
    
    <span class="name"><cti:msg2 key=".fileSize"/>:</span>&nbsp;
    <span class="value spaced" id="fileLength"><cti:msg2 key="yukon.common.loading"/></span>
    
    <span class="name"><cti:msg2 key=".numberOfLines"/>:</span>&nbsp;
    <span class="value spaced"><input id="numLines" type="text" size="3" value="${numLines}"></span>
</div>
<div id="logOutput" class="clear monospace lite-container" style="overflow: auto;"></div>

</cti:standardPage>