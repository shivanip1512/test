<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ tag body-content="empty" %>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}">
	<tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${pageScope.disabled}" inputClass="${pageScope.inputClass}"/>
</tags:nameValue2>