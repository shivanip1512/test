<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="disconnectStatus">

    <hr>
    <div class="filter-section">
        <cti:url var="filterUrl" value="/dr/program/disconnectStatusTable"/>
        <form:form id="disconnect-form" action="${filterUrl}" method="get">
            <input type="hidden" name="programId" value="${programId}"/>
            <i:inline key="yukon.common.filterBy"/>&nbsp;
            <cti:msg2 var="allLabel" key=".allDisconnectStatuses"/>
            <select id="disconnectStatus" name="disconnectStatus" multiple data-placeholder="${allLabel}">
                <c:forEach var="status" items="${disconnectStatuses}">
                    <option value="${status}">${status}</option>
                </c:forEach>
            </select>
            <cti:button nameKey="filter" classes="js-filter primary action fn vab"/>
        </form:form>
    </div>
    <hr>

    <cti:url var="dataUrl" value="/dr/program/disconnectStatusTable">
        <cti:param name="programId" value="${programId}"/>
        <c:forEach var="status" items="${selectedStatuses}">
            <cti:param name="disconnectStatus" value="${status}"/>
        </c:forEach>
    </cti:url>
    <div id="disconnect-status-table" data-url="${dataUrl}">
        <%@ include file="filteredResultsTable.jsp" %>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.disconnectStatus.js"/>
    
</cti:standardPage>