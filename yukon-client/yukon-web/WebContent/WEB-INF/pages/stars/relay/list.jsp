<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>

<cti:standardPage module="operator" page="relays.list">

<cti:url var="dataUrl" value="/stars/relay" />

<tags:sectionContainer2 nameKey="searchCriteria">
    <form action="${dataUrl}">
        <div class="column-10-10-4 clearfix">
            <div class="column one">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".name">
                        <input type="text" id="selectedName" name="selectedName" value="${fn:escapeXml(criteria.name)}">
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column two">
                <tags:nameValueContainer2>
                    <tags:nameValue2 nameKey=".serialNumber">
                        <input type="text" id="selectedSerialNumber" name="selectedSerialNumber" value="${fn:escapeXml(criteria.serialNumber)}">
                    </tags:nameValue2>
                </tags:nameValueContainer2>
            </div>
            <div class="column three nogutter">
                <cti:button type="button" classes="button" nameKey="showAll" href="${dataUrl}" />
                <cti:button type="submit" classes="button primary" nameKey="search" />
            </div>
        </div>
    </form>
</tags:sectionContainer2>

<h3><i:inline key=".searchResults.title"/>&nbsp;<span class="badge">${relays.hitCount}</span></h3>

<c:choose>
<c:when test="${relays.hitCount > 0}">
    <div data-url="${dataUrl}" data-static style="width:60%">
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <th class="row-icon" />
                    <tags:sort column="${name}" />                
                    <tags:sort column="${serialNumber}" />
                </tr>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="relay" items="${relays.resultList}">
                    <cti:url var="detailUrl" value="/stars/relay/home?deviceId=${relay.deviceId}"/>
                    <tr>
                        <td>
                            <c:if test="${notesIds.contains(relay.deviceId)}">
                                <cti:msg2 var="viewAllNotesTitle" key="yukon.web.common.paoNotesSearch.viewAllNotes"/>
                                <cti:icon icon="icon-notes-pin" classes="js-view-all-notes cp" title="${viewAllNotesTitle}" data-pao-id="${relay.deviceId}"/>
                            </c:if>
                        </td>
                        <td><a href="${detailUrl}">${fn:escapeXml(relay.name)}</a></td>
                        <td>${fn:escapeXml(relay.serialNumber)}</td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <tags:pagingResultsControls result="${relays}" adjustPageCount="true" hundreds="true" />
    </div>
    </c:when>
    <c:otherwise>
        <span class="empty-list"><i:inline key="yukon.common.search.noResultsFound"/></span>
    </c:otherwise>
    </c:choose>
    <div class="dn" id="js-pao-notes-popup"></div>
    <cti:includeScript link="/resources/js/pages/yukon.tools.paonotespopup.js"/>
</cti:standardPage>