<%@ attribute name="backingBean" required="true" rtexprvalue="true" type="com.cannontech.web.util.ListBackingBean" %>
<%@ tag body-content="empty"
    description="use to insert input field tags to preserve current sort field" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:if test="${!empty pageScope.backingBean.sort}">
    <form:hidden path="sort"/>
</c:if>
<c:if test="${!empty pageScope.backingBean.descending}">
    <form:hidden path="descending"/>
</c:if>
<c:if test="${!empty pageScope.backingBean.itemsPerPage}">
    <form:hidden path="itemsPerPage"/>
</c:if>
