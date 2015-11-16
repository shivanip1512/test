<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="searchResults">

    <cti:includeScript link="/resources/js/pages/yukon.dr.dataUpdater.showAction.js"/>

    <tags:simpleDialog id="drDialog"/>

    <div id="findForm" class="action-area stacked">
       <form accept-charset="ISO-8859-1" method="get" action="<cti:url value="/dr/search"/>" enctype="application/x-www-form-urlencoded">
            <label class="box fl">
                <i:inline key=".searchBoxLabel"/>
                <input type="text" id="textinput" class="search_text" value="${quickSearchBean.name}" name="name"/>
            </label>
            <cti:button nameKey="search" type="submit" icon="icon-magnifier" classes="fl"/>
       </form>
    </div>

    <cti:msg2 var="searchTitle" key=".searchResult" argument="${quickSearchBean.name}"/>
    <tags:sectionContainer title="${searchTitle}">
        <c:choose>
            <c:when test="${searchResult.hitCount == 0}">
                <span class="empty-list"><i:inline key=".noResults"/></span>
            </c:when>
            <c:otherwise>
                <cti:url value="/dr/search" var="url">
                    <cti:param name="name" value="${quickSearchBean.name}"/>
                </cti:url>
                <div data-url="${url}" data-static>
                    <table class="compact-results-table has-actions">
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th><i:inline key=".type"/></th>
                            <th><i:inline key=".state"/></th>
                            <th></th>
                        </tr>
                        <c:forEach var="pao" items="${searchResult.resultList}">
                            <tr>
                                <td><cti:paoDetailUrl yukonPao="${pao}">${fn:escapeXml(pao.name)}</cti:paoDetailUrl></td>
                                <td><i:inline key=".paoType.${pao.paoIdentifier.paoType}"/></td>
                                <td><dr:stateText pao="${pao}"/></td>
                                <td><dr:listActions pao="${pao}"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <tags:pagingResultsControls result="${searchResult}" hundreds="true" adjustPageCount="true"/>
                </div>
            </c:otherwise>
        </c:choose>
    </tags:sectionContainer>

</cti:standardPage>