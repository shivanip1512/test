<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<%@ tag body-content="empty" %>
<%@ attribute name="value" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="true" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<c:set var="labelFound" value="false"/>
<c:forEach var="item" items="${items}">
    <c:if test="${value == item[itemValue]}">
        <cti:formatObject value="${item[itemLabel]}"/>
        <c:set var="labelFound" value="true"/>
    </c:if>
</c:forEach>
    
<c:if test="${!labelFound}">
    ${defaultItemLabel}
</c:if>