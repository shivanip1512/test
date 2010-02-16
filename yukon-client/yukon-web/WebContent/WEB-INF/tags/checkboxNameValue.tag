<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="i" tagdir="/WEB-INF/tags/i18n" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>

<i:nameValue nameKey="${nameKey}">
	<tags:checkbox path="${path}" onclick="${pageScope.onclick}"/>
</i:nameValue>