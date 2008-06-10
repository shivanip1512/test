<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<table class="compactResultsTable">

    <c:forEach var="deviceInfoMap" items="${deviceInfoList}" varStatus="status">
    
        <c:if test="${status.count == 1}">
        
            <tr>
                <c:forEach var="info" items="${deviceInfoMap}">
                    <th>
                        ${info.key}
                    </th>
                </c:forEach>
            </tr>
        
        </c:if>
    
        <tr>
            <c:forEach var="info" items="${deviceInfoMap}">
                <td>${info.value}</td>
            </c:forEach>
        </tr>
        
    </c:forEach>
</table>