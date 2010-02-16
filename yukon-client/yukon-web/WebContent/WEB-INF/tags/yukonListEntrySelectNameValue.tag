<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="accountId" required="true" type="java.lang.Integer"%>
<%@ attribute name="listName" required="true" type="java.lang.String"%>
<%@ attribute name="defaultItemValue" required="false" type="java.lang.String"%>
<%@ attribute name="defaultItemLabel" required="false" type="java.lang.String"%>

<i:nameValue nameKey="${nameKey}">
	<tags:yukonListEntrySelect path="${path}" accountId="${accountId}" listName="${listName}" defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}"/>
</i:nameValue>