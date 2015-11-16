<%@ tag body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>

<%@ attribute name="name" required="true" %>
<%@ attribute name="selectedItem" type="java.lang.Object" %>
<%@ attribute name="selectedValue" type="java.lang.Object" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="cssClass" %>
<%@ attribute name="itemValue" %>
<%@ attribute name="itemLabel" %>
<%@ attribute name="itemLabelKey" %>
<%@ attribute name="defaultItemValue" %>
<%@ attribute name="defaultItemLabel" %>

<select name="${name}"<c:if test="${not empty cssClass}"> class="${cssClass}"</c:if>>
    
    <c:if test="${not empty pageScope.defaultItemLabel}">
        <option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</option>
    </c:if>
    
    <c:forEach items="${items}" var="item">
        
        <c:set var="selected" value=""/>
        <c:set var="value" value="${item[itemValue]}"/>
        
        <c:if test="${empty itemValue}">
            <c:set var="value" value="${item}"/>
        </c:if>
        
        <c:set var="label" value="${item[itemLabel]}"/>
        <c:if test="${empty itemLabel}">
            <c:set var="label" value="${item}"/>
        </c:if>
        <c:if test="${not empty itemLabelKey}">
            <cti:msg2 var="label" key="${item[itemLabelKey]}"/>
        </c:if>
        
        <c:if test="${item eq pageScope.selectedItem or value eq pageScope.selectedValue}">
            <c:set var="selected" value="selected"/>
        </c:if>
        
        <option value="${value}" ${selected}>${label}</option>
    </c:forEach>
    
</select>