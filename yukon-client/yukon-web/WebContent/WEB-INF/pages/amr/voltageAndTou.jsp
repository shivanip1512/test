<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="amr" page="meterDetailVoltageAndTou">
    
	<div style="width:600px;">
        <%-- METER INFORMATION WIDGET --%>
        <tags:widgetContainer deviceId="${deviceId}">
           <tags:widget bean="meterInformation" />
        </tags:widgetContainer>
            
        <%-- VOLTAGE --%>
        <tags:widgetContainer deviceId="${deviceId}">
            <c:choose>
                <c:when test="${threePhaseVoltageOrCurrentSupported}">
                    <cti:msg2 var="widgetTitle" key=".touSimpleAttributeWidgetTitle.voltageAndCurrent"/>
                    <tags:widget bean="simpleAttributes" title="${widgetTitle}" 
                                 attributes="VOLTAGE_PHASE_A,VOLTAGE_PHASE_B,VOLTAGE_PHASE_C,CURRENT_PHASE_A,CURRENT_PHASE_B,CURRENT_PHASE_C,NEUTRAL_CURRENT"/>
                </c:when>
                <c:otherwise>
                    <cti:msg2 var="widgetTitle" key=".touSimpleAttributeWidgetTitle.voltage"/>
                    <tags:widget bean="simpleAttributes" title="${widgetTitle}" 
                                 attributes="VOLTAGE,MINIMUM_VOLTAGE,MAXIMUM_VOLTAGE"/>
                </c:otherwise>
            </c:choose>
        </tags:widgetContainer>
            
        <%-- TOU SCHEDULE --%>
        <c:if test="${not empty schedules && fn:length(schedules) > 0}">
            <tags:widgetContainer deviceId="${deviceId}">
                <tags:widget bean="touSchedule" />
            </tags:widgetContainer>
        </c:if>
    </div>
</cti:standardPage>
