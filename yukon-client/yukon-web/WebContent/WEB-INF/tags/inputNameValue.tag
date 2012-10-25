<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti"%>

<%@ tag body-content="empty" %>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>
<%@ attribute name="rowClass" rtexprvalue="true" %>
<%@ attribute name="escapeHtml" required="false" type="java.lang.Boolean" %>
<%@ attribute name="escapeJavascript" required="false" type="java.lang.Boolean" %>

<cti:default var="escapeHtml" value="false"/>
<cti:default var="escapeJavascript" value="false"/>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${path}" rowClass="${rowClass}" >
	<tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${pageScope.disabled}" inputClass="${pageScope.inputClass}" escapeHtml="${escapeHtml}" escapeJavascript="${escapeJavascript}"/>
</tags:nameValue2>