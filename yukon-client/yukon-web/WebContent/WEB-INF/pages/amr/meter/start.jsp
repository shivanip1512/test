<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage title="Meter Home Page" module="amr">
	<cti:standardMenu menuSelection="meters" />
	<cti:breadCrumbs>
		<cti:crumbLink url="/operator/Operations.jsp" title="Operations Home" />
		<cti:crumbLink title="Metering" />
</cti:breadCrumbs>

	<h2>Metering</h2>
	<br>
	
	<ct:widgetContainer identify="false">

		<table class="widgetColumns">
		
			<tr>
		
				<td class="widgetColumnCell" valign="top">
					<cti:msg var="outagesWidgetPopupInfoText" key="yukon.web.modules.amr.outageMonitorsWidget.popupInfo"/>
					<ct:widget bean="outageMonitorsWidget" helpText="${outagesWidgetPopupInfoText}"/>
				</td>
				
				<td class="widgetColumnCell" valign="top">
					<ct:widget bean="meterSearchWidget" />
				</td>
			
			</tr>
			
		</table>

	</ct:widgetContainer>

</cti:standardPage>
