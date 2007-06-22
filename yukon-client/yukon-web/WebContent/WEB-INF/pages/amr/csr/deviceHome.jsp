<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Home Page" module="operations">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/csr/search" title="Device selection"  />
    &gt; Device page
</cti:breadCrumbs>

	<table class="widgetColumns">
		<tr>
			<td>
				<h2 style="display: inline;">Device Name: <cti:deviceName deviceId="${deviceId}"></cti:deviceName></h2>
			</td>
			<td align="right">
				<amr:searchResultsLink></amr:searchResultsLink>
			</td>
		</tr>
	</table>
	
	<br />
	
	<div class="widgetColumns">
		<div class="left">
			
			<c:if test="${mspSupported}">
				<ct:widget height="100px" bean="accountInformationWidget" identify="false" deviceId="${deviceId}"/>
			</c:if>
			
			<ct:widget height="65px" bean="meterInformationWidget" identify="false" deviceId="${deviceId}"/>
		
			<cti:titledContainer title="Meter Readings">
				<br/><br/><br/><br/><center>TBD</center><br/><br/><br/><br/>
			</cti:titledContainer>
		
		
			<cti:titledContainer title="Actions">
				Move Out<br/>
				High Bill Complaint (not supported)<br/>
				Meter Change Out<br/>
				STARS<br/>
				Read History<br/>
			</cti:titledContainer>

		</div>
		<div class="right">

			<ct:widget height="300px" bean="trendWidget" identify="false" deviceId="${deviceId}"/>

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