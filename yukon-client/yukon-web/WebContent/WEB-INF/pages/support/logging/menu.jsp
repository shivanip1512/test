<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<cti:standardPage module="support">
<cti:standardMenu menuSelection="logs"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/support/" title="Support" />
    <cti:crumbLink>Log File Menu</cti:crumbLink>
</cti:breadCrumbs>

<h4>SortBy:: <a href="?file=${file}&sortType=alphabetic" id="alphabetic" name="alphabetic">Alphabetic</a> or <a href="?file=${file}&sortType=date" id="date" name="alphabetic">Date</a></h4>

<!-- Display and link to the local log files -->
<b id="dir" class="logLable">Directories</b><br/>
<c:forEach var="dirName" items="${dirList}">
	&nbsp&nbsp&nbsp<a href="?file=${file}${dirName}&sortType=${oldStateSort}">${dirName}</a><br/>
</c:forEach>
<br />

<!-- Display and link to the local log files -->
<c:forEach var="logSection" items="${localLogList}">
	<b id="logLable" class="logLable">${logSection.key}</b><br/>
	<c:forEach var="fileName" items="${logSection.value}">
		&nbsp&nbsp&nbsp<a href="tail?file=${file}${fileName}" >${fileName}</a><br/>
	</c:forEach>
	<br />
</c:forEach>

</cti:standardPage>