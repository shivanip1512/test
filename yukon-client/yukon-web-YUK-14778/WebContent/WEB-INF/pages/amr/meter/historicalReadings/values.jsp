<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table">
    <thead>
        <tr>
            <tags:sort column="${timestamp}"/>
            <tags:sort column="${value}"/>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="row" items="${points}">
            <tr>
                <td>${fn:escapeXml(row[0])}</td>
                <td>${fn:escapeXml(row[1])}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>