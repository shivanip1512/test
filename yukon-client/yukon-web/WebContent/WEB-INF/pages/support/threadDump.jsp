<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="other|threadDump"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/support/" title="Support" />
    <cti:crumbLink>Thread Dump</cti:crumbLink>
</cti:breadCrumbs>

<c:forEach items="${dump}" var="threadEntry">
<h3 class="indentedElementHeading">${threadEntry.key.name}</h3>
<div>
<c:forEach items="${threadEntry.value}" var="stackEntry">
<div>${stackEntry}</div>
</c:forEach>
</div>
</c:forEach>

</cti:standardPage>
