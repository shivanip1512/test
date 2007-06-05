<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="amr" tagdir="/WEB-INF/tags/amr" %>

<cti:standardPage title="High Bill Complaint Device Selection" module="operations">
<cti:standardMenu/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/spring/csr/search" title="Device selection"  />
    &gt; Device page
</cti:breadCrumbs>

<script type="text/javascript" src="/JavaScript/json.js"></script>


<amr:searchResultsLink></amr:searchResultsLink>

	<div>
		Device: ${device.paoName}
	</div>

	
</cti:standardPage>