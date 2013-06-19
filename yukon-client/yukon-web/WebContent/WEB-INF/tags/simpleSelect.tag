<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n"%>
<%@ tag body-content="empty" %>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="selectedItem" required="false" type="java.lang.Object"%>
<%@ attribute name="selectedValue" required="false" type="java.lang.Object"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="cssClass" required="false" type="java.lang.String"%>
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
<c:set var="isSelected" value=""/>
<c:set var="value" value="${item[itemValue]}"/>
<c:if test="${empty itemValue}"><c:set var="value" value="${item}"/></c:if>
<c:set var="label" value="${item[itemLabel]}"/>
<c:if test="${empty itemLabel}"><c:set var="label" value="${item}"/></c:if>
<c:if test="${not empty itemLabelKey}"><cti:msg2 key="${item[itemLabelKey]}" var="label"/></c:if>
<c:if test="${item eq pageScope.selectedItem or value eq pageScope.selectedValue}"><c:set var="isSelected" value="selected"/></c:if>
<option value="${value}" ${isSelected}>${label}</option>
</c:forEach>
