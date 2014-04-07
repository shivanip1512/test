<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cti" uri="http://cannontech.com/tags/cti" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<%@ attribute name="arguments" type="java.lang.Object" %>
<%@ attribute name="htmlEscape" type="java.lang.Boolean" description="Passed to cti:msg2 tag. Default: false" %>
<%@ attribute name="htmlEscapeArguments" type="java.lang.Boolean" description="Passed to cti:msg2 tag. Default: true" %>
<%@ attribute name="key" required="true" type="java.lang.Object" %>

<cti:default var="htmlEscape" value="${false}"/>
<cti:default var="htmlEscapeArguments" value="${true}"/>

<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE">
    <span class="i18n-inline">
        <cti:msg2 key="${key}" arguments="${arguments}" debug="true" fallback="true" htmlEscape="${htmlEscape}" htmlEscapeArguments="${htmlEscapeArguments}"/>
        <span class="i18n-inline-debug">
            <c:forEach var="entry" items="${msg2TagDebugMap}">${entry.key}=${entry.value}<br></c:forEach>
        </span>
    </span>
</cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE">
<cti:msg2 key="${key}" arguments="${arguments}" htmlEscape="${htmlEscape}" htmlEscapeArguments="${htmlEscapeArguments}"/>
</cti:checkGlobalRolesAndProperties>