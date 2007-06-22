<%@ attribute name="period" required="true" type="java.lang.Integer"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<cti:includeScript link="/JavaScript/dataUpdater.js"/>
<cti:includeScript link="/JavaScript/scriptaculous/effects.js"/>
<c:url var="updateUrl" value="/spring/updater/update" />

<script type="text/javascript">
initiateCannonDataUpdate("${updateUrl}", ${period});
</script>

<div id="cannonUpdaterErrorDiv" style="display: none; position: fixed; bottom: 0; left: 0; width: auto; background: red; color: white; font-weight: bold">
Connection to server has been lost
</div>
