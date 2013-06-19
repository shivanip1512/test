<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="home">
    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>

    <div class="column_12_12">
        <div class="column one">
            <tags:boxContainer2 nameKey="favorites">
                <c:if test="${empty favorites}">
                    <span class="empty-list"><i:inline key=".noFavorites"/></span>
                </c:if>
                <c:if test="${!empty favorites}">
                    <table class="compactResultsTable rowHighlighting">
                        <thead>
                        <tr>
                            <th></th>
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
                            <th>
                                <i:inline key="yukon.web.modules.dr.home.actionsHeader"/>
                            </th>
                        </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                        <c:forEach var="pao" items="${favorites}">
                            <tr>
                                <td>
                                    <dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}" isFavorite="true" fromHomePage="true"/>
                                </td>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}">
                                        <spring:escapeBody htmlEscape="true">${pao.name}</spring:escapeBody>
                                    </cti:paoDetailUrl>
                                </td>
                                <td>
                                    <i:inline key=".paoType.${pao.paoIdentifier.paoType}"/>
                                </td>
                                <td>
                                    <dr:stateText pao="${pao}"/>
                                </td>
                                <td class="nonwrapping">
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
                    <table class="compactResultsTable rowHighlighting">
                        <thead>
                        <tr>
                            <th></th>
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
                            <th>
                                <i:inline key=".actionsHeader" />
                            </th>
                        </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                        <c:forEach var="pao" items="${recents}">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                <td><dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}"
                                    isFavorite="${favoritesByPaoId[pao.paoIdentifier.paoId]}"
                                    fromHomePage="true" isRecentlyViewedItem="true"/></td>
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
                                <td class="nonwrapping">
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
