<%@ attribute name="assetId" required="true" type="java.lang.Integer" %>
<%@ attribute name="assetAvailabilitySummary" required="true" type="com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary" %>
<%@ attribute name="pieJSONData" required="true" type="net.sf.json.JSONObject" %>
<%@ attribute name="showDetails" required="true" type="java.lang.Boolean" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:msgScope paths="modules.operator.hardware.assetAvailability">
<c:set var="activeSize" value="${assetAvailabilitySummary.activeSize}"/>
<c:set var="inactiveSize" value="${assetAvailabilitySummary.inactiveSize}"/>
<c:set var="optedOutSize" value="${assetAvailabilitySummary.optedOutSize}"/>
<c:set var="unavailableSize" value="${assetAvailabilitySummary.unavailableSize}"/>

<div class="column-12-12 clearfix">
    <div class="column one">
        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".active">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${activeSize}"/>
                <span class="success">${activeSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".inactive">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${inactiveSize}"/>
                <span class="warning">${inactiveSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".optedOut">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${optedOutSize}"/>
                <span class="disabled">${optedOutSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".unavailable">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${unavailableSize}"/>
                <span class="error">${unavailableSize} ${numberOfDevices}</span>
            </tags:nameValue2>
        </tags:nameValueContainer2>
    </div>
    <div class="column two nogutter">

            <!-- Pie Chart -->
            <div style="max-height: 100px;">
                <flot:jsonPieChart data="${pieJSONData}" classes="small-pie"/>
            </div>
    </div>
</div>

<div class="column-24 clearfix">
    <div class="column one nogutter">
        <c:if test="${showDetails}">
            <cti:url var="assetsUrl" value="assetDetails">
                <cti:param name="assetId" value="${assetId}"/>
            </cti:url>
            <div><a href="${assetsUrl}">
                <cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.viewDetails"/></a>
            </div>
        </c:if>
        
        <div class="action-area">
            <cti:button id="pingButton" nameKey="pingDevices" busy="true"/>
            <span id="pingResults" style="display:none" >
                <span class="dib fl" style="padding-right:10px">
                    <i:inline key="yukon.web.modules.operator.hardware.assetAvailability.pingResults"/>
                </span>
                <tags:updateableProgressBar 
                    countKey="ASSET_AVAILABILITY_READ/${assetId}/SUCCESS_COUNT"
                    totalCountKey="ASSET_AVAILABILITY_READ/${assetId}/TOTAL_COUNT"
                    failureCountKey="ASSET_AVAILABILITY_READ/${assetId}/FAILED_COUNT" 
                    hideCount="true" hidePercent="false" containerClasses="dib fl" 
                    completionCallback="Yukon.DrAssetDetails.unbusyPingButton" />
            </span>
        </div>
    </div>
</div>
    
</cti:msgScope>

