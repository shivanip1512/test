<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:nameValueContainer2>
    <tags:nameValue2 nameKey=".deviceName">${fn:escapeXml(relay.name)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".type">
       <tags:paoType yukonPao="${relay}" showLink="false"/>
    </tags:nameValue2>
    <tags:nameValue2 nameKey=".serialNumber">${fn:escapeXml(relay.rfnIdentifier.sensorSerialNumber)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".manufacturer">${fn:escapeXml(relay.rfnIdentifier.sensorManufacturer)}</tags:nameValue2>
    <tags:nameValue2 nameKey=".model">${fn:escapeXml(relay.rfnIdentifier.sensorModel)}</tags:nameValue2>
</tags:nameValueContainer2>
