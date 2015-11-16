<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="disabled" type="java.lang.Boolean" %>
<%@ attribute name="inputClass" %>
<%@ attribute name="maxlength" %>
<%@ attribute name="nameClass" %>
<%@ attribute name="valueClass" %>
<%@ attribute name="nameKey" required="true" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="rowClass" rtexprvalue="true" %>
<%@ attribute name="size" %>

<tags:nameValue2 nameKey="${nameKey}" labelForId="${path}" rowClass="${rowClass}" nameClass="${nameClass}" 
        valueClass="${valueClass}">
    <tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${pageScope.disabled}" 
            inputClass="${pageScope.inputClass}"/>
</tags:nameValue2>