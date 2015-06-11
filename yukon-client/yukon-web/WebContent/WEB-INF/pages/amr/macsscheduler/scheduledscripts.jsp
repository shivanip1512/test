<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="url" value="/macsscheduler/schedules/innerView"/>

<cti:standardPage module="tools" page="scripts">
    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
    <cti:url var="baseUrl" value="${url}">
        <cti:param name="sortBy" value="${param.sortBy}" />
        <cti:param name="descending" value="${param.descending}" />
    </cti:url>
    <div id="scripts-container" data-url="${baseUrl}">
        <jsp:include page="${url}"/>
    </div>
</cti:standardPage>
