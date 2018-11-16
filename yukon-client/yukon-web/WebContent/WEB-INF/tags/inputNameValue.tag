<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="disabled" type="java.lang.Boolean" %>
<%@ attribute name="readonly" type="java.lang.Boolean" description="Sets the readonly attribute on the input field. This will disable the field, but still submit the value."%>
<%@ attribute name="inputClass" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="nameClass" %>
<%@ attribute name="valueClass" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="rowClass" rtexprvalue="true" %>
<%@ attribute name="size" %>
<%@ attribute name="property" %>
<%@ attribute name="minPermissionLevel" %>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${path}" rowClass="${rowClass}" nameClass="${nameClass}" 
        valueClass="${valueClass}">
        
    <c:set var="disable" value="${pageScope.disabled}"/>
    <c:if test="${!disable && !empty property && !empty minPermissionLevel}">
        <cti:checkRolesAndProperties value="${property}" level="${minPermissionLevel}">
             <c:set var="hasAccess" value="true"/>
        </cti:checkRolesAndProperties>
         <c:if test="${!hasAccess}">
             <c:set var="disable" value="true"/>
         </c:if>
    </c:if>

    <tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${disable}" 
            inputClass="${pageScope.inputClass}" readonly="${readonly}"/>
</tags:nameValue2>