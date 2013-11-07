<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="dr" tagdir="/WEB-INF/tags/dr"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<cti:standardPage module="support" page="testAll">
    <cti:standardMenu menuSelection="events" />
    <table class="compact-results-table">
    <tr><th>Key</th><th>Message</th></tr>
    <c:forEach items="${allKeys}" var="key">
    <tr>
    <td>${key}</td>
    <td><cti:msg2 key="${key}"/></td>
    </tr>
    </c:forEach>
    </table>
</cti:standardPage>