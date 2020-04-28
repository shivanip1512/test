<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="operator" page="${page}">
    <cti:checkEnergyCompanyOperator showError="true" accountId="${accountId}">
    
        <div id="page-actions" class="dn">
            <cm:pointCreationOption paoId="${deviceId}" includeDividerAfter="true"/>
            <cti:url var="download" value="/common/pao/${deviceId}/download"/>
            <cm:dropdownOption key="yukon.common.download" icon="icon-page-white-excel" href="${download}"/>
        </div>
        
        <cti:url var="url" value="/stars/operator/hardware/points">
            <cti:param name="deviceId" value="${deviceId}"/>
            <cti:param name="accountId" value="${accountId}"/>
        </cti:url>
        <div data-url="${url}" data-static>
            <%@ include file="/WEB-INF/pages/common/pao/points.table.jsp" %>
        </div>
        <tags:simplePopup id="manual-entry-popup" title=""/>
    </cti:checkEnergyCompanyOperator>
</cti:standardPage>