<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:standardPage module="support">
<cti:standardMenu menuSelection="information|system"/>
<cti:breadCrumbs>
    <cti:crumbLink url="/operator/Operations.jsp" title="Operations Home"  />
    <cti:crumbLink url="/support/" title="Support" />
    <cti:crumbLink>System</cti:crumbLink>
</cti:breadCrumbs>

<h3 class="indentedElementHeading">Version Details</h3>
<div>${versionDetails}</div>

<h3 class="indentedElementHeading">Build Information</h3>
<div>
    <ul>
        <c:forEach var="entry" items="${buildInfo}">
            <li>${entry.key}: ${entry.value}</li>
        </c:forEach>
    </ul>
</div>

<h3 class="indentedElementHeading">System Information</h3>
<pre>
${systemInformation}
</pre>

</cti:standardPage>
