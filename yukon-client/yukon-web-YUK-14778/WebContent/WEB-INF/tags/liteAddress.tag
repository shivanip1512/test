<%@ attribute name="address" required="true" type="com.cannontech.database.data.lite.LiteAddress"%>
<%@ attribute name="inLine" required="false" description="If true no line break will be added." type="java.lang.Boolean" %>
<%@ attribute name="ignore" required="false" description="Treat this value as null" type="java.lang.String" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<c:if test="${not empty address}">
    <c:choose>
        <c:when test="${not empty pageScope.inLine && pageScope.inLine}">
            <tags:notNullDataLine value="${address.locationAddress1}" inLine="${pageScope.inLine}"
                ignore="${pageScope.ignore}" />
            <tags:notNullDataLine value="&nbsp;${address.locationAddress2}"
                inLine="${pageScope.inLine}" ignore="${pageScope.ignore}" />
        </c:when>
        <c:otherwise>
            <tags:notNullDataLine value="${address.locationAddress1}" ignore="${pageScope.ignore}" />
            <tags:notNullDataLine value="${address.locationAddress2}" ignore="${pageScope.ignore}" />
        </c:otherwise>
    </c:choose>
    <c:if test="${not empty address.cityName}">
        <tags:notNullDataLine value="${address.cityName}," inLine="true"
            ignore="${pageScope.ignore}," />
    </c:if>
    <tags:notNullDataLine value="${address.stateCode} " inLine="true" ignore="${pageScope.ignore}" />
    <tags:notNullDataLine value="${address.zipCode}" inLine="true" ignore="${pageScope.ignore}" />
</c:if>