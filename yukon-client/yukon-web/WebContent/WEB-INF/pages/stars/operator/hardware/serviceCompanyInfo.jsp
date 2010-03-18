<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<table class="noStyle">
    <tr>
        <td style="padding-top: 0px;vertical-align: top;"><spring:escapeBody htmlEscape="true">${companyName}</spring:escapeBody></td>
    </tr>
    <c:if test="${not empty locationAddress1}">
        <tr>
            <td>
                <spring:escapeBody htmlEscape="true">${locationAddress1}</spring:escapeBody>
            </td>
        </tr>
    </c:if>
    <c:if test="${not empty locationAddress2}">
        <tr>
            <td>
                <spring:escapeBody htmlEscape="true">${locationAddress2}</spring:escapeBody>
            </td>
        </tr>
    </c:if>
    <c:if test="${not empty locationAddress3}">
        <tr>
            <td>
                <spring:escapeBody htmlEscape="true">${locationAddress3}</spring:escapeBody>
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