<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:standardPage module="amr" page="meterDetailVoltageAndTou">
    
	<div style="width:600px;">
        <%-- METER INFORMATION WIDGET --%>
        <tags:widgetContainer deviceId="${deviceId}">
           <tags:widget bean="meterInformationWidget" />
        </tags:widgetContainer>
            
        <%-- VOLTAGE --%>
        <br>
        <tags:widgetContainer deviceId="${deviceId}">
            <tags:widget bean="simpleAttributesWidget" title="Voltage" attributes="VOLTAGE,MINIMUM_VOLTAGE,MAXIMUM_VOLTAGE" />
        </tags:widgetContainer>
            
        <%-- TOU SCHEDULE --%>
        <c:if test="${not empty schedules && fn:length(schedules) > 0}">
            <br>
            <tags:widgetContainer deviceId="${deviceId}">
                <tags:widget bean="touScheduleWidget" />
            </tags:widgetContainer>
        </c:if>
    
    </div>

</cti:standardPage>
