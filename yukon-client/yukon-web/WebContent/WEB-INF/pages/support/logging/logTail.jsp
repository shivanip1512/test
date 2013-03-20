<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:url var="updateUrl" value='/support/logging/tail/update?file=/${file}' />

<cti:standardPage module="support" page="logTail">

<cti:includeScript link="/JavaScript/logUpdater.js"/>

<script type="text/javascript">
initiateCannonLogUpdate("${updateUrl}" ,2);
</script>

<div id="logInfo" style="width:600px;">
    <tags:nameValueContainer2 tableClass="stacked striped">
    	<tags:nameValue2 nameKey=".fileName"><span id="fileName">${logFileName}</span></tags:nameValue2>
    	<tags:nameValue2 nameKey=".lastModified"><span id="lastMod">${fileDateMod}</span></tags:nameValue2>
    	<tags:nameValue2 nameKey=".fileSize"><span id="fileLength">${fileLength}</span>&nbsp;<i:inline key=".fileSize.unit"/>&nbsp;</tags:nameValue2>
    	<tags:nameValue2 nameKey=".numberOfLinesShown"><span id="numLinesShown">${numLines}</span></tags:nameValue2>
    </tags:nameValueContainer2>
</div>

<form id="newLinesForm" class="stacked">
    <input type="hidden" id="file" name="file" value="${file}" size=5>
    <span id="numTail"><i:inline key=".numberOfLinesTailed"/>&nbsp;<input onchange="$('newLinesForm').action = 'tail?file=${file}'; submit();" id="numLines" name="numLines" value=${numLines} size=5 ></span>
    <span class="button-container">
        <cti:button id="submitLines" onclick="$('newLinesForm').action = 'tail?file=${file}'; submit();" nameKey="change"/>
        <cti:button id="pauseButton" onclick="startOrPauseUpdate();" type="button" nameKey="pause"/>
        <cti:button id="downloadButton" onclick="$('newLinesForm').action = 'download?file=${file}'; submit();" nameKey="download"/>
    </span>
</form>


<div id="logContents">
<div id="logOutput" class="monospace"><c:forEach var="line" items="${logContents}"><div>${line}
</div></c:forEach></div>
</div>
</cti:standardPage>
