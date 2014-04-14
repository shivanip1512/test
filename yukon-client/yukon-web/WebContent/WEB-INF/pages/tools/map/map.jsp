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
        <div class="column-12-12">
            <div class="column one">
                <tags:selectedDevices deviceCollection="${collection}" id="selectedDevices"/>
            </div>
            <div class="column two nogutter">
            </div>
        </div>
    </div>

    <div id="map" class="clearfix map f-focus" tabindex="0"></div>

    <cti:includeScript link="OPEN_LAYERS"/>
    <cti:includeCss link="/resources/js/lib/open-layers/ol.css"/>
    <cti:includeScript link="/JavaScript/yukon.collection.map.js"/>
</cti:standardPage>