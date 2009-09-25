<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="scenarioList">
    <cti:standardMenu menuSelection="details|scenarios"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.scenarios"/></cti:crumbLink>
    </cti:breadCrumbs>

    <h2><cti:msg key="yukon.web.modules.dr.scenarioList.scenarios"/></h2>
    <br>

    <c:set var="baseUrl" value="/spring/dr/scenario/list"/>
    <cti:url var="submitUrl" value="${baseUrl}"/>
    <cti:url var="clearFilterUrl" value="${baseUrl}">
        <c:if test="${!empty param.itemsPerPage}">
            <cti:param name="itemsPerPage" value="${param.itemsPerPage}"/>
        </c:if>
        <c:if test="${!empty param.sort}">
            <cti:param name="sort" value="${param.sort}"/>
        </c:if>
        <c:if test="${!empty param.descending}">
            <cti:param name="descending" value="${param.descending}"/>
        </c:if>
    </cti:url>

    <script type="text/javascript">
    function clearFilter() {
        window.location = '${clearFilterUrl}';
    }
    </script>

    <cti:msg var="filterLabel" key="yukon.web.modules.dr.scenarioList.filters"/>
    <tags:abstractContainer type="triangle" title="${filterLabel}" showInitially="false">
    <form:form action="${submitUrl}" commandName="backingBean" method="get">
        <c:if test="${!empty param.sort}">
            <input type="hidden" name="sort" value="${param.sort}"/>
        </c:if>
        <c:if test="${!empty param.descending}">
            <input type="hidden" name="descending" value="${param.descending}"/>
        </c:if>
        <c:if test="${!empty param.itemsPerPage}">
            <input type="hidden" name="itemsPerPage" value="${param.itemsPerPage}"/>
        </c:if>

        <table cellspacing="10">
            <tr>
                <cti:msg var="fieldName" key="yukon.web.modules.dr.scenarioList.filter.name"/>
                <td>${fieldName}</td>
                <td><form:input path="name"/></td>
            </tr>
        </table>
        
        <br>
        <input type="submit" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.submit"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.clear"/>"
            onclick="javascript:clearFilter()"/>
    </form:form>
    </tags:abstractContainer><br>

    <c:if test="${searchResult.hitCount == 0}">
        <cti:msg key="yukon.web.modules.dr.scenarioList.noResults"/>
    </c:if>
    <c:if test="${searchResult.hitCount > 0}">
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"/>
        <table id="scenarioList" class="resultsTable activeResultsTable">
            <tr>
                <th>
                    <dr:sortByLink key="yukon.web.modules.dr.scenarioList.heading.name"
                        baseUrl="${baseUrl}" fieldName="NAME"/>
                </th>
            </tr>
            <c:forEach var="scenario" items="${scenarios}">
                <c:url var="scenarioURL" value="/spring/dr/scenario/detail">
                    <c:param name="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
                </c:url>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td>
                        <a href="${scenarioURL}"><spring:escapeBody htmlEscape="true">${scenario.name}</spring:escapeBody></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"/>
    </c:if>

</cti:standardPage>
