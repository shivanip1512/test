<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<c:url var="updateUrl" value='/logging/tail/update/${logFileName}?root=${logFilePath}' />


<cti:standardPage title="Log File View" module="blank">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/logging/" title="Log File Menu"  />
    &gt; Log Tailer
</cti:breadCrumbs>

<cti:includeScript link="/JavaScript/scriptaculous/effects.js"/>
<cti:includeScript link="/JavaScript/logUpdater.js"/>

<script type="text/javascript">
initiateCannonLogUpdate("${updateUrl}" ,2);
</script>

<div id="logInfo" style="width:600px;">
<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="File Name "><span id="fileName">${logFileName}</span></tags:nameValue>
	<tags:nameValue name="File Path "><span id="filePath">${logFilePath}</span></tags:nameValue>
	<tags:nameValue name="Last Modified "><span id="lastMod">${fileDateMod}</span></tags:nameValue>
	<tags:nameValue name="File Size "><span id="fileLength">${fileLength}</span> KB </tags:nameValue>
	<tags:nameValue name="Number of Lines Shown "><span id="numLinesShown">${numLines}</span></tags:nameValue>
</tags:nameValueContainer>
</div>

<br />

<form id="newLinesForm">
<input type="hidden" name="root" value="${logFilePath}" size=5 />
<span id="numTail" name="numTail">number of lines tailed = <input onchange="$('newLinesForm').action = '../tail/${logFileName}?root=${logFilePath}'; submit();" id="numLines" name="numLines" value=${numLines} size=5 /></span>
&nbsp&nbsp<button id="submitLines" onclick="$('newLinesForm').action = '../tail/${logFileName}?root=${logFilePath}'; submit();">Change</button>
&nbsp&nbsp<button id="pauseButton" onclick="startOrPauseUpdate();" type="button">Pause</button>
&nbsp&nbsp<button id="viewButton"" onclick="$('newLinesForm').action = '../view/${logFileName}?root=${logFilePath}'; submit();" type="button">View</button>
&nbsp&nbsp<button id="downloadButton"" onclick="$('newLinesForm').action = '../download/${logFileName}?root=${logFilePath}'; submit();" type="button">Download</button>
</form>


<div id="cannonUpdaterErrorDiv" style="display: none; position: fixed; bottom: 0; left: 0; width: auto; background: red; color: white; font-weight: bold">
Connection to server has been lost
</div>

<div id="logContents">
<pre id="logOutput"><c:forEach var="line" items="${logContents}"><div>${line}</div></c:forEach></pre>
</div>
</cti:standardPage>
