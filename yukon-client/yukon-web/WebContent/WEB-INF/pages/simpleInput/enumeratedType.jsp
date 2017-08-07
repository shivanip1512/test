<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:displayForPageEditModes modes="VIEW">
    <c:forEach var="option" items="${inputType.optionList}">
        <c:if test="${status.value == option.value}">
            <cti:msg2 key="${option}"/>
            <input type="hidden" name="${status.expression}" value="${option.value}"/>
        </c:if>
    </c:forEach>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <c:set var="inputClass" value="${status.error ? 'error' : ''}"/>
    <select name="${status.expression}" class="${inputClass}">
        <c:forEach var="option" items="${inputType.optionList}">
            <c:choose>
                <c:when test="${!option.enabled}">
                    <c:set var="inputstate" value=" disabled"/>
                </c:when>
                <c:when test="${status.value == option.value}">
                    <c:set var="inputstate" value=" selected"/>
                </c:when>
                <c:otherwise>
                    <c:set var="inputstate" value=""/>
                </c:otherwise>
            </c:choose>
            <option value="${option.value}"${inputstate}><cti:msg2 key="${option}"/></option>
        </c:forEach>
    </select>
</cti:displayForPageEditModes>