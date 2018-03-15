<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:icon nameKey="dmvTestRunning" icon="icon-flag-blue" classes="fn dn js-dmvTestRunning" data-pao-id="${paoId}"/>

<cti:dataUpdaterCallback function="yukon.da.updaters.dmvTestRunning" initialize="true" value="${type}/${paoId}/DMV_TEST_RUNNING_FLAG"/>