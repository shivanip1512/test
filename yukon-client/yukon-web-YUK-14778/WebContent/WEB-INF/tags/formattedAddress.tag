<%@ attribute name="address" required="true" type="com.cannontech.database.data.lite.LiteAddress"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<div>
    <c:if test="${not empty address}">
        <c:set var="locationAddress1" value="${address.locationAddress1}"/>
        <c:if test="${not empty locationAddress1 && (fn:length(fn:trim(locationAddress1)) > 0)}">${locationAddress1}<br></c:if>
        
        <c:set var="locationAddress2" value="${address.locationAddress2}"/>
        <c:if test="${not empty locationAddress2 && (fn:length(fn:trim(locationAddress2)) > 0)}">${locationAddress2}<br></c:if>
        
        <c:if test="${not empty address.cityName}">${address.cityName}, </c:if>
        <c:if test="${not empty address.stateCode}">${address.stateCode} </c:if>
        <c:if test="${not empty address.zipCode}">${address.zipCode} </c:if>
    </c:if>    
</div>