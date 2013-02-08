<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="system">

<h3 class="indentedElementHeading"><i:inline key="yukon.web.modules.support.system.versionDetails"/></h3>
<div>${versionDetails}</div>

<h3 class="indentedElementHeading"><i:inline key="yukon.web.modules.support.system.buildInformation"/></h3>
<div>
    <ul>
        <c:forEach var="entry" items="${buildInfo}">
            <li>${entry.key}: ${entry.value}</li>
        </c:forEach>
    </ul>
</div>

<h3 class="indentedElementHeading"><i:inline key="yukon.web.modules.support.system.systemInformation"/></h3>
<pre>
${systemInformation}
</pre>

</cti:standardPage>
