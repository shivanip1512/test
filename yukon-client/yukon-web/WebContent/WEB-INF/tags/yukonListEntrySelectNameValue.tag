<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="energyCompanyId" required="true" type="java.lang.String"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>
<%@ attribute name="onchange" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}">
	<tags:yukonListEntrySelect path="${path}" energyCompanyId="${energyCompanyId}" listName="${listName}"
	                           defaultItemValue="${pageScope.defaultItemValue}"
	                           defaultItemLabel="${pageScope.defaultItemLabel}"
	                           onchange="${pageScope.onchange}" />
</tags:nameValue2>