<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<cti:msgScope paths="modules.operator.gateways">

<table class="compact-results-table">
<thead>
    <tr>
        <th><i:inline key=".name"/></th>
        <th><i:inline key=".updateServer.url"/></th>
        <th><i:inline key=".updateServer.login.user"/></th>
        <th><i:inline key=".updateServer.login.password"/></th>
    </tr>
</thead>
<tfoot></tfoot>
<tbody>
    <c:forEach var="gateway" items="${allSettings}">
    <tr>
        <td>${gateway.name}</td>
        <td>${gateway.updateServerUrl}</td>
        <td>${gateway.updateServerLogin.username}</td>
        <td>${gateway.updateServerLogin.password}</td>
    </tr>
    </c:forEach>
    </table>
</tbody>

</cti:msgScope>