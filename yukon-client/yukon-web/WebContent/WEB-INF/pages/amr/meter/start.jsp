<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="meteringStart">
	
	<ct:widgetContainer identify="false">

		<table class="widgetColumns">
		
			<tr>
		
				<td class="widgetColumnCell">
				
					<%-- OUTAGE MONITORS WIDGET --%>
					<ct:widget bean="outageMonitors"/>
					
					<%-- TAMPER FLAGS WIDGET --%>
					<ct:widget bean="tamperFlagMonitors"/>
                    
                    <%-- STATUS POINT MONITORS WIDGET --%>
                    <ct:widget bean="statusPointMonitors"/>
                    
                    <%-- PORTER RESPONSE MONITORS WIDGET --%>
                    <ct:widget bean="porterResponseMonitors"/>
                    
                    <%-- VALIDATION MONITORS WIDGET --%>
                    <ct:widget bean="validationMonitors"/>
					
					<%-- SCHEDULED GROUP REQUESTS WIDGET --%>
					<cti:checkRolesAndProperties value="SCHEDULER">
						<ct:widget bean="scheduledGroupRequestExecution"/>
					</cti:checkRolesAndProperties>
					
				</td>
				
				<td class="widgetColumnCell">
					<ct:widget bean="meterSearch" />
					<cti:checkRole role="DeviceActionsRole.ROLEID">
                        <ct:widget bean="systemActionsMenu" />
                    </cti:checkRole>
				</td>
			
			</tr>
			
		</table>

	</ct:widgetContainer>

</cti:standardPage>