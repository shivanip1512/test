<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="d" tagdir="/WEB-INF/tags/dialog" %>

<cti:standardPage module="adminSetup" page="config.weather">
<cti:includeScript link="/JavaScript/yukon.weather.js"/>

<div class="box clear dashboard">
    <div class="clearfix box">
        <div class="category fl">
            <a href="weather" class="icon icon-32 fl icon-32-cloud2"></a>
            <div class="box fl meta">
                <div><a class="title" href="<cti:url value="/admin/config/weather"/>">
                    <i:inline key="yukon.common.setting.subcategory.WEATHER"/>
                </a></div>
                <div class="detail"><i:inline key="yukon.common.setting.subcategory.WEATHER.description"/></div>
            </div>
        </div>
    </div>
    <div>
        <div id="weatherStationDialog" style="display:none">
            <%@ include file="_weatherStations.jsp" %>
        </div>
        <div id="weatherLocationsLoading">
            <img src="<cti:url value="/WebConfig/yukon/Icons/spinner.gif"/>" alt="<cti:msg2 key="yukon.web.components.waiting"/>"/>
            <i:inline key=".loadingWeatherLocations"/>
        </div>
        <div id="weatherLocations">
            <!-- Not loaded initially to increase page load speed -->
        </div>
        <div class="action-area">
           <cti:button icon="icon-plus-green" nameKey="createWeatherLocation" id="newWeatherLocationBtn"/>
        </div>
    </div>
</div>

</cti:standardPage>