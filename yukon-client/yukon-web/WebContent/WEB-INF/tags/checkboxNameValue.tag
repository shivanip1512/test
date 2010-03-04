<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="onclick" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}">
	<tags:checkbox path="${path}" onclick="${pageScope.onclick}"/>
</tags:nameValue2>