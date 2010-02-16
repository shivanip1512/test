<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="true" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<form:select path="${path}" id="${path}">
	<c:if test="${not empty pageScope.defaultItemValue && not empty pageScope.defaultItemLabel}">
		<form:option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</form:option>
	</c:if>
	<form:options items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}"/>
</form:select>