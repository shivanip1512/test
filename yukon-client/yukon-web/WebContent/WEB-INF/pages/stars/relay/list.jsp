<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="operator" page="relays.list">

<cti:url var="dataUrl" value="/stars/relay" />

    <hr>
    <form:form action="${dataUrl}" method="GET" class="filter-section" modelAttribute="criteria">
        <i:inline key="yukon.common.filterBy"/>
        <select name="type">
            <option value=""><i:inline key="yukon.common.allTypes"/></option>
            <c:forEach var="relayType" items="${relayTypes}">
                <c:set var="selected"/>
                <c:if test="${selectedRelayType == relayType}">
                    <c:set var="selected" value="selected='selected'"/>
                </c:if>
                <option value="${relayType}" ${selected}><i:inline key="${relayType.formatKey}"/></option>
            </c:forEach>
        </select>
        <cti:msg2 var="nameLabel" key="yukon.common.name"/>
        <tags:input path="name" placeholder="${nameLabel}"/>
        <cti:msg2 var="serialNumberLabel" key="yukon.common.serialNumber"/>
        <tags:input path="serialNumber" placeholder="${serialNumberLabel}"/>
        
        <cti:button type="submit" classes="button primary fn vab" nameKey="filter"/>
    </form:form>
    <hr>    

<h3><i:inline key="yukon.common.filteredResults"/>&nbsp;<span class="badge">${relays.hitCount}</span></h3>

<c:choose>
<c:when test="${relays.hitCount > 0}">
    <cti:url var="tableDataUrl" value="/stars/relay">
        <cti:param name="name" value="${criteria.name}"/>
        <cti:param name="serialNumber" value="${criteria.serialNumber}"/>
        <cti:param name="type" value="${selectedRelayType}"/>
    </cti:url>
    <div data-url="${tableDataUrl}" data-static>
        <table class="compact-results-table row-highlighting">
            <thead>
                <tr>
                    <th class="row-icon" />
                    <tags:sort column="${name}" />                
                    <tags:sort column="${serialNumber}" />
                    <tags:sort column="${type}" />
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
                        <td><i:inline key="${relay.type.formatKey}"/></td>
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