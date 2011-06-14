<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ attribute name="id" rtexprvalue="true"%>

<c:choose>
    <c:when test="${tabContainer}">
        <div <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if> class="tabContainer_content f_tab">
            <jsp:doBody/>
        </div>
    </c:when>
    <c:otherwise>
        <div class="errorMessage">ERROR: The &lt;tabElement&gt; tag must be enclosed in a &lt;tabContainer&gt; tag</div>
    </c:otherwise>
</c:choose>