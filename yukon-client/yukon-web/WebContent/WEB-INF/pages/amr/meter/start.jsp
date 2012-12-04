<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="meteringStart">
	
	<ct:widgetContainer identify="false">

		<table class="widgetColumns">
		
			<tr>
		
				<td class="widgetColumnCell first">
				
					<%-- OUTAGE MONITORS WIDGET --%>
					<ct:widget bean="outageMonitorsWidget"/>
					
					<%-- TAMPER FLAGS WIDGET --%>
					<ct:widget bean="tamperFlagMonitorsWidget"/>
                    
                    <%-- STATUS POINT MONITORS WIDGET --%>
                    <ct:widget bean="statusPointMonitorsWidget"/>
                    
                    <%-- PORTER RESPONSE MONITORS WIDGET --%>
                    <ct:widget bean="porterResponseMonitorsWidget"/>
                    
                    <%-- VALIDATION MONITORS WIDGET --%>
                    <ct:widget bean="validationMonitorsWidget"/>
					
					<%-- SCHEDULED GROUP REQUESTS WIDGET --%>
					<cti:checkRolesAndProperties value="SCHEDULER">
						<ct:widget bean="scheduledGroupRequestExecutionWidget"/>
					</cti:checkRolesAndProperties>
					
				</td>
				
				<td class="widgetColumnCell last">
                    <cti:msg2 key="yukon.web.widgets.meterSearchWidget.helpText" var="helpText"/>
					<ct:widget bean="meterSearchWidget" helpText="${helpText}" />
					<cti:checkRole role="DeviceActionsRole.ROLEID">
                        <ct:widget bean="systemActionsMenuWidget" />
                    </cti:checkRole>
				</td>
			
			</tr>
			
		</table>

	</ct:widgetContainer>

</cti:standardPage>
