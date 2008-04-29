<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="ct" tagdir="/WEB-INF/tags"%>

<spring:bind path="${input.field}">
	
    <ct:inputName input="${input}" error="${status.error}" />
    <br><br>
    
    <c:forEach var="option" items="${input.type.optionList}">
        <input type="radio" name="${status.expression}" value="${option.value}" <c:if test="${option.value == status.value}">checked</c:if>>${option.text}
        <br>
    </c:forEach>
    <br> 
    
</spring:bind>