<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"  %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<cti:displayForPageEditModes modes="VIEW">
    <c:forEach var="option" items="${inputType.optionList}">
        <c:if test="${status.value == option.value}">
            <cti:msg2 key="${option}"/>
        </c:if>
    </c:forEach>
</cti:displayForPageEditModes>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <select name="${status.expression}">
        <c:forEach var="option" items="${inputType.optionList}">
            <option value="${option.value}" <c:if test="${status.value == option.value}">selected</c:if>><cti:msg2 key="${option}"/></option>
        </c:forEach>
    </select>
</cti:displayForPageEditModes>