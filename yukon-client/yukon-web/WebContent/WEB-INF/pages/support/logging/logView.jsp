<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="support" page="logView">

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
<cti:outputContent writable="${logContents}"/>
</pre>

</cti:standardPage>
