<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="amr" page="meteringStart">

    <ct:widgetContainer identify="false">

        <div class="column-12-12">
            <div class="column one">
                <!-- This is needed so the dependencies for datetime are imported -->
                <div class="dn">
                    <dt:date name="CRONEXP_ONETIME_DATE"/>
               </div>
            
                <cti:checkRolesAndProperties value="DEVICE_DATA_MONITORING OUTAGE_PROCESSING TAMPER_FLAG_PROCESSING STATUS_POINT_MONITORING PORTER_RESPONSE_MONITORING VALIDATION_ENGINE">
                    <ct:widget bean="allMonitorsWidget"/>
                </cti:checkRolesAndProperties>

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
                    
                    <cti:checkRolesAndProperties value="RF_DATA_STREAMING_ENABLED">
                        <c:if test="${showOverloadedGatewaysWidget}">
                            <cti:msg2 key="yukon.web.widgets.overloadedGatewaysWidget.helpText" var="gatewayHelpText"/>
                            <ct:widget bean="overloadedGatewaysWidget" helpText="${gatewayHelpText}"/>
                        </c:if>
                    </cti:checkRolesAndProperties>
            </div>
        </div>
    </ct:widgetContainer>
</cti:standardPage>
