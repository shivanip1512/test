<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage title="Log File Menu" module="blank">
<cti:standardMenu/>

<h3>DB Connection</h3>
JDBC URL: ${dbUrl}<br>
JDBC User: ${dbUser}<br>

<!-- Display and link to the local log files -->
<h3>Local Log Files</h3>
<ul style="font-size: 10px">
<c:forEach var="file" items="${localLogList}">
<li>
${file}: <a href="view/${file}">view</a> |
<a href="download/${file}">download</a>
</li>
</c:forEach>
</ul>

</cti:standardPage>