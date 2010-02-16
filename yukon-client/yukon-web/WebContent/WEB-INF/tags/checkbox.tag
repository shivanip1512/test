<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>

<form:checkbox path="${path}" onclick="${pageScope.onclick}"/>