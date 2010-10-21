<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="true" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
	<spring:bind path="${path}">
	    <tags:showListEntry path="${path}" items="${items}" 
                            itemValue="${itemValue}" itemLabel="${itemLabel}"/>
	</spring:bind>
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<spring:bind path="${path}">

<c:set var="inputClass" value=""/>
<c:if test="${status.error}">
	<c:set var="inputClass" value="error"/>
</c:if>

<c:choose>
    <c:when test="${not empty pageScope.onchange}">
        <form:select path="${path}" id="${path}" onchange="${onchange}" cssClass="${inputClass}">
            <c:if test="${not empty pageScope.defaultItemLabel}">
                <form:option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</form:option>
            </c:if>
            <form:options items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}"/>
        </form:select>
    </c:when>
    <c:otherwise>
        <form:select path="${path}" id="${path}" cssClass="${inputClass}">
            <c:if test="${not empty pageScope.defaultItemLabel}">
                <form:option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</form:option>
            </c:if>
            <form:options items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}"/>
        </form:select>
    </c:otherwise>
</c:choose>

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

</spring:bind>

</cti:displayForPageEditModes>