<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<cti:msgScope paths="modules.adminSetup.config.weather">
    <c:if test="${empty weatherLocations}">
        <span class="empty-list"><i:inline key=".noWeatherLocations"/></span>
    </c:if>
    <c:if test="${empty weatherStations}">
        <div class="error">
            <i:inline key=".error.problemLoadingStationData"/>
        </div>
    </c:if>
    <c:if test="${not empty weatherLocations}">
        <div id="dispatchError" class="error" style="display:none">
            <i:inline key=".error.dispatchNotConnected"/>
        </div>
        <table class="compact-results-table">
            <thead>
                <th><i:inline key=".primary"/></th>
                <th><i:inline key=".name"/></th>
                <th><i:inline key=".stationId"/></th>
                <th><i:inline key=".stationDescription"/></th>
                <th><i:inline key=".temp"/></th>
                <th><i:inline key=".humidity"/></th>
                <th><i:inline key=".lastUpdated"/></th>
            </thead>
            <tfoot></tfoot>
            <tbody>
                <c:forEach var="weatherLocation" items="${weatherLocations}">
                    <tr>
                        <td><input type="radio" name="primaryWeatherLocation" value="${weatherLocation.paoIdentifier.paoId}"
                            <c:if test="${weatherLocation.primary == true}">
                                checked="checked"
                            </c:if> 
                            </input>
                        </td>
                        <td>${fn:escapeXml(weatherLocation.name)}</td>
                        <td>
                            ${fn:escapeXml(weatherLocation.stationId)}
                        </td>
                        <td>${fn:escapeXml(weatherStations[weatherLocation.stationId].stationDesc)}</td>
                        <td>
                        <c:if test="${empty fn:escapeXml(weatherLocation.tempPoint)}">
	                        <i:inline key=".weatherInput.notConfigured"/>
                        </c:if>

                        <c:if test="${not empty fn:escapeXml(weatherLocation.tempPoint)}">
                            <cti:dataUpdaterCallback
                             function="yukon.weather.updateWeatherInputFields"
                             initialize="true"
                             value="WEATHER_STATION/${weatherLocation.paoIdentifier.paoId}/JSON_META_DATA" />
                            <span id="temperatureField_${weatherLocation.paoIdentifier.paoId}" class="js-formula-temperature-field">
                                <cti:dataUpdaterValue identifier="${weatherLocation.paoIdentifier.paoId}/TEMPERATURE" type="WEATHER_STATION"/>
                            </span>
                        </c:if>
                        </td>
                        
                        <td>
                        <c:if test="${empty fn:escapeXml(weatherLocation.humidityPoint)}">
	                        <i:inline key=".weatherInput.notConfigured"/>
                        </c:if>
                        <c:if test="${not empty fn:escapeXml(weatherLocation.humidityPoint)}">
                            <span id="humidityField_${weatherLocation.paoIdentifier.paoId}" class="js-formula-humidity-field">
                                <cti:dataUpdaterValue identifier="${weatherLocation.paoIdentifier.paoId}/HUMIDITY" type="WEATHER_STATION"/>
                            </span>
                        </c:if>
                        </td>
                        <td>
                            <span id="timestampField_${weatherLocation.paoIdentifier.paoId}" class="js-formula-timestamp-field">
                               <cti:dataUpdaterValue identifier="${weatherLocation.paoIdentifier.paoId}/TIMESTAMP" type="WEATHER_STATION"/>
                            </span>
                            <cti:button id="deleteWeatherLocation_${weatherLocation.paoIdentifier.paoId}"
                                href="removeWeatherLocation?paoId=${weatherLocation.paoIdentifier.paoId}" nameKey="remove"
                                renderMode="image" icon="icon-cross" classes="fr"/>
                            <d:confirm on="#deleteWeatherLocation_${weatherLocation.paoIdentifier.paoId}"
                                 nameKey="confirmDelete" argument="${fn:escapeXml(weatherLocation.name)}"/>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </c:if>
</cti:msgScope>