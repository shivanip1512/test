<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="items" required="true" type="java.lang.Object"%>
<%@ attribute name="itemValue" required="false" type="java.lang.String"%>
<%@ attribute name="itemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="groupItems" required="false" type="java.lang.Boolean"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="emptyValueKey" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>
<%@ attribute name="rowId" required="false" type="java.lang.String" %>

<c:choose>
    <c:when test="${not empty pageScope.rowId}">
        <tags:nameValue2 rowId="${pageScope.rowId}" nameKey="${nameKey}">
        	<tags:selectWithItems path="${path}" items="${items}" itemValue="${pageScope.itemValue}" itemLabel="${pageScope.itemLabel}" 
                                  defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}" 
                                  emptyValueKey="${pageScope.emptyValueKey}" onchange="${pageScope.onchange}" inputClass="${pageScope.inputClass}"
                                  groupItems="${pageScope.groupItems}"/>
        </tags:nameValue2>
    </c:when>
    <c:otherwise>
        <tags:nameValue2 nameKey="${nameKey}">
            <tags:selectWithItems path="${path}" items="${items}" itemValue="${pageScope.itemValue}" itemLabel="${pageScope.itemLabel}" 
                                   defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}"
                                   emptyValueKey="${pageScope.emptyValueKey}" onchange="${pageScope.onchange}" inputClass="${pageScope.inputClass}"
                                   groupItems="${pageScope.groupItems}"/>
         </tags:nameValue2>
    </c:otherwise>
</c:choose>
    