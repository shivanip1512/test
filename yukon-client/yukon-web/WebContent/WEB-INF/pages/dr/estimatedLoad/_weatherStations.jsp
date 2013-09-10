<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="modules.dr.estimatedLoad">
    <input type='hidden' id='dialogState' value="${dialogState}"/>
    <div id="weatherLocationSearch" style="${dialogState eq 'searching' ? '' : 'display:none'}">
        <div class="stacked">
            We will search the NOAA database and return the closest weather stations.
            <br>
            Valid inputs include only decimal values. S & W are negative. (e.g. 37°44'55.49''S = -37.74861)
        </div>
        <form:form id="findCloseStationsForm" commandName="weatherLocationBean" action="findCloseStations">
            <div class="column_12_12 clearfix">
                <div class="column one">
                    <tags:nameValueStacked nameKey=".latitude">
                        <tags:input path="latitude"/>
                    </tags:nameValueStacked>
                </div>
                <div class="column two nogutter">
                    <tags:nameValueStacked nameKey=".longitude">
                        <tags:input path="longitude"/>
                    </tags:nameValueStacked>
                </div>
            </div>
        </form:form>
        <div class="actionArea">
            <cti:button id="searchWeatherStations" icon="icon-magnifier" label="Search"/>
        </div>
    </div>
    <div id="weatherLocationSearchResults" style="${dialogState eq 'saving' ? '' : 'display:none'}">
        <form:form id="saveWeatherLocationForm" commandName="weatherLocationBean" action="saveWeatherLocation">
            <tags:hidden path="latitude"/>
            <tags:hidden path="longitude"/>
            <tags:nameValueStacked nameKey=".name">
                <tags:input path="name" size="40"/>
            </tags:nameValueStacked>
            <h4><i:inline key=".weatherStations"/></h4>
            <c:forEach varStatus="status" var="station" items="${weatherStationResults}">
                <c:set var="label" value="${station.stationDesc} - ${distanceToStation[station.stationId]} m"/>
                <form:radiobutton path="stationId" value="${station.stationId}" label="${label}"/><br>
            </c:forEach>
            <tags:bind path="stationId"></tags:bind>
        </form:form>
        <div class="actionArea">
            <cti:button id="saveWeatherStationBtn" classes="action primary" label="Save Weather Location"/>
            <cti:button id="searchWeatherStationsAgain" icon="icon-magnifier" label="Search again"/>
        </div>
    </div>
</cti:msgScope>