<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="rows" required="true" type="java.lang.Integer" %>
<%@ attribute name="cols" required="true" type="java.lang.Integer" %>
<%@ attribute name="rowClass" required="false"%>
<%@ attribute name="nameClass" %>

<tags:nameValue2 nameKey="${nameKey}" rowClass="${rowClass}" nameClass="${nameClass}">
	<tags:textarea path="${path}" rows="${rows}" cols="${cols}"/>
</tags:nameValue2>