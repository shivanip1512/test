<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"  %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
<select <tags:attributeHelper name="id" value="${param.id}"/> name="${status.expression}" class="${inputClass}">
<option value="true"<c:if test="${status.value}">selected</c:if>><i:inline key="yukon.common.true"/></option>
<option value="false"<c:if test="${not status.value and not empty status.value}">selected</c:if>><i:inline key="yukon.common.false"/></option>
</select>
