<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Log File View" module="blank">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/logging/" title="Log File Menu"  />
    <cti:crumbLink url="/logging/tail/${logFileName}?root=${logFilePath}" title="Log Tailer"  />
    &gt; Log View
</cti:breadCrumbs>

<div id="logInfo" style="width:600px;">
<tags:nameValueContainer altRowOn="true">
	<tags:nameValue name="File Name "><span id="fileName">${logFileName}</span></tags:nameValue>
	<tags:nameValue name="File Path "><span id="filePath">${logFilePath}</span></tags:nameValue>
	<tags:nameValue name="Last Modified "><span id="lastMod">${fileDateMod}</span></tags:nameValue>
	<tags:nameValue name="File Size "><span id="fileLength">${fileLength}</span> KB </tags:nameValue>
</tags:nameValueContainer>
</div>

<!-- logView.jsp shows the contents of a requested log file -->
<pre>
${logContents} 
</pre>

</cti:standardPage>
