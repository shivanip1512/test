<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ tag body-content="empty" %>
<%@ attribute name="value" required="false" type="java.lang.String"%>
<%@ attribute name="path" required="false" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="true" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<c:set var="labelFound" value="false"/>
<c:forEach var="item" items="${items}">
    <c:choose>
        <c:when test="${not empty path}">
            <c:if test="${status.value == item[itemValue]}">
                ${item[itemLabel]}
                <c:set var="labelFound" value="true"/>
            </c:if>
        </c:when>
        <c:when test="${not empty value}">
            <c:if test="${value == item[itemValue]}">
                ${item[itemLabel]}
                <c:set var="labelFound" value="true"/>
            </c:if>
        </c:when>
    </c:choose>
</c:forEach>
    
<c:if test="${!labelFound}">
    ${defaultItemLabel}
</c:if>
