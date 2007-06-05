<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:standardPage title="High Bill Complaint Device Selection" module="operations">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/highBill/deviceSelector" title="Device selector"  />
    &gt; CSR Home
</cti:breadCrumbs>

<script type="text/javascript" src="/JavaScript/json.js"></script>

	<div>
		Device: ${device.paoName}
	</div>
	
</cti:standardPage>