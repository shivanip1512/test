<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ tag body-content="empty" %>
<%@ attribute name="name" required="true" type="java.lang.String"%>
<%@ attribute name="selectedItem" required="false" type="java.lang.Object"%>
<%@ attribute name="selectedValue" required="false" type="java.lang.Object"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<select name="${name}">
<c:forEach items="${items}" var="item">
<c:set var="isSelected" value=""/>
<c:set var="value" value="${item[itemValue]}"/>
<c:if test="${empty itemValue}"><c:set var="value" value="${item}"/></c:if>
<c:set var="label" value="${item[itemLabel]}"/>
<c:if test="${empty itemLabel}"><c:set var="label" value="${item}"/></c:if>
<c:if test="${item eq pageScope.selectedItem or value eq pageScope.selectedValue}"><c:set var="isSelected" value="selected"/></c:if>
<option value="${value}" ${isSelected}>${label}</option>
</c:forEach>
</select>