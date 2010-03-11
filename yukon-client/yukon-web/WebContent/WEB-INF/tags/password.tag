<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>

<form:password path="${path}" id="${path}" disabled="${disabled}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" />