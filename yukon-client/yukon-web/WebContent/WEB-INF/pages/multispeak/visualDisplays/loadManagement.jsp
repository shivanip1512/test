<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="vdTags" tagdir="/WEB-INF/tags/visualDisplays"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<cti:msg var="pageTitle" key="yukon.web.modules.visualDisplays.loadManagement.pageTitle" />

<cti:standardPage title="${pageTitle}" module="visualDisplays">

	<cti:standardMenu menuSelection="visualDisplays|loadManagement"/>
	
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

	<%-- POWER SUPPLIER DATA TABLE: Current Load, Currnt IH, Load To Peak, Peak IH Load --%>
	<table class="resultsTable" style="width:800px;">
	
		<%-- header row --%>
		<tr>
			<th>Power Supplier</th>
			<th colspan="2"><cti:msg key="yukon.web.modules.visualDisplays.dataTypeEnum.CURRENT_LOAD"/></th>
			<th colspan="2"><cti:msg key="yukon.web.modules.visualDisplays.dataTypeEnum.CURRENT_IH"/></th>
			<th colspan="2"><cti:msg key="yukon.web.modules.visualDisplays.dataTypeEnum.LOAD_TO_PEAK"/></th>
			<th colspan="2"><cti:msg key="yukon.web.modules.visualDisplays.dataTypeEnum.PEAK_IH_LOAD"/></th>
		</tr>
		
		<%-- power supplier rows --%>
		<c:forEach var="powerSupplier" items="${powerSuppliers}">
			<tr class="<tags:alternateRow odd="" even="altRow"/>" >
				<td style="font-weight:bold;padding-left:10px;">${powerSupplier.powerSupplierType.description}</td>
				<vdTags:valueQuality pointId="${powerSupplier.currentLoadPointId}"/>
				<vdTags:valueQuality pointId="${powerSupplier.currentIhPointId}"/>
				<vdTags:valueQuality pointId="${powerSupplier.loadToPeakPointId}"/>
				<vdTags:valueQuality pointId="${powerSupplier.peakIhLoadPointId}"/>
			</tr>
		</c:forEach>	  
		
	</table>
	<br><br>
	
	<%-- HOURLY DATA TABLE: Current Day vs Peak Day --%>
	<c:set var="hourNumberCellWidth" value="60" />
	<c:set var="powerSuplierCellWidth" value="${fn:length(powerSuppliers) * 300 + hourNumberCellWidth}" />
	
	<table class="resultsTable" style="width:${powerSuplierCellWidth}px">

		<%-- header row (hr + power supplier names) --%>
		<tr>
			<th style="width:${hourNumberCellWidth}px" nowrap><cti:msg key="yukon.web.modules.visualDisplays.loadManagement.hrEndLabel"/></th>
			<c:forEach var="powerSupplier" items="${powerSuppliers}">
				<th colspan="4">${powerSupplier.powerSupplierType.description}</th>
			</c:forEach>	  
		</tr>
		
		<%-- current vs peak day per power supplier row--%>
		<c:set var="alternate" value="${true}" scope="request"/>
		<tr class="<tags:alternateRow odd="" even="altRow"/>">
		
			<td>&nbsp;</td>
		
			<c:forEach var="powerSupplier" items="${powerSuppliers}">
		
				<td colspan="2" align="center" nowrap>
					<span style="font-weight:bold;"><cti:msg key="yukon.web.modules.visualDisplays.loadManagement.currentDayLabel"/></span>
				</td>
				
				<td colspan="2" align="center" nowrap>
					<span style="font-weight:bold;"><cti:msg key="yukon.web.modules.visualDisplays.loadManagement.peakDayLabel"/></span>
					<br>
					<cti:pointValue format="{time|MM/dd/yyyy}" pointId="${powerSupplier.peakIhLoadPointId}"/>
					<br>
					<cti:msg key="yukon.web.modules.visualDisplays.loadManagement.hrLabel"/> <cti:pointValue format="{time|HH zz}" pointId="${powerSupplier.peakIhLoadPointId}"/>
				</td>
		
			</c:forEach>	  
		</tr>
		
		
		<%-- hourly data rows --%>
		<c:forEach var="i" begin="1" end="24">
			<tr class="<tags:alternateRow odd="" even="altRow"/>">
				
				<td align="right" style="font-weight:bold;">${i}</td>
			
				<c:forEach var="powerSupplier" items="${powerSuppliers}">
					
					<vdTags:valueQuality pointId="${powerSupplier.todayIntegratedHourlyDataPointIdList[i-1]}"/>
					<vdTags:valueQuality pointId="${powerSupplier.peakDayIntegratedHourlyDataPointIdList[i-1]}"/>
					
				</c:forEach>
			</tr>
			
		</c:forEach>
	  
    </table>
	    
</cti:standardPage>