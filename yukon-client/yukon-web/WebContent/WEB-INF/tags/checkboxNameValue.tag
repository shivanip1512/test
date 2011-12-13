<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti"%>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>
<%@ attribute name="id" required="false" type="java.lang.String" description="Note: Setting an id will cause the name to act as a label for the checkbox."%>
<%@ attribute name="excludeColon" required="false" %>
<%@ attribute name="checkBoxDescriptionNameKey" required="false" type="java.lang.String"%>
<%@ attribute name="rowClass" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${pageScope.id}" excludeColon="${pageScope.excludeColon}" rowClass="${pageScope.rowClass}">
	<tags:checkbox path="${path}" onclick="${pageScope.onclick}" id="${pageScope.id}" descriptionNameKey="${checkBoxDescriptionNameKey}"/>
</tags:nameValue2>