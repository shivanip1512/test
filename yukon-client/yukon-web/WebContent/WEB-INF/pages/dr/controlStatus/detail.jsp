<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="dr" page="controlStatus">

    <hr>
    <div class="filter-section">
        <cti:url var="filterUrl" value="/dr/program/controlStatusTable"/>
        <form:form id="control-status-form" action="${filterUrl}" method="get">
            <input type="hidden" name="programId" value="${programId}"/>
            <i:inline key="yukon.common.filterBy"/>&nbsp;
            <cti:msg2 var="allLabel" key=".allControlStatuses"/>
            <select id="controlStatuses" name="controlStatuses" multiple data-placeholder="${allLabel}">
                <c:forEach var="status" items="${controlStatuses}">
                    <option value="${status}"><i:inline key="${status.formatKey}"/></option>
                </c:forEach>
            </select>
            <cti:msg2 var="allLabel" key=".allRestoreStatuses"/>
            <select id="restoreStatuses" name="restoreStatuses" multiple data-placeholder="${allLabel}">
                <c:forEach var="status" items="${restoreStatuses}">
                    <option value="${status}"><i:inline key="${status.formatKey}"/></option>
                </c:forEach>
            </select>
            <cti:button nameKey="filter" classes="js-filter primary action fn vab"/>
        </form:form>
    </div>
    <hr>

    <cti:url var="dataUrl" value="/dr/program/controlStatusTable">
        <cti:param name="programId" value="${programId}"/>
    </cti:url>
    <div id="control-status-table" data-url="${dataUrl}">
        <%@ include file="filteredResultsTable.jsp" %>
    </div>
    
    <cti:includeScript link="/resources/js/pages/yukon.dr.controlStatus.js"/>
    
</cti:standardPage>