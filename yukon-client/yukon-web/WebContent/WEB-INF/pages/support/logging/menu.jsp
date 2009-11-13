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

<h4>Sort By: <a href="?file=${file}&sortType=alphabetic" id="alphabetic" name="alphabetic">Alphabetic</a> or <a href="?file=${file}&sortType=date" id="date" name="alphabetic">Date</a></h4>

<!-- Display and link to the local log files -->
<h3 class="indentedElementHeading">Directories</h3>
<div>
<c:forEach var="dirName" items="${dirList}">
	<a href="?file=${file}${dirName}/&sortType=${oldStateSort}">${dirName}/</a><br/>
</c:forEach>
</div>

<!-- Display and link to the local log files -->
<c:forEach var="logSection" items="${localLogList}">
	<h3 class="indentedElementHeading">${logSection.key}</h3>
    <div>
	<c:forEach var="fileName" items="${logSection.value}">
		<a href="tail?file=${file}${fileName}" >${fileName}</a><br/>
	</c:forEach>
	</div>
</c:forEach>

</cti:standardPage>