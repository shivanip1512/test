<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%-- line number is key plus 2. (adjust for zero-indexed map key and header row) --%>
<cti:msgScope paths="yukon.common.device.bulk">
<table class="compact-results-table">
    <thead>
        <tr>
            <th><i:inline key=".lineNumber"/></th>
            <th><i:inline key="yukon.common.error"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="e" items="${exceptionRowNumberMap}">
            <tr>
                <td><i:inline key=".line" arguments="${e.key + 2}"/></td>
                <td>${fn:escapeXml(e.value.message)}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</cti:msgScope>