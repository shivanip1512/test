<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<cti:standardPage module="dr" page="searchResults">
    <cti:standardMenu/>

    <tags:simpleDialog id="drDialog"/>
    <cti:includeCss link="/WebConfig/yukon/styles/calendarControl.css"/>
    <cti:includeScript link="/JavaScript/calendarControl.js"/>
    <cti:includeScript link="/JavaScript/calendarTagFuncs.js"/>
    <dr:favoriteIconSetup/>

    <cti:breadCrumbs>
        <cti:crumbLink url="/operator/Operations.jsp">
            <cti:msg key="yukon.web.modules.dr.searchResults.breadcrumb.operationsHome"/>
        </cti:crumbLink>
        <cti:crumbLink url="/spring/dr/home">
            <cti:msg key="yukon.web.modules.dr.searchResults.breadcrumb.drHome"/>
        </cti:crumbLink>
        <cti:crumbLink><cti:msg key="yukon.web.modules.dr.searchResults.breadcrumb.home"/></cti:crumbLink>
    </cti:breadCrumbs>

    <c:set var="baseUrl" value="/spring/dr/search"/>
    <cti:msg var="searchTitle" key="yukon.web.modules.dr.searchResults.searchResult" argument="${quickSearchBean.name}"/>
    <tags:pagedBox title="${searchTitle}" searchResult="${searchResult}"
        baseUrl="${baseUrl}">
        <table class="compactResultsTable rowHighlighting">
            <tr>
                <th></th>
                <th>
                    <tags:sortLink key="yukon.web.modules.dr.searchResults.nameHeader"
                        baseUrl="${baseUrl}" fieldName="NAME"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.dr.searchResults.typeHeader"
                        baseUrl="${baseUrl}" fieldName="TYPE"/>
                </th>
                <th>
                    <tags:sortLink key="yukon.web.modules.dr.searchResults.stateHeader"
                        baseUrl="${baseUrl}" fieldName="STATE"/>
                </th>
                <th>
                    <cti:msg key="yukon.web.modules.dr.searchResults.actionsHeader"/>
                </th>
            </tr>
            <c:forEach var="pao" items="${searchResult.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <td><dr:favoriteIcon paoId="${pao.paoIdentifier.paoId}" isFavorite="${favoritesByPaoId[pao.paoIdentifier.paoId]}"/></td>
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
            <c:if test="${searchResult.hitCount == 0}">
                <tr><td></td><td colspan="3"><cti:msg key="yukon.web.modules.dr.searchResults.noResults"/></td></tr>
            </c:if>
        </table>
    </tags:pagedBox>

</cti:standardPage>
