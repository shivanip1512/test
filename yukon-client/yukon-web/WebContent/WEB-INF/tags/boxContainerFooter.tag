<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ attribute name="styleClass" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>

<div class="box-container-footer clearfix ${pageScope.styleClass}" <c:if test="${not empty pageScope.id}">id="${pageScope.id}"</c:if> >
    <div class="footer">
        <jsp:doBody/>
    </div>
</div>