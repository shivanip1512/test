<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meter.points">
    <cti:url var="url" value="/common/pao/${paoId}/points"/>
    <div data-url="${url}" data-static>
        <%@ include file="/WEB-INF/pages/common/pao/points.table.jsp" %>
    </div>
    <div id="page-buttons" class="dn">
        <cti:url var="download" value="/common/pao/${paoId}/download"/>
        <cti:button nameKey="download" href="${download}" icon="icon-page-white-excel"/>
    </div>
    <tags:simplePopup id="manual-entry-popup" title=""/>
</cti:standardPage>