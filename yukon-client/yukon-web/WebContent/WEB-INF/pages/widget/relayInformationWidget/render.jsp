<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".deviceName">${fn:escapeXml(relay.name)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(relay.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".manufacturer">${fn:escapeXml(relay.rfnIdentifier.sensorManufacturer)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".model">${fn:escapeXml(relay.rfnIdentifier.sensorModel)}</tags:nameValue2>
</tags:nameValueContainer2>

<%-- Edit Popup --%>
<cti:url var="editUrl" value="/widget/relayInformationWidget/edit">
    <cti:param name="deviceId" value="${relay.paoIdentifier.paoId}"/>
    <cti:param name="shortName" value="relayInformationWidget"/>
</cti:url>
<div id="relay-info-popup" data-dialog
    data-event="yukon:widget:relay:info:save"
    data-width="500"
    data-title="<cti:msg2 key=".edit" arguments="${relay.name}"/>"
    data-ok-text="<cti:msg2 key="yukon.common.save"/>"
    data-url="${editUrl}"></div>
    
    
<div class="action-area">
    <cti:checkRolesAndProperties value="ENDPOINT_PERMISSION" level="UPDATE">
        <cti:button nameKey="edit" icon="icon-pencil" data-popup="#relay-info-popup"/>
    </cti:checkRolesAndProperties>
</div>

<cti:includeScript link="/resources/js/widgets/yukon.widget.relay.info.js"/>
