<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="newRouteName" required="true" type="java.lang.String"%>
<%@ attribute name="oldRouteName" required="false" type="java.lang.String"%>

<div class="okGreen">
    <cti:msg key="yukon.web.modules.amr.routeLocateSettings.updateInfo.newRouteName" /> ${newRouteName}
</div>