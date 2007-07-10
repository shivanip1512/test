<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Home Page" module="amr">
<cti:standardMenu menuSelection="deviceselection"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/csr/search" title="Device Selection"  />
    &gt; Device Details
</cti:breadCrumbs>

	<table class="widgetColumns">
		<tr>
			<td>
				<h2 style="display: inline;"><cti:deviceName deviceId="${deviceId}"></cti:deviceName></h2>
			</td>
			<td align="right">
				<amr:searchResultsLink></amr:searchResultsLink>
			</td>
		</tr>
	</table>
	
	<br />
	
	<div class="widgetColumns">
		<div class="left">
			<ct:widget height="75px" bean="meterInformationWidget" identify="false" deviceId="${deviceId}"/>
					
			<ct:widget bean="meterReadingsWidget" identify="false" deviceId="${deviceId}"/>

			<c:if test="${mspSupported}">
				<ct:widget height="185px" bean="accountInformationWidget" identify="false" deviceId="${deviceId}"/>
			</c:if>
		
			<cti:titledContainer title="Actions">
				<c:choose>
					<c:when test="${highBillSupported}">
						<c:url var="highBillUrl" value="/spring/csr/highBill">
							<c:param name="deviceId" value="${deviceId}" />
						</c:url>
						<a href="${highBillUrl}">High Bill Complaint</a><br/>
					</c:when>
					<c:otherwise>
						High Bill Complaint (not supported)<br/>
					</c:otherwise>
				</c:choose>
		        <cti:checkRole role="CommanderRole.ROLEID">
						<c:url var="commanderUrl" value="/apps/CommandDevice.jsp">
							<c:param name="deviceID" value="${deviceId}" />
						</c:url>
						<a href="${commanderUrl}">Manual Commander</a><br/>
		        </cti:checkRole>
			</cti:titledContainer>

		</div>
		<div class="right">

			<ct:widget bean="trendWidget" identify="false" deviceId="${deviceId}"/>
			<c:if test="${disconnectSupported}">
				<ct:widget bean="disconnectMeterWidget" identify="false" deviceId="${deviceId}"/>
			</c:if>
	
			<c:if test="${outageSupported}">
				<ct:widget bean="meterOutagesWidget" identify="false" deviceId="${deviceId}"/>
			</c:if>

		</div>
	</div>
	<div style="clear: both"></div>
	
	<ct:dataUpdateEnabler period="5"/>
</cti:standardPage>