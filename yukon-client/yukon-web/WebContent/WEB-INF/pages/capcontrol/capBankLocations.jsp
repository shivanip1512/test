<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>

<cti:includeScript link="/JavaScript/itemPicker.js" />
<cti:includeScript link="/JavaScript/tableCreation.js" />
<cti:includeScript link="/JavaScript/paoPicker.js" />
<cti:includeCss link="/WebConfig/yukon/styles/itemPicker.css" />
        
<cti:standardPage title="Capbank Locations" module="capcontrol">

<script type="text/javascript" src="/capcontrol/js/cbc_funcs.js"></script>

<cti:standardMenu/>

<cti:breadCrumbs>
  	<cti:crumbLink url="subareas.jsp" title="Home" />
  	<cti:crumbLink url="${baseAddress}" title="${baseTitle}" />
    <cti:crumbLink url="${areaAddress}" title="${areaTitle}" />
    <cti:crumbLink url="${stationAddress}" title="${stationTitle}" />
    <cti:crumbLink title="${assetTitle}" />
</cti:breadCrumbs>


<div id="capBankTable">
		<table class="resultsTable" id="scheduledTable" width="90%" border="0" cellspacing="0" cellpadding="3" align="center">
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
				<tr class="altRow" >
					<td><c:out value="${bank.ccName}"/></td>
					<td><c:out value="${addList[i.index].serialNumber}"/></td>
					<td><c:out value="${bank.ccArea}"/></td>
					<td><c:out value="${addList[i.index].drivingDirections}"/></td>
				</tr>
				
			</c:forEach>
			</tbody>
		</table>
</div>

</cti:standardPage>