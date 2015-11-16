<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="itemValue" %>
<%@ attribute name="itemLabel" %>
<%@ attribute name="groupItems" type="java.lang.Boolean" %>
<%@ attribute name="defaultItemValue" %>
<%@ attribute name="defaultItemLabel" %>
<%@ attribute name="emptyValueKey" %>
<%@ attribute name="onchange" %>
<%@ attribute name="inputClass" %>
<%@ attribute name="rowId" %>
<%@ attribute name="id" %>

<c:choose>
    <c:when test="${not empty pageScope.rowId}">
        <tags:nameValue2 rowId="${pageScope.rowId}" nameKey="${nameKey}">
            <tags:selectWithItems path="${path}" items="${items}" itemValue="${pageScope.itemValue}" itemLabel="${pageScope.itemLabel}" 
                                  defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}" 
                                  emptyValueKey="${pageScope.emptyValueKey}" onchange="${pageScope.onchange}" inputClass="${pageScope.inputClass}"
                                  groupItems="${pageScope.groupItems}" id="${pageScope.id}"/>
        </tags:nameValue2>
    </c:when>
    <c:otherwise>
        <tags:nameValue2 nameKey="${nameKey}">
            <tags:selectWithItems path="${path}" items="${items}" itemValue="${pageScope.itemValue}" itemLabel="${pageScope.itemLabel}" 
                                   defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}"
                                   emptyValueKey="${pageScope.emptyValueKey}" onchange="${pageScope.onchange}" inputClass="${pageScope.inputClass}"
                                   groupItems="${pageScope.groupItems}" id="${pageScope.id}"/>
         </tags:nameValue2>
    </c:otherwise>
</c:choose>
    