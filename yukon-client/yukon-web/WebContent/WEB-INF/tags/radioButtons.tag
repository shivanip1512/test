<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ attribute name="path" required="true" type="java.lang.String" description="This value is the same as the path variable we use for Spring." %>
<%@ attribute name="items" required="true" type="java.lang.Object" description="A list of items that can be used in a foreach." %>

<c:forEach var="item" items="${items}">
    <cti:msg2 var="optionLabel" key="${item}" />
    <form:radiobutton path="${path}" value="${item}" label="${optionLabel}" />
</c:forEach>
