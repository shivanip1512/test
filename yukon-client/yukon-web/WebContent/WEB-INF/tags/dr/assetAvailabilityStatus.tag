<%@ attribute name="assetId" required="true" type="java.lang.Integer" %>
<%@ attribute name="assetAvailabilitySummary" required="true" type="com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary" %>
<%@ attribute name="pieJSONData" required="true" type="net.sf.json.JSONObject" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<script type="text/javascript">
jQuery(function() {

    pingUnavailable = function() {
//     FIXME - waiting for the Ping Service...
        alert("Do the Ping Thing here...");
    }

    jQuery("#pingButton").click(pingUnavailable);

})
</script>

<cti:msgScope paths="modules.operator.hardware.assetAvailability">
<cti:msg2 var="device" key=".device"/>
<cti:msg2 var="devices" key=".devices"/>
<c:set var="runningSize" value="${assetAvailabilitySummary.communicatingRunningSize}"/>
<c:set var="notRunningSize" value="${assetAvailabilitySummary.communicatingNotRunningSize}"/>
<c:set var="optedOutSize" value="${assetAvailabilitySummary.optedOutSize}"/>
<c:set var="unavailableSize" value="${assetAvailabilitySummary.unavailableSize}"/>

<div class="column_12_12 clearfix">
    <div class="column one">

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".running">
                <c:choose>
                    <c:when test="${runningSize == '1'}">
                        <span class="success">${runningSize} ${device}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="success">${runningSize} ${devices}</span>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".notRunning">
                <c:choose>
                    <c:when test="${notRunningSize == '1'}">
                        <span class="warning">${notRunningSize} ${device}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="warning">${notRunningSize} ${devices}</span>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".optedOut">
                <c:choose>
                    <c:when test="${optedOutSize == '1'}">
                        <span class="disabled">${optedOutSize} ${device}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="disabled">${optedOutSize} ${devices}</span>
                    </c:otherwise>
                </c:choose>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".unavailable">
                <c:choose>
                    <c:when test="${unavailableSize == '1'}">
                        <span class="error">${unavailableSize} ${device}</span>
                    </c:when>
                    <c:otherwise>
                        <span class="error">${unavailableSize} ${devices}</span>
                    </c:otherwise>
                </c:choose>
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
</cti:msgScope>

    <div class="actionArea">
        <cti:button id="pingButton" nameKey="pingDevices" busy="true" classes="f-disableAfterClick"/>
    </div>
