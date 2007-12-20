<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="Device Profile Page" module="amr">
	<cti:standardMenu menuSelection="deviceselection" />
	<cti:breadCrumbs>
	    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	    <cti:crumbLink url="/spring/csr/search" title="Device Selection"  />
	    <cti:crumbLink url="/spring/csr/home?deviceId=${deviceId}" title="Device Detail"  />
	    &gt; Profile
	</cti:breadCrumbs>
	
	<h2>Profile</h2>
	<br>
	
	<div style="width: 50%;">
		<ct:widgetContainer deviceId="${deviceId}">
			<ct:widget bean="meterInformationWidget" />
			
			<ct:widget bean="profileWidget" />
			
			<ct:widget bean="peakReportWidget" />
		</ct:widgetContainer>
	</div>

</cti:standardPage>
