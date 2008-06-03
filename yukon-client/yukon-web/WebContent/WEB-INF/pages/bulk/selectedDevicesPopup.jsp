<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table>
    <c:forEach var="deviceInfoMap" items="${deviceInfoList}">
        <tr>
            <c:forEach var="info" items="${deviceInfoMap}">
                <td>${info.value}</td>
            </c:forEach>
        </tr>
    </c:forEach>
</table>