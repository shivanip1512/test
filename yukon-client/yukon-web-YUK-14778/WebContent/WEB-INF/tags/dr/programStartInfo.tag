<%@ attribute name="page" required="true" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<p>
<c:if test="${backingBean.startNow}">
    <cti:msg key="yukon.web.modules.dr.program.${pageScope.page}.startingNow"/>
</c:if>
<c:if test="${!backingBean.startNow}">
    <cti:formatDate var="formattedStartDate" type="BOTH" value="${backingBean.startDate}"/>
    <cti:msg key="yukon.web.modules.dr.program.startProgram.startingAt"
        argument="${formattedStartDate}"/>
</c:if>
</p>

<p>
<c:if test="${backingBean.scheduleStop}">
    <cti:formatDate var="formattedStopDate" type="BOTH" value="${backingBean.stopDate}"/>
    <cti:msg key="yukon.web.modules.dr.program.${pageScope.page}.stoppingAt"
        argument="${formattedStopDate}"/>
</c:if>
<c:if test="${!backingBean.scheduleStop}">
    <cti:msg key="yukon.web.modules.dr.program.${pageScope.page}.stopNotScheduled"/>
</c:if>
</p><br>
