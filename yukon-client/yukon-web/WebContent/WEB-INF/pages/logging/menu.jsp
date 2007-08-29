<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<cti:standardPage title="Log File Menu" module="blank">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    &gt; Log File Menu
</cti:breadCrumbs>

<h4>Version Details</h4>
&nbsp&nbsp&nbsp ${versionDetails}

<h4>DB Connection</h4>
&nbsp&nbsp&nbsp JDBC URL: ${dbUrl}<br />
&nbsp&nbsp&nbsp JDBC User: ${dbUser}<br />

<h4>SortBy:: <a href="?root=${oldStatePath}&sortType=alphabetic" id="alphabetic" name="alphabetic">Alphabetic</a> or <a href="?root=${oldStatePath}&sortType=date" id="date" name="alphabetic">Date</a></h4>

<!-- Display and link to the local log files -->
<b id="dir" class="logLable">Directories</b><br/>
<c:forEach var="dirName" items="${dirList}">
	&nbsp&nbsp&nbsp<a href="?root=${oldStatePath}${dirName}&sortType=${oldStateSort}">${dirName}</a><br/>
</c:forEach>
<br />

<!-- Display and link to the local log files -->
<c:forEach var="logSection" items="${localLogList}">
	<b id="logLable" class="logLable">${logSection.key}</b><br/>
	<c:forEach var="fileName" items="${logSection.value}">
		&nbsp&nbsp&nbsp<a href='tail/<c:url value='${fileName}'/>?root=${oldStatePath}' >${fileName}</a><br/>
	</c:forEach>
	<br />
</c:forEach>

</cti:standardPage>