<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="search">
    
    <div class="stacked">
        <form action="" method="get" id="formSiteSearch" class="clearfix yukon-search-form">
            <input id="query" name="q" value="${fn:escapeXml(query)}" size="50" type="text"
                class="fl search-field" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>">
               <input type="hidden" name="itemsPerPage" value="${itemsPerPage}">
            <cti:msg2 key="yukon.common.search" var="searchLabel"/>
            <cti:button classes="primary action ML15" type="submit" label="${searchLabel}"/>
        </form>
    </div>
    
    <div class="stacked">
        <c:choose>
            <c:when test="${not empty error}"><i:inline key="${error}"/></c:when>
            <c:otherwise>
                <c:choose>
                    <c:when test="${empty results.resultList}">
                        <i:inline key=".noResults" arguments="${fn:escapeXml(query)}"/>
                    </c:when>
                    <c:otherwise>
                        <cti:list var="msgArgs">
                            <cti:item value="${results.startIndex + 1}"/>
                            <cti:item value="${results.endIndex}"/>
                            <cti:item value="${results.hitCount}"/>
                            <cti:item value="${query}"/>
                        </cti:list>
                        <i:inline key=".results.summary" arguments="${msgArgs}"/>
                    </c:otherwise>
                </c:choose>
            </c:otherwise>
        </c:choose>
    </div>
    <c:if test="${!empty results.resultList}">
        <hr>
        <cti:url var="url" value="search"><cti:param name="q" value="${query}"/></cti:url>
        <div data-url="${url}" data-static>
            <c:forEach var="result" items="${results.resultList}">
                <div class="stacked-md">
                    <div>
                        <a href="<cti:url value="${result.path}"/>">
                            <cti:searchTerm term="${query}" asLuceneTerms="true">
                                <c:choose>
                                    <c:when test="${result.legacyPage}">
                                        <i:inline key="${result.title}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <cti:pageName userPage="${result.userPage}"/>
                                    </c:otherwise>
                                </c:choose>
                           </cti:searchTerm>
                       </a>
                    </div>
                    <div><cti:msg2 key="${result.summary}" blankIfMissing="true"/></div>
                </div>
            </c:forEach>
            <tags:pagingResultsControls result="${results}" adjustPageCount="true"/>
        </div>
    </c:if>
    
</cti:standardPage>