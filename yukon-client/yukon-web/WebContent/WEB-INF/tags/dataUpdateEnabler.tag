<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/dataUpdater.js"/>
<cti:includeScript link="SCRIPTACULOUS_EFFECTS"/>
<c:url var="updateUrl" value="/spring/updater/update" />

<script type="text/javascript">
initiateCannonDataUpdate("${updateUrl}", <cti:getProperty property="WebClientRole.DATA_UPDATER_DELAY_MS"/>);
</script>

<div id="cannonUpdaterErrorDiv" style="display: none">
    <span class="icon ex_large_icon alert"></span>
    <span class="meta">
        Connection to server has been lost (<span id="cannonUpdaterErrorDivCount"></span>)
    </span>
</div>
