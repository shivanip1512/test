<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<cti:url var="updateUrl" value='/spring/support/logging/tail/update?file=/${file}' />

<cti:standardPage module="support" page="logTail">

<cti:includeScript link="/JavaScript/logUpdater.js"/>

<script type="text/javascript">
initiateCannonLogUpdate("${updateUrl}" ,2);
</script>

<div id="logInfo" style="width:600px;">
<tags:nameValueContainer altRowOn="true">
    <cti:msg2 var="fileNameLabel" key=".fileName"/>
	<tags:nameValue name="${fileNameLabel} "><span id="fileName">${logFileName}</span></tags:nameValue>
    <cti:msg2 var="lastModifiedLabel" key=".lastModified"/>
	<tags:nameValue name="${lastModifiedLabel} "><span id="lastMod">${fileDateMod}</span></tags:nameValue>
    <cti:msg2 var="fileSizeLabel" key=".fileSize"/>
	<tags:nameValue name="${fileSizeLabel} "><span id="fileLength">${fileLength}</span> <i:inline key=".fileSize.unit"/></tags:nameValue>
    <cti:msg2 var="numberOfLinesShownLabel" key=".numberOfLinesShown"/>
	<tags:nameValue name="${numberOfLinesShownLabel} "><span id="numLinesShown">${numLines}</span></tags:nameValue>
</tags:nameValueContainer>
</div>

<br>

<form id="newLinesForm">
<input type="hidden" id="file" name="file" value="${file}" size=5>
<span id="numTail"><i:inline key=".numberOfLinesTailed"/> = <input onchange="$('newLinesForm').action = 'tail?file=${file}'; submit();" id="numLines" name="numLines" value=${numLines} size=5 ></span>
&nbsp&nbsp<button id="submitLines" onclick="$('newLinesForm').action = 'tail?file=${file}'; submit();"><i:inline key=".changeButton"/></button>
&nbsp&nbsp<button id="pauseButton" onclick="startOrPauseUpdate();" type="button"><i:inline key=".pauseButton"/></button>
&nbsp&nbsp<button id="downloadButton" onclick="$('newLinesForm').action = 'download?file=${file}'; submit();" type="button"><i:inline key=".downloadButton"/></button>
</form>


<div id="logContents">
<div id="logOutput"><c:forEach var="line" items="${logContents}"><div>${line}
</div></c:forEach></div>
</div>
</cti:standardPage>
