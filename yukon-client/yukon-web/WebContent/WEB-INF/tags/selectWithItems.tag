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
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="groupItems" required="false" type="java.lang.Boolean"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="emptyValueKey" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
	<spring:bind path="${path}">
        <c:choose>
            <c:when test="${not empty itemValue}">
                <tags:listItem value="${status.value}" items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}" defaultItemLabel="${defaultItemLabel}"/>
            </c:when>
            <c:otherwise>
                <tags:listItem value="${status.value}" items="${items}"/>
            </c:otherwise>
        </c:choose>
	</spring:bind>
</cti:displayForPageEditModes>

<%-- EDIT/CREATE MODE --%>
<cti:displayForPageEditModes modes="EDIT,CREATE">

<spring:bind path="${path}">

<c:set var="theInputClass" value="${pageScope.inputClass}"/>
<c:if test="${status.error}">
	<c:set var="theInputClass" value="${pageScope.inputClass} error"/>
</c:if>

<form:select path="${path}" id="${path}" cssClass="${theInputClass}" onchange="${pageScope.onchange}">
    <c:if test="${not empty pageScope.defaultItemLabel}">
        <form:option value="${pageScope.defaultItemValue}">${pageScope.defaultItemLabel}</form:option>
    </c:if>
    
    <c:choose>
        <c:when test="${pageScope.groupItems}">
            <c:forEach var="group" items="${items}">
                <optgroup label="<cti:msg2 key="${group.key}"/>">
                    <c:forEach var="item" items="${group.value}">
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
                        
                        <cti:enumName var="enumSafeValue" value="${valueArg}"/>
                        
                        <form:option value="${enumSafeValue}">
                            <c:choose>
                                <c:when test="${not empty fn:trim(labelArg)}">
                                    <cti:formatObject value="${labelArg}"/>
                                </c:when>
                                <c:otherwise>
                                    <c:choose>
                                        <c:when test="${not empty pageScope.emptyValueKey}">
                                            <cti:msg2 key="${pageScope.emptyValueKey}"/>
                                        </c:when>
                                        <c:otherwise>
                                            <cti:msg2 key="defaults.na"/>
                                        </c:otherwise>
                                    </c:choose>
                                </c:otherwise>
                            </c:choose>
                        </form:option>
                    </c:forEach>
                </optgroup>        
            </c:forEach>
        </c:when>
        <c:otherwise>
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
                
                <cti:enumName var="enumSafeValue" value="${valueArg}"/>
                
                <form:option value="${enumSafeValue}">
                    <c:choose>
                        <c:when test="${not empty fn:trim(labelArg)}">
                            <cti:formatObject value="${labelArg}"/>
                        </c:when>
                        <c:otherwise>
                            <c:choose>
                                <c:when test="${not empty pageScope.emptyValueKey}">
                                    <cti:msg2 key="${pageScope.emptyValueKey}"/>
                                </c:when>
                                <c:otherwise>
                                    <cti:msg2 key="defaults.na"/>
                                </c:otherwise>
                            </c:choose>
                        </c:otherwise>
                    </c:choose>
                </form:option>
            </c:forEach>
       </c:otherwise>
    </c:choose>    
</form:select>

<c:if test="${status.error}">
	<br>
	<form:errors path="${path}" cssClass="errorMessage"/>
</c:if>

</spring:bind>

</cti:displayForPageEditModes>