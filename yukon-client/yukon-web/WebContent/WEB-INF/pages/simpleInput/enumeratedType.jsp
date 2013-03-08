<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<select name="${status.expression}">
	<c:forEach var="option" items="${inputType.optionList}">
        <cti:msg2 var="text" key="${option.obj}" blankIfMissing="true"/>
        <c:choose>
            <c:when test="${empty text}">
                <option value="${option.value}" <c:if test="${status.value == option.value}">selected</c:if>>${option.text}</option>
            </c:when>
            <c:otherwise>
                <option value="${option.value}" <c:if test="${status.value == option.value}">selected</c:if>>${text}</option>
            </c:otherwise>
        </c:choose>
	</c:forEach>
</select>
