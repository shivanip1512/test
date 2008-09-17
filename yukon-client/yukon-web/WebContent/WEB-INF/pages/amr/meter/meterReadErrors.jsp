<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>



<c:if test="${not empty errorMsg}">
    <br>
    <div class="notAuthorized">${errorMsg}</div>
</c:if>

<c:if test="${not empty results}">

    <br>
    <div class="notAuthorized">There was an error reading the meter</div>
    
    <c:forEach items="${results}" var="result">
    
        <br>
    
        <c:forEach items="${result.errors}" var="error">
            <tags:hideReveal title="${error.description} (${error.errorCode})" showInitially="false">
            ${error.porter}<br>
            ${error.troubleshooting}<br>
            </tags:hideReveal><br>
        </c:forEach>
        
    </c:forEach>
    
</c:if>
