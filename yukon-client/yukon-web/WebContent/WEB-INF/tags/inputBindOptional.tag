<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ tag body-content="empty" %>
<%@ attribute name="bindPath" required="true" type="java.lang.Boolean" description="If true, this tag acts identical to input.tag. If false, this tag will result in a plain html input with no spring binding."%>
<%@ attribute name="pathOrName" required="true" type="java.lang.String" rtexprvalue="true" description="if bindPath is true this acts as a spring binding path, otherwise this sets the name field on the plain html input."%>
<%@ attribute name="autocomplete" type="java.lang.Boolean"%>

<%@ attribute name="disabled" type="java.lang.Boolean"%>
<%@ attribute name="readonly" type="java.lang.Boolean"%>
<%@ attribute name="size"%>
<%@ attribute name="maxlength"%>
<%@ attribute name="inputClass"%>
<%@ attribute name="id"%>

<c:if test="${pageScope.bindPath}">
    <tags:input path="${pageScope.pathOrName}"
        disabled="${pageScope.disabled}"
        readonly="${pageScope.readonly}"
        size="${pageScope.size}"
        maxlength="${pageScope.maxlength}"
        autocomplete="${pageScope.autocomplete}"
        inputClass="${pageScope.inputClass}"
        id="${pageScope.id}"/>
</c:if>

<c:if test="${not pageScope.bindPath}">
    <c:if test="${not empty pageScope.disabled && pageScope.disabled}">
        <c:set var="disabledAttr" value="disabled=\"${pageScope.disabled}\""/>
    </c:if>
    <c:if test="${not empty pageScope.readonly && pageScope.disabled}">
        <c:set var="readonlyAttr" value="readonly=\"${pageScope.readonly}\""/>
    </c:if>
    <c:if test="${not empty pageScope.size}">
        <c:set var="sizeAttr" value="size=\"${pageScope.size}\""/>
    </c:if>
    <c:if test="${not empty pageScope.maxlength}">
        <c:set var="maxlengthAttr" value="maxlength=\"${pageScope.maxlength}\""/>
    </c:if>
    <c:if test="${not empty pageScope.inputClass}">
        <c:set var="inputClassAttr" value="class=\"${pageScope.inputClass}\""/>
    </c:if>

    <c:if test="${not empty pageScope.id}">
        <c:set var="idAttr" value="id=\"${pageScope.id}\""/>
    </c:if>
    <c:if test="${empty pageScope.id}">
        <c:set var="idAttr" value="id=\"${pageScope.pathOrName}\""/>
    </c:if>

    <input name="${pathOrName}" type="text"
        ${pageScope.idAttr}
        ${pageScope.disabledAttr}
        ${pageScope.readonlyAttr}
        ${pageScope.sizeAttr}
        ${pageScope.inputClassAttr}/>
</c:if>