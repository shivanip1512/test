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

<tags:nameValue2 nameKey="${nameKey}">
	<tags:yukonListEntrySelect id="${pageScope.id}" path="${path}" energyCompanyId="${energyCompanyId}" listName="${listName}"
	                           defaultItemValue="${pageScope.defaultItemValue}"
	                           defaultItemLabel="${pageScope.defaultItemLabel}"
	                           onchange="${pageScope.onchange}" />
</tags:nameValue2>