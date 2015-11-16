<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<cti:icon nameKey="verificationInProgress" icon="icon-flag-red" classes="fn dn js-verification" data-pao-id="${paoId}"/>

<cti:dataUpdaterCallback function="yukon.da.updaters.verification" initialize="true" value="${type}/${paoId}/VERIFICATION_FLAG"/>