<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="compact-results-table">
    <thead>
        <tr>
            <c:forEach var="column" items="${pops.columns}">
                <tags:sort column="${column}"/>
            </c:forEach>
        </tr>
    </thead>
    <tfoot></tfoot>
    <tbody>
        <c:forEach var="pop" items="${pops.data}">
            <tr>
                <td>${pop.city}</td>
                <td>${pop.population}</td>
            </tr>
        </c:forEach>
    </tbody>
</table>