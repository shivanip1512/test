<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="rows" required="true" type="java.lang.Integer"%>
<%@ attribute name="cols" required="true" type="java.lang.Integer"%>

<form:textarea path="${path }" rows="${rows}" cols="${cols}"/>
