<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="nameClass" %>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>
<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="rowClass" rtexprvalue="true" %>
<%@ attribute name="size" required="false" type="java.lang.String"%>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${path}" rowClass="${rowClass}" nameClass="${nameClass}">
	<tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${pageScope.disabled}" inputClass="${pageScope.inputClass}"/>
</tags:nameValue2>