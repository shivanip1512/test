<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ tag body-content="empty" %>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="true" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="emptyValueKey" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
	<spring:bind path="${path}">
	    <tags:listItem value="${status.value}" items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}"/>
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
            <c:forEach var="item" items="${items}">
                <form:option value="${item[itemValue]}"><cti:formatObject value="${item[itemLabel]}"/></form:option>
            </c:forEach>
        </form:select>
    </c:when>
    <c:otherwise>
        <form:select path="${path}" id="${path}" cssClass="${inputClass}">
            <c:if test="${not empty pageScope.defaultItemLabel}">
                <form:option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</form:option>
            </c:if>
            <c:forEach var="item" items="${items}">
                <form:option value="${item[itemValue]}">
                    <c:choose>
                        <c:when test="${not empty fn:trim(item[itemLabel])}">
                            <cti:formatObject value="${item[itemLabel]}"/>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${not empty pageScope.emptyValueKey}">
                                    <i:inline key="${pageScope.emptyValueKey}"/>
                                </c:when>
                                <c:otherwise>
                                    <i:inline key="defaults.na"/>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </form:option>
            </c:forEach>
        </form:select>
    </c:otherwise>
</c:choose>

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

</spring:bind>

</cti:displayForPageEditModes>