<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="dr" page="scenarioList">

    <tags:simpleDialog id="drDialog" />
    <cti:includeScript link="/JavaScript/hideReveal.js"/>
    <cti:includeScript link="/JavaScript/drEstimatedLoad.js"/>

    <cti:url var="baseUrl" value="/dr/scenario/list" />
    <cti:url var="submitUrl" value="${baseUrl}" />
    <cti:url var="clearFilterUrl" value="${baseUrl}">
        <c:if test="${!empty param.itemsPerPage}">
            <cti:param name="itemsPerPage" value="${param.itemsPerPage}" />
        </c:if>
        <c:if test="${!empty param.sort}">
            <cti:param name="sort" value="${param.sort}" />
        </c:if>
        <c:if test="${!empty param.descending}">
            <cti:param name="descending" value="${param.descending}" />
        </c:if>
    </cti:url>
    
<script type="text/javascript">
function clearFilter() {
    window.location = '${clearFilterUrl}';
}
</script>

    <cti:msg2 var="filterLabel" key=".filters" />
    <tags:simplePopup id="filterPopup" title="${filterLabel}">
        <form:form action="${submitUrl}" commandName="backingBean" method="get">
            <tags:sortFields backingBean="${backingBean}" />
            <tags:nameValueContainer>
                <cti:msg2 var="fieldName" key=".filter.name" />
                <tags:nameValue name="${fieldName}">
                    <form:input path="name" size="40" />
                </tags:nameValue>
            </tags:nameValueContainer>
            <div class="action-area">
                <cti:button nameKey="filter" classes="primary action" type="submit"/>
                <cti:button nameKey="showAll" onclick="javascript:clearFilter()"/>
            </div>
        </form:form>
    </tags:simplePopup>

    <cti:msg var="scenarioTitle" key="yukon.web.modules.dr.scenarioList.scenarios" />
    <tags:pagedBox title="${scenarioTitle}" searchResult="${searchResult}"
        filterDialog="filterPopup" baseUrl="${baseUrl}"
        isFiltered="${isFiltered}" showAllUrl="${clearFilterUrl}">
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <cti:msg key="yukon.web.modules.dr.scenarioList.noResults" />
            </c:when>
            <c:otherwise>
                <table id="scenarioList" class="compact-results-table row-highlighting has-actions">
                    <thead>
                        <tr>
                            <th><tags:sortLink nameKey="heading.name"
                                baseUrl="${baseUrl}" fieldName="NAME"/></th>
                            <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                <th><i:inline key=".heading.kwSavings"/></th>
                            </cti:checkRolesAndProperties>
                            <th class="action-column"></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="scenario" items="${scenarios}">
                            <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}" />
                            <c:url var="scenarioUrl" value="/dr/scenario/detail">
                                <c:param name="scenarioId" value="${scenarioId}" />
                            </c:url>
                            <tr>
                                <td><a href="${scenarioUrl}">${fn:escapeXml(scenario.name)}</a>
                                </td>
                                <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                    <cti:dataUpdaterCallback
                                        function="Yukon.EstimatedLoad.displaySummaryValue"
                                        identifier="ESTIMATED_LOAD/${scenarioId}/SCENARIO"/>
                                    <td data-pao="${scenarioId}">
                                        <cti:icon icon="icon-error" classes="dn"/>
                                        <cti:icon icon="icon-spinner"/>
                                        <span class="f-kw-savings">
                                            <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                        </span>
                                    </td>
                                </cti:checkRolesAndProperties>
                                <td><dr:scenarioListActions pao="${scenario}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </tags:pagedBox>

    <c:if test="${hasFilterErrors}">
        <script type="text/javascript">
            jQuery("#filterPopup").dialog("open");
        </script>
    </c:if>
    <dt:pickerIncludes/>
</cti:standardPage>
