<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<cti:standardPage title="Log File View" module="blank">
<cti:standardMenu/>

<!-- logView.jsp shows the contents of a requested log file -->
<pre>
<b>
${logFileName}
</b>
${logContents} 
</pre>

</cti:standardPage>
