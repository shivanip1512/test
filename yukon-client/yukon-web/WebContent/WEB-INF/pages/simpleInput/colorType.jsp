<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<!-- using class name "js-color-input" instead of <input type="color"> since <input type="color"> does not support alpha is some browsers -->
<input <c:if test="${not empty param.mode and param.mode == 'VIEW'}">disabled="disabled"</c:if> <tags:attributeHelper name="id" value="${param.id}"/> type="hidden" name="${status.expression}" value="${status.value}" class="js-color-input">