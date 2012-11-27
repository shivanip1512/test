<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="database|info"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
	<cti:crumbLink url="/support/" title="Support" />
    <cti:crumbLink>Database Information</cti:crumbLink>
</cti:breadCrumbs>


<h3 class="indentedElementHeading">DB Connection</h3>
<div>
JDBC URL: ${dbUrl}<br />
JDBC User: ${dbUser}<br />
</div>

</cti:standardPage>
