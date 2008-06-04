<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>

<cti:standardPage title="Commander Results" module="amr">
<cti:standardMenu menuSelection="devicegroups|commander"/>
   	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
   	    <cti:crumbLink url="/spring/group/commander/groupProcessing" title="Group Processing" />
	    &gt; Command Executing
	</cti:breadCrumbs>

<table class="compactResultsTable">
<tr><th>Command</th><th>Devices</th><th>Success Count</th><th>Failure Count</th></tr>
<c:forEach items="${resultList}" var="result">
<tr>
<td><a href="resultDetail?resultKey=${result.key}">${result.command}</a></td>
<td><cti:msg key="${result.deviceCollection.description}"/></td>
<td><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/SUCCESS_COUNT"/></td>
<td><cti:dataUpdaterValue type="COMMANDER" identifier="${result.key}/FAILURE_COUNT"/></td>
</tr>
</c:forEach>
</table>



	
</cti:standardPage>