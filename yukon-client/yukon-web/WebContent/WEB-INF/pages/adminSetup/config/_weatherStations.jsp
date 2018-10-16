<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<cti:msgScope paths="modules.adminSetup.config.weather">
    <input type='hidden' id='dialogState' value="${dialogState}"/>
    <div id="weatherStationSearchTitle" style="display:none">
        <i:inline key=".weatherStations.searchDialog"/>
    </div>
    <div id="weatherLocationSearch" style="${dialogState eq 'searching' ? '' : 'display:none'}">
        <c:if test="${numberWeatherStations eq 0}">
            <span class="error">
                <i:inline key=".error.unableToLoadWeaterStations"/>
            </span>
        </c:if>
        <div class="stacked">
            <i:inline key=".weatherStations.search"/>
        </div>
        <form:form id="findCloseStationsForm" modelAttribute="weatherLocationBean" action="findCloseStations">
            <cti:csrfToken/>
            <div class="column-12-12 clearfix">
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
        <div class="action-area">
            <cti:button
                nameKey="weatherStations.search"
                id="searchWeatherStations"
                busy="true"
                icon="icon-magnifier"/>
        </div>
    </div>

    <div id="weatherLocationSearchResults" style="${dialogState eq 'saving' ? '' : 'display:none'}">
        <form:form id="saveWeatherLocationForm" modelAttribute="weatherLocationBean" action="saveWeatherLocation">
            <cti:csrfToken/>
            <tags:hidden path="latitude"/>
            <tags:hidden path="longitude"/>
            <div id="dispatchError-popup" class="error" style="display:none">
                <tags:alertBox key=".error.dispatchNotConnected"/>
            </div>
            <h4><i:inline key=".name"/></h4>
            <div class="stacked">
                <tags:input path="name" size="40" maxlength="60"/>
            </div>
            <div><i:inline key=".searchResultInfo"/></div>
            <h4><i:inline key=".weatherStations"/></h4>
            <c:forEach varStatus="status" var="station" items="${weatherStationResults}">
                <c:set var="label" value="${station.stationDesc} - ${distanceToStation[station.stationId]}"/>
                <cti:msg2 var="miles" key=".weatherStation.milesUnit"/>
                <form:radiobutton path="stationId" value="${station.stationId}" label="${label} ${miles}"/><br>
            </c:forEach>
            <tags:bind path="stationId"></tags:bind>
        </form:form>
        <div class="action-area">
            <cti:button id="saveWeatherStationBtn" classes="action primary" label="Save Weather Location"/>
            <cti:button id="searchWeatherStationsAgain" icon="icon-magnifier" label="Search again"/>
        </div>
    </div>

</cti:msgScope>