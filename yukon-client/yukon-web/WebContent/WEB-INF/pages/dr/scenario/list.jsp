<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="dt" tagdir="/WEB-INF/tags/dateTime" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="scenarioList">
    
    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/yukon.dr.estimated.load.js"/>
    
    <c:choose>
        <c:when test="${scenarios.hitCount == 0}">
            <span class="empty-list"><i:inline key=".noResults"/></span>
        </c:when>
        <c:otherwise>
            
            <div id="filter-popup" class="dn" data-title="<cti:msg2 key=".filters"/>">
                
                <form action="<cti:url var="url" value="/dr/scenario/list"/>" method="get">
                    <input type="hidden" name="itemsPerPage" value="${paging.itemsPerPage}">
                    <tags:nameValueContainer2>
                        <tags:nameValue2 nameKey=".filter.name">
                            <input type="text" name="name" size="40" value="${name}">
                        </tags:nameValue2>
                    </tags:nameValueContainer2>
                    
                    <div class="action-area">
                        <cti:button nameKey="filter" type="submit" classes="action primary"/>
                        <cti:url var="url" value="/dr/scenario/list">
                            <cti:param name="itemsPerPage" value="${paging.itemsPerPage}"/>
                        </cti:url>
                        <cti:button nameKey="showAll" href="${url}"/>
                    </div>
                </form>
            </div>
            
            <cti:msg2 var="scenariosTitle" key=".scenarios"/>
            <c:set var="controls">
                <a href="javascript:void(0);" data-popup="#filter-popup" data-popup-toggle>
                    <cti:icon icon="icon-filter"/>&nbsp;
                    <i:inline key="yukon.common.filter"/>
                </a>
            </c:set>
            <tags:sectionContainer title="${scenariosTitle}" controls="${controls}">
                <c:choose>
                    <c:when test="${areas.hitCount == 0}">
                        <span class="empty-list"><i:inline key=".noResults"/></span>
                    </c:when>
                    <c:otherwise>
                        <cti:url var="url" value="/dr/scenario/list">
                            <c:if test="${not empty name}">
                                <cti:param name="name" value="${name}"></cti:param>
                            </c:if>
                        </cti:url>
                        <div data-url="${url}" data-static>
                            <table class="compact-results-table has-actions">
                                <thead>
                                    <tr>
                                        <th><i:inline key=".heading.name"/></th>
                                        <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                            <th><i:inline key=".heading.kwSavings"/></th>
                                        </cti:checkRolesAndProperties>
                                        <th class="action-column"></th>
                                    </tr>
                                </thead>
                                <tfoot></tfoot>
                                <tbody>
                                    <c:forEach var="scenario" items="${scenarios.resultList}">
                                        <c:set var="scenarioId" value="${scenario.paoIdentifier.paoId}"/>
                                        <c:url var="scenarioUrl" value="/dr/scenario/detail">
                                            <c:param name="scenarioId" value="${scenarioId}"/>
                                        </c:url>
                                        <tr>
                                            <td><a href="${scenarioUrl}">${fn:escapeXml(scenario.name)}</a>
                                            </td>
                                            <cti:checkRolesAndProperties value="ENABLE_ESTIMATED_LOAD">
                                                <cti:dataUpdaterCallback
                                                    function="yukon.dr.estimatedLoad.displaySummaryValue"
                                                    identifier="ESTIMATED_LOAD/${scenarioId}/SCENARIO"/>
                                                <td data-pao="${scenarioId}">
                                                    <cti:icon icon="icon-error" classes="dn"/>
                                                    <span class="js-kw-savings">
                                                        <i:inline key="yukon.web.modules.dr.estimatedLoad.calculating"/>
                                                    </span>
                                                </td>
                                            </cti:checkRolesAndProperties>
                                            <td><dr:scenarioListActions pao="${scenario}"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                            <tags:pagingResultsControls result="${scenarios}" adjustPageCount="true"/>
                        </div>
                    </c:otherwise>
                </c:choose>
            </tags:sectionContainer>
        </c:otherwise>
    </c:choose>

    <dt:pickerIncludes/>

</cti:standardPage>