<%@ taglib tagdir="/WEB-INF/tags" prefix="ct"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<jsp:directive.page import="com.cannontech.cbc.cache.CapControlCache" />
<link rel="StyleSheet" href="/WebConfig/yukon/styles/YukonGeneralStyles.css" type="text/css" />
<table id="Table" class="miniResultsTable resultsTable" align="center">
    <tr>
        <th colspan="4" style="color: black; font-size: 14">Feeder CapBank Information</th>
    </tr>
	<tr>
		<th>CapBank Name</th>
		<th>Display Order</th>
		<th>Close Order</th>
		<th>Trip Order</th>
	</tr>
	<c:forEach var="cap" items="${capBankList}">
		<tr class="<ct:alternateRow odd="" even="altRow"/>">
			<td>${cap.ccName}</td>
			<td>${cap.controlOrder}</td>
			<td>${cap.closeOrder}</td>
			<td>${cap.tripOrder}</td>
		</tr>
	</c:forEach>
</table>