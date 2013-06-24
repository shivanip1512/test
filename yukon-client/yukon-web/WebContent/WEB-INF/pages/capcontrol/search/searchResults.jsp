<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<cti:standardPage module="capcontrol" page="search.${pageName}">
<cti:linkTabbedContainer mode="section">
    <cti:msg var="name_sub" key="yukon.web.modules.capcontrol.search.orphanedSubs.tab.title" />
    <c:url var="url_sub" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubstations__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedSubs")}' />
    <cti:linkTab selectorName="${name_sub}" tabHref="${url_sub}" initiallySelected="${tab_on}"/>
 
    <cti:msg var="name_bus" key="yukon.web.modules.capcontrol.search.orphanedBuses.tab.title" />
    <c:url var="url_bus" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oSubBuses__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedBuses")}' />
    <cti:linkTab selectorName="${name_bus}" tabHref="${url_bus}" initiallySelected="${tab_on}"/>
 
    <cti:msg var="name_feed" key="yukon.web.modules.capcontrol.search.orphanedFeeders.tab.title" />
    <c:url var="url_feed" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oFeeders__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedFeeders")}' />
    <cti:linkTab selectorName="${name_feed}" tabHref="${url_feed}" initiallySelected="${tab_on}"/>
 
    <cti:msg var="name_bank" key="yukon.web.modules.capcontrol.search.orphanedBanks.tab.title" />
    <c:url var="url_bank" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oBanks__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedBanks")}' />
    <cti:linkTab selectorName="${name_bank}" tabHref="${url_bank}" initiallySelected="${tab_on}"/>
 
    <cti:msg var="name_cbc" key="yukon.web.modules.capcontrol.search.orphanedCbcs.tab.title" />
    <c:url var="url_cbc" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oCBCs__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedCbcs")}' />
    <cti:linkTab selectorName="${name_cbc}" tabHref="${url_cbc}" initiallySelected="${tab_on}"/>
 
    <cti:msg var="name_reg" key="yukon.web.modules.capcontrol.search.orphanedRegulators.tab.title" />
    <c:url var="url_reg" value="/capcontrol/search/searchResults?cbc_lastSearch=__cti_oRegulators__" />
    <c:set var="tab_on" value='${pageName.equals("orphanedRegulators")}' />
    <cti:linkTab selectorName="${name_reg}" tabHref="${url_reg}" initiallySelected="${tab_on}"/>
</cti:linkTabbedContainer>

    <%@include file="/capcontrol/capcontrolHeader.jspf"%>
    
    <jsp:setProperty name="CtiNavObject" property="moduleExitPage" value=""/>
    
    <c:set var="baseUrl" value="/capcontrol/search/searchResults"/>
    
    <form id="parentForm" action="feeders.jsp" method="post">
        <input type="hidden" name="${lastAreaKey}" />
        <input type="hidden" name="${lastSubKey}" />
    
        <tags:pagedBox2 nameKey="searchContainer"
                arguments="${label},${resultsFound}" 
                argumentSeparator=","
                searchResult="${searchResult}" 
                baseUrl="${baseUrl}"
                styleClass="padBottom">
            <c:choose>
            
                <c:when test="${searchResult.hitCount == 0}">
                    <cti:msg key="yukon.web.modules.dr.searchResults.noResults" />
                </c:when>
            
                <c:otherwise>
                
                    <table id="resTable" class="compactResultsTable rowHighlighting" align="center">
                        <tr>
                            <th><i:inline key=".name"/></th>
                            <th nowrap="nowrap"><i:inline key=".itemType"/></th>
                            <th><i:inline key=".description"/></th>
                            <th><i:inline key=".parent"/></th>
                        </tr>
                        <c:forEach items="${searchResult.resultList}" var="row">
                            <tr>
            	                <td nowrap="nowrap">
                                    <c:choose>
                                        <c:when test="${row.paobject}">
                                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                                <a href="/editor/cbcBase.jsf?type=2&itemid=${row.itemId}" class="tierIconLink">
                                                    <cti:icon icon="icon-pencil"/>
                                                </a>
                                                <a href="/editor/deleteBasePAO.jsf?value=${row.itemId}" class="tierIconLink">
                                                    <cti:icon icon="icon-cross"/>
                                                </a>
                                            </cti:checkRolesAndProperties>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                                <a href="/editor/pointBase.jsf?parentId=${row.parentId}&itemid=${row.itemId}" class="tierIconLink">
                                                    <cti:icon icon="icon-pencil"/>
                                                </a>
                                                <a href="/editor/deleteBasePoint.jsf?value=${row.itemId}" class="tierIconLink">
                                                    <cti:icon icon="icon-cross"/>
                                                </a>
                                            </cti:checkRolesAndProperties>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:if test="${row.controller}">
                                        <cti:checkRolesAndProperties value="CBC_DATABASE_EDIT">
                                            <a class="tierIconLink" href="/editor/copyBase.jsf?itemid=${row.itemId}&type=1>"><cti:icon icon="icon-page-copy"/></a>
                                        </cti:checkRolesAndProperties>
                                    </c:if>
                                    <spring:escapeBody htmlEscape="true">${row.name}</spring:escapeBody>
                                </td>
            	                <td nowrap="nowrap">${row.itemType}</td>
            	                <td nowrap="nowrap"><spring:escapeBody htmlEscape="true">${row.itemDescription}</spring:escapeBody></td>
            	                <td>${row.parentString}</td>
            	            </tr>
            	        </c:forEach>
            	    </table>
                    
            	</c:otherwise>
            </c:choose>
            
        </tags:pagedBox2>
        
    </form>
    
</cti:standardPage>