<%@ tag body-content="empty" %>

<%@ attribute name="path" required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<table class="bitTable">
    <c:forEach var="i" begin="0" end="1">
        <tr>
            <c:forEach var="j" begin="${i * 8 + 1}" end="${i * 8 + 8}">
                <td class="tac">${j}</td>
            </c:forEach>
        </tr>
        <tr>
            <c:forEach var="j" begin="${i * 8}" end="${i * 8 + 7}">
                <td><tags:checkbox path="${path}[${j}]"/></td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>