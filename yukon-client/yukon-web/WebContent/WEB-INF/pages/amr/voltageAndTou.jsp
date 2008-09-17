
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<cti:standardPage title="Voltage & TOU" module="amr">

    <cti:standardMenu menuSelection="meters" />
    
    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
        <cti:crumbLink url="/spring/meter/search" title="Meters"  />
        <cti:crumbLink url="/spring/meter/home?deviceId=${deviceId}">
            <cti:deviceName deviceId="${deviceId}"></cti:deviceName>
        </cti:crumbLink>
        &gt; Voltage &amp; TOU
    </cti:breadCrumbs>
    
    <h2>Voltage &amp; TOU</h2>
    <br>
    
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
