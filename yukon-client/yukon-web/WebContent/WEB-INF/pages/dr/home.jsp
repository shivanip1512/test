<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="home">
    <cti:standardMenu menuSelection="home"/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.home.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.home.breadcrumb.home"/></cti:crumbLink>
    </cti:breadCrumbs>

<table class="widgetColumns">
    <tr>
        <td class="widgetColumnCell" valign="top" rowspan="2">
            <div class="widgetContainer">
            <c:if test="${empty favorites}">
                <cti:msg key="yukon.web.modules.dr.home.noFavorites"/>
            </c:if>
            <c:if test="${!empty favorites}">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.home.favorites"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <table class="compactResultsTable rowHighlighting">
                        <tr>
                            <th></th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.nameHeader"
                                    baseUrl="${baseUrl}" fieldName="NAME"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.typeHeader"
                                    baseUrl="${baseUrl}" fieldName="TYPE"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.stateHeader"
                                    baseUrl="${baseUrl}" fieldName="STATE"
                                    sortParam="favSort" descendingParam="favDescending"/>
                            </th>
                            <th>
                                <cti:msg key="yukon.web.modules.dr.home.actionsHeader"></cti:msg>
                            </th>
                        </tr>
                        <c:forEach var="pao" items="${favorites}">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                <td><dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}" isFavorite="true" fromHomePage="true"/></td>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}">
                                        <spring:escapeBody>${pao.name}</spring:escapeBody>
                                    </cti:paoDetailUrl>
                                </td>
                                <td>
                                    <cti:msg key="yukon.web.modules.dr.paoType.${pao.paoIdentifier.paoType}"/>
                                </td>
                                <td>
                                    <dr:stateText pao="${pao}"/>
                                </td>
                                <td class="nonwrapping">
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </tags:abstractContainer>
            </c:if>
            </div>
        </td>
        <td class="widgetColumnCell" valign="top">
            <div class="widgetContainer">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.home.quickSearches"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <cti:checkRolesAndProperties value="SHOW_CONTROL_AREAS">
                        <cti:url var="quickLinkUrl" value="/spring/dr/controlArea/list">
                            <cti:param name="state" value="active"/>
                        </cti:url>
                        <a href="${quickLinkUrl}"><cti:msg key="yukon.web.modules.dr.home.activeControlAreasQuickSearch"/></a><br>
                    </cti:checkRolesAndProperties>
                    <cti:url var="quickLinkUrl" value="/spring/dr/program/list">
                        <cti:param name="state" value="active"/>
                    </cti:url>
                    <a href="${quickLinkUrl}"><cti:msg key="yukon.web.modules.dr.home.activeProgramsQuickSearch"/></a><br>
                    <cti:url var="quickLinkUrl" value="/spring/dr/loadGroup/list">
                        <cti:param name="state" value="active"/>
                    </cti:url>
                    <a href="${quickLinkUrl}"><cti:msg key="yukon.web.modules.dr.home.activeLoadGroupsQuickSearch"/></a><br>
                </tags:abstractContainer>
            </div>
        </td>
    </tr>
    <tr>
        <td class="widgetColumnCell" valign="top">
            <div class="widgetContainer">
            <c:if test="${empty recents}">
                <cti:msg key="yukon.web.modules.dr.home.noRecents"/>
            </c:if>
            <c:if test="${!empty recents}">
                <cti:msg var="boxTitle" key="yukon.web.modules.dr.home.recents"/>
                <tags:abstractContainer type="box" title="${boxTitle}">
                    <table class="compactResultsTable rowHighlighting">
                        <tr>
                            <th></th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.nameHeader"
                                    baseUrl="${baseUrl}" fieldName="NAME"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.typeHeader"
                                    baseUrl="${baseUrl}" fieldName="TYPE"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th>
                                <tags:sortLink key="yukon.web.modules.dr.home.stateHeader"
                                    baseUrl="${baseUrl}" fieldName="STATE"
                                    sortParam="rvSort" descendingParam="rvDescending"/>
                            </th>
                            <th>
                                <cti:msg key="yukon.web.modules.dr.home.actionsHeader"></cti:msg>
                            </th>
                        </tr>
                        <c:forEach var="pao" items="${recents}">
                            <tr class="<tags:alternateRow odd="" even="altRow"/>">
                                <td><dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}"
                                    isFavorite="${favoritesByPaoId[pao.paoIdentifier.paoId]}"
                                    fromHomePage="true" isRecentlyViewedItem="true"/></td>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${pao}">
                                        <spring:escapeBody>${pao.name}</spring:escapeBody>
                                    </cti:paoDetailUrl>
                                </td>
                                <td>
                                    <cti:msg key="yukon.web.modules.dr.paoType.${pao.paoIdentifier.paoType}"/>
                                </td>
                                <td>
                                    <dr:stateText pao="${pao}"/>
                                </td>
                                <td class="nonwrapping">
                                    <dr:listActions pao="${pao}"/>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </tags:abstractContainer>
            </c:if>
            </div>
        </td>
    </tr>
</table>

</cti:standardPage>
