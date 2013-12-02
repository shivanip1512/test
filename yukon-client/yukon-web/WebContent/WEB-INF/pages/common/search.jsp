<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="tools" page="search">

    <script type="text/javascript">
        jQuery(function() {
            // Manually wire the submit button as it currently does... nothing.
            jQuery(document).on('click', '#manualSearchButton',
            function(event) {
                event.stopPropagation();
                jQuery("#formSiteSearch").submit();
            });

        });
    </script>

    <div class="stacked">
        <form action="" method="get" id="formSiteSearch" class="clearfix yukon-search-form">
            <input id="searchString" name="q" value="${fn:escapeXml(searchString)}" size="50" type="text"
                class="fl search-field" placeholder="<cti:msg2 key='yukon.common.search.placeholder'/>">
               <input type="hidden" name="itemsPerPage" value="${itemsPerPage}">
            <cti:msg2 key="yukon.common.search" var="searchLabel"/>
            <cti:button label="${searchLabel}" id="manualSearchButton"/>
        </form>
    </div>

<h1>NEW RESULTS</h1>
    <div class="stacked">
        <c:if test="${!empty error}">
            <i:inline key="${error}"/>
        </c:if>
        <c:if test="${empty error}">
            <c:if test="${empty results.resultList}">
                <i:inline key=".noResults" arguments="${fn:escapeXml(searchString)}"/>
            </c:if>
            <c:if test="${!empty results.resultList}">
                <cti:list var="msgArgs">
                    <cti:item value="${results.startIndex + 1}"/>
                    <cti:item value="${results.endIndex}"/>
                    <cti:item value="${results.hitCount}"/>
                    <cti:item value="${searchString}"/>
                </cti:list>
                <i:inline key=".results.summary" arguments="${msgArgs}"/>
            </c:if>
        </c:if>
    </div>
    <c:if test="${!empty results.resultList}">
        <hr>
        <div class="stacked">
            <table class="compactResultTable sortable-table dashed full-width with-visited">
                <thead>
                    <tr><th><i:inline key=".results.page.title"/></th></tr>
                </thead>
                <tbody>
                    <c:forEach var="result" items="${results.resultList}">
                        <tr>
                            <td>
                                <div>
                                    <a href="<cti:url value="${result.path}"/>">
                                        <cti:searchTerm term="${searchString}" asLuceneTerms="true">
                                            <c:if test="${result.backedBySiteMapPage}">
                                                <i:inline key="${result.siteMapPage}"/>
                                            </c:if>
                                            <c:if test="${!result.backedBySiteMapPage}">
                                                <cti:pageName userPage="${result.userPage}"/>
                                            </c:if>
                                       </cti:searchTerm>
                                   </a>
                                </div>
                                <div><cti:msg2 key="${result.summary}" blankIfMissing="true"/></div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <cti:url value="/search" var="baseUrl">
                <cti:param name="q" value="${searchString}"/>
            </cti:url>
            <tags:pagingResultsControls baseUrl="${baseUrl}" result="${results}" adjustPageCount="true"/>
        </div>
    </c:if>

<h1>OLD RESULTS</h1>
    <div class="stacked">
        <c:if test="${!empty error}">
            <i:inline key="${error}"/>
        </c:if>
        <c:if test="${empty error}">
            <c:if test="${empty oldResults.resultList}">
                <i:inline key=".noResults" arguments="${fn:escapeXml(searchString)}"/>
            </c:if>
            <c:if test="${!empty oldResults.resultList}">
                <cti:list var="msgArgs">
                    <cti:item value="${oldResults.startIndex + 1}"/>
                    <cti:item value="${oldResults.endIndex}"/>
                    <cti:item value="${oldResults.hitCount}"/>
                    <cti:item value="${searchString}"/>
                </cti:list>
                <i:inline key=".results.summary" arguments="${msgArgs}"/>
            </c:if>
        </c:if>
    </div>
    <c:if test="${!empty oldResults.resultList}">
        <hr>
        <div class="stacked">
            <table class="compactResultTable sortable-table dashed full-width with-visited">
                <thead>
                    <tr><th><i:inline key=".results.page.title"/></th></tr>
                </thead>
                <tbody>
                    <c:forEach var="result" items="${oldResults.resultList}">
                        <tr>
                            <td>
                                <div>
                                    <a href="<cti:url value="${result.path}"/>">
                                        <cti:searchTerm term="${searchString}" asLuceneTerms="true">
                                            <c:if test="${result.backedBySiteMapPage}">
                                                <i:inline key="${result.siteMapPage}"/>
                                            </c:if>
                                            <c:if test="${!result.backedBySiteMapPage}">
                                                <cti:pageName userPage="${result.userPage}"/>
                                            </c:if>
                                       </cti:searchTerm>
                                   </a>
                                </div>
                                <div><cti:msg2 key="${result.summary}" blankIfMissing="true"/></div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
            <cti:url value="/search" var="baseUrl">
                <cti:param name="q" value="${searchString}"/>
            </cti:url>
            <tags:pagingResultsControls baseUrl="${baseUrl}" result="${oldResults}" adjustPageCount="true"/>
        </div>
    </c:if>
</cti:standardPage>