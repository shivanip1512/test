<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="meteringStart">

	<ct:widgetContainer identify="false">

		<div class="column_12_12">
            <div class="column one">

                <ct:widget bean="allMonitorsWidget"/>

					<%-- SCHEDULED GROUP REQUESTS WIDGET --%>
					<cti:checkRolesAndProperties value="SCHEDULER">
						<ct:widget bean="scheduledGroupRequestExecutionWidget"/>
					</cti:checkRolesAndProperties>
					
            </div>
            <div class="column two nogutter">
                    <cti:msg2 key="yukon.web.widgets.meterSearchWidget.helpText" var="helpText"/>
					<ct:widget bean="meterSearchWidget" helpText="${helpText}" />
					<cti:checkRolesAndProperties value="DEVICE_ACTIONS">
                        <ct:widget bean="systemActionsMenuWidget" />
                    </cti:checkRolesAndProperties>
            </div>
        </div>
    </ct:widgetContainer>
</cti:standardPage>
