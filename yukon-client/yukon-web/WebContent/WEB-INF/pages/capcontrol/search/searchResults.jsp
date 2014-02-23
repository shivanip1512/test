<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<c:set var="showTabs" value='${pageName == "orphanedSubs" || pageName == "orphanedBuses" || pageName == "orphanedFeeders" || pageName == "orphanedBanks" || pageName == "orphanedCbcs" || pageName == "orphanedRegulators"}' />

<cti:standardPage module="capcontrol" page="search.${pageName}">
<c:if test="${showTabs}">
<cti:linkTabbedContainer mode="section">
    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedSubs.tab.title"
                 initiallySelected='${pageName == "orphanedSubs"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedBuses.tab.title"
                 initiallySelected='${pageName == "orphanedBuses"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedFeeders.tab.title"
                 initiallySelected='${pageName == "orphanedFeeders"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedBanks.tab.title"
                 initiallySelected='${pageName == "orphanedBanks"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedCbcs.tab.title"
                 initiallySelected='${pageName == "orphanedCbcs"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__" />
    </cti:linkTab>

    <cti:linkTab selectorKey="yukon.web.modules.capcontrol.search.orphanedRegulators.tab.title"
                 initiallySelected='${pageName == "orphanedRegulators"}'>
        <c:url value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__" />
    </cti:linkTab>
</cti:linkTabbedContainer>
</c:if>

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <c:set var="baseUrl" value="/capcontrol/search/searchResults"/>
    
    <form id="parentForm" action="feeders.jsp" method="post">
        <cti:csrfToken/>
        <input type="hidden" name="${lastAreaKey}" />
        <input type="hidden" name="${lastSubKey}" />
        <cti:list var="arguments">
                    <cti:item value="${label}"/>
                    <cti:item value="${resultsFound}"/>
        </cti:list>
        <tags:pagedBox2 nameKey="searchContainer"
                arguments="${arguments}" 
                searchResult="${searchResult}" 
                baseUrl="${baseUrl}"
                styleClass="padBottom">
            <c:choose>
            
                <c:when test="${searchResult.hitCount == 0}">
                    <cti:msg key="yukon.web.modules.dr.searchResults.noResults" />
                </c:when>
            
                <c:otherwise>
                
                    <table id="resTable" class="compact-results-table">
                        <thead>
                            <tr>
                                <th><i:inline key=".name"/></th>
                                <th><i:inline key=".itemType"/></th>
                                <th><i:inline key=".description"/></th>
                                <th><i:inline key=".parent"/></th>
                            </tr>
                        </thead>
                        <tfoot></tfoot>
                        <tbody>
                            <c:forEach items="${searchResult.resultList}" var="row">
                                <tr>
                                    <td>
                                        <c:choose>
                                            <c:when test="${row.paobject}">
                                                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                                    <a href="/editor/cbcBase.jsf?type=2&itemid=${row.itemId}" class="tierIconLink">
                                                        ${fn:escapeXml(row.name)}
                                                    </a>
                                                </cti:checkRolesAndProperties>
                                            </c:when>
                                            <c:otherwise>
                                                <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                                    <a href="/editor/pointBase.jsf?parentId=${row.parentId}&itemid=${row.itemId}" class="tierIconLink">
                                                        ${fn:escapeXml(row.name)}
                                                    </a>
                                                </cti:checkRolesAndProperties>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>${row.itemType}</td>
                                    <td>${fn:escapeXml(row.itemDescription)}</td>
                                    <td>${row.parentString}</td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                    
                </c:otherwise>
            </c:choose>
            
        </tags:pagedBox2>
        
    </form>
    
</cti:standardPage>