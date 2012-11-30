<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="vdTags" tagdir="/WEB-INF/tags/visualDisplays"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:msg var="pageTitle" key="yukon.web.modules.visualDisplays.probabilityForPeakLoad.pageTitle" />

<cti:standardPage title="${pageTitle}" module="visualDisplays">

	<cti:standardMenu menuSelection="visualDisplays|probabilityForPeakLoad"/>
	
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink title="${pageTitle}"  />
	</cti:breadCrumbs>
	
	<cti:includeScript link="/JavaScript/visualDisplays.js"/>
	
	<h2>${pageTitle}</h2>
    <br>
    
    <%-- LAST TRANSMITTED --%>
    <div class="fwb">
    	Last Transmitted: <span id="lastTransmitted"><cti:formatDate type="BOTH" value="${now}"/></span>
    </div>
    <br>

	<%-- HOURLY DATA TABLE: Current Day vs Peak Day --%>
	<c:set var="hourNumberCellWidth" value="60" />
	<c:set var="powerSuplierCellWidth" value="${fn:length(powerSuppliers) * 200 + hourNumberCellWidth}" />
	<table class="resultsTable" style="width:${powerSuplierCellWidth}px">

		<%-- header row (hr + power supplier names) --%>
		<tr>
			<th style="width:${hourNumberCellWidth}px" nowrap>HR End</th>
			<c:forEach var="powerSupplier" items="${powerSuppliers}">
				<th colspan="2">${powerSupplier.powerSupplierType.description}</th>
			</c:forEach>	  
		</tr>
		
		<%-- current vs peak day per power supplier row--%>
		<c:set var="alternate" value="${true}" scope="request"/>
		<tr class="<tags:alternateRow odd="" even="altRow"/>">
		
			<td>&nbsp;</td>
		
			<c:forEach var="powerSupplier" items="${powerSuppliers}">
			
				<td align="center" nowrap>
					<span style="font-weight:bold;"><cti:msg key="yukon.web.modules.visualDisplays.probabilityForPeakLoad.todayLabel"/></span>
					<br>
					<cti:pointValue format="{time|MM/dd/yyyy}" pointId="${powerSupplier.todayLoadControlPredictionPointIdList[0]}"/>
				</td>
				
				<td align="center" nowrap>
					<span style="font-weight:bold;"><cti:msg key="yukon.web.modules.visualDisplays.probabilityForPeakLoad.tomorrowLabel"/></span>
					<br>
					<cti:pointValue format="{time|MM/dd/yyyy}" pointId="${powerSupplier.tomorrowLoadControlPredictionPointIdList[0]}"/>
				</td>
				
			</c:forEach>	
			  
		</tr>
		
		
		<%-- hourly data rows --%>
		<c:forEach var="i" begin="1" end="24">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				
				<td align="right" style="font-weight:bold;">${i}</td>
			
				<c:forEach var="powerSupplier" items="${powerSuppliers}">
					
					<vdTags:valueTodayTomorrowPercentages todayPointId="${powerSupplier.todayLoadControlPredictionPointIdList[i-1]}" 
														  tomorrowPointId="${powerSupplier.tomorrowLoadControlPredictionPointIdList[i-1]}" />
					
				</c:forEach>
			</tr>
			
		</c:forEach>
	  
    </table>

</cti:standardPage>