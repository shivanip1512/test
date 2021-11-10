<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="count" required="true" type="java.lang.Integer" description="The selected number of items per page." %>
<%@ attribute name="hundreds" type="java.lang.Boolean" description="When 'true', the '100' items per page options will appear. Default: 'false'." %>
<%@ attribute name="thousands" type="java.lang.Boolean" description="When 'true', the '1000' items per page options will appear and '25' and '50' will no longer appear. Default: 'false'." %>

<c:set var="pageSizeOptions" value="10,25,50,100,250,500,1000"/>

<span class="fl page-size">
    <i:inline key="yukon.common.paging.itemsPerPage"/>&nbsp;
    <select class="js-items-per-page paging-dropdown" data-page-size="${count}">
        <c:forTokens var="itemsPerPage" items="${pageSizeOptions}" delims=",">
            <c:set var="selectedText" value="${itemsPerPage == count ? 'selected=selected' : ''}"/>
            <option ${selectedText}>${itemsPerPage}</option>
        </c:forTokens>
    </select>
</span>
