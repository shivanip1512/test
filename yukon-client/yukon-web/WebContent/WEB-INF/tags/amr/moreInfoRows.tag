<%@ attribute name="infoList" required="true" type="java.util.List"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="altRow" value="false"/>

<c:forEach var="x" items="${infoList}">
    
    <c:choose>
    <c:when test="${altRow}">
        <tr class="altRow">
    </c:when>
    <c:otherwise>
        <tr class="xxxxx">
    </c:otherwise> 
    </c:choose>
    
        <td width="40%" class="label"><c:out value="${x.label}"/>:</td>
        <td><c:out value="${x.value}"/></td>
    </tr>
    
    <c:set var="altRow" value="${!altRow}" />
    
</c:forEach>