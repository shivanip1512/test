<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cm" tagdir="/WEB-INF/tags/contextualMenu" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage module="dr" page="${type}.assetDetails">

<input id="assetId" type="hidden" value="${assetId}"/>

<cti:includeScript link="/resources/js/pages/yukon.dr.asset.details.js"/>

<cti:msgScope paths="modules.operator.hardware.assetAvailability">

<div id="page-buttons" class="dn">
    <cti:button id="dd-download" icon="icon-page-white-excel" nameKey="download"/>
</div>
    
    <div class="column-12-12">
        <div class="column one">
            <div class="page-action-area stacked">
                <strong class="fl"><cti:msg2 key=".filter"/></strong>
                <button data-filter="ACTIVE" class="left on">
                    <span class="b-label green"><cti:msg2 key=".active"/></span></button>
                <button data-filter="INACTIVE" class="middle on">
                    <span class="b-label orange"><cti:msg2 key=".inactive"/></span></button>
                <button data-filter="OPTED_OUT" class="middle on">
                    <span class="b-label grey"><cti:msg2 key=".optedOut"/></span></button>
                <button data-filter="UNAVAILABLE" class="right on">
                    <span class="b-label red"><cti:msg2 key=".unavailable"/></span></button>
            </div>
        </div>
        <div class="column two nogutter">
            <!-- show Legend with Pie Chart -->
            <div style="max-height: 100px;">
                <dr:assetAvailabilityStatus assetId="${assetId}"
                        assetAvailabilitySummary="${assetAvailabilitySummary}" 
                        pieJSONData="${pieJSONData}" showDetails="false" showDownload="false"/>
            </div>
        </div>
    </div>
</cti:msgScope>

    <%-- Paged results table goes here... --%>
    <h2><cti:msg2 key="modules.operator.hardware.assetAvailability.devices"/></h2>
    <div class="clear device-detail-table">
        <dr:assetDetailsResult result="${result}" assetId="${assetId}"/>
    </div>

</cti:standardPage>