<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>

<cti:standardPage module="tools" page="scripts">
    <dt:pickerIncludes />
    
    <div id="js-error-msg" class="dn user-message error"></div>

    <c:set var="url" value="/macsscheduler/schedules/innerView" />
    <cti:url var="baseUrl" value="${url}" />
    <div id="scripts-container" data-url="${baseUrl}">
        <jsp:include page="${url}" />
    </div>

    <cti:checkRolesAndProperties value="MACS_SCRIPTS" level="CREATE">
        <div class="action-area">
            <cti:url var="createUrl" value="/macsscheduler/schedules/create" />
            <cti:button nameKey="create" icon="icon-plus-green" href="${createUrl}" />
        </div>
    </cti:checkRolesAndProperties>

    <cti:includeScript link="/resources/js/pages/yukon.ami.macs.js" />
</cti:standardPage>
