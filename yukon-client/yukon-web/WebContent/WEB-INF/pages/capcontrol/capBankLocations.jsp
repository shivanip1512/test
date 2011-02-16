<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:includeScript link="/JavaScript/tableCreation.js" />
        
<cti:standardPage title="Capbank Locations" module="capcontrol">

<script type="text/javascript" src="/capcontrol/js/capcontrolGeneral.js"></script>

<cti:standardMenu/>

<cti:breadCrumbs>
  	<cti:crumbLink url="/spring/capcontrol/tier/areas" title="Home" />
  	<cti:crumbLink url="${baseAddress}" title="${baseTitle}" />
    <cti:crumbLink url="${areaAddress}" title="${areaTitle}" />
    <cti:crumbLink url="${stationAddress}" title="${stationTitle}" />
    <cti:crumbLink title="${assetTitle}" />
</cti:breadCrumbs>


<div id="capBankTable">
		<table class="resultsTable" align="center">
		<thead>
			<tr id="header">
				<th>Cap Bank Name</th>
				<th>CBC Serial Number</th>
				<th>Address</th>
				<th>Driving Directions</th>
			</tr>
		</thead>
			<tbody id="tableBody">
			<c:forEach var="bank" items="${capBankList}" varStatus="i">
				<tr class="<tags:alternateRow odd="tableCell" even="altTableCell"/>">
					<td><c:out value="${bank.name}"/></td>
					<td><c:out value="${bank.serialNumber}"/></td>
					<td><c:out value="${bank.address}"/></td>
					<td><c:out value="${bank.directions}"/></td>
				</tr>
				
			</c:forEach>
			</tbody>
		</table>
</div>

</cti:standardPage>