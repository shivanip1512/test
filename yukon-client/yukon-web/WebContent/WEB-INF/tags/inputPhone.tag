<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ tag body-content="empty" %>
<%@ attribute name="nameKey" required="true" type="java.lang.String"%>

<%@ attribute name="path" required="true" type="java.lang.String"%>
<%@ attribute name="size" required="false" type="java.lang.String"%>
<%@ attribute name="maxlength" required="false" type="java.lang.String"%>
<%@ attribute name="disabled" required="false" type="java.lang.Boolean"%>
<%@ attribute name="inputClass" required="false" type="java.lang.String"%>

<%-- VIEW MODE --%>
<cti:displayForPageEditModes modes="VIEW">
    <spring:bind path="${path}">
        <tags:nameValue2 nameKey="${nameKey}" labelForId="${path}">
            <cti:formatPhoneNumber value="${status.value}"/>
        </tags:nameValue2>
    </spring:bind>
</cti:displayForPageEditModes>

<%-- CREATE/EDIT MODE --%>
<cti:displayForPageEditModes modes="CREATE,EDIT">
    <tags:nameValue2 nameKey="${nameKey}" labelForId="${path}">
        <tags:input path="${path}" size="${pageScope.size}" maxlength="${pageScope.maxlength}" disabled="${pageScope.disabled}" inputClass="${pageScope.inputClass} js-format-phone"/>
    </tags:nameValue2>
</cti:displayForPageEditModes>