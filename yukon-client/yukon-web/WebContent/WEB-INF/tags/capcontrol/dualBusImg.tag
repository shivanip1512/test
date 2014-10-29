<%@ attribute name="paoId" required="true" %>
<%@ attribute name="type" required="true" %>

<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<span class="dn js-tooltip">
    <cti:capControlValue paoId="${paoId}" type="${type}" format="DUALBUS_MESSAGE"/>
</span>

<span class="dn js-dual-bus js-has-tooltip" data-pao-id="${paoId}">
    <cti:icon icon="icon-bullet-orange"/>
</span>

<cti:dataUpdaterCallback function="yukon.da.updaters.dualBus" initialize="true" value="${type}/${paoId}/DUALBUS"/>