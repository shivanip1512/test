<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr" %>

<h2><cti:msg2 key="yukon.web.modules.operator.hardware.assetAvailability.devices"/></h2>
<dr:assetDetailsResult result="${result}" type="${type}" assetId="${assetId}" itemsPerPage="${itemsPerPage}"/>
