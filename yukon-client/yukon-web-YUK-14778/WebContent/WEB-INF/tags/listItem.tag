<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ tag body-content="empty" %>
<%@ attribute name="value" required="true" type="java.lang.Object"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<c:set var="labelFound" value="false"/>
<c:forEach var="item" items="${items}">

    <c:set var="valueArg" value="${pageScope.itemValue}"/>
    <c:if test="${not empty itemValue}">
        <c:set var="valueArg" value="${item[itemValue]}"/>
    </c:if>
    <c:if test="${empty itemValue}">
        <c:set var="valueArg" value="${item}"/>
    </c:if>

    <c:set var="labelArg" value="${pageScope.itemLabel}"/>
    <c:if test="${not empty itemLabel}">
        <c:set var="labelArg" value="${item[itemLabel]}"/>
    </c:if>
    <c:if test="${empty itemLabel}">
        <c:set var="labelArg" value="${item}"/>
    </c:if>
    
    <c:if test="${value == valueArg}">
        <cti:formatObject value="${labelArg}"/>
        <c:set var="labelFound" value="true"/>
    </c:if>
</c:forEach>
    
<c:if test="${!labelFound}">
    ${defaultItemLabel}
</c:if>