<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="hardware.points">
    <cti:url var="url" value="/stars/operator/hardware/points">
        <cti:param name="deviceId" value="${deviceId}"/>
        <cti:param name="accountId" value="${accountId}"/>
    </cti:url>
    <div data-url="${url}" data-static>
        <%@ include file="/WEB-INF/pages/common/device/points.table.jsp" %>
    </div>
    <div id="page-buttons" class="dn">
        <cti:url var="download" value="/common/device/download">
            <cti:param name="deviceId" value="${deviceId}"/>
        </cti:url>
        <cti:button nameKey="download" href="${download}" icon="icon-page-white-excel"/>
    </div>
</cti:standardPage>