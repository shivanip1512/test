<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:msgScope paths="yukon.web.modules.amr.meterEventsReport.report">

<cti:toJson id="meterEventsTableModelData" object="${meterEventsTableModelData}"/>

<cti:formatDate var="fromInstantFormatted" value="${meterEventsFilter.fromInstant}" type="DATE"/>
<cti:formatDate var="toInstantFormatted" value="${meterEventsFilter.toInstant}" type="DATE"/>
<cti:url var="filteredUrl" value="meterEventsTable">
    <c:if test="${not empty meterEventsFilter.fromInstant}"><cti:param name="fromInstant" value="${fromInstantFormatted}" /></c:if>
    <c:if test="${not empty meterEventsFilter.toInstant}"><cti:param name="toInstant" value="${toInstantFormatted}" /></c:if>
    <c:if test="${not empty meterEventsFilter.onlyLatestEvent}"><cti:param name="onlyLatestEvent" 
        value="${meterEventsFilter.onlyLatestEvent}" /></c:if>
    <c:if test="${not empty meterEventsFilter.onlyAbnormalEvents}"><cti:param name="onlyAbnormalEvents" 
        value="${meterEventsFilter.onlyAbnormalEvents}" /></c:if>
    <c:if test="${not empty meterEventsFilter.includeDisabledPaos}"><cti:param name="includeDisabledPaos" 
        value="${meterEventsFilter.includeDisabledPaos}" /></c:if>
    <c:forEach items="${meterEventsFilter.attributes}" var="attribute">
        <cti:param name="attributes" value="${attribute}" />
    </c:forEach>
    <cti:mapParam value="${deviceCollection.collectionParameters}"/>
</cti:url>
<cti:url var="sortedUrl" value="${filteredUrl}">
    <c:if test="${not empty sort}"><cti:param name="sort" value="${sort}" /></c:if>
    <c:if test="${not empty descending}"><cti:param name="descending" value="${descending}" /></c:if>
</cti:url>

<c:set var="controls">
    <c:if test="${not empty meterEvents.resultList}">
        <cm:deviceCollectionMenu deviceCollection="${collectionFromReportResults}"
            key="yukon.web.modules.common.contextualMenu.actions"/>
    </c:if>
</c:set>
<tags:sectionContainer2 nameKey="tableTitle" controls="${controls}">
    <c:choose>
        <c:when test="${empty meterEvents.resultList}">
            <span class="empty-list"><i:inline key=".noEvents" /></span>
        </c:when>
        <c:otherwise>
            <table id="eventsTable" class="compact-results-table f-traversable has-actions sortable-table dashed">
                <thead>
                    <tr>
                        <th><tags:sortLink nameKey="tableHeader.deviceName" baseUrl="${filteredUrl}"
                              fieldName="NAME" isDefault="false" overrideParams="true"/></th>
                        <th><tags:sortLink nameKey="tableHeader.meterNumber" baseUrl="${filteredUrl}"
                              fieldName="METER_NUMBER" isDefault="false" overrideParams="true"/></th>
                        <th><tags:sortLink nameKey="tableHeader.deviceType" baseUrl="${filteredUrl}"
                              fieldName="TYPE" overrideParams="true"/></th>
                        <th><tags:sortLink nameKey="tableHeader.date" baseUrl="${filteredUrl}"
                              fieldName="DATE" isDefault="true" overrideParams="true"/></th>
                        <th><tags:sortLink nameKey="tableHeader.event" baseUrl="${filteredUrl}"
                              fieldName="EVENT" overrideParams="true"/></th>
                        <th><tags:sortLink nameKey="tableHeader.value" baseUrl="${filteredUrl}"
                              fieldName="VALUE" overrideParams="true"/></th>
                        <th></th>
                    </tr>
                </thead>
                <tfoot></tfoot>
                <tbody>
                    <c:forEach var="event" items="${meterEvents.resultList}">
                        <tr>
                            <td>
                                <cti:paoDetailUrl  yukonPao="${event.meter}" >
                                    ${fn:escapeXml(event.meter.name)}
                                </cti:paoDetailUrl>
                            </td>
                            <td>${event.meter.meterNumber}</td>
                            <td><tags:paoType yukonPao="${event.meter}"/></td>
                            <td><cti:formatDate type="BOTH" value="${event.pointValueHolder.pointDataTimeStamp}"/></td>
                            <td>${fn:escapeXml(event.pointName)}</td>
                            <td>
                                <cti:pointStatus pointId="${event.pointValueHolder.id}" rawState="${event.pointValueHolder.value}"/>
                                <cti:pointValueFormatter format="VALUE" value="${event.pointValueHolder}" />
                            </td>
                            <td>
                                <cm:singleDeviceMenu deviceId="${event.meter.paoIdentifier.paoId}" triggerClasses="fr"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
    <tags:pagingResultsControls baseUrl="${filteredUrl}" result="${meterEvents}" overrideParams="true"/>
</tags:sectionContainer2>
</cti:msgScope>