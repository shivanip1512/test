<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}">
	<tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}"/>
</tags:nameValue2>