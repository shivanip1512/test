<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%-- line number is key plus 2. (adjust for zero-indexed map key and header row) --%>
<cti:msgScope paths="yukon.common.device.bulk">
<table class="compact-results-table">
    <thead>
        <tr>
            <c:choose>
                <c:when test="${isFileUpload}">
                    <th><i:inline key=".lineNumber"/></th>
                </c:when>
                <c:otherwise>
                    <th><i:inline key=".deviceName"/></th>
                </c:otherwise>
            </c:choose>
            <th><i:inline key="yukon.common.error"/></th>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="e" items="${exceptionRowNumberMap}">
            <tr>
                <c:choose>
                    <c:when test="${isFileUpload}">
                        <td><i:inline key=".line" arguments="${e.key + 2}"/></td>
                    </c:when>
                    <c:otherwise>
                        <td>${e.value.args[1]}</td>
                        </c:otherwise>
                </c:choose>
                <td><cti:msg2 key="${e.value}" htmlEscape="true"/></td>
            </tr>
        </c:forEach>
    </tbody>
</table>
</cti:msgScope>