<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<cti:url var="url" value="/macsscheduler/schedules/innerView"/>

<cti:standardPage module="tools" page="scripts">
    <cti:includeScript link="/JavaScript/yukon.ami.macs.js" />
    <cti:url var="baseUrl" value="${url}">
        <cti:param name="sortBy" value="${param.sortBy}" />
        <cti:param name="descending" value="${param.descending}" />
    </cti:url>
    <div data-reloadable data-url="${baseUrl}">
        <jsp:include page="${url}"/>
    </div>
</cti:standardPage>