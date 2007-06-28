<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Home Page" module="amr">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/csr/search" title="Device selection"  />
    &gt; Device page
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
			<ct:widget bean="meterReadingsWidget" identify="false" deviceId="${deviceId}"/>

			<ct:widget height="75px" bean="meterInformationWidget" identify="false" deviceId="${deviceId}"/>
			
			<c:if test="${mspSupported}">
				<ct:widget height="185px" bean="accountInformationWidget" identify="false" deviceId="${deviceId}"/>
			</c:if>
		
			<cti:titledContainer title="Actions">
				Move Out<br/>
				
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
				Meter Change Out<br/>
				STARS<br/>
				Read History<br/>
			</cti:titledContainer>

		</div>
		<div class="right">

			<ct:widget bean="trendWidget" identify="false" deviceId="${deviceId}"/>

			<ct:widget bean="disconnectMeterWidget" identify="false" deviceId="${deviceId}"/>


			<cti:titledContainer title="Profile">
				<br/><br/><br/><br/><center>TBD</center><br/><br/><br/><br/>
			</cti:titledContainer>

			<cti:titledContainer title="Outages">
				<br/><br/><br/><br/><center>TBD</center><br/><br/><br/><br/>
			</cti:titledContainer>
		</div>
	</div>
	<div style="clear: both"></div>
	
	<ct:dataUpdateEnabler period="5"/>
</cti:standardPage>