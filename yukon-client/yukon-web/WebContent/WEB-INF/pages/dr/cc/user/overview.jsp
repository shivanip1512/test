<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="cc.user.overview">
    
    <tags:sectionContainer2 nameKey="currentEvents">
        <c:set var="events" value="${currentEvents}"/>
        <%@ include file="eventTable.jsp" %>
    </tags:sectionContainer2>
    
    <tags:sectionContainer2 nameKey="pendingEvents">
        <c:set var="events" value="${pendingEvents}"/>
        <%@ include file="eventTable.jsp" %>
    </tags:sectionContainer2>
    
    <tags:sectionContainer2 nameKey="recentEvents">
        <c:set var="events" value="${recentEvents}"/>
        <%@ include file="eventTable.jsp" %>
    </tags:sectionContainer2>
    
</cti:standardPage>