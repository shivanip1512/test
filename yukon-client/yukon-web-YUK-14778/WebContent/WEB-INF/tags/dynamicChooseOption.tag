<%@ attribute name="optionId" required="true" type="java.lang.String"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:choose>
    <c:when test="${dynamicChoose}">
        <span id="${dynamicChooseId}${optionId}"><jsp:doBody/></span>
    </c:when>
    <c:otherwise>
        <div class="error" style="font-weight: bold">
            ERROR: The &lt;dynamicChooseOption&gt; tag must be enclosed in a &lt;dynamicChoose&gt; tag
        </div>
    </c:otherwise>
</c:choose>