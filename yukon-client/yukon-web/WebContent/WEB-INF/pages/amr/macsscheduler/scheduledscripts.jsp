<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>

<cti:standardPage module="tools" page="scripts">
<dt:pickerIncludes />

<c:set var="url" value="/macsscheduler/schedules/innerView"/>
    <cti:url var="baseUrl" value="${url}"/>
    <div id="scripts-container" data-url="${baseUrl}">
        <jsp:include page="${url}"/>
    </div>
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
</cti:standardPage>
