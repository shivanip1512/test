<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="onclick" %>
<%@ attribute name="id" description="Note: Setting an id will cause the name to act as a label for the checkbox." %>
<%@ attribute name="excludeColon" %>
<%@ attribute name="checkBoxDescriptionNameKey" %>
<%@ attribute name="rowClass" %>
<%@ attribute name="inputClass" description="CSS class names applied to the checkbox." %>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${pageScope.id}" excludeColon="${pageScope.excludeColon}" rowClass="${pageScope.rowClass}">
    <tags:checkbox path="${path}" 
        onclick="${pageScope.onclick}" 
        id="${pageScope.id}" 
        descriptionNameKey="${checkBoxDescriptionNameKey}"
        styleClass="${pageScope.inputClass}"/>
</tags:nameValue2>