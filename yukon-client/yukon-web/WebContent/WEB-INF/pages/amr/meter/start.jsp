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
				
					<%-- OUTAGE MONITORS WIDGET --%>
					<ct:widget bean="outageMonitorsWidget" helpText="${outageMonitorsWidgetPopupInfoText}"/>
					
					<%-- TAMPER FLAGS WIDGET --%>
					<ct:widget bean="tamperFlagMonitorsWidget" helpText="${tamperFlagMonitorsWidgetPopupInfoText}"/>
                    
                    <%-- VALIDATION MONITORS WIDGET --%>
                    <cti:msg var="validationMonitorsWidgetPopupInfoText" key="yukon.web.modules.common.vee.widget.popupInfo"/>
                    <ct:widget bean="validationMonitorsWidget" helpText="${validationMonitorsWidgetPopupInfoText}"/>
					
					<%-- SCHEDULED GROUP REQUESTS WIDGET --%>
					<cti:checkRolesAndProperties value="SCHEDULER">
						<ct:widget bean="scheduledGroupRequstExecutionWidget" helpText="${scheduledGroupRequstExecutionWidgetPopupInfoText}"/>
					</cti:checkRolesAndProperties>
					
				</td>
				
				<td class="widgetColumnCell" valign="top">
					<ct:widget bean="meterSearchWidget" />
					<cti:checkRolesAndProperties value="PHASE_DETECT">
                        <ct:widget bean="systemActionsMenuWidget" />
                    </cti:checkRolesAndProperties>
				</td>
			
			</tr>
			
		</table>

	</ct:widgetContainer>

</cti:standardPage>
