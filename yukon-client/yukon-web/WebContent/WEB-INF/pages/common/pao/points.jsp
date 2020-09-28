<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meter.points">

    <div id="page-actions" class="dn">
        <cm:pointCreationOption paoId="${paoId}"/>
        <cti:url var="download" value="/common/pao/${paoId}/download"/>
        <cm:dropdownOption key="yukon.common.download" icon="icon-page-white-excel" href="${download}"/>
    </div>

    <cti:url var="url" value="/common/pao/${paoId}/points"/>
    <div data-url="${url}" data-static>
        <%@ include file="/WEB-INF/pages/common/pao/points.table.jsp" %>
    </div>
    <dt:pickerIncludes/>
    <tags:simplePopup id="manual-entry-popup" title=""/>
</cti:standardPage>