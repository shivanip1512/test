<%@ attribute name="assetId" required="true" type="java.lang.Integer" %>
<%@ attribute name="assetAvailabilitySummary" required="true" type="com.cannontech.dr.assetavailability.SimpleAssetAvailabilitySummary" %>
<%@ attribute name="pieJSONData" required="true" type="net.sf.json.JSONObject" %>
<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="flot" tagdir="/WEB-INF/tags/flotChart" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>


<cti:msgScope paths="modules.operator.hardware.assetAvailability">
<c:set var="runningSize" value="${assetAvailabilitySummary.communicatingRunningSize}"/>
<c:set var="notRunningSize" value="${assetAvailabilitySummary.communicatingNotRunningSize}"/>
<c:set var="optedOutSize" value="${assetAvailabilitySummary.optedOutSize}"/>
<c:set var="unavailableSize" value="${assetAvailabilitySummary.unavailableSize}"/>

<div class="column_12_12 clearfix">
    <div class="column one">

        <tags:nameValueContainer2>
            <tags:nameValue2 nameKey=".running">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${runningSize}"/>
                <span class="success">${runningSize} ${numberOfDevices}</span>
            </tags:nameValue2>
            
            <tags:nameValue2 nameKey=".notRunning">
                <cti:msg2 var="numberOfDevices" key=".numberOfDevices" argument="${notRunningSize}"/>
                <span class="warning">${notRunningSize} ${numberOfDevices}</span>
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
</cti:msgScope>

