<%@ tag trimDirectiveWhitespaces="true" body-content="empty" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="defaultItemValue" %>
<%@ attribute name="defaultItemLabel" %>
<%@ attribute name="energyCompanyId" required="true" %>
<%@ attribute name="id" %>
<%@ attribute name="listName" required="true" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="onchange" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="nameClass" %>
<%@ attribute name="valueClass" %>
<%@ attribute name="inputClass" description="CSS class names applied to the select." %>

<tags:nameValue2 nameKey="${nameKey}" valueClass="${pageScope.valueClass}" nameClass="${pageScope.nameClass}">
    <tags:yukonListEntrySelect id="${pageScope.id}"
                               path="${path}" 
                               energyCompanyId="${energyCompanyId}" 
                               listName="${listName}"
                               defaultItemValue="${pageScope.defaultItemValue}"
                               defaultItemLabel="${pageScope.defaultItemLabel}"
                               onchange="${pageScope.onchange}"
                               classes="${pageScope.inputClass}"/>
</tags:nameValue2>