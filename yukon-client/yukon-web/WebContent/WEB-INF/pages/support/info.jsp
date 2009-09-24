<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="information"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink>Support</cti:crumbLink>
</cti:breadCrumbs>


<h3 class="indentedElementHeading">Version Details</h3>
<div>${versionDetails}</div>

<h3 class="indentedElementHeading">System Information</h3>
<pre>
${systemInformation}
</pre>

</cti:standardPage>
