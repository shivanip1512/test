<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="dialog" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <c:if test="${empty weatherLocations}">
        <span class="empty-list"><i:inline key=".noWeatherLocations"/></span>
    </c:if>

    <c:if test="${not empty weatherLocations}">
        <table class="compactResultsTable sortable-table">
            <thead>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".stationId"/></th>
                <th><i:inline key=".stationDescription"/></th>
                <th><i:inline key=".temp"/></th>
                <th><i:inline key=".humidity"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="weatherLocation" items="${weatherLocations}">
                    <tr>
                        <td>${fn:escapeXml(weatherLocation.name)}</td>
                        <td>${fn:escapeXml(weatherLocation.stationId)}</td>
                        <td>${fn:escapeXml(weatherStations[weatherLocation.stationId].stationDesc)}</td>
                        <td>
                            <cti:dataUpdaterValue identifier="${weatherLocation.paoId}/TEMP" type="WEATHER_STATION"/>
                        </td>
                        <td>
                            <cti:dataUpdaterValue identifier="${weatherLocation.paoId}/HUMIDITY" type="WEATHER_STATION"/>
                            <cti:button id="deleteWeatherLocation_${weatherLocation.paoId}"
                                href="removeWeatherLocation?paoId=${weatherLocation.paoId}" nameKey="remove"
                                renderMode="image" icon="icon-cross" classes="fr"/>
                            <dialog:confirm on="#deleteWeatherLocation_${weatherLocation.paoId}"
                                 nameKey="confirmDelete" argument="${fn:escapeXml(weatherLocation.name)}"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</cti:msgScope>