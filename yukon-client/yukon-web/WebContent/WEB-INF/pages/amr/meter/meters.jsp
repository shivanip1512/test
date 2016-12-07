<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="amr" page="meterSearchResults">

    <h3><i:inline key=".searchCriteria"/></h3>
    <form id="filter-form" action="<cti:url value="/meter/search"/>" class="stacked">
        <div class="clearfix">
            <c:forEach var="filter" items="${filterByList}">
                <div style="width: 21em; text-align: right; float: left; margin-bottom: 5px; margin-right: 5px;">
                    <label>
                        <i:inline key="${filter.formatKey}"/>&nbsp;
                        <input style="width: 10em" type="text" name="${filter.name}" value="${fn:escapeXml(filter.filterValue)}">
                    </label>
                </div>
            </c:forEach>
        </div>
        <div class="action-area">
            <cti:button nameKey="search" classes="primary action js-ami-search"/>
            <cti:button nameKey="showAll" classes="js-ami-search-clear "/>
        </div>
    </form>
    
    <h3>
        <i:inline key=".searchResults" arguments="${meterSearchResults.hitCount}"/>&nbsp;
        <c:if test="${meterSearchResults.hitCount > 0}">
            <cm:deviceCollectionMenu deviceCollection="${deviceGroupCollection}"
                triggerClasses="pull-icon-down"
                key="yukon.web.modules.common.contextualMenu.actions"/>
        </c:if>
    </h3>
    <c:choose>
        <c:when test="${meterSearchResults.hitCount > 0}">
            <cti:url var="dataUrl" value="/meter/search">
                <c:forEach var="filter" items="${filterByList}">
                    <cti:param name="${filter.name}" value="${filter.filterValue}"/>
                </c:forEach>
            </cti:url>
            <div data-url="${dataUrl}" data-static>
                <table class="compact-results-table has-actions">
                    <thead>
                        <tr>
                            <tags:sort column="${nameColumn}"/>
                            <tags:sort column="${meterNumberColumn}"/>
                            <tags:sort column="${typeColumn}"/>
                            <tags:sort column="${addressColumn}"/>
                            <tags:sort column="${routeColumn}"/>
                           <th><cti:icon icon="icon-cog"/></th>
                        </tr>
                    </thead>
                    <tfoot></tfoot>
                    <tbody>
                        <c:forEach var="row" items="${meterSearchResults.resultList}">
                            <tr>
                                <td>
                                    <cti:paoDetailUrl yukonPao="${row}">
                                        <c:if test="${!empty row.name}">${fn:escapeXml(row.name)}</c:if>
                                    </cti:paoDetailUrl>
                                </td>
                                <td><c:if test="${!empty row.meterNumber}">${fn:escapeXml(row.meterNumber)}</c:if></td>
                                <td><tags:paoType yukonPao="${row}"/></td>
                                <td><c:if test="${!empty row.serialOrAddress}">${fn:escapeXml(row.serialOrAddress)}</c:if></td>
                                <td><c:if test="${!empty row.route}">${fn:escapeXml(row.route)}</c:if></td>
                                <td><cm:singleDeviceMenu deviceId="${row.paoIdentifier.paoId}" /></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <tags:pagingResultsControls result="${meterSearchResults}" adjustPageCount="true" hundreds="true"/>
            </div>
        </c:when>
        <c:otherwise>
            <span class="empty-list"><i:inline key=".notFound"/></span>
        </c:otherwise>
    </c:choose>
        
    <cti:includeScript link="/resources/js/pages/yukon.ami.search.js"/>
    
</cti:standardPage>