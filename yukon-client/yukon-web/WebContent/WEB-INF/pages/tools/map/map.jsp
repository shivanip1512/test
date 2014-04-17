<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<cti:standardPage module="tools" page="map">

<style>
.map {
  height: 600px;
  width: 100%;
  border: 1px solid #bbb;
  box-shadow: 0px 0px 5px #ddd;
}
</style>

    <div class="stacked clearfix">
        <tags:selectedDevices deviceCollection="${deviceCollection}" id="selectedDevices"/>
    </div>

    <div id="map" class="clearfix map f-focus" tabindex="0"></div>
    <div id="mouse-position" class="warning"></div>
    
    <cti:url value="/tools/map/locations" var="locationsUrl">
        <c:forEach items="${deviceCollection.collectionParameters}" var="cp">
            <cti:param name="${cp.key}" value="${cp.value}"/>
        </c:forEach>
    </cti:url>
    <input id="locations" type="hidden" value="${fn:escapeXml(locationsUrl)}">
    <cti:toJson object="${iconMap}" id="icon-map"/>

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/JavaScript/yukon.collection.map.js"/>
</cti:standardPage>