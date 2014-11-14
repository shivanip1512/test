<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ tag import="com.cannontech.core.roleproperties.YukonRoleProperty"%>

<cti:includeScript link="YUKON_UPDATER"/>
<c:url var="updateUrl" value="/updater/update" />

<script type="text/javascript">
yukon.dataUpdater.start("${updateUrl}", <cti:getProperty property="DATA_UPDATER_DELAY_MS"/>);
</script>

<div id="data-updater-error" style="display: none">
    <span class="meta" aria-live="assertive"><i:inline key="yukon.web.tags.dataUpdateEnabler.lostConnection" /> (<span id="data-updater-error-count"></span>)</span>
</div>
