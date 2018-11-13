<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<c:set var="deviceId" value="${widgetParameters.deviceId}"/>
<c:if test="${empty deviceId}">
    <c:set var="deviceId" value="${widgetParameters.controlAreaOrProgramOrScenarioId}"/>
</c:if>

<cti:deviceName deviceId="${deviceId}" var="deviceName"/>
${deviceName}