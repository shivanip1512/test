<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://cannontech.com/tags/cti" prefix="cti" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags" %>
<%@ tag body-content="empty" %>

<%@ attribute name="key" required="true" type="java.lang.String"%>
<%@ attribute name="arguments" required="false" type="java.lang.String"%>

<cti:checkGlobalRolesAndProperties value="I18N_DESIGN_MODE"><span class="i18nInline"><cti:msg2 key="${key}" arguments="${arguments}" debug="true" fallback="true"/><span class="i18nInlineDebug">
<c:forEach items="${msg2TagDebugMap}" var="entry">${entry.key}=${entry.value}<br></c:forEach></span></span></cti:checkGlobalRolesAndProperties>
<cti:checkGlobalRolesAndProperties value="!I18N_DESIGN_MODE"><cti:msg2 key="${key}" arguments="${arguments}"/></cti:checkGlobalRolesAndProperties>