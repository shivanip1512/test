<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<cti:standardPage module="support" page="system">
    <div><strong><i:inline key=".versionDetails"/></strong></div>
    <div class="stacked">${versionDetails}</div>
    <div><strong><i:inline key=".buildInformation"/></strong></div>
    <div class="stacked">
        <ul class="simple-list">
            <c:forEach var="entry" items="${buildInfo}">
                <li>${entry.key}: ${entry.value}</li>
            </c:forEach>
        </ul>
    </div>
    <div><strong><i:inline key=".systemInformation"/></strong></div>
    <pre>${systemInformation}</pre>
</cti:standardPage>