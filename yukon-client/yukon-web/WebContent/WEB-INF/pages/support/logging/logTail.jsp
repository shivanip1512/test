<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<cti:url var="updateUrl" value='/logging/tail/update?file=/${file}' />


<cti:standardPage module="support">
<cti:standardMenu menuSelection="logs"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/support/logging/menu" title="Log File Menu"  />
    <cti:crumbLink>Log Tailer</cti:crumbLink>
</cti:breadCrumbs>

<cti:includeScript link="/JavaScript/scriptaculous/effects.js"/>
<cti:includeScript link="/JavaScript/logUpdater.js"/>

<script type="text/javascript">
initiateCannonLogUpdate("${updateUrl}" ,2);
</script>

<div id="logInfo" style="width:600px;">
<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="File Name "><span id="fileName">${logFileName}</span></tags:nameValue>
	<tags:nameValue name="Last Modified "><span id="lastMod">${fileDateMod}</span></tags:nameValue>
	<tags:nameValue name="File Size "><span id="fileLength">${fileLength}</span> KB </tags:nameValue>
	<tags:nameValue name="Number of Lines Shown "><span id="numLinesShown">${numLines}</span></tags:nameValue>
</tags:nameValueContainer>
</div>

<br />

<form id="newLinesForm">
<input type="hidden" id="file" name="file" value="${file}" size=5 />
<span id="numTail" name="numTail">number of lines tailed = <input onchange="$('newLinesForm').action = 'tail?file=${file}'; submit();" id="numLines" name="numLines" value=${numLines} size=5 /></span>
&nbsp&nbsp<button id="submitLines" onclick="$('newLinesForm').action = 'tail?file=${file}'; submit();">Change</button>
&nbsp&nbsp<button id="pauseButton" onclick="startOrPauseUpdate();" type="button">Pause</button>
&nbsp&nbsp<button id="viewButton"" onclick="$('newLinesForm').action = 'view?file=${file}'; submit();" type="button">View</button>
&nbsp&nbsp<button id="downloadButton"" onclick="$('newLinesForm').action = 'download?file=${file}'; submit();" type="button">Download</button>
</form>


<div id="cannonUpdaterErrorDiv" style="display: none; position: fixed; bottom: 0; left: 0; width: auto; background: red; color: white; font-weight: bold">
Connection to server has been lost
</div>

<div id="logContents">
<pre id="logOutput"><c:forEach var="line" items="${logContents}"><div>${line}</div></c:forEach></pre>
</div>
</cti:standardPage>
