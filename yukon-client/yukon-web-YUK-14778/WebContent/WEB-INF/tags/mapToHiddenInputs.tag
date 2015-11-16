<%@ attribute name="values" required="true" type="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:forEach items="${values}" var="entry"><input name="${entry.key}" value="${entry.value}" type="hidden">
</c:forEach>