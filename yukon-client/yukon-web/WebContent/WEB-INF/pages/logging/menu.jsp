<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage title="Log File Menu" module="blank">
<cti:standardMenu/>

<!-- menu.jsp shows a menu of local and remote log filenames -->

<!-- Display and link to the local log files -->
<b><c:out value="Local Log Files"/></b><br>
<ul style="font-size: 10px">
<c:forEach var="file" items="${localLogList}">
<li>
${file}: <a href="view/${file}">view</a> |
<a href="download/${file}">download</a>
</li>
</c:forEach>
</ul>

</cti:standardPage>