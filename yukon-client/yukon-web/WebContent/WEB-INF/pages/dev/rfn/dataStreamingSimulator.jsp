<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dev" page="rfnTest.viewDataStreamingSimulator">
    <c:if test="${simulatorRunning}">
        <cti:button label="Stop Simulator" href="stopDataStreamingSimulator"/>
    </c:if>
    <c:if test="${not simulatorRunning}">
        <cti:button label="Start Simulator" href="startDataStreamingSimulator"/>
    </c:if>
</cti:standardPage>