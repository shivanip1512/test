<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="home">
    <tags:simpleDialog id="drDialog"/>

    <div class="column-12-12">
        <div class="column one">
            <tags:boxContainer2 nameKey="favorites">
                <c:if test="${empty favorites}">
                    <span class="empty-list"><i:inline key=".noFavorites"/></span>
                </c:if>
                <c:if test="${!empty favorites}">
                    <table class="compact-results-table row-highlighting has-actions">
                        <thead>
                        <tr>
                            <th>
                                <tags:sortLink nameKey="nameHeader" baseUrl="${baseUrl}" fieldName="NAME"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                            <th>
                                <tags:sortLink nameKey="typeHeader" baseUrl="${baseUrl}" fieldName="TYPE"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                            <th>
                                <tags:sortLink nameKey="stateHeader" baseUrl="${baseUrl}" fieldName="STATE"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                        </tr>
                        </thead>
                        <tfoot></tfoot>
                        <cti:msg2 var="undoText" key="yukon.common.undo"/>
                        <tbody data-undo-text="${undoText}">
                        <c:forEach var="pao" items="${favorites}">
                            <cti:msg2 var="removedText" key="yukon.web.modules.operations.dashboard.favorites.removed" htmlEscapeArguments="false"
                                arguments="${pao.name}"/>
                            <tr data-removed-text="${removedText}">
                                <td>
                                    <c:set var="paoType" value="${pao.paoIdentifier.paoType}" />

                                    <c:set var="paoPageName" value="" />
                                    <c:if test="${fn:contains(paoType.name(), 'AREA')}">
                                        <c:set var="paoPageName" value="controlAreaDetail" />
                                    </c:if>
                                    <c:if test="${fn:contains(paoType.name(), 'PROGRAM')}">
                                        <c:set var="paoPageName" value="programDetail" />
                                    </c:if>
                                    <c:if test="${fn:contains(paoType.name(), 'SCENARIO')}">
                                        <c:set var="paoPageName" value="scenarioDetail" />
                                    </c:if>
                                    <c:if test="${fn:contains(paoType.name(), 'GROUP')}">
                                        <c:set var="paoPageName" value="groupDetail" />
                                    </c:if>

                                    <cti:paoDetailUrl yukonPao="${pao}" var="detailUrl"/>

                                    <cti:button classes="b-favorite remove" nameKey="favorite" renderMode="image" icon="icon-star" 
                                            data-name="${paoPageName}"
                                            data-module="dr"
                                            data-path="${detailUrl}"
                                            data-label-args="['${pao.name}']"/>

                                    <a href="${detailUrl}">${fn:escapeXml(pao.name)}</a>
                                </td>
                                <td>
                                    <i:inline key=".paoType.${pao.paoIdentifier.paoType}"/>
                                </td>
                                <td>
                                    <span class="fl"><dr:stateText pao="${pao}"/></span>
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </tags:boxContainer2>
        </div>
        <div class="column two nogutter">
            <tags:boxContainer2 nameKey="quickSearches">
                 <cti:checkRolesAndProperties value="SHOW_CONTROL_AREAS">
                     <cti:url var="quickLinkUrl" value="/dr/controlArea/list">
                         <cti:param name="state" value="active"/>
                     </cti:url>
                     <a href="${quickLinkUrl}"><i:inline key=".activeControlAreasQuickSearch"/></a><br>
                 </cti:checkRolesAndProperties>
                 <cti:url var="quickLinkUrl" value="/dr/program/list">
                     <cti:param name="state" value="ACTIVE"/>
                 </cti:url>
                 <a href="${quickLinkUrl}"><i:inline key=".activeProgramsQuickSearch"/></a><br>
                 <cti:url var="quickLinkUrl" value="/dr/loadGroup/list">
                     <cti:param name="state" value="active"/>
                 </cti:url>
                 <a href="${quickLinkUrl}"><i:inline key=".activeLoadGroupsQuickSearch"/></a><br>
            </tags:boxContainer2>
            <tags:boxContainer2 nameKey="recents">
                <c:if test="${empty recents}">
                    <span class="empty-list"><i:inline key=".noRecents" /></span>
                </c:if>
                <c:if test="${!empty recents}">
                    <table class="compact-results-table row-highlighting has-actions">
                        <thead>
                        <tr>
                            <th>
                                <tags:sortLink nameKey="nameHeader" baseUrl="${baseUrl}" fieldName="NAME"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th>
                                <tags:sortLink nameKey="typeHeader" baseUrl="${baseUrl}" fieldName="TYPE"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th>
                                <tags:sortLink nameKey="stateHeader" baseUrl="${baseUrl}" fieldName="STATE"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th class="action-column"></th>
                        </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                        <c:forEach var="pao" items="${recents}">
                            <tr>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}">
                                        <spring:escapeBody htmlEscape="true">${pao.name}</spring:escapeBody>
                                    </cti:paoDetailUrl>
                                </td>
                                <td>
                                    <i:inline key=".paoType.${pao.paoIdentifier.paoType}" />
                                </td>
                                <td>
                                    <dr:stateText pao="${pao}"/>
                                </td>
                                <td>
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:if>
            </tags:boxContainer2>
        </div>
    </div>
</cti:standardPage>
