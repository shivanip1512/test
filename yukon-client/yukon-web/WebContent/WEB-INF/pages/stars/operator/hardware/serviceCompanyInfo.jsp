<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<table>
    <tr>
        <td><spring:escapeBody htmlEscape="true">${companyName}</spring:escapeBody></td>
    </tr>
    <c:if test="${not empty address}">
        <tr>
            <td>    
                <tags:address address="${address}"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${not empty mainPhoneNumber}">
        <tr>
            <td>
                <cti:formatPhoneNumber value="${mainPhoneNumber}" htmlEscape="true"/>
            </td>
        </tr>
    </c:if>
</table>