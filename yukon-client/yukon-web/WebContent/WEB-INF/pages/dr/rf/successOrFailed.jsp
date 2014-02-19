<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.dr.rf.details">
    <input type="hidden" class="f-title" value="${fn:escapeXml(title)}">
    
    <div data-reloadable>
        <%@ include file="successOrFailedPage.jsp" %>
    </div>
    
    <div class="action-area">
        <cti:button nameKey="close" classes="f-close"/>
        <cti:button nameKey="download" icon="icon-page-white-excel" href="/dr/rf/details/${type}/${test}/download" classes="right"/>
        <cti:button nameKey="inventoryAction" icon="icon-cog-go" href="/dr/rf/details/${type}/${test}/inventoryAction" busy="true" classes="middle"/>
        <cti:button nameKey="collectionAction" icon="icon-cog-go" href="/dr/rf/details/${type}/${test}/collectionAction" busy="true" classes="left"/>
    </div>
</cti:msgScope>