<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="rows" required="true" type="java.lang.Integer"%>
<%@ attribute name="cols" required="true" type="java.lang.Integer"%>
<%@ attribute name="rowClass" required="false"%>

<tags:nameValue2 nameKey="${nameKey}" rowClass="${rowClass}">
	<tags:textarea path="${path}" rows="${rows}" cols="${cols}"/>
</tags:nameValue2>