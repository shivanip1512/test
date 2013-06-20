<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ tag import="com.cannontech.core.roleproperties.YukonRoleProperty"%>

<cti:includeScript link="/JavaScript/dataUpdater.js"/>
<c:url var="updateUrl" value="/updater/update" />

<script type="text/javascript">
initiateCannonDataUpdate("${updateUrl}", <cti:getProperty property="DATA_UPDATER_DELAY_MS"/>);
</script>

<div id="cannonUpdaterErrorDiv" style="display: none">
    <span class="meta">Connection to server has been lost (<span id="cannonUpdaterErrorDivCount"></span>)</span>
</div>
