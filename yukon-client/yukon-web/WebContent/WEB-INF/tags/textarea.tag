<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="rows" required="true" type="java.lang.Integer"%>
<%@ attribute name="cols" required="true" type="java.lang.Integer"%>
<%@ attribute name="id" required="false" type="java.lang.String"%>
<%@ attribute name="isResizable" required="false" type="java.lang.Boolean"%>
<%@ attribute name="placeholder" required="false" type="java.lang.String"%>
<%@ attribute name="maxLength" required="false" type="java.lang.Integer"%>
<%@ attribute name="autofocus" required="false" type="java.lang.Boolean"%>

<spring:bind path="${path}">

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
    <c:set var="note" value="${fn:escapeXml(status.value)}"/>
    ${fn:replace(note, "
", "<br>")}

</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

	<c:set var="inputClass" value=""/>
	<c:if test="${status.error}">
		<c:set var="inputClass" value="error"/>
	</c:if>
	
    <c:set var="resizable" value=""/>
    <c:if test="${isResizable == false}">
        <c:set var="resizable" value="resize: none;"/>
    </c:if>
    
    <c:choose>
        <c:when test="${autofocus == false}">
            <form:textarea path="${path}" rows="${rows}" cols="${cols}" cssClass="${inputClass}" id="${id}" 
                   cssStyle="${resizable}" placeholder="${placeholder}" maxlength="${maxLength}" tabindex="-1"/>
        </c:when>
        <c:otherwise>
            <form:textarea path="${path}" rows="${rows}" cols="${cols}" cssClass="${inputClass}" id="${id}" 
                   cssStyle="${resizable}" placeholder="${placeholder}" maxlength="${maxLength}"/>
        </c:otherwise>
    </c:choose>
	
	<c:if test="${status.error}">
		<br>
		<form:errors path="${path}" cssClass="error"/>
	</c:if>
	
</cti:displayForPageEditModes>

</spring:bind>