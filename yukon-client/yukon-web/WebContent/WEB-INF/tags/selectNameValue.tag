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

<tags:nameValue2 nameKey="${nameKey}">
	<tags:selectWithItems path="${path}" items="${items}" itemValue="${itemValue}" itemLabel="${itemLabel}" 
                          defaultItemValue="${pageScope.defaultItemValue}" defaultItemLabel="${pageScope.defaultItemLabel}" 
                          emptyValueKey="${pageScope.emptyValueKey}" onchange="${onchange}" inputClass="${inputClass}"
                          groupItems="${pageScope.groupItems}"/>
</tags:nameValue2>