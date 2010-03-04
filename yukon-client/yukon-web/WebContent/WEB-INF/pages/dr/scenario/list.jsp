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
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/home">
            <cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.drHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.scenarios"/></cti:crumbLink>
    </cti:breadCrumbs>

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
    <tags:simplePopup id="filterPopup" title="${filterLabel}">
    <form:form action="${submitUrl}" commandName="backingBean" method="get">
        <tags:sortFields backingBean="${backingBean}"/>

        <table cellspacing="10">
            <tr>
                <cti:msg var="fieldName" key="yukon.web.modules.dr.scenarioList.filter.name"/>
                <td>${fieldName}</td>
                <td><form:input path="name" size="40"/></td>
            </tr>
        </table>
        
        <br>
        <div class="actionArea">
            <input type="submit" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.submit"/>"/>
            <input type="button" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.clear"/>"
                onclick="javascript:clearFilter()"/>
        </div>
    </form:form>
    </tags:simplePopup><br>

    <cti:msg var="scenarioTitle" key="yukon.web.modules.dr.scenarioList.scenarios"/>
    <tags:pagedBox title="${scenarioTitle}" searchResult="${searchResult}"
        filterDialog="filterPopup" baseUrl="${baseUrl}" isFiltered="${isFiltered}"
        showAllUrl="${clearFilterUrl}">
        <table id="scenarioList" class="compactResultsTable rowHighlighting">
            <tr>
                <th class="favoritesColumn"></th>
                <th>
                    <tags:sortLink key="yukon.web.modules.dr.scenarioList.heading.name"
                        baseUrl="${baseUrl}" fieldName="NAME"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.dr.scenarioList.heading.actions"/>
                </th>
            </tr>
            <c:forEach var="scenario" items="${scenarios}">
                <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
                <c:url var="scenarioUrl" value="/spring/dr/scenario/detail">
                    <c:param name="scenarioId" value="${scenarioId}"/>
                </c:url>
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><dr:favoriteIcon paoId="${scenarioId}" isFavorite="${favoritesByPaoId[scenarioId]}"/></td>
                    <td>
                        <a href="${scenarioUrl}"><spring:escapeBody htmlEscape="true">${scenario.name}</spring:escapeBody></a>
                    </td>
                    <td style="white-space: nowrap;">
                        <dr:scenarioListActions pao="${scenario}"/>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${searchResult.hitCount == 0}">
                <tr><td></td><td colspan="3"><cti:msg key="yukon.web.modules.dr.scenarioList.noResults"/></td></tr>
            </c:if>
        </table>
    </tags:pagedBox>

</cti:standardPage>
