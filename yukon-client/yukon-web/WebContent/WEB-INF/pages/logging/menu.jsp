<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<cti:standardPage title="Log File Menu" module="blank">
<cti:standardMenu/>

<!-- menu.jsp shows a menu of local and remote log filenames -->

<!-- Display and link to the local log files -->
<b><c:out value="Local Log Files"/></b><br>
<ul>
<c:forEach var="file" items="${localLogList}">
<li>
<a href="<c:out value="view/${file}"/>"><c:out value="${file}"/></a>
<a href="<c:out value="download/${file}" />"> (Download)</a>
</li>
</c:forEach>
</ul>

<!-- Display and link to the remote log files  -->
<b><c:out value="Remote Log Files"/></b><br>
<ul>
<c:forEach var="file" items="${remoteLogList}">
<li>
<a href="<c:out value="view/${file}"/>"><c:out value="${file}"/></a>    
<a href="<c:out value="download/${file}"/>"> (Download)</a>
</li>
</c:forEach>
</ul>

</cti:standardPage>