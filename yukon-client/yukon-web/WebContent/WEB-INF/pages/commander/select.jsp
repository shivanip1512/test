<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="commanderSelect" page="select">
    <cti:standardMenu menuSelection="${menuSelection}" />
    
    <%-- BREAD CRUMBS --%>
    <cti:breadCrumbs>
        <cti:msg key="yukon.web.components.button.home.label" var="homeLabel"/>
        <cti:crumbLink url="/operator/Operations.jsp" title="${homeLabel}" />
        
        <%-- bulk home --%>
        <cti:crumbLink><i:inline key=".pageTitle"/></cti:crumbLink>
    </cti:breadCrumbs>
    
    <h2><i:inline key=".pageTitle"/></h2>
    <br>
    
    <script>
        jQuery(function() {
            jQuery('.f_show_all').click(function () {
                <c:forEach var="filter" items="${filters}">
                  jQuery('#${filter.name}').val('');
                </c:forEach>
                
                jQuery('#filterForm')[0].submit();
            });
        });
    </script>
    
    <cti:url var="baseUrl" value="/commander/select"/>
    
    <form id="filterForm" action="${baseUrl}">
        <tags:boxContainer2 nameKey="deviceSearch">
            <input type="hidden" name="startIndex" value="${deviceSearchResults.startIndex}" />
            <input type="hidden" name="itemsPerPage" value="${deviceSearchResults.count}" />
            <input type="hidden" name="orderBy" value="${orderBy}" />
            <input type="hidden" name="category" value="${category}" />
            
            <div class="filters">
                <c:forEach var="filter" items="${filters}">
                    <c:if test="${filter.searchField.visible}">
                    <div class="searchFilterContainer">
                        <label for="${filter.name}"><i:inline key="${filter}" /> </label><input
                            type="text" id="${filter.name}" name="${filter.name}"
                            value="${filter.filterValue}"
                        />
                    </div>
                    </c:if>
                </c:forEach>
            </div>
            
            <div class="actionArea clear">
                <cti:button nameKey="search" type="submit" />
                <cti:button nameKey="showAll" styleClass="f_show_all"/>
            </div>
        </tags:boxContainer2>
    </form>
    
    <br>
    
    <cti:msg2 var="deviceSearchResultsTitle" key=".deviceSearchResults.title" />
    <tags:pagedBox title="${deviceSearchResultsTitle}" searchResult="${deviceSearchResults}" baseUrl="${baseUrl}" pageByHundereds="true">
        <table class="compactResultsTable rowHighlighting">
            <c:if test="${deviceSearchResults.hitCount > 0}">
                <tr>
                <c:forEach var="field" items="${fields}">
                    <c:if test="${field.visible}">
                    <th><tags:sortLink nameKey="deviceSearchField.${field}" baseUrl="${baseUrl}" fieldName="${field}" sortParam="orderBy"/></th>
                    </c:if>
                </c:forEach>
                </tr>
            </c:if>
            <c:forEach var="searchResultRow" items="${deviceSearchResults.resultList}">
                <tr class="<tags:alternateRow odd="" even="altRow"/>">
                    <c:forEach var="field" items="${fields}">
                        <c:if test="${field.visible}">
                        <c:set var="value" value="${fn:escapeXml(searchResultRow.map[field.fieldName])}"/>
                        <c:choose>
                            <c:when test="${field eq 'NAME'}">
                            <cti:url var="commandUrl" value="/commander/command">
                                <cti:param name="deviceId" value="${searchResultRow.map['id']}"/>
                            </cti:url>
                            <td><a href="${commandUrl}">${value}</a></td>
                            </c:when>
                            <c:when test="${field eq 'TYPE'}"><td>${value}</td></c:when>
                            <c:otherwise><td><c:choose>
                                <c:when test="${empty value}">-</c:when>
                                <c:otherwise>${value}</c:otherwise>
                            </c:choose></td></c:otherwise>
                        </c:choose>
                        </c:if>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </tags:pagedBox>
</cti:standardPage>