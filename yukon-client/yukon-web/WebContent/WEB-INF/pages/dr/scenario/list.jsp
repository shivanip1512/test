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

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.scenarioList.breadcrumb.operationsHome"/>
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
                <td><form:input path="name" size="40"/></td>
            </tr>
        </table>
        
        <br>
        <input type="submit" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.submit"/>"/>
        <input type="button" value="<cti:msg key="yukon.web.modules.dr.scenarioList.filter.clear"/>"
            onclick="javascript:clearFilter()"/>
    </form:form>
    </tags:simplePopup><br>

    <c:if test="${searchResult.hitCount == 0}">
        <cti:msg key="yukon.web.modules.dr.scenarioList.noResults"/><br>
        <a href="javascript:void(0)" onclick="$('filterPopup').show()"><cti:msg key="yukon.web.modules.dr.paging.filter"/></a>
        <a href="javascript:void(0)" onclick="javascript:clearFilter()"><cti:msg key="yukon.web.modules.dr.loadGroupList.filter.clear"/></a>
    </c:if>
    <c:if test="${searchResult.hitCount > 0}">
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"
            filter="$('filterPopup').show()"/>
        <cti:msg var="scenarioTitle" key="yukon.web.modules.dr.scenarioList.scenarios"/>
        <tags:abstractContainer type="box" title="${scenarioTitle}">
            <table id="scenarioList" class="compactResultsTable rowHighlighting">
                <tr>
                    <th>
                        <dr:sortByLink key="yukon.web.modules.dr.scenarioList.heading.name"
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
                        <td>
                            <a href="${scenarioUrl}"><spring:escapeBody htmlEscape="true">${scenario.name}</spring:escapeBody></a>
                        </td>
                        <td style="white-space: nowrap;">
                            <span id="actionSpan_${scenarioId}">
                                <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}">
                                    <cti:url var="startScenarioUrl" value="/spring/dr/program/startMultipleProgramsDetails">
                                        <cti:param name="scenarioId" value="${scenarioId}"/>
                                    </cti:url>
                                    <a id="startLink_${scenarioId}" href="javascript:void(0)"
                                        onclick="openSimpleDialog('drDialog', '${startScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.startMultiplePrograms.title"/>')">
                                        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon"/>
                                    </a>

                                    <cti:url var="stopScenarioUrl" value="/spring/dr/program/stopMultipleProgramsDetails">
                                        <cti:param name="scenarioId" value="${scenarioId}"/>
                                    </cti:url>
                                    <a id="stopLink_${scenarioId}" href="javascript:void(0)"
                                        onclick="openSimpleDialog('drDialog', '${stopScenarioUrl}', '<cti:msg key="yukon.web.modules.dr.program.stopMultiplePrograms.title"/>')">
                                        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon"/>
                                    </a>
                                </cti:checkPaoAuthorization>
                                <cti:checkPaoAuthorization permission="CONTROL_COMMAND" pao="${scenario}" invert="true">
                                    <cti:msg var="noScenarioControl" key="yukon.web.modules.dr.scenarioDetail.noControl"/>
                                    <span title="${noScenarioControl}">
                                        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.startIcon.disabled"/>
                                    </span>
                                    <span title="${noScenarioControl}">
                                        <cti:logo key="yukon.web.modules.dr.scenarioDetail.actions.stopIcon.disabled"/>
                                    </span>
                                </cti:checkPaoAuthorization>
                            </span>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </tags:abstractContainer>
        <dr:searchNavigation searchResult="${searchResult}" baseUrl="${baseUrl}"
            filter="$('filterPopup').show()"/>
    </c:if>

</cti:standardPage>
