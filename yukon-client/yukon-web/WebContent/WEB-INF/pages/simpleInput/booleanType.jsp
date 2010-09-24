<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
    <c:set var="inputClass" value="error"/>
</c:if>
<select <tags:attributeHelper name="id" value="${param.id}"/> name="${status.expression}" class="${inputClass}">
<option value="true"<c:if test="${status.value}">selected</c:if>>TRUE</option>
<option value="false"<c:if test="${not status.value and not empty status.value}">selected</c:if>>FALSE</option>
</select>
