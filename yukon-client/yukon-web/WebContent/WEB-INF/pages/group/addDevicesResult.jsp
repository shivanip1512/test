<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Bulk Device add to Group result" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	<c:url var="homeUrl" value="/spring/group/home">
		<c:param name="groupName" value="${group.fullName}" />
	</c:url>
	
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
	    <cti:crumbLink url="${homeUrl}" title="Groups Home" />
	    &gt; Bulk Device add to Group result
	</cti:breadCrumbs>
	
	<h2>Group: <a href="${homeUrl}">${fn:escapeXml(group.fullName)}</a></h2>

	<div>
		${resultInfo}
	</div>
	
	<div id="resultsDiv">
		<c:url var="updateUrl" value="/spring/group/editor/addDevicesResultUpdate" />
		<jsp:include flush="true" page="${updateUrl}" />
	</div>
	
</cti:standardPage>